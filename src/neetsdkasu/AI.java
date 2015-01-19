package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class AI
{
	public static final String AI_NAME = "Leonardone";
	
	public static void main(String[] args) throws java.lang.Exception
	{
		Scanner in = new Scanner(System.in);
		
		System.out.println(AI_NAME);
		
		Stage stage = new Stage();
		
		for (;;)
		{
			if (input(in, stage) == false)
			{
				break;
			}
			
			stage.compute();
			
			output(stage);
		}
		
	}
	

	
	public static void output(Stage stage)
	{
		System.out.println(stage.performers.size());
		for (Unit unit : stage.performers)
		{
			System.out.println(unit.id + " " + unit.getCommandChar());
		}
		System.out.flush();
	}
	
	public static boolean input(Scanner in, Stage stage)
	{
		if (in.hasNextLong() == false)
		{
			return false;
		}
		
		long time = in.nextLong();
		
		int stage_number = in.nextInt();
		
		if (stage.number != stage_number)
		{
			stage.nextStage(stage_number);
		}
		
		stage.nextTurn(in.nextInt());
		
		stage.resource_count = in.nextInt();
		
		stage.unit_count = in.nextInt();
		for (int i = 0; i < stage.unit_count; i++)
		{
			stage.addUnit(new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
		}
		
		stage.enemy_count = in.nextInt();
		for (int i = 0; i < stage.enemy_count; i++)
		{
			new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
		}
		
		int resource_position_count = in.nextInt();
		for (int i = 0; i < resource_position_count; i++)
		{
			stage.resources.add(new Position(in.nextInt(), in.nextInt()));
		}
		
		in.next(); // END
		
		return true;
	}
}

enum Type
{
	WORKER, KNIGHT, FIGHTER, ASSASSIN, CASTLE, VILLAGE, BASE
}

enum Command
{
	WORKER, KNIGHT, FIGHTER, ASSASSIN, CASTLE, VILLAGE, BASE,
	UP, DOWN, LEFT, RIGHT, STAY;
}

class Position
{
	public final int y, x;
	public Position(int y, int x)
	{
		this.y = y;
		this.x = x;
	}
	@Override public int hashCode()
	{
		return (y << 8) | x;
	}
	@Override public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		Position p = (Position)o;
		return (y == p.y) && (x == p.x);
	}
}

class Unit
{
	static final Type[] TYPES = new Type[]{
		Type.WORKER, Type.KNIGHT, Type.FIGHTER, Type.ASSASSIN, Type.CASTLE, Type.VILLAGE, Type.BASE
	};
	
	public int id, hp;
	public Position position;
	public Type type;
	public Command command = Command.STAY;
	public Unit()
	{
	}
	public Unit(int id, int y, int x, int hp, int type)
	{
		this.id = id;
		this.position = new Position(y, x);
		this.hp = hp;
		this.type = TYPES[type];
	}
	char getCommandChar()
	{
		switch (command)
		{
		case WORKER:
		case KNIGHT:
		case FIGHTER:
		case ASSASSIN:
		case VILLAGE:
		case BASE:
			return (char)('0' + command.ordinal());
		case UP:
		case DOWN:
		case LEFT:
		case RIGHT:
			return command.name().charAt(0);
		default:
			return '?';
		}
	}
	@Override public int hashCode()
	{
		return id;
	}
	@Override public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		Unit u = (Unit)o;
		return id == u.id;
	}
}

class Stage
{
	int number = 0;
	int turn = 0;
	int resource_count = 0;
	int unit_count = 0;
	int enemy_count = 0;
	
	HashSet<Position> resources = new HashSet<>();
	HashMap<Unit, Unit> workers = new HashMap<>();
	ArrayList<Unit> performers = new ArrayList<>();
	
	Searcher searcher = new Searcher();
	
	Unit castle = null;
	
	public void nextStage(int number)
	{
		this.number = number;
		resources.clear();
		searcher.init();
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
			castle = unit;
			break;
		}
	}
	
	public void compute()
	{
		searcher.update(workers);
		
		int needs = Math.min(searcher.howManyNeedWorkers(), workers.size());
		
		searcher.assign(workers, needs);
		
		searcher.run(performers);
	}
}


