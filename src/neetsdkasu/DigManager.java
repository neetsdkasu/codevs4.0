package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class DigManager
{
	DigUnit[] member = new DigUnit[5];
	
	public DigManager(Position resource_position)
	{
		for (int i = 0; i < member.length; i++)
		{
			member[i] = new DigUnit(resource_position);
		}
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		for (DigUnit unit : member)
		{
			unit.update(units);
		}
	}
	
	public void getRequests(ArrayList<Request> requests)
	{
		for (DigUnit unit : member)
		{
			if (unit.isNoUnit())
			{
				requests.add(unit.getRequest());
			}
		}
	}
	
	public void compute(ArrayList<Unit> units)
	{
		int count = 0;
		for (DigUnit unit : member)
		{
			if (unit.isNoUnit())
			{
				count++;
				continue;
			}
			unit.compute(units);
		}
	}

}
