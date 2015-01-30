package neetsdkasu.codevs4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.codevs4.*;

public class Unit
{
	static final Type[] TYPES = new Type[]{
		Type.WORKER, Type.KNIGHT, Type.FIGHTER, Type.ASSASSIN, Type.CASTLE, Type.VILLAGE, Type.BASE
	};
	
	public final int id;
	public final int hp;
	public final Position position;
	public final Type type;
	public Command command = Command.STAY;
	
	public Unit(int id, Position position, int hp, int type_number)
	{
		this.id = id;
		this.hp = hp;
		this.position = position;
		this.type = TYPES[type_number];
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
			return Position.isMirror() ? 'D' : 'U';
		case DOWN:
			return Position.isMirror() ? 'U' : 'D';
		case LEFT:
			return Position.isMirror() ? 'R' : 'L';
		case RIGHT:
			return Position.isMirror() ? 'L' : 'R';
		default:
			return 'X';
		}
	}
	
	public String getAction()
	{
		return id + " " + getCommandChar();
	}
	
	public boolean moveTo(Position to)
	{
		int diffX = to.getX() - position.getX();
		int diffY = to.getY() - position.getY();
		if (diffY == 0 && diffX == 0)
		{
			return false;
		}
		else if (diffY == 0)
		{
			command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
		}
		else if (diffX == 0)
		{
			command = (diffY < 0) ? Command.UP : Command.DOWN;
		}
		else if (Math.abs(diffY) > Math.abs(diffX))
		{
			command = (diffY < 0) ? Command.UP : Command.DOWN;
		}
		else
		{
			command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
		}
		return true;
	}
	
	public boolean moveTo2(Position to)
	{
		int diffX = to.getX() - position.getX();
		int diffY = to.getY() - position.getY();
		if (diffY == 0 && diffX == 0)
		{
			return false;
		}
		else if (diffX != 0)
		{
			command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
		}
		else if (diffY != 0)
		{
			command = (diffY < 0) ? Command.UP : Command.DOWN;
		}
		return true;
	}

	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		return id == ((Unit)o).id;
	}
}
