package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class DigUnit
{
	final Position resource_position;
	Unit unit = null;
	
	public DigUnit(Position resource_position)
	{
		this.resource_position = resource_position;
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		if (unit == null)
		{
			return;
		}
		if (units.containsKey(unit))
		{
			unit = units.remove(unit);
			if (resource_position.equals(unit.position))
			{
				unit.assigned = true;
				units.put(unit, unit);
			}
			return;
		}
		unit = null;
	}
	
	public Request getRequest()
	{
		if (unit != null)
		{
			return null;
		}
		int priority = (resource_position.getX() + resource_position.getY() < 100) ? 0 : 500;
		return new Request(priority, 1 << Type.WORKER.ordinal()){
			@Override
			public void assign(Unit unit)
			{
				setUnit(unit);
			}
		};
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
	
	public void compute(ArrayList<Unit> units)
	{
		if (unit == null)
		{
			return;
		}
		if (resource_position.equals(unit.position))
		{
			return;
		}
		units.add(unit);
		unit.moveTo(resource_position);
	}
}
