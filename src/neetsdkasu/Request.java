package neetsdkasu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import neetsdkasu.*;

public abstract class Request implements Comparable<Request>
{ 
	public final int priority;
	public final int needtypes;
	
	public Request(int priority, int needtypes)
	{
		this.priority = priority;
		this.needtypes = needtypes;
	}
	
	public abstract void assign(Unit unit);
	
	public int compareTo(Request o)
	{
		return priority - o.priority;
	}
} 
