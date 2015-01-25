package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class SearcherUnit
{
	
	final Position first, second, third, goal;
	final boolean mirror;
	Unit unit = null;
	Position current;
	boolean finished = false;
	
	public SearcherUnit(int id)
	{
		mirror = (id & 1) == 1;
		int z = id >> 1;
		int se = 4 + z * 9;
		int ee = 95 - z * 9;
		if (mirror)
		{
			first = new Position(4, se);
			second = new Position(ee, se);
			goal = new Position(4, ee);
		}
		else
		{
			first = new Position(se, 4);
			second = new Position(se, ee);
			goal = new Position(ee, 4);
		}
		third = new Position(ee, ee);
		
		current = first;
	}
	
	public void reset()
	{
		current = first;
		finished = true;
		unit = null;
	}
	
	boolean isCurrent()
	{
		if (unit == null)
		{
			return finished;
		}
		return (current.equals(unit.position));
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		if (unit == null)
		{
			return;
		}
		if (units.containsKey(unit) == false)
		{
			unit = null;
			return;
		}
		unit = units.remove(unit);
		if (current.equals(goal) && isCurrent())
		{
			finished = true;
			units.put(unit, unit);
			unit = null;
			return;
		}
	}
	
	public Unit getUnit()
	{
		return unit;
	}
	
	public boolean isNoUnit()
	{
		return unit == null;
	}
	
	public void setUnit(Unit unit)
	{
		this.unit = unit;
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public void compute(ArrayList<Unit> units)
	{
		if (finished || unit == null)
		{
			return;
		}
		units.add(unit);
		Position position = unit.position;
		if (isCurrent() == false)
		{
			int diffX = current.getX() - position.getX();
			int diffY = current.getY() - position.getY();
			if (Math.abs(diffX) < Math.abs(diffY))
			{
				unit.command = (diffY < 0) ? Command.UP : Command.DOWN;
			}
			else
			{
				unit.command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
			}
			return;
		}
		if (mirror)
		{
			if (current.getX() == goal.getX())
			{
				unit.command = Command.UP;
				current = new Position(current, -1, 0);
			}
			else if (current.getY() == third.getY())
			{
				unit.command = Command.RIGHT;
				current = new Position(current, 0, 1);
			}
			else
			{
				unit.command = Command.DOWN;
				current = new Position(current, 1, 0);
			}
		}
		else
		{
			if (current.getY() == goal.getY())
			{
				unit.command = Command.LEFT;
				current = new Position(current, 0, -1);
			}
			else if (current.getX() == third.getX())
			{
				unit.command = Command.DOWN;
				current = new Position(current, 1, 0);
			}
			else
			{
				unit.command = Command.RIGHT;
				current = new Position(current, 0, 1);
			}
		}
	}
}
