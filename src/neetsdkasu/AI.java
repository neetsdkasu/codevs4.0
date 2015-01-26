package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import neetsdkasu.*;

public class AI
{
	public static final String AI_NAME = "Leonardone";
	
	public static void main(String[] args) throws java.lang.Exception
	{
		Scanner in = new Scanner(System.in);
		
		System.out.println(AI_NAME);
		
		Stage stage = new Stage();
		
		for (;;)
		{
			if (input(in, stage) == false)
			{
				break;
			}
			
			stage.compute();
			
			output(stage);
		}
		
	}
	

	
	public static void output(Stage stage)
	{
		System.out.println(stage.performers.size());
		for (Unit unit : stage.performers)
		{
			System.out.println(unit.id + " " + unit.getCommandChar());
		}
		System.out.flush();
	}
	
	public static boolean input(Scanner in, Stage stage)
	{
		if (in.hasNextLong() == false)
		{
			return false;
		}
		
		long time = in.nextLong();
		
		int stage_number = in.nextInt();
		
		if (stage.number != stage_number)
		{
			stage.nextStage(stage_number);
		}
		
		stage.nextTurn(in.nextInt());
		
		stage.resource_count = in.nextInt();
		
		stage.unit_count = in.nextInt();
		for (int i = 0; i < stage.unit_count; i++)
		{
			stage.addUnit(new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
		}
		
		stage.enemy_count = in.nextInt();
		for (int i = 0; i < stage.enemy_count; i++)
		{
			stage.addEnemy(new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
		}
		
		int resource_position_count = in.nextInt();
		for (int i = 0; i < resource_position_count; i++)
		{
			stage.addResourcePosition(new Position(in.nextInt(), in.nextInt(), true));
		}
		
		in.next(); // END
		
		return true;
	}
}



