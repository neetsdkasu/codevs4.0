package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import neetsdkasu.*;

public class Stage
{
	int number = -1;
	int turn = 0;
	int resource_count = 0;
	int unit_count = 0;
	int enemy_count = 0;
	int all_workers = 0;
	
	HashMap<Position, DigManager> resources = new HashMap<>();
	HashMap<Unit, Unit> workers = new HashMap<>();
	ArrayList<Unit> performers = new ArrayList<>();
	ArrayList<Request> assign_requests = new ArrayList<>();
	HashMap<Unit, Unit> worker_makers = new HashMap<>();
	HashMap<Unit, Unit> battler_makers = new HashMap<>();
	int[] make_requests = new int[7];
	VillageManager villages = new VillageManager();
	BaseManager bases = new BaseManager();
	BattlerManager battlersmgr = new BattlerManager();
	HashMap<Unit, Unit> battlers = new HashMap<>();
	GuardManager guard = new GuardManager();
	
	Unit enemy_castle = null;
	
	SearcherManager searcher = new SearcherManager();
	
	Unit castle = null;
	
	public void nextStage(int number)
	{
		this.number = number;
		resources.clear();
		searcher.nextStage();
		battlersmgr.nextStage();
		castle = null;
		enemy_castle = null;
	}
	
	public void nextTurn(int turn)
	{
		this.turn = turn;
		workers.clear();
		performers.clear();
		assign_requests.clear();
		worker_makers.clear();
		battler_makers.clear();
		Arrays.fill(make_requests, 0);
		villages.nextTurn();
		battlers.clear();
		bases.nextTurn();
		all_workers = 0;
	}
	
	public void addUnit(Unit unit)
	{
		switch (unit.type)
		{
		case WORKER:
			workers.put(unit, unit);
			break;
		case KNIGHT:
		case FIGHTER:
		case ASSASSIN:
			battlers.put(unit, unit);
			break;
		case CASTLE:
			if (castle == null)
			{
				Position.setMirror(unit.position.getAbsoluteX() > 50 && unit.position.getAbsoluteY() > 50);
			}
			castle = unit;
			guard.setCastle(unit.position);
			worker_makers.put(unit, unit);
			break;
		case VILLAGE:
			villages.addVillage(unit);
			worker_makers.put(unit, unit);
			break;
		case BASE:
			bases.addBase(unit);
			battler_makers.put(unit, unit);
			break;
		}
	}
	
	public void addEnemy(Unit unit)
	{
		switch (unit.type)
		{
		case CASTLE:
			if (enemy_castle == null)
			{
				enemy_castle = unit;
				battlersmgr.setTarget(unit.position);
			}
			break;
		}
	}
	
	public void addResourcePosition(Position resource_position)
	{
		if (resources.containsKey(resource_position))
		{
			return;
		}
		resources.put(resource_position, new DigManager(resource_position));
	}
	
	void hearAssignRequests()
	{
		searcher.getRequests(assign_requests);
		
		for (DigManager dig : resources.values())
		{
			dig.getRequests(assign_requests);
		}
		
		battlersmgr.getRequests(assign_requests);
		
		guard.getRequests(assign_requests);
		
		Collections.sort(assign_requests);
		
	}
	
