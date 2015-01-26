package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class SearcherManager
{
	static final int MAX_MEMBER_COUNT = 10;

	SearcherUnit[] member = new SearcherUnit[MAX_MEMBER_COUNT];
	int needs;
	
	public SearcherManager()
	{
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			member[i] = new SearcherUnit(i);
		}
	}
	
	public void nextStage()
	{
		for (SearcherUnit unit : member)
		{
			unit.nextStage();
		}
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		needs = 0;
		for (SearcherUnit unit : member)
		{
			unit.update(units);
			if (unit.isFinished() == false)
			{
				needs++;
			}
		}
	}
	
	public int howManyNeedWorkers()
	{
		return needs;
	}
	
	public void assign(HashMap<Unit, Unit> units, int count)
	{
		if (needs == 0)
		{
			return;
		}
		int i = 0;
		for (Iterator<Unit> it = units.keySet().iterator(); it.hasNext();)
		{
			Unit unit = units.get(it.next());
			if (unit.type != Type.WORKER)
			{
				continue;
			}
			if ((count == 0) || (i == MAX_MEMBER_COUNT))
			{
				break;
			}
			for (;i < MAX_MEMBER_COUNT; i++)
			{
				if (member[i].isFinished() || !member[i].isNoUnit())
				{
					continue;
				}
				member[i].setUnit(unit);
				needs--;
				count--;
				it.remove();
				break;
			}
		}
	}
	
	public void getRequests(ArrayList<Request> requests)
	{
		for (SearcherUnit unit : member)
		{
			if (unit.isFinished() || !unit.isNoUnit())
			{
				continue;
			}
			requests.add(unit.getRequest());
		}
	}
	
	public void compute(ArrayList<Unit> units)
	{
		for (SearcherUnit unit : member)
		{
			unit.compute(units);
		}
	}
}

