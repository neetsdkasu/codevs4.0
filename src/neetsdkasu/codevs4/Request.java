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

public abstract class Request implements Comparable<Request>
{
	public final Position position;
	public final boolean must_be;
	public final Type type;
	public final int priority;
	
	public Request(Position position, boolean must_be, Type type, int priority)
	{
		this.position = position;
		this.must_be = must_be;
		this.type = type;
		this.priority = priority;
	}
		
	abstract void assign(Unit unit);
	
	@Override
	public int compareTo(Request o)
	{
		return priority - o.priority;
	}
}
