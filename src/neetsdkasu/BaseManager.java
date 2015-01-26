package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class BaseManager
{
	static final int BASE_MAX_COUNT = 3;
	
	HashMap<Position, Integer> bases = new HashMap<>();
	
	int count = 0;
	
	public void nextTurn()
	{
		count = 0;
		bases.clear();
	}
	
	public void addBase(Unit unit)
	{
		Position position = unit.position;
		if (bases.containsKey(position))
		{
			bases.put(position, bases.get(position).intValue() + 1);
		}
		else
		{
			bases.put(position, Integer.valueOf(1));
			count++;
		}
	}
	
	public boolean needBasePosition(Position position)
	{
		if (bases.containsKey(position) == false)
		{
			bases.put(position, Integer.valueOf(1));
			count++;
			return true;
		}
		Integer c = bases.get(position);
		if (c < 5)
		{
			bases.put(position, c + 1);
			return true;
		}
		return false;
	}
	
	public int needBases()
	{
		return (count > BASE_MAX_COUNT) ? 0 : BASE_MAX_COUNT - count;
	}
}
