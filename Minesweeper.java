import java.util.*;

public class Minesweeper {
	private Difficulty difficulty;
	private BoxState[] boxStates;
	private MineMap map;
	private Map<Integer, BoxState> lastChanges;
	private boolean gameOver;
	private boolean won;
	
	public Minesweeper()
	{
		lastChanges = new HashMap<Integer, BoxState>();
		changeDifficulty(Difficulty.EASY);
	}
	
	public void changeDifficulty(Difficulty difficulty)
	{
		this.difficulty = difficulty;
		reset();
	}

	public void reset()
	{
		gameOver = false;
		won = false;
		
		switch (difficulty) {
		case EASY:
			map = new MineMap(10, 10, 6);
			break;
		case MEDIUM:
			map = new MineMap(15, 15, 60);
			break;
		case HARD:
			map = new MineMap(20, 20, 100);
			break;
		default:
			break;
		}
		
		map.generate();
		
		lastChanges.clear();
		boxStates = new BoxState[map.getRows() * map.getColumns()];
		
		for (int i = 0; i < boxStates.length; i++) {
			changeState(i, BoxState.UNEXPLORED);
		}
	}
	
	public void makeMove(int position)
	{
		if (boxStates[position] == BoxState.FLAGGED) {
            return;
        }
		
        int v = map.getValueAt(position);
        
        if (v == 0) {
            revealArea(position);
        }
        else if (v == -1) {
        	changeState(position, BoxState.MINE);
        	gameOver = true;
        }
        else {
        	changeState(position, BoxState.SAFE);
        }
        
        won = checkForWin();
	}
	
	public void toggleFlag(int position)
	{
		if (isFlagged(position)) {
			changeState(position, BoxState.UNEXPLORED);
		}
		else if (boxStates[position] == BoxState.UNEXPLORED) {
			changeState(position, BoxState.FLAGGED);
		}
	}
	
	public Map<Integer, BoxState> getLastChanges()
	{
		Map<Integer, BoxState> last = new HashMap<Integer, BoxState>(lastChanges);
		lastChanges.clear();
		return last;
	}
	
	public int getMapValue(int position)
	{
		return map.getValueAt(position);
	}
	
	public int getMapRows()
	{
		return map.getRows();
	}
	
	public int getMapColumns()
	{
		return map.getColumns();
	}
	
	public boolean isGameOver()
	{
		return gameOver;
	}
	
	public boolean isWon()
	{
		return won;
	}
	
	public int minesLeft()
	{
		int flagCount = 0;
		for (int i = 0; i < boxStates.length; i++) {
			if (boxStates[i] == BoxState.FLAGGED) {
				flagCount++;
			}
		}
		int minesLeftI = map.getMines() - flagCount;
		return (minesLeftI > 0) ? minesLeftI : 0;
	}
	
	private void revealArea(int position)
	{
        if (isFlagged(position)) {
            return;
        }
        
        changeState(position, BoxState.SAFE);
        
        for (int i : map.getNeighbors(position)) {
            int v = map.getValueAt(i);
            boolean recursivelyReveal = v == 0 && boxStates[i] == BoxState.UNEXPLORED;
            if (recursivelyReveal) {
                revealArea(i);
            }
            else {                
                if (isFlagged(i)) {
                    continue;
                }
                changeState(i, BoxState.SAFE);
            }
        }
	}
	
	private void changeState(int position, BoxState state)
	{
		boxStates[position] = state;
		lastChanges.put(position, state);
	}
	
	private boolean checkForWin()
	{
		for (int i = 0; i < boxStates.length; i++) {
			if (map.getValueAt(i) != -1) {
				if (boxStates[i] == BoxState.UNEXPLORED) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isFlagged(int position)
	{
		return boxStates[position] == BoxState.FLAGGED;
	}
}