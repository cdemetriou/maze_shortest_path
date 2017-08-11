
import java.util.*;


public class Path 
{

	private Position.DIR dir;
    public boolean foundDest = false;
	
	//Keeps the distance from start to finish
	private Map<Position, Integer> distance = new HashMap<Position, Integer>();
	//Stores the nodes that are to be visited
	private Queue<Position> posQueue;
	//Map containing each node with its child
	private Map<Position,Position> prevElement;
	//Positions to the shortest path 
	public static Queue<Position> shortestPath;
    private static Queue<Position> tempPos;

    //Method reversing the order of the elements in the given queue
    private void reverse(Queue q)
    {
    	Collections.reverse((List<Position>) q);
    }

    //Goes back from the maze's exit to the start to find the shortest path
    private void backtrace()
	{
		shortestPath = new LinkedList<Position>();
		Position start = new Position(0,0);
		Position end   = new Position (Maze.SIZE -1, Maze.SIZE - 1);
		shortestPath.add(end);

        tempPos = new LinkedList<Position>();
        tempPos.add(end);

        //Iterates until the start is found
		while (!shortestPath.contains(start))
		{
			//Adds to the queue the parent of top element of queue
            Position child = new Position();
            child = tempPos.poll();
           
            Position parent = new Position();
            parent = prevElement.get(child);
            
            shortestPath.add(parent);
            tempPos.add(parent);
		}
        reverse(shortestPath);
	}

    //Method called for the special case of using the teleport-land route
    private void handleLandLocation(Position teleportPosition)
    {
        Position landPosition = new Position();
        boolean foundLand = false;
        
        //Find the landing cell
        for (Map.Entry<Position, Maze.CELL> e : Maze.mazeMap.entrySet())
        {
            Maze.CELL val = e.getValue();
            if (val == Maze.CELL.LAND)
            {
                landPosition = e.getKey();
                foundLand = true;
            }
        }

        if(foundLand)
        {
            //Handling land position
            int currLandPos;
            if(distance.containsKey(landPosition))
            {
                currLandPos = distance.get(landPosition);
                int dist = distance.get(teleportPosition) + 1;
                if(dist < currLandPos)
                {
                    distance.put(landPosition, dist);
                    prevElement.put(landPosition, teleportPosition);
                    posQueue.add(landPosition);
                }
            }
            else
            {
                int dist = distance.get(teleportPosition) + 1;
                distance.put(landPosition, dist);
                prevElement.put(landPosition, teleportPosition);
                posQueue.add(landPosition);
            }
        }
    }

    //Method finding all paths by implementing breadth-first search
	public Path()
	{
		try
		{
			prevElement = new HashMap<Position, Position>();
			posQueue = new LinkedList<Position>();
			Position teleport = new Position();
					
			//Maze start position
			Position start = new Position(0,0);
			int dist = 0;
			
			//Maze exit position
            Position destination = new Position(Maze.SIZE-1, Maze.SIZE-1);
            
			distance.put(start, dist);
			posQueue.add(start);

			//Iterate until there are no more neighbouring nodes
			while (!posQueue.isEmpty())
			{
				Position pos = posQueue.poll();
				
				//Check if you found the end position
				if(pos.equals(destination))
                {
                    //Found destination, setting found value to true
                    foundDest = true;
                }

				//Check for neighbours that are visitable, add to queue
				for (int i = 0; i < 4; i++)
				{
					switch (i)
					{
						case (0):
							dir = Position.DIR.UP;
                            break;
                        case (1):
							dir = Position.DIR.DOWN;
                            break;
                        case (2):
							dir = Position.DIR.LEFT;
                            break;
                        case (3):
							dir = Position.DIR.RIGHT;
                            break;
                    }
					Position newPos = new Position(pos.getX(),pos.getY());
					newPos.move(dir);

					//Check if its not a valid cell location, move to the next position
					if(newPos.getY() > Maze.SIZE-1 || newPos.getY() < 0 || newPos.getX() > Maze.SIZE-1 || newPos.getX() < 0)
					{
						continue;
					}
					
					Maze.CELL cell = Maze.mazeMap.get(newPos);
					boolean visited = distance.containsKey(newPos);
					
					//Enter if the cell is valid to visit
					if (cell == Maze.CELL.EMPTY || cell == Maze.CELL.LAND)
                    {
						//Enter if its the first time the position is checked
                        if (!visited)
                        {
                            dist = distance.get(pos) + 1;
                            distance.put(newPos, dist);
                            prevElement.put(newPos, pos);
                            posQueue.add(newPos);
                        }
                        else
                        {
                            int lastDist = distance.get(newPos);
                            int newDist = distance.get(pos) + 1;
                            
                            //Visited but need to recalculate path
                            if (lastDist > newDist)
                            {
                                distance.remove(newPos);
                                distance.put(newPos, newDist);
                                prevElement.put(newPos, pos);
                                posQueue.add(newPos);
                            }
                        }
                    }
					//Enter if the cell is the teleport cell
                    else if (cell == Maze.CELL.TELEPORT)
                    {
                        teleport = newPos;
                        if (!visited)
                        {
                            dist = distance.get(pos) + 1;
                            distance.put(newPos, dist);
                            prevElement.put(newPos, pos);
                            posQueue.add(newPos);
                        }
                        else
                        {
                            int lastDist = distance.get(newPos);
                            int newDist = distance.get(pos) + 1;
                            
                            //Visited but need to recalculate path
                            if (lastDist > newDist)
                            {
                                distance.remove(newPos);
                                distance.put(newPos, newDist);
                                prevElement.put(newPos, pos);
                                posQueue.add(newPos);
                            }
                        }
                        //Handle the case of going through the teleport-land path
                        handleLandLocation(teleport);
                    }
				}
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		//If the end is reached find the shortest path
        if(foundDest)
        {
            backtrace();
        }
        else
            System.out.println("This maze is a DEADEND, no route from start to end found.");
	}
}