	void assignUnits()
	{
		Iterator<Unit> wkey = workers.keySet().iterator();
		
		for (Iterator<Request> it = assign_requests.iterator(); it.hasNext(); )
		{
			Request request = it.next();
			if ((request.needtypes & (1 << Type.WORKER.ordinal())) > 0)
			{
				if (wkey.hasNext())
				{
					Unit unit = workers.get(wkey.next());
					while (unit.assigned)
					{
						if (wkey.hasNext())
						{
							unit = workers.get(wkey.next());
						}
						else
						{
							unit = null;
							break;
						}
					}
					if (unit != null)
					{
						request.assign(unit);
						wkey.remove();
						it.remove();
						continue;
					}
				}
				make_requests[Type.WORKER.ordinal()]++;
			}
			
			if ((request.needtypes & (1 << Type.ASSASSIN.ordinal())) > 0)
			{
				Iterator<Unit> bkey = battlers.keySet().iterator();
				if (bkey.hasNext())
				{
					Unit unit = battlers.get(bkey.next());
					while (unit.type != Type.ASSASSIN)
					{
						if (bkey.hasNext())
						{
							unit = battlers.get(bkey.next());
						}
						else
						{
							unit = null;
							break;
						}
					}
					if (unit != null)
					{
						request.assign(unit);
						bkey.remove();
						it.remove();
						continue;
					}
				}
				make_requests[Type.ASSASSIN.ordinal()]++;
			}
			
			if ((request.needtypes & (1 << Type.KNIGHT.ordinal())) > 0)
			{
				Iterator<Unit> bkey = battlers.keySet().iterator();
				if (bkey.hasNext())
				{
					Unit unit = battlers.get(bkey.next());
					while (unit.type != Type.KNIGHT)
					{
						if (bkey.hasNext())
						{
							unit = battlers.get(bkey.next());
						}
						else
						{
							unit = null;
							break;
						}
					}
					if (unit != null)
					{
						request.assign(unit);
						bkey.remove();
						it.remove();
						continue;
					}
				}
				make_requests[Type.KNIGHT.ordinal()]++;
			}
		}
	}
	
	void assignMakeRequests()
	{
		Iterator<Unit> bmkey = battler_makers.keySet().iterator();
		for (int i = 0; i < make_requests[Type.ASSASSIN.ordinal()]; i++)
		{
			if (resource_count < 60 || !bmkey.hasNext())
			{
				break;
			}
			resource_count -= 60;
			Unit unit = battler_makers.get(bmkey.next());
			bmkey.remove();
			unit.command = Command.ASSASSIN;
			performers.add(unit);
		}

		bmkey = battler_makers.keySet().iterator();
		for (int i = 0; i < make_requests[Type.KNIGHT.ordinal()]; i++)
		{
			if (resource_count < 20 || !bmkey.hasNext())
			{
				break;
			}
			resource_count -= 20;
			Unit unit = battler_makers.get(bmkey.next());
			bmkey.remove();
			unit.command = Command.KNIGHT;
			performers.add(unit);
		}

		Iterator<Unit> wkey = workers.keySet().iterator();

		while (wkey.hasNext() && resource_count > 500)
		{
			if (bases.needBases() == 0)
			{
				break;
			}
			Unit unit = workers.get(wkey.next());
			if (bases.needBasePosition(unit.position) == false)
			{
				continue;
			}
			wkey.remove();
			resource_count -= 500;
			unit.command = Command.BASE;
			performers.add(unit);
		}
				
		Iterator<Unit> wmkey = worker_makers.keySet().iterator();
		for (int i = 0; i < make_requests[Type.WORKER.ordinal()]; i++)
		{
			if (resource_count < 40 || !wmkey.hasNext())
			{
				break;
			}
			resource_count -= 40;
			Unit unit = worker_makers.get(wmkey.next());
			wmkey.remove();
			unit.command = Command.WORKER;
			performers.add(unit);
		}
		

		
		wkey = workers.keySet().iterator();
		
		while (wkey.hasNext() && resource_count > 100)
		{
			if (villages.needVillages() == 0)
			{
				break;
			}
			Unit unit = workers.get(wkey.next());
			if (villages.needVillagePosition(unit.position) == false)
			{
				continue;
			}
			resource_count -= 100;
			wkey.remove();
			unit.command = Command.VILLAGE;
			performers.add(unit);
		}
	}
	
	void update()
	{
		searcher.update(workers);
		
		for (DigManager dig : resources.values())
		{
			dig.update(workers);
		}
		
		battlersmgr.update(battlers);
		
		guard.update(battlers);
	}
	
	public void compute()
	{
		all_workers = workers.size();
		
		update();
		
		hearAssignRequests();
		
		assignUnits();
		
		assignMakeRequests();
		
		searcher.compute(performers);

		for (DigManager dig : resources.values())
		{
			dig.compute(performers);
		}
		
		battlersmgr.compute(performers);
		
		guard.compute(performers);

	}
}

