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
	
	Set<Unit> action_units = new HashSet<>();
	
	ResourceSearcherManager resource_searcher_manager = new ResourceSearcherManager();
	
	Unit castle = null;
	
	Set<Position> worker_maker_position = new HashSet<>();
	Set<Position> battler_maker_position = new HashSet<>();
	List<Unit> worker_makers = new ArrayList<>();
	List<Unit> battler_makers = new ArrayList<>();
	
	List<Request> requests = new ArrayList<>();

	public void setTurnState(TurnState state)
	{
		before_state = last_state;
		last_state = state;
		
		action_units.clear();
		worker_makers.clear();
		battler_makers.clear();
		worker_maker_position.clear();
		battler_maker_position.clear();
		requests.clear();
		
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
		before_state = null;
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
		resource_searcher_manager.reset(castle.position);
	}
	
	void update()
	{
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
		
		
		resource_searcher_manager.update(last_state.units);
		
	}
	
	void getRequests()
	{
		resource_searcher_manager.getRequests(requests, worker_maker_position);
		
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
			case BASE:
				break;
			}
		}
	}
	
	void compute()
	{
		
		update();
		
		getRequests();
		assignRequests();
		
		resource_searcher_manager.compute(action_units);
	}
	
}
