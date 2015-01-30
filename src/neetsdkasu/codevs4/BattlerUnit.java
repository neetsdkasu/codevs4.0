package neetsdkasu.codevs4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import neetsdkasu.codevs4.*;

public class BattlerUnit
{
	static Random rand = new Random();
	
	List<Unit> members = new ArrayList<>();
	Position start, target;
	int member_count, max_count;
	boolean all_members = false;
	Type type;
	int priority;
	int movetype;
	
	public BattlerUnit(Position start, int member_count, Type type, int priority)
	{
		this.target = this.start = start;
		this.max_count = this.member_count = member_count;
		this.type = type;
		this.priority = priority;
		movetype = rand.nextInt(100);
	}
	public BattlerUnit(Position start, int member_count)
	{
		this(start, member_count, Type.ASSASSIN, 0);
	}
	
	public Position getTarget()
	{
		return target;
	}

	
	public void setTarget(Position target)
	{
		this.target = target;
	}
	
	public boolean update(Map<Unit, Unit> units)
	{
		if (members.size() == 0)
		{
			return false;
		}
		List<Unit> list = new ArrayList<>();
		for (Unit unit : members)
		{
			if (units.containsKey(unit))
			{
				list.add(units.remove(unit));
			}
		}
		members = list;
		return members.size() > 0;
	}
	
	public void assign(Unit unit)
	{
		members.add(unit);
		member_count--;
	}
	
	public boolean weak()
	{
		int border = max_count * 2500;
		int count = 0;
		for (Unit unit : members)
		{
			count += unit.hp;
		}
		return count <= border;
	}
	
	public void getRequests(List<Request> requests, Set<Position> battler_maker_position)
	{
		for (int i = 0; i < member_count; i++)
		{
			requests.add(new Request(start, false, type, priority){
				@Override
				public void assign(Unit unit)
				{
					members.add(unit);
					member_count--;
				}
			});
		}
	}
	
	public boolean isDied()
	{
		return members.size() == 0 && member_count == 0;
	}
	
	public void compute(Set<Unit> action_units)
	{
		if (all_members)
		{
			for (Unit unit : members)
			{
				if (movetype < 50)
				{
					if (unit.moveTo(target))
					{
						action_units.add(unit);
					}
				}
				else
				{
					if (unit.moveTo2(target))
					{
						action_units.add(unit);
					}
				}
			}
		}
		else
		{
			int count = 0;
			for (Unit unit : members)
			{
				if (movetype < 50)
				{
					if (unit.moveTo(start))
					{
						action_units.add(unit);
						count++;
					}
				}
				else
				{
					if (unit.moveTo2(start))
					{
						action_units.add(unit);
						count++;
					}
				}
			}
			if (member_count == 0 && count == 0)
			{
				all_members = true;
			}
		}
	}
	
}
