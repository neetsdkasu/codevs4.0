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
	Position current = null;
	Unit unit = null;
	int distance = 0;
	
	public ResourceSearcherUnit(int id)
	{
		this.id = id;
		int tmp = (id >> 1) * 9;
		rotation = (id & 1) * 2 - 1;
		left = top = tmp + 4;
		right = bottom = 95 - tmp;
		length = ((right - left) + (bottom - top)) * 2;
	}
	
	public void reset(Position castle)
	{
		current = new Position(top, left);
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
		if (reach > 75)
		{
			return 0;
		}
		else if (reach > 50)
		{
			return 50;
		}
		else if (reach > 0)
		{
			return 100;
		}
		return -1;
	}
	
	public boolean compute()
	{
		if (unit == null || distance <= 0)
		{
			return false;
		}
		if (current.equals(unit.position))
		{
			int x = current.getX();
			int y = current.getY();
			if (y > top && y < bottom)
			{
				current = current.move((x == left) ? -rotation : rotation, 0);
			}
			else if (x > left && x < right)
			{
				current = current.move(0, (y == top) ? rotation : -rotation);
			}
			else if (x == left)
			{
				if (y == top)
				{
					current = (rotation > 0) ? current.move(0, 1) : current.move(1, 0);
				}
				else
				{
					current = (rotation > 0) ? current.move(-1, 0) : current.move(0, 1);
				}
			}
			else if (y == top)
			{
				current = (rotation > 0) ? current.move(1, 0) : current.move(0, -1);
			}
			else
			{
				current = (rotation > 0) ? current.move(0, -1) : current.move(-1, 0);
			}
			distance--;
		}
		return unit.moveTo(current);
	}
}
