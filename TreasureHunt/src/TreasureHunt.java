/** Treasure Hunt
 * Found the treasure in a 8x8 grid 
 * If the treasure in an adjacent cell, the text "Big Fire" shall be displayed in the clicked cell.
 * If the treasure is 2 cells far from the clicked cell, the text "Small Fire" shall be displayed. 
 * 
 * @author Marco Bruti
 *
 */
public class TreasureHunt {
	GridFrame gf;
	private int[][] grid;
	private int treasure_i,treasure_j;
	private int n_tent=0;
	private boolean found=false;
	
	/** Constructor. Define the graphical frame and the related 8x8 grid */
	public TreasureHunt() {
		gf=new GridFrame(this);
		grid=new int[8][8];
	}
	
	/** reset all the grid cells to a fixed value
	 * 
	 * @param i	row
	 * @param j	column
	 * @param val	value to set
	 */
	public void setGrid(int i,int j, int val) {
		grid[i][j]=val;
	}
	
	/** Return the value of the row where the treasure is located.
	 * 
	 * @return	row 
	 */
	public int getTreasure_i() {
		return treasure_i;
	}
	
	/** Return the value of the column where the treasure is located.
	 * 
	 * @return	column 
	 */
	public int getTreasure_j() {
		return treasure_j;
	}
	
	/** Increments the number of attempts */
	public void incNtent() {
		n_tent++;
	}
	
	/** Position the treasure */
	public void positionTreasure() {
		n_tent=0;
		treasure_i=(int)(Math.random()*8);
		treasure_j=(int)(Math.random()*8);
		for(int i=0;i<8;i++) 
			for (int j=0;j<8;j++)
				if ((i==treasure_i) && (j==treasure_j)) 
					grid[i][j]=1;
				else 
					grid[i][j]=0;
	}
	
	/** Invoked when the treasure is found. */ 
	public void foundTreasure() {
		gf.createDialog(200,100,"Found in "+n_tent+" attempts!");
		found=true;
		gf.resetButtons();
		positionTreasure();
	}
	
	/** Print a text version of the grid on the console */
	public void printGrid() {
		System.out.print("  0 1 2 3 4 5 6 7\n");
		for(int i=0;i<8;i++) {
			System.out.printf("%1d ",i);
			for(int j=0;j<8;j++)
				if ((grid[i][j]==1) && found)
					System.out.print("$ ");
				else if (grid[i][j]==2)	
					System.out.print("* ");
				else  
					System.out.print("- ");
			System.out.print("\n");
		}
	}
	
	/** Init the game */
	public void start() {
		System.out.print("Treasure Hunt! (c)2021 Marco Bruti\n\n");
		printGrid();
		positionTreasure();
	}
	
	/** Entry point
	 * <p>A <code>TreasureHunt</code> object is created
	 * and it is initialized with the <code>start</code> method</p>
	 * 
	 *  @param args	not used
	 */
	public static void main(String[] args) {
		TreasureHunt th=new TreasureHunt();
		th.start();
	}
}
