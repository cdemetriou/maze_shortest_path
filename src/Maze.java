
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;


public class Maze extends JFrame 
{

	//Size of the provided map
	public final static int SIZE = 10;
	//Enumeration of cell types available
	public enum CELL {WALL, EMPTY, TELEPORT, LAND};
	
	private CELL typeofcell;
	private static MazePanel mazepanel;
	public static HashMap<Position,CELL> mazeMap;
	
	
	private Maze() throws IOException
	{
		
		setTitle("Maze");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		setSize(dim.width/4, dim.height/4);
		setLocation(new Point(dim.width/4, dim.height/4));
		Container contentPane = this.getContentPane();
		
		mazeMap = new HashMap<Position,CELL>();
		
		//Initialise map with empty cells
		CELL cell = CELL.EMPTY;
		for (int row = 0; row < SIZE; row++) 
		{
		    for (int col = 0; col < SIZE; col++) 
		    {		    	   
	    	   Position p = new Position(row,col);
	    	   mazeMap.put(p, cell);
		    }
		}
		
		//Read csv file and populate maze map with positions and cell values
	    try
	    {
	    	int x = 0;
	    	typeofcell = null; 
	    	String line =  null;
	    	
		    BufferedReader br = new BufferedReader(new FileReader("map1.csv"));
			
		    while ((line = br.readLine()) != null)
		    {
				String str[] = new String[SIZE-1];
				str = line.split(",");
		
				for (int y = 0; y < str.length; y++)
				{
					
					if (str[y].contains("W")) typeofcell = CELL.WALL;
					else if (str[y].contains("T")) typeofcell = CELL.TELEPORT;
					else if (str[y].contains("L")) typeofcell = CELL.LAND;
					else  typeofcell = CELL.EMPTY;

					//Populate hash map
					Position pos = new Position(x,y);
					mazeMap.put( pos, typeofcell);					
		        }
				x++;
			}		
			br.close();
	    } 
	    catch (IOException e) 
	    {
	    	System.out.println("Given file not found.");
	    	System.exit(0);
	    }
	    
	    //Create the maze panel with grid layout
		mazepanel = new MazePanel(mazeMap);
		mazepanel.setLayout(new GridLayout(SIZE, SIZE));
		contentPane.add(mazepanel, BorderLayout.CENTER);
	}

	public static void main(String[] args) throws Exception 
	{
		JFrame frm = new Maze();
	    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frm.setVisible(true); 
	    
	    Path path = new Path();
	    if (path.foundDest)
	    {
	    	mazepanel.drawPath();
	    }
	}	
	
	
	private class MazePanel extends JPanel implements ActionListener
	{   

		//Button array to keep track of each button location
		public JButton[][] buttonGrid = new JButton[SIZE][SIZE];
		private Border emptyBorder = BorderFactory.createEmptyBorder();

		
		public MazePanel(Map<Position, CELL> mazeMap)
		{
			setBackground(Color.white);
			
			//Populate the panel
			for (int i = 0; i < SIZE; i++) 
			{
				for (int j = 0; j < SIZE; j++) 
				{
					Position p = new Position(i,j);
					CELL s = mazeMap.get(p); 
					
					//Create button and store it
					buttonGrid[i][j]= makeButton(p, s);
				}
			}  	
		}
		
		//Factory method for making buttons
		private JButton makeButton(Position p, CELL typeofcell) 
		{
			JButton button = new JButton();
			button.setBorder(emptyBorder);
			
			//Set button color based on cell value
			switch(typeofcell)
			{
				case WALL:
					button.setBackground(Color.BLACK);
					break;
				case EMPTY:
					button.setBackground(Color.WHITE);
					break;
				case TELEPORT:
					button.setBackground(Color.RED);
					break;
				case LAND:
					button.setBackground(Color.GREEN);
					break;
			}
			
			button.addActionListener(this);
	        this.add(button);
	        return button;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			for (int row = 0; row < SIZE; row++) 
			{
			    for (int col = 0; col < SIZE; col++) 
			    {
			       if (buttonGrid[row][col] == e.getSource())
			       {
			    	   //Get position and cell type of clicked button
			    	   Position p = new Position(row,col);
			    	   CELL cellType = mazeMap.get(p);

			    	   //Transfer TELEPORT - Reset previous, set new and update map
			    	   if ((e.getModifiers()&InputEvent.SHIFT_MASK) != 0 
			    			   && cellType != CELL.LAND)
			    	   {
			    		   cellType = CELL.TELEPORT;
			    		   
			    		   //Find previous teleport and set it to white cell
			    		   resetPos(cellType);
			    		   
			    		   mazeMap.put(p, cellType);
			    		   buttonGrid[row][col].setBackground(Color.RED);
			    	   }
			    	   //Transfer LAND - Reset previous, set new and update map
			    	   else if ((e.getModifiers()&InputEvent.ALT_MASK) != 0 
			    			   	&& cellType != CELL.TELEPORT)
			    	   {
			    		   cellType = CELL.LAND;
			    		   
			    		   //Find previous land and set it to white cell
			    		   resetPos(cellType);
			    		   
			    		   mazeMap.put(p, cellType);
			    		   buttonGrid[row][col].setBackground(Color.GREEN);
			    	   }
			    	   //Change between wall and empty path and update map
			    	   switch(cellType)
			    	   {
	    	   			case WALL:
			    	   		mazeMap.put(p, CELL.EMPTY);
							buttonGrid[row][col].setBackground(Color.WHITE);
							break;
						case EMPTY:
							mazeMap.put(p, CELL.WALL);
							buttonGrid[row][col].setBackground(Color.BLACK);
							break;
			    	   }
		    	   }
			    }
			}
			
			//Find paths again when the a cell is changed
			Path path = new Path();
			if (path.foundDest)
			{
		    	mazepanel.drawPath();
		    }
			else
			{
				//Remove path numbering 
				for (int row = 0; row < SIZE; row++) 
				{
				    for (int col = 0; col < SIZE; col++) 
				    {
				    	buttonGrid[row][col].setText("");
				    }
				}
			}
			
		}
		
		//Method finding the previous teleporter or landing 
		//and reseting it to empty 
		private void resetPos(CELL value)
		{
			Position key = new Position();
			CELL val = null;
			CELL whiteCell = CELL.EMPTY;
			
			for (Map.Entry<Position, CELL> e : mazeMap.entrySet()) 
			{
			    val = e.getValue();
			    if (val == value)
			    {
			    	key = e.getKey();
			    }    
			}
  		   	int prevX = key.getX();
  		   	int prevY = key.getY();
  		   	buttonGrid[prevX][prevY].setBackground(Color.WHITE);
  		   	mazeMap.put(key, whiteCell);
		}	
		
		//Method drawing the shortest path
		private void drawPath()
		{
			int count = 1;
			
			for (int row = 0; row < SIZE; row++) 
			{
			    for (int col = 0; col < SIZE; col++) 
			    {
			    	buttonGrid[row][col].setText("");
			    }
			}
			for (Position p : Path.shortestPath)
			{
				int x = p.getX();
				int y = p.getY();
				
				for (int row = 0; row < SIZE; row++) 
				{
				    for (int col = 0; col < SIZE; col++) 
				    {
				    	if (row == x && col == y)
				    	{				    		
				    		buttonGrid[row][col].setText(String.valueOf(count));
				    		buttonGrid[row][col].setForeground(Color.BLUE);

				    		count++;
				    	}				    	
				    }
				}
			}
		}
	}
}


