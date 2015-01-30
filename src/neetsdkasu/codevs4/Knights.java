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

public class Knights
{
	static int value = 0;
	static int getValue()
	{
		value++;
		return value & 1;
	}
	
	Map<Integer, List<Unit>> members = new HashMap<>();
	Position[] targets = new Position[25];
	Position target = null;
	int type;
	int count = 0;
	int member_count = 10;
	
	public Knights()
	{
		type = getValue();
	}
	
	public void setTarget(Position target)
	{
		if (target.getX() < 2)
		{
			target = target.move(0, 2 - target.getX());
		}
		if (target.getY() < 2)
		{
			target = target.move(2 - target.getY(), 0);
		}
		if (target.getX() > 97)
		{
			target = target.move(0, 97 - target.getX());
		}
		if (target.getY() > 97)
		{
			target = target.move(97 - target.getY(), 0);
		}
		this.target = target;
		for (int i = 0; i < 5; i++)
		{
			int y = i - 2;
			for (int j = 0; j < 5; j++)
			{
				int n = j + i * 5;
				int x = j - 2;
				targets[24 - n] = target.move(y, x);
			}
		}
		targets[0] = null;
		targets[4] = null;
		targets[24] = null;
		targets[20] = null;
	}
	
	public boolean fully()
	{
		return count >= 21 * member_count;
	}
	
	public void maxattack()
	{
		for (int i = 0; i < targets.length; i++)
		{
			targets[i] = target;
		}
		targets[0] = null;
		targets[4] = null;
		targets[24] = null;
		targets[20] = null;
	}
	
	public boolean isDied()
	{
		for (int i = 0; i < 25; i++)
		{
			if (members.get(i) == null)
			{
				continue;
			}
			for (Unit unit : members.get(i))
			{
				if (unit.hp > 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public Position getTarget()
	{
		return target;
	}
	
	public void reset()
	{
		members.clear();
		Arrays.fill(targets, null);
		target = null;
		count = 0;
	}
	
	public boolean reached()
	{
		List<Unit> list = members.get(12);
		if (list == null)
		{
			return false;
		}
		for (Unit unit : list)
		{
			if (unit.position.equals(target))
			{
				return true;
			}
		}
		return false;
	}
	
	public void getRequests(List<Request> requests, Set<Position> battler_maker_position)
	{
		if (fully())
		{
			return;
		}
		Position position = target;
		if (position == null)
		{
			position = new Position(20, 20);
		}
		for (int i = 0; i < 25; i++)
		{
			if (i == 0 || i == 4 || i == 20 || i == 24)
			{
				continue;
			}
			if (members.get(i) == null)
			{
				members.put(i, new ArrayList<Unit>());
			}
			List<Unit> list = members.get(i);
			for (int j = list.size(); j < member_count; j++)
			{
				requests.add(new Request(position, false, Type.KNIGHT, 1) {
					@Override
					public void assign(Unit unit)
					{
						list.add(unit);
						count++;
					}
				}); 
			}
		}
	}
	
	public void update(Map<Unit, Unit> units)
	{
		for (int i = 0; i < 25; i++)
		{
			if (members.get(i) == null)
			{
				continue;
			}
			List<Unit> list = new ArrayList<>();
			for (Unit unit : members.get(i))
			{
				if (units.containsKey(unit))
				{
					list.add(units.remove(unit));
				}
			}
			members.put(i, list);
		}
	}
	
	public void compute(Set<Unit> action_units)
	{
		for (int i = 0; i < 25; i++)
		{
			if (i == 0 || i == 4 || i == 20 || i == 24)
			{
				continue;
			}
			if (members.get(i) == null)
			{
				continue;
			}
			for (Unit unit : members.get(i))
			{
				if (type == 0)
				{
					if (unit.moveTo2(targets[i]))
					{
						action_units.add(unit);
					}
				}
				else
				{
					if (unit.moveTo(targets[i]))
					{
						action_units.add(unit);
					}
				}
			}
		}
	}
}
