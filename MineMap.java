import java.awt.*;
import java.util.*;

public class MineMap
{
    private int rows;
    private int columns;
    private int mines;
    private int[] map;

    /**
     * Constructor for objects of class Board
     */
    public MineMap(int rows, int columns, int mines)
    {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;
        map = new int[rows * columns];
    }
    
    public void generate()
    {
        ArrayList<Integer> minePositions = new ArrayList<Integer>();
        ArrayList<Integer> r = new ArrayList<Integer>();
        for (int i = 0; i < (rows * columns); i++) {
            r.add(i);
        }
        Collections.shuffle(r);
        minePositions.addAll(r.subList(0, mines));
        
        for (Integer i : minePositions) {
            map[i] = -1;
        }
        
        for (int i = 0; i < (rows * columns); i++) {
            if (map[i] == -1) {
                continue;
            }
            map[i] = countSurroundingMines(i);
        }
    }
    
    private int countSurroundingMines(int position)
    {
    	int count = 0;
        for (int i : getNeighbors(position)) {
            if (map[i] == -1) {
                count++;
            }
        } 
        return count;
    }
    
    public ArrayList<Integer> getNeighbors(int position)
    {
    	ArrayList<Integer> neighborIndexes = new ArrayList<Integer>();
    	
    	int x = position % columns;
        int y = position / columns;
        
        ArrayList<Point> neighbors = new ArrayList<Point>();
        neighbors.add(new Point(x - 1, y - 1));
        neighbors.add(new Point(x, y - 1));
        neighbors.add(new Point(x + 1, y - 1));
        neighbors.add(new Point(x + 1, y));
        neighbors.add(new Point(x + 1, y + 1));
        neighbors.add(new Point(x, y + 1));
        neighbors.add(new Point(x - 1, y + 1));
        neighbors.add(new Point(x - 1, y));
        
        for (Point p : neighbors) {
            if ((p.x < 0) || (p.x > columns - 1) ||
                (p.y < 0) || (p.y > rows - 1)) {
                continue;
            }
            neighborIndexes.add(p.x + p.y * columns);
        }
        return neighborIndexes;
    }
    
    public int getValueAt(int position)
    {
        return map[position];
    }

	public int getColumns() {
		return columns;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getMines() {
		return mines;
	}
}
