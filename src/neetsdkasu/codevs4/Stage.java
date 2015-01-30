package neetsdkasu.codevs4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import neetsdkasu.codevs4.*;

public class Stage
{
	static final Command[] COMMANDS = new Command[]{
		Command.WORKER, Command.KNIGHT, Command.FIGHTER, Command.ASSASSIN, Command.CASTLE, Command.VILLAGE, Command.BASE,
		Command.UP, Command.DOWN, Command.LEFT, Command.RIGHT, Command.STAY
	};
	static final int[] COMMAND_COSTS = new int[]{
		40, 20, 40, 60, 0, 100, 500, 0, 0, 0, 0, 0
	};
	
	TurnState before_state = null;
	TurnState last_state = null;
	
	Random rand = new Random();
	
	Set<Unit> action_units = new HashSet<>();
	
	TargetSearcherManager target_searcher_manager = new TargetSearcherManager();
	ResourceSearcherManager resource_searcher_manager = new ResourceSearcherManager();
	DiggerManager digger_manager = new DiggerManager();
	
	Unit castle = null;
	Unit castle_worker = null;
	Unit enemy_castle = null;
	
	Set<Position> worker_maker_position = new HashSet<>();
	Set<Position> battler_maker_position = new HashSet<>();
	List<Unit> worker_makers = new ArrayList<>();
	List<Unit> battler_makers = new ArrayList<>();
	List<Unit> idle_workers = new ArrayList<>();
	
	List<BattlerUnit> attackers = new ArrayList<>();
	
	List<Request> requests = new ArrayList<>();
	
	BattlerUnit[] guardians = new BattlerUnit[4];

	Knights[] knights = new Knights[5];
	Knights wall_kngiht = null;
	
	boolean[][] viewed = new boolean[46][46];
	List<Position> nonview_position = new ArrayList<>();
	
	Map<Position, Integer> enemies_count = new HashMap<>();

	public void setTurnState(TurnState state)
	{
		before_state = last_state;
		last_state = state;
		
		idle_workers.clear();
		action_units.clear();
		worker_makers.clear();
		battler_makers.clear();
		worker_maker_position.clear();
		battler_maker_position.clear();
		requests.clear();
		enemies_count.clear();
		
		if (last_state.turn == 0 || before_state == null)
		{
			nextStage();
		}
		
		compute();
	}
	
	public Set<? extends Unit> getActionUnits()
	{
		return action_units;
	}
	
