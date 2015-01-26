package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class BattlerManager
{
	Battlers[] idle = new Battlers[3];
	ArrayList<Battlers> attackers = new ArrayList<>();
	
	public Position target = null;
	
	public void nextStage()
	{
		target = null;
		Arrays.fill(idle, null);
		attackers.clear();
	}
	
	public void setTarget(Position target)
	{
		this.target = target;
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		for (int i = 0; i < idle.length; i++)
		{
			if (idle[i] == null)
			{
				continue;
			}
			idle[i].update(units);
		}
		for (Iterator<Battlers> it= attackers.iterator(); it.hasNext(); )
		{
			Battlers battlers = it.next();
			battlers.update(units);
			if (battlers.isAlive() == false)
			{
				it.remove();
			}
		}
	}
	
	
	public void getRequests(ArrayList<Request> requests)
	{
		if (target == null)
		{
			return;
		}
		for (int i = 0; i < idle.length; i++)
		{
			if (idle[i] == null)
			{
				idle[i] = new Battlers(target);
			}
			for (int j = idle[i].needBattlers(); j > 0; j--)
			{
				requests.add(idle[i].getRequest());
			}
		}
	}
	
	public void compute(ArrayList<Unit> units)
	{
		for (int i = 0; i < idle.length; i++)
		{
			if (idle[i] == null)
			{
				continue;
			}
			if (idle[i].needBattlers() == 0)
			{
				attackers.add(idle[i]);
				idle[i] = new Battlers(target);
			}
		}
		for (Battlers battlers : attackers)
		{
			battlers.compute(units);
		}
	}
}
