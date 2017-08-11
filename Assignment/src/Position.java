
final public class Position 
{

	public enum DIR {UP, DOWN, LEFT, RIGHT};
	public int x, y;
	public DIR direction;
	
	public Position(){}
	
	public Position(int xCoord, int yCoord) 
	{
		x = xCoord;
		y = yCoord;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void move(DIR dir)
	{
		direction = dir;
		
		switch(direction)
		{
		case UP:
			this.y -= 1;
			break;
		case DOWN:
			this.y += 1;
			break;
		case LEFT:
			this.x -= 1;
			break;
		case RIGHT:
			this.x += 1;
			break;
		}	
	}

	public String toString() 
	{
		return  ""+x+","+y+"";
	}

	public boolean equals(Object o) 
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position Position = (Position) o;
		return x == Position.x && y == Position.y;
	}

	public int hashCode() 
	{
		int result = x;
		result = 31 * result + y;
		return result;
	}
	
}
