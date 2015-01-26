package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public class VillageManager
{
	static final int VILLAGE_MAX_COUNT = 4;
	
	HashMap<Position, Integer> villages = new HashMap<>();
	
	int count = 0;
	
	public void nextTurn()
	{
		count = 0;
		villages.clear();
	}
	
	public void addVillage(Unit unit)
	{
		Position position = unit.position;
		if (villages.containsKey(position))
		{
			villages.put(position, villages.get(position).intValue() + 1);
		}
		else
		{
			villages.put(position, Integer.valueOf(1));
			count++;
		}
	}
	
	public boolean needVillagePosition(Position position)
	{
		if (villages.containsKey(position) == false)
		{
			villages.put(position, Integer.valueOf(1));
			count++;
			return true;
		}
		Integer c = villages.get(position);
		if (c < 2)
		{
			villages.put(position, c + 1);
			return true;
		}
		return false;
	}
	
	public int needVillages()
	{
		return (count > VILLAGE_MAX_COUNT) ? 0 : VILLAGE_MAX_COUNT - count;
	}
	
}
