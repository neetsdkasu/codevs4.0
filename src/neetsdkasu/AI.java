package neetsdkasu;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;

public class AI
{
	public static final String AI_NAME = "Leonardone";
	
	public static void main(String[] args) throws java.lang.Exception
	{
		System.out.println(AI_NAME);
		
		for (;;)
		{
			Status status = new Status();
			input(status);
			compute(status);
			output(status);
		}
	}
	
	static boolean input(Status status) throws java.lang.Exception
	{
		Scanner in = new Scanner(System.in);
		
		status.time = in.nextLong();
		
		status.stage = in.nextInt();
		
		status.turn = in.nextInt();
		
		status.resouce = in.nextInt();
		
		status.my_unit_count = in.nextInt();
		for (int i = 0; i < status.my_unit_count; i++)
		{
			Unit unit = new Unit();
		
			unit.id = in.nextLong();
		
			unit.y = in.nextInt();
		
			unit.x = in.nextInt();
		
			unit.hp = in.nextInt();
		
			int type = in.nextInt();
			unit.type = Unit.TYPES[type];
			status.my_units.add(unit);
			status.unit_type_count[type]++;
		}
		
		status.enemy_count = in.nextInt();
		for (int i = 0; i < status.enemy_count; i++)
		{
			Unit unit = new Unit();
		
			unit.id = in.nextLong();
		
			unit.y = in.nextInt();
		
			unit.x = in.nextInt();
		
			unit.hp = in.nextInt();
		
			int type = in.nextInt();
			unit.type = Unit.TYPES[type];
			status.enemies.add(unit);
		}
		
		status.resource_count = in.nextInt();
		for (int i = 0; i < status.resource_count; i++)
		{
			Resource resouce = new Resource();
		
			resouce.y = in.nextInt();
		
			resouce.x = in.nextInt();
			status.resources.add(resouce);
		}
		
		in.nextLine();
		
		return true;
	}
	
	static void output(Status status) throws java.lang.Exception
	{
		System.out.println(status.command_unit_count);
		for (Unit unit : status.my_units)
		{
			if (unit.stay())
			{
				continue;
			}
			System.out.println(unit.id + " " + unit.getCommandChar());
		}
		System.out.flush();
	}
	
	static void compute(Status status)
	{
		for (Unit unit : status.my_units)
		{
			switch (unit.type)
			{
			case CASTLE:
			case VILLAGE:
				if (status.resouce >= 40 
					&& status.unit_type_count[Type.WORKER.ordinal()] < 100)
				{
					unit.command = Command.WORKER;
					status.resouce -= 40;
					status.command_unit_count++;
				}
				break;
			case BASE:
				if (status.resouce >= 60)
				{
					unit.command = Command.ASSASSIN;
					status.resouce -= 60;
					status.command_unit_count++;
				}
				else if (status.resouce >= 40)
				{
					unit.command = Command.FIGHTER;
					status.resouce -= 40;
					status.command_unit_count++;
				}
				else if (status.resouce >= 20)
				{
					unit.command = Command.KNIGHT;
					status.resouce -= 20;
					status.command_unit_count++;
				}
				break;
			case WORKER:
				if (status.resouce >= 500)
				{
					unit.command = Command.BASE;
					status.resouce -= 500;
					status.command_unit_count++;
					break;
				}
				else if (status.resouce >= 200
					&& status.unit_type_count[Type.VILLAGE.ordinal()] < 2)
				{
					unit.command = Command.VILLAGE;
					status.resouce -= 200;
					status.command_unit_count++;
					break;
				}
				break;
			case KNIGHT:
			case FIGHTER:
			case ASSASSIN:
				if (unit.y == 95)
				{
					unit.command = (unit.x > 80) ? Command.LEFT : Command.UP;
				}
				else if (unit.x == 80)
				{
					unit.command = (unit.y > 80) ? Command.UP : Command.RIGHT;
				}
				else if (unit.y == 80)
				{
					unit.command = (unit.x < 95) ? Command.RIGHT : Command.DOWN;
				}
				else if (unit.x == 95)
				{
					unit.command = (unit.y < 95) ? Command.DOWN : Command.LEFT;
				}
				else
				{
					unit.command = (unit.y < unit.x) ? Command.DOWN : Command.RIGHT;
				}
				status.command_unit_count++;
				break;
			}
		}
	}
	
}

enum Type
{
	WORKER, KNIGHT, FIGHTER, ASSASSIN, CASTLE, VILLAGE, BASE
}

enum Command
{
	WORKER, KNIGHT, FIGHTER, ASSASSIN, CASTLE, VILLAGE, BASE,
	UP, DOWN, LEFT, RIGHT, STAY
}

class Unit
{
	public static final Type[] TYPES = new Type[]{
		Type.WORKER, Type.KNIGHT, Type.FIGHTER, Type.ASSASSIN, Type.CASTLE, Type.VILLAGE, Type.BASE
	};

	public long id;
	public int y;
	public int x;
	public int hp;
	public Type type;
	public Command command = Command.STAY;
	
	public boolean stay()
	{
		switch (command)
		{
		case WORKER:
		case KNIGHT:
		case ASSASSIN:
		case VILLAGE:
		case BASE:
		case UP:
		case DOWN:
		case LEFT:
		case RIGHT:
			return false;
		default:
			return true;
		}
	}
	
	public char getCommandChar()
	{
		switch (command)
		{
		case WORKER:
		case KNIGHT:
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
			return '\0';
		}
	}
}

class Resource
{
	public int y;
	public int x;
	public Resource() {}
	public Resource(int y, int x)
	{
		this.y = y;
		this.x = x;
	}
	@Override
	public int hashCode()
	{
		return (y << 8) | x;
	}
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		Resource res = (Resource)o;
		return y == res.y && x == res.x;
	}
}

class Status
{
	public long time;
	public int stage;
	public int turn;
	public int resouce;
	public int my_unit_count;
	public int enemy_count;
	public int resource_count;
	public int command_unit_count;
	public int[] unit_type_count = new int[7];
	public ArrayList<Unit> my_units = new ArrayList<>();
	public ArrayList<Unit> enemies = new ArrayList<>();
	public HashSet<Resource> resources = new HashSet<>();
}
