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

public class ResourceSearcherManager
{
	
	ResourceSearcherUnit[] members = new ResourceSearcherUnit[10];
	
	public ResourceSearcherManager()
	{
		for (int i = 0; i < members.length; i++)
		{
			members[i] = new ResourceSearcherUnit(i);
		}
	}
	
	public void reset(Position castle)
	{
		for (ResourceSearcherUnit member : members)
		{
			member.reset(castle);
		}
	}
	
	public void update(Map<Unit, Unit> units)
	{
		for (ResourceSearcherUnit member : members)
		{
			Unit unit = member.getUnit();
			if (unit != null)
			{
				if (member.getPriority() < 0)
				{
					member.setUnit(null);
				}
				else
				{
					member.setUnit(units.remove(unit));
				}
			}
		}
	}
	
		
	public void changeRoll(DiggerManager digger)
	{
		for (ResourceSearcherUnit member : members)
		{
			Unit unit = member.getUnit();
			if (unit != null)
			{
				if (digger.assign(unit))
				{
					member.setUnit(null);
				}
			}
		}
	}
	
	public void getRequests(List<Request> requests, Set<Position> worker_maker_position)
	{
		for (ResourceSearcherUnit member : members)
		{
			if (member.getUnit() != null)
			{
				continue;
			}
			int priority = member.getPriority();
			if (priority < 0)
			{
				continue;
			}
			Position current = member.getCurrent();
			Position target = null;
			int distance = Integer.MAX_VALUE;
			for (Position position : worker_maker_position)
			{
				if (position.distance(current) < distance);
				{
					target = position;
					distance = position.distance(current);
				}
			}
			if (target != null)
			{
				requests.add(new Request(target, false, Type.WORKER, priority){
					@Override
					public void assign(Unit unit)
					{
						member.setUnit(unit);
					}
				});
			}
		}
	}
	
	public void compute(Set<Unit> action_units)
	{
		for (ResourceSearcherUnit member : members)
		{
			if (member.compute())
			{
				action_units.add(member.getUnit());
			}
		}
	}
}
