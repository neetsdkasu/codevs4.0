package neetsdkasu.codevs4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.codevs4.*;

public class DiggerManager
{
	Map<Position, List<Unit>> members = new HashMap<>();
	Map<Position, Unit> guardians = new HashMap<>();
	int member_count = 0;
	int limit = 0;
	
	public void reset()
	{
		guardians.clear();
		members.clear();
		member_count = 0;
		limit = 50;
	}
	
	public void setLimit(int limit)
	{
		this.limit = limit;
	}
	
	public void setResourcePosition(List<Position> positions)
	{
		for (Position position : positions)
		{
			if (members.containsKey(position) == false)
			{
				members.put(position, new ArrayList<Unit>());
			}
		}
	}
	
	public boolean assign(Unit unit)
	{
		if (member_count >= limit)
		{
			return false;
		}
		List<Unit> target = null, list;
		int distance = Integer.MAX_VALUE;
		for (Position position : members.keySet())
		{
			list = members.get(position);
			if (list.size() >= 5)
			{
				continue;
			}
			if (position.distance(unit.position) < distance)
			{
				target = list;
				distance = position.distance(unit.position);
			}
		}
		if (target != null)
		{
			target.add(unit);
			member_count++;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void update(Map<Unit, Unit> units)
	{
		member_count = 0;
		for (Position position : members.keySet())
		{
			List<Unit> list = new ArrayList<>();
			for (Unit unit : members.get(position))
			{
				if (units.containsKey(unit))
				{
					list.add(units.remove(unit));
					member_count++;
				}
			}
			members.put(position, list);
		}
		for (Position position : guardians.keySet())
		{
			Unit unit = guardians.get(position);
			if (unit != null)
			{
				guardians.put(position, units.remove(unit));
			}
		}
	}
	
	public void getRequests(List<Request> requests, Set<Position> worker_maker_position)
	{
		int count = member_count;
		for (Position position : members.keySet())
		{
			List<Unit> list = members.get(position);
			if (list.size() >= 5)
			{
				if (guardians.get(position) == null)
				{
					requests.add(new Request(position, false, Type.ASSASSIN, 10){
						@Override
						public void assign(Unit unit)
						{
							guardians.put(position, unit);
						}
					});
				}
				continue;
			}
			if (count >= limit)
			{
				continue;
			}
			int priority = (position.getX() + position.getY() < 100) ? 0 : 100;
			int distance = Integer.MAX_VALUE;
			Position target = null;
			for (Position maker : worker_maker_position)
			{
				if (position.distance(maker) < distance)
				{
					target = maker;
					distance = position.distance(maker);
				}
			}
			if (target != null)
			{
				requests.add(new Request(target, false, Type.WORKER, priority){
					@Override
					public void assign(Unit unit)
					{
						list.add(unit);
					}
				});
				count++;
			}
		}
	}
	
	public void compute(Set<Unit> action_units)
	{
		for (Position position : members.keySet())
		{
			for (Unit unit : members.get(position))
			{
				if (unit.moveTo(position))
				{
					action_units.add(unit);
				}
			}
		}
		for (Position position : guardians.keySet())
		{
			Unit unit = guardians.get(position);
			if (unit != null)
			{
				if (unit.moveTo(position))
				{
					action_units.add(unit);
				}
			}
		}
	}
}
