import java.awt.*;
import java.awt.event.*;

/** This frame contains a menu bar and 8x8 buttons to implement the clickable grid
 * 
 * @author Marco Bruti 
 *
 */
public class GridFrame extends Frame {
	Button[][] btns;
	MenuBar menuBar;
	Menu menuActions,menuHelp;
	MenuItem menuItemRestart,menuItemQuit,menuItemAbout;
	TreasureHunt th;
	
	/** Create a dialog box with an ok button
	 * 
	 * @param width	width in pixel
	 * @param height	height in pixel
	 * @param text	String to display
	 */
	public void createDialog(int width, int height, String text) {
		Dialog d=new Dialog(this,"Tresure Hunt",true);
		d.setLayout(new FlowLayout());
		Label l=new Label(text);
		Button ok=new Button("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});
		d.add(l);
		d.add(ok);
		d.setSize(width,height);
		d.setVisible(true);
	}
	
	/** Reset the labels of the button to a question mark */
	public void resetButtons() {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				btns[i][j].setLabel("?");
			}
		}	
	}
	
	/** Private embedded class to implement a listener for the menu bar*/
	private class MenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==menuItemQuit) {
				dispose();
				System.exit(0);
			} else if (e.getSource()==menuItemRestart) {
				resetButtons();
				th.positionTreasure();
			} else if (e.getSource()==menuItemAbout) {
				createDialog(300,100,"Click on cells to search for the treasure.");
			}
		}
	}
	
	/** Private embedded class to implement a listener for the grid buttons*/
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<8;i++) { 
				for(int j=0;j<8;j++) {
					if (e.getSource()==btns[i][j]) {
						if (!btns[i][j].getLabel().equals("?")) return;
						th.incNtent();
						if ((i==th.getTreasure_i()) && (j==th.getTreasure_j())) {
							btns[i][j].setLabel("$$$");
							th.foundTreasure();
							th.printGrid();
							return;
						} else	if ((Math.abs(i-th.getTreasure_i())<=1) && (Math.abs(j-th.getTreasure_j())<=1))
							btns[i][j].setLabel("Big Fire!");
						else if ((Math.abs(i-th.getTreasure_i())<=2) && (Math.abs(j-th.getTreasure_j())<=2))
							btns[i][j].setLabel("Small Fire!");
						else
							btns[i][j].setLabel("Splash!");
						th.setGrid(i,j,2);
						th.printGrid();
					}
				}
			}
		}
	}
	
	/** Constructor for the frame
	 * 
	 * @param th	hook to the main class to send notification about input events
	 */
	public GridFrame(TreasureHunt th) { 
		btns=new Button[8][8];
		this.setLayout(null);
		this.setTitle("Treasure Hunt (c)2021 Marco Bruti");
		menuBar = new MenuBar();
		menuActions=new Menu("Actions");
		menuHelp=new Menu("Help");		
		menuItemRestart=new MenuItem("Restart");
		menuItemQuit=new MenuItem("Quit");
		menuItemAbout=new MenuItem("About");
		menuActions.add(menuItemRestart);
		menuActions.add(menuItemQuit);
		menuHelp.add(menuItemAbout);
		menuBar.add(menuActions);
		menuBar.setHelpMenu(menuHelp);
		menuItemQuit.addActionListener(new MenuListener());
		menuItemRestart.addActionListener(new MenuListener());
		menuItemAbout.addActionListener(new MenuListener());		
		setMenuBar(menuBar);
		setVisible(true);
		int x_insets=getInsets().left+getInsets().right;
		int y_insets=getInsets().top+getInsets().bottom;
		Rectangle r = new Rectangle(0,0,520+x_insets,520+y_insets);
	    setBounds(r);
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				Button b=new Button();
				b.setLabel("?");
				b.setBounds(j*64+getInsets().left,i*64+getInsets().top,64,64);
				b.addActionListener(new ButtonListener());
				add(b);
				btns[i][j]=b;
			}
		}
		addWindowListener(new WindowAdapter() {
			@Override  
			public void windowClosing(WindowEvent e) {			
				System.exit(0);
			};
		});
		this.th=th;
	}
}
