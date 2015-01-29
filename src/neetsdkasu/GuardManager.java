package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class GuardManager
{
	HashMap<Unit, Unit> member = new HashMap<>();
	ArrayList<Unit> idle = new ArrayList<>();
	Position castle = null;
	int count = 0;
	
	public void nextStage()
	{
		idle.clear();
		member.clear();
		castle = null;
		count = 0;
	}
	
	public void setCastle(Position position)
	{
		this.castle = position;
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		count = 0;
		idle.clear();
		for (Iterator<Unit> it = member.keySet().iterator(); it.hasNext(); )
		{
			Unit unit = member.get(it.next());
			if (units.containsKey(unit))
			{
				unit = units.remove(unit);
				if (unit.position.equals(castle))
				{
					count++;
					idle.add(unit);
					switch (count & 0x3)
					{
					case 0:
						unit.command = Command.RIGHT;
						break;
					case 1:
						unit.command = Command.DOWN;
						break;
					case 2:
						unit.command = Command.LEFT;
						break;
					default:
						unit.command = Command.UP;
						break;
					}
				}
				else
				{
					int diff = Math.abs(unit.position.getX() - castle.getX()) + Math.abs(unit.position.getY() - castle.getY());
					if (diff == 1)
					{
						count++;
						unit.moveTo(castle);
						idle.add(unit);
					}
					else
					{
						member.put(unit, unit);
					}
				}
			}
			else
			{
				it.remove();
			}
		}
	}
	
	public void getRequests(ArrayList<Request> requests)
	{
		if (count > 500)
		{
			return;
		}
		for (int i = member.size(); i < 20; i++)
		{
			requests.add(new Request(10, 1 << Type.KNIGHT.ordinal()){
				@Override
				public void assign(Unit unit)
				{
					member.put(unit, unit);
				}
			});
		}
			
	}
	
	
	public void compute(ArrayList<Unit> units)
	{
		if (castle == null)
		{
			return;
		}
		for (Iterator<Unit> it = member.keySet().iterator(); it.hasNext(); )
		{
			Unit unit = member.get(it.next());
			if (castle.equals(unit.position))
			{
				continue;
			}
			units.add(unit);
			unit.moveTo(castle);
		}
		for (Unit unit : idle)
		{
			units.add(unit);
		}
	}
	
}