	void nextStage()
	{
		rand.setSeed(rand.nextLong());
		
		before_state = null;
		castle_worker = null;
		enemy_castle = null;
		wall_kngiht = null;
		
		for (int i = 0; i < 40; i++)
		{
			Arrays.fill(viewed[i], 0, 40 - i, false);
			Arrays.fill(viewed[i], 40 - i, viewed.length, true);
		}
		
		nonview_position.clear();
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8 - i; j++)
			{
				nonview_position.add(new Position(i * 5 + 2, j * 5 + 2));
			}
		}
		
		for (Iterator<Unit> it = last_state.units.keySet().iterator(); it.hasNext(); )
		{
			Unit unit = last_state.units.get(it.next());
			if (unit.type == Type.CASTLE)
			{
				castle = unit;
				Position.setMirror(castle.position.getRealX() > 50);
				break;
			}
		}
		digger_manager.reset();
		resource_searcher_manager.reset(castle.position);
		target_searcher_manager.reset(castle.position);
		attackers.clear();

		guardians[0] = new BattlerUnit(castle.position.move(1, 0), 10);
		guardians[1] = new BattlerUnit(castle.position.move(0, 1), 10);
		guardians[2] = new BattlerUnit(castle.position.move(-1, 0), 10);
		guardians[3] = new BattlerUnit(castle.position.move(0, -1), 10);
		
		for (int i = 0; i < knights.length; i++)
		{
			knights[i] = new Knights();
			knights[i].setTarget(castle.position);
		}
		
	}
	
	void update()
	{
		if (castle_worker != null)
		{
			castle_worker = last_state.units.remove(castle_worker);
			if (castle_worker != null)
			{
				idle_workers.add(castle_worker);
			}
		}
		
		
		if (wall_kngiht != null)
		{
			wall_kngiht.update(last_state.units);
			if (wall_kngiht.isDied())
			{
				wall_kngiht = null;
			}
		}
		
		if (enemy_castle == null)
		{
			for (Unit unit : last_state.enemies.keySet())
			{
				if (unit.type == Type.CASTLE)
				{
					enemy_castle = unit;
					for (BattlerUnit battler : attackers)
					{
						battler.setTarget(enemy_castle.position);
					}
					target_searcher_manager.changeRoll(attackers, enemy_castle.position);
					break;
				}
			}
		}
		
		digger_manager.setResourcePosition(last_state.resource_positions);
		
		for (Iterator<Unit> it = last_state.units.keySet().iterator(); it.hasNext(); )
		{
			Unit key = it.next(), unit;
			switch (key.type)
			{
			case CASTLE:
				castle = last_state.units.get(key);
			case VILLAGE:
				unit = last_state.units.get(key);
				worker_maker_position.add(unit.position);
				worker_makers.add(unit);
				it.remove();
				break;
			case BASE:
				unit = last_state.units.get(key);
				battler_maker_position.add(unit.position);
				battler_makers.add(unit);
				it.remove();
				break;
			}
		}
		
		digger_manager.update(last_state.units);
		resource_searcher_manager.update(last_state.units);
		target_searcher_manager.update(last_state.units);
		
		resource_searcher_manager.changeRoll(digger_manager, enemies_count);
		
		digger_manager.getIdelWorkers(idle_workers);
		
		
		for (int i = 0; i < guardians.length; i++)
		{
			guardians[i].update(last_state.units);
			if (guardians[i].weak())
			{
				guardians[i] = new BattlerUnit(guardians[i].start, 10);
			}
		}

		for (Iterator<BattlerUnit> it = attackers.iterator(); it.hasNext(); )
		{
			BattlerUnit battler = it.next();
			battler.update(last_state.units);
			if (battler.isDied())
			{
				it.remove();
			}
			else
			{
				if (enemy_castle == null)
				{
					if (last_state.turn % 30 == 0)
					{
						battler.setTarget(new Position(99 - rand.nextInt(30), 99 - rand.nextInt(30)));
					}
				}
			}
		}
		
		if (last_state.turn > 120)
		{
			if (attackers.size() < 10)
			{
				BattlerUnit battler = new BattlerUnit(castle.position, 10);
				if (enemy_castle != null)
				{
					battler.setTarget(enemy_castle.position);
				}
				else
				{
					battler.setTarget(new Position(99 - rand.nextInt(30), 99 - rand.nextInt(30)));
				}
				attackers.add(battler);
			}
		}
		
		for (int i = 0; i < knights.length; i++)
		{
			knights[i].update(last_state.units);
			if (knights[i].isDied())
			{
				knights[i] = new Knights();
				knights[i].setTarget(castle.position);
			}
		}
	}

	
	void getRequests()
	{
		if (castle_worker == null)
		{
			requests.add(new Request(castle.position, true, Type.WORKER, 0){
				@Override
				public void assign(Unit unit)
				{
					castle_worker = unit;
				}
			});
		}
		if (last_state.turn < 150 && battler_makers.size() < 3)
		{
			requests.add(new Request(castle.position, true, Type.BASE, 0){
				@Override
				public void assign(Unit unit)
				{
				}
			});
		}
		if (last_state.turn > 200 && battler_makers.size() < 3)
		{
			requests.add(new Request(castle.position, true, Type.BASE, 0){
				@Override
				public void assign(Unit unit)
				{
				}
			});
		}
		
		digger_manager.getRequests(requests, worker_maker_position);
		resource_searcher_manager.getRequests(requests, worker_maker_position);
		
		if (last_state.turn > 100)
		{
			//target_searcher_manager.getRequests(requests, battler_maker_position);
			
			for (BattlerUnit unit : guardians)
			{
				//unit.getRequests(requests, battler_maker_position);
			}
			
			for (BattlerUnit unit : attackers)
			{
				//unit.getRequests(requests, battler_maker_position);
			}
		}
		
		for (int i = 0; i < knights.length; i++)
		{
			if (knights[i].fully())
			{
				continue;
			}
			knights[i].getRequests(requests, battler_maker_position);
			break;
		}
		
		Collections.sort(requests);
	}

	void assignRequests()
	{
		request_loop:
		for (Request request : requests)
		{
			switch (request.type)
			{
			case WORKER:
			case KNIGHT:
			case FIGHTER:
			case ASSASSIN:
				for (Iterator<Unit> it = last_state.units.keySet().iterator(); it.hasNext(); )
				{
					Unit key = it.next();
					if (request.type != key.type)
					{
						continue;
					}
					if (request.must_be && !request.position.equals(key.position))
					{
						continue;
					}
					request.assign(last_state.units.get(key));
					it.remove();
					continue request_loop;
				}
				if (last_state.resource_count < COMMAND_COSTS[request.type.ordinal()])
				{
					continue request_loop;
				}
				List<Unit> maker = (request.type == Type.WORKER) ? worker_makers : battler_makers;
				for (Iterator<Unit> it = maker.iterator(); it.hasNext(); )
				{
					Unit unit = it.next();
					if (request.must_be && !request.position.equals(unit.position))
					{
						continue;
					}
					last_state.resource_count -= COMMAND_COSTS[request.type.ordinal()];
					unit.command = COMMANDS[request.type.ordinal()];
					action_units.add(unit);
					it.remove();
					break;
				}
				break;
			case VILLAGE:
				if (worker_makers.size() > 2 || last_state.turn < 150)
				{
					break;
				}
			case BASE:
				if (last_state.resource_count < COMMAND_COSTS[request.type.ordinal()])
				{
					continue request_loop;
				}
				for (Iterator<Unit> it = idle_workers.iterator(); it.hasNext(); )
				{
					Unit unit = it.next();
					if (request.must_be && !request.position.equals(unit.position))
					{
						continue;
					}
					last_state.resource_count -= COMMAND_COSTS[request.type.ordinal()];
					unit.command = COMMANDS[request.type.ordinal()];
					action_units.add(unit);
					it.remove();
					break;
				}
				break;
			}
		}
	}
	
	void checkView()
	{
		if (nonview_position.isEmpty())
		{
			return;
		}
		
		Set<Position> positions = new HashSet<>();
		Position basepoint = new Position(99, 99);
		
		for (Unit unit : last_state.units.keySet())
		{
			if (basepoint.distance(unit.position) >= 44)
			{
				continue;
			}
			if (positions.add(unit.position) == false)
			{
				continue;
			}
			int x = 99 - unit.position.getX();
			int y = 99 - unit.position.getY();
			for (int dy = -4; dy <= 4; dy++)
			{
				if (y + dy < 0 || y + dy > 45)
				{
					continue;
				}
				for (int dx = -4; dx <= 4; dx++)
				{
					if (x + dx < 0 || x + dx > 45)
					{
						continue;
					}
					if (Math.abs(dx) + Math.abs(dy) > 4)
					{
						continue;
					}
					viewed[y + dy][x + dx] = true;
				}
			}
		}
		
		outside_loop:
		for (Iterator<Position> it = nonview_position.iterator(); it.hasNext(); )
		{
			Position position = it.next();
			int x = position.getX();
			int y = position.getY();
			for (int dy = -2; dy <= 2; dy++)
			{
				for (int dx = -2; dx <= 2; dx++)
				{
					if (viewed[y + dy][x + dx] == false)
					{
						continue outside_loop;
					}
				}
			}
			it.remove();
		}
		
	}
	
	void compute()
	{
		if (last_state.turn == 300)
		{
			digger_manager.setLimit(100);
		}
		
		for (Unit unit : last_state.enemies.keySet())
		{
			if (enemies_count.containsKey(unit.position))
			{
				enemies_count.put(unit.position, enemies_count.get(unit.position) + 1);
			}
			else
			{
				enemies_count.put(unit.position, Integer.valueOf(1));
			}
		}
		
		
		checkView();
		
		update();
		
		getRequests();
		assignRequests();
		
		resource_searcher_manager.compute(action_units);
		digger_manager.compute(action_units);
		target_searcher_manager.compute(action_units);
		
		if (castle_worker != null && castle_worker.moveTo(castle.position))
		{
			action_units.add(castle_worker);
		}
		
		for (BattlerUnit unit : guardians)
		{
			unit.compute(action_units);
		}

		for (BattlerUnit unit : attackers)
		{
			unit.compute(action_units);
		}
		
		for (int i = 0; i < knights.length; i++)
		{
			if (knights[i].fully())
			{
				if (enemy_castle == null)
				{
					if (last_state.turn % 20 == 0 || knights[i].reached())
					{
						if (nonview_position.isEmpty())
						{
							knights[i].setTarget(new Position(99 - rand.nextInt(35), 99 - rand.nextInt(35)));
						}
						else
						{
							Position position = nonview_position.get(((i + 7) * 9) % nonview_position.size());
							knights[i].setTarget(new Position(99 - position.getY(), 99 - position.getX()));
						}
					}
				}
				else
				{
					if (enemy_castle.position.equals(knights[i].getTarget()) == false)
					{
						knights[i].setTarget(enemy_castle.position);
						//knights[i].maxattack();
					}
				}
				if (i == 2)
				{
					if (wall_kngiht == null || wall_kngiht.isDied())
					{
						wall_kngiht = knights[i];
						for (int j = i + 1; j < knights.length; j++)
						{
							knights[j - 1] = knights[j];
						}
						knights[knights.length - 1] = new Knights();
						knights[knights.length - 1].setTarget(castle.position);
						i--;
						continue;
					}
				}
			}
			knights[i].compute(action_units);
		}
		
	}
	
}
