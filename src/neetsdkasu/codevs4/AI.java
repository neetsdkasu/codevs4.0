package neetsdkasu.codevs4;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;


import neetsdkasu.codevs4.*;

public class AI
{
	public static void main(String[] args)
	{
		(new AI("Leonardone", System.in, System.out)).run();
	}
	
	
	String name;
	Scanner in;
	PrintStream out;
	Stage stage;
	
	public AI(String name, InputStream is, PrintStream out)
	{
		this.name = name;
		in = new Scanner(is);
		this.out = out;
		stage = new Stage();
	}
	
	public void run()
	{
		out.println(name);
		while (input())
		{
			output();
		}
	}
	
	boolean input()
	{
		if (in.hasNext() == false)
		{
			return false;
		}
		
		TurnState state = new TurnState();
		
		state.time = in.nextInt();
		
		state.stage = in.nextInt();
		
		state.turn = in.nextInt();
		
		state.resource_count = in.nextInt();
		
		state.unit_count = in.nextInt();
		
		for (int i = 0; i < state.unit_count; i++)
		{
			Unit unit = new Unit(
				in.nextInt(),
				new Position(in.nextInt(), in.nextInt(), true),
				in.nextInt(),
				in.nextInt()
			);
			state.units.put(unit, unit);
		}
		
		state.enemy_count = in.nextInt();
		
		for (int i = 0; i < state.enemy_count; i++)
		{
			Unit unit = new Unit(
				in.nextInt(),
				new Position(in.nextInt(), in.nextInt(), true),
				in.nextInt(),
				in.nextInt()
			);
			state.enemies.put(unit, unit);
		}
		
		state.resource_position_count = in.nextInt();
		
		for (int i = 0; i < state.resource_position_count; i++)
		{
			Position position = new Position(in.nextInt(), in.nextInt(), true);
			state.resource_positions.add(position);
		}
		
		in.next();
		
		stage.setTurnState(state);
		
		return true;
	}
	
	void output()
	{
		Set<? extends Unit> action_units = stage.getActionUnits();
		
		out.println(action_units.size());
		
		for (Unit unit : action_units)
		{
			out.println(unit.getAction());
		}
		
	}
}