class Searcher
{
	static final int MAX_MEMBER_COUNT = 5;
	
	static int getStartY(int i)
	{
		return 4 + i * 9;
	}
	
	static int getEndEdge(int i)
	{
		return 95 - i * 9;
	}
	
	Unit[] member = new Unit[MAX_MEMBER_COUNT];
	Unit[] idle = new Unit[MAX_MEMBER_COUNT];
	Position[] current = new Position[MAX_MEMBER_COUNT];
	int idle_count = 0;
	boolean[] finished = new boolean[MAX_MEMBER_COUNT];
	int needs = -1;
	
	public Searcher()
	{
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			current[i] = new Position(getStartY(i), 4);
		}
	}
	
	public void init()
	{
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			current[i] = new Position(getStartY(i), 4);
		}
		Arrays.fill(member, null);
		Arrays.fill(finished, false);
		Arrays.fill(idle, null);
		idle_count = 0;
		needs = -1;
	}
	
	public void update(HashMap<Unit, Unit> units)
	{
		needs = -1;
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			Unit temp = member[i];
			if (temp == null)
			{
				continue;
			}
			if (units.containsKey(temp))
			{
				member[i] = units.remove(temp);
			}
			else
			{
				member[i] = null;
			}
		}
	}
	
	void check()
	{
		idle_count = 0;
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			idle[i] = null;
			if (finished[i] == false)
			{
				finished[i] = (current[i].y == getEndEdge(i)) && (current[i].x == 4);
			}
			if (finished[i] && member[i] != null)
			{
				idle[idle_count] = member[i];
				idle_count++;
				member[i] = null;
			}
		}
	}
	
	void calcNeedWorkers()
	{
		check();
		needs = 0;
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			if (finished[i] || (member[i] != null))
			{
				continue;
			}
			if (idle_count > 0)
			{
				idle_count--;
				member[i] = idle[idle_count];
				idle[idle_count] = null;
			}
			else
			{
				needs++;
			}
		}
	}
	
	public int howManyNeedWorkers()
	{
		if (needs < 0)
		{
			calcNeedWorkers();
		}
		return needs;
	}
	
	public void assign(HashMap<Unit, Unit> units, int count)
	{
		if (idle_count > 0)
		{
			for (Unit unit : idle)
			{
				units.put(unit, unit);
			}
			Arrays.fill(idle, null);
			idle_count = 0;
			return;
		}
		int i = 0;
		for (Iterator<Unit> it = units.keySet().iterator(); it.hasNext();)
		{
			Unit unit = units.get(it.next());
			if ((count == 0) || (i == MAX_MEMBER_COUNT))
			{
				break;
			}
			for (;i < MAX_MEMBER_COUNT; i++)
			{
				if (finished[i] || (member[i] != null))
				{
					continue;
				}
				member[i] = unit;
				needs--;
				count--;
				it.remove();
				break;
			}
		}
	}
	
	public void run(ArrayList<Unit> units)
	{
		for (int i = 0; i < MAX_MEMBER_COUNT; i++)
		{
			Unit unit = member[i];
			if (finished[i] || (unit == null))
			{
				continue;
			}
			Position temp = current[i];
			if (temp.equals(unit.position))
			{
				int edge = getEndEdge(i);
				if (temp.y == edge)
				{
					unit.command = Command.LEFT;
					current[i] = new Position(temp.y, temp.x - 1);
				}
				else if (temp.x == edge)
				{
					unit.command = Command.DOWN;
					current[i] = new Position(temp.y + 1, temp.x);
				}
				else
				{
					unit.command = Command.RIGHT;
					current[i] = new Position(temp.y, temp.x + 1);
				}
			}
			else
			{
				int diffY = temp.y - unit.position.y;
				int diffX = temp.x - unit.position.x;
				if (Math.abs(diffY) > Math.abs(diffX))
				{
					unit.command = (diffY < 0) ? Command.UP : Command.DOWN;
				}
				else
				{
					unit.command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
				}
			}
			units.add(unit);
		}
	}
}

