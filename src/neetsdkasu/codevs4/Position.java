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

public class Position
{
	static boolean mirror = false;
	
	public static boolean isMirror()
	{
		return mirror;
	}
	
	public static void setMirror(boolean mirror)
	{
		Position.mirror = mirror;
	}
	
	int x;
	int y;
	boolean calc_mirror;
	
	public Position(int y, int x, boolean calc_mirror)
	{
		this.y = y;
		this.x = x;
		this.calc_mirror = calc_mirror;
	}
	
	public Position(int y, int x)
	{
		this(y, x, false);
	}
	
	public int getX()
	{
		return (calc_mirror && isMirror()) ? 99 - x : x;
	}
	
	public int getY()
	{
		return (calc_mirror && isMirror()) ? 99 - y : y;
	}
	
	public int getRealX()
	{
		return (calc_mirror || !isMirror()) ? x : 99 - x;
	}
	
	public int getRealY()
	{
		return (calc_mirror || !isMirror()) ? y : 99 - y;
	}
	
	public Position move(int dx, int dy)
	{
		return new Position(getX() + dx, getY() + dy);
	}
	
	public int distance(Position other)
	{
		return Math.abs(other.getX() - getX()) + Math.abs(other.getY() - getY());
	}
	
	@Override
	public int hashCode()
	{
		return (getX() << 8) | getY();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass().equals(o.getClass()) == false) return false;
		Position p = (Position)o;
		return getY() == p.getY() && getX() == p.getX();
	}
}
