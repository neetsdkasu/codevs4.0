package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class Unit
{
	static final Type[] TYPES = new Type[]{
		Type.WORKER, Type.KNIGHT, Type.FIGHTER, Type.ASSASSIN, Type.CASTLE, Type.VILLAGE, Type.BASE
	};
	
	public int id, hp;
	public Position position;
	public Type type;
	public Command command = Command.STAY;
	public boolean assigned = false;
	
	public Unit()
	{
	}
	
	public Unit(int id, int y, int x, int hp, int type)
	{
		this.id = id;
		this.position = new Position(y, x, true);
		this.hp = hp;
		this.type = TYPES[type];
	}
	
	public char getCommandChar()
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
			return '?';
		}
	}
	
	public void moveTo(Position to)
	{
		int diffX = to.getX() - position.getX();
		int diffY = to.getY() - position.getY();
		if (Math.abs(diffX) < Math.abs(diffY))
		{
			command = (diffY < 0) ? Command.UP : Command.DOWN;
		}
		else
		{
			command = (diffX < 0) ? Command.LEFT : Command.RIGHT;
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

