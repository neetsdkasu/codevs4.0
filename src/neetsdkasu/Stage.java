package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class Stage
{
	int number = 0;
	int turn = 0;
	int resource_count = 0;
	int unit_count = 0;
	int enemy_count = 0;
	
	HashSet<Position> resources = new HashSet<>();
	HashMap<Unit, Unit> workers = new HashMap<>();
	ArrayList<Unit> performers = new ArrayList<>();
	
	SearcherManager searcher = new SearcherManager();
	
	Unit castle = null;
	
	public void nextStage(int number)
	{
		this.number = number;
		resources.clear();
		searcher.reset();
		castle = null;
	}
	
	public void nextTurn(int turn)
	{
		this.turn = turn;
		workers.clear();
		performers.clear();
	}
	
	public void addUnit(Unit unit)
	{
		switch (unit.type)
		{
		case WORKER:
			workers.put(unit, unit);
			break;
		case CASTLE:
			if (castle == null)
			{
				Position.setMirror(unit.position.getAbsoluteX() > 50 && unit.position.getAbsoluteY() > 50);
			}
			castle = unit;
			break;
		}
	}
	
	public void compute()
	{
		searcher.update(workers);
		
		int needs = Math.min(searcher.howManyNeedWorkers(), workers.size());
		
		searcher.assign(workers, needs);
		
		searcher.compute(performers);
	}
}

