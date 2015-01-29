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

public class TurnState
{
	public int time = 0;
	public int stage = 0;
	public int turn = 0;
	public int resource_count = 0;
	public int unit_count = 0;
	public HashMap<Unit, Unit> units = new HashMap<>();
	public int enemy_count = 0;
	public HashMap<Unit, Unit> enemies = new HashMap<>();
	public int resource_position_count = 0;
	public HashMap<Position, Position> resource_positions = new HashMap<>();
}
