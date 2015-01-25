package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class Position
{
	static boolean mirror = false;
	
	public static void setMirror(boolean mirror)
	{
		Position.mirror = mirror;
	}
	
	public static boolean isMirror()
	{
		return Position.mirror;
	}
	
	private final int y, x;
	private boolean absolute;
	
	public Position(int y, int x, boolean absolute)
	{
		this.y = y;
		this.x = x;
		this.absolute = absolute;
	}
	
	public Position(int y, int x)
	{
		this(y, x, false);
	}
	
	public Position(Position p, int dy, int dx)
	{
		this(p.y + dy, p.x + dx, p.absolute);
	}
	
	public int getX()
	{
		return (absolute && Position.isMirror()) ? 99 - x : x;
	}
	
	public int getY()
	{
		return (absolute && Position.isMirror()) ? 99 - y : y;
	}
	
	public int getAbsoluteX()
	{
		return (Position.isMirror() && !absolute) ? 99 - x : x;
	}
	
	public int getAbsoluteY()
	{
		return (Position.isMirror() && !absolute) ? 99 - y : y;
	}
	
	
	@Override public int hashCode()
	{
		return (getY() << 8) | getX();
	}
	@Override public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		Position p = (Position)o;
		return (getY() == p.getY()) && (getX() == p.getX());
	}
}