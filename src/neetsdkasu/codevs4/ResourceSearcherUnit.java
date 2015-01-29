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

public class ResourceSearcherUnit
{
	final int id;
	final int left, top, bottom, right, rotation, length;
	Position current = null, start = null;
	Unit unit = null;
	int distance = 0;
	
	public ResourceSearcherUnit(int id)
	{
		this.id = id;
		int tmp = (id >> 1) * 8;
		rotation = (id & 1) * 2 - 1;
		left = top = tmp + 4;
		right = bottom = 95 - tmp;
		length = ((right - left) + (bottom - top)) * 2;
	}
	
	public void reset(Position castle)
	{
		int diffX = left - castle.getX();
		int diffY = top - castle.getY();
		if (Math.abs(diffX) < Math.abs(diffY))
		{
			current = new Position(left, castle.getY());
		}
		else
		{
			current = new Position(castle.getX(), top);
		}
		unit = null;
		distance = length;
	}
	
	public Unit setUnit(Unit unit)
	{
		Unit temp = this.unit;
		this.unit = unit;
		return temp;
	}
	
	public Unit getUnit()
	{
		return unit;
	}
	
	public Position getCurrent()
	{
		return current;
	}
	
	public int getPriority()
	{
		if (distance < 0)
		{
			return -1;
		}
		int reach = (distance * 100) / length;
		if (reach < 25)
		{
			return 0;
		}
		else if (reach < 50)
		{
			return 50;
		}
		else
		{
			return 100;
		}
	}
	
	public boolean compute()
	{
		if (unit == null)
		{
			return false;
		}
		if (current.equals(unit.position))
		{
			int x = current.getX();
			int y = current.getY();
			if (y > top && y < bottom)
			{
				current = current.move(0, (x == left) ? rotation : -rotation);
			}
			else
			{
				current = current.move((y == top) ? rotation : -rotation, 0);
			}
			distance--;
		}
		unit.moveTo(current);
		return true;
	}
}
