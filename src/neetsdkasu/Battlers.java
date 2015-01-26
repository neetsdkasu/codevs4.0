package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class Battlers
{
	static final int BATTLER_MAX_COUNT = 5;
	
	Unit[] member = new Unit[BATTLER_MAX_COUNT];
	Position target;
	
	public Battlers(Position target)
	{
		this.target = target;
	}
	
	int assign_count = 0;
	int alive = 0;
	
	public void update(HashMap<Unit, Unit> units)
	{
		alive = 0;
		for (int i = 0; i < member.length; i++)
		{
			Unit unit = member[i];
			if (unit == null)
			{
				continue;
			}
			member[i] = units.remove(unit);
			if (member[i] != null) {
				alive++;
			}
		}
	}
	
	public void setTarget(Position target)
	{
		if (target != null)
		{
			this.target = target;
		}
	}
	
	public boolean isAlive()
	{
		return alive > 0;
	}
	
	public int needBattlers()
	{
		return BATTLER_MAX_COUNT - assign_count;
	}
	
	public Request getRequest()
	{
		return new Request(0, 1 << Type.ASSASSIN.ordinal()){
			@Override
			public void assign(Unit unit)
			{
				for (int i = 0; i < member.length; i++)
				{
					if (member[i] == null)
					{
						member[i] = unit;
						assign_count++;
						return;
					}
				}
			}
		};
	}
	
	public void compute(ArrayList<Unit> units)
	{
		if (assign_count < BATTLER_MAX_COUNT)
		{
			return;
		}
		for (int i = 0; i < member.length; i++)
		{
			Unit unit = member[i];
			if (unit == null)
			{
				continue;
			}
			if (target.equals(unit.position))
			{
				continue;
			}
			units.add(unit);
			unit.moveTo(target);
		}
	}
}
