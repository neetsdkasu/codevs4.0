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

public class TargetSearcherManager
{
	
	TargetSearcherUnit[] members = new TargetSearcherUnit[8];
	
	boolean searching = true;
	
	public TargetSearcherManager()
	{
		for (int i = 0; i < members.length; i++)
		{
			members[i] = new TargetSearcherUnit(i);
		}
	}
	
	public void changeRoll(List<BattlerUnit> attackers, Position target)
	{
		if (searching)
		{
			BattlerUnit battler = new BattlerUnit(target, members.length);
			for (TargetSearcherUnit member : members)
			{
				Unit unit = member.getUnit();
				if (unit != null)
				{
					member.setUnit(null);
					battler.assign(unit);
				}
			}
			attackers.add(battler);
			searching = false;
		}
	}
	
	public void reset(Position castle)
	{
		for (TargetSearcherUnit member : members)
		{
			member.reset(castle);
		}
		searching = true;
	}
	
	public void update(Map<Unit, Unit> units)
	{
		if (searching == false)
		{
			return;
		}
		for (TargetSearcherUnit member : members)
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
	
	
	public void getRequests(List<Request> requests, Set<Position> battler_maker_position)
	{
		if (searching == false)
		{
			return;
		}
		for (TargetSearcherUnit member : members)
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
			for (Position position : battler_maker_position)
			{
				if (position.distance(current) < distance);
				{
					target = position;
					distance = position.distance(current);
				}
			}
			if (target != null)
			{
				requests.add(new Request(target, false, Type.ASSASSIN, 0){
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
		if (searching == false)
		{
			return;
		}
		for (TargetSearcherUnit member : members)
		{
			if (member.compute())
			{
				action_units.add(member.getUnit());
			}
		}
	}
}
