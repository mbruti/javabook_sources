import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class JumpingJack extends JFrame { 
	JLabel scoreLabel,creditsLabel;
	JButton jumpButton,startButton;
	JJCanvas jjCanvas;
	int score, level, lives,hi;
	public Jack myJack;
	GamePhase gamePhase=GamePhase.Over;
	public Bar myBars[];
	public JumpingJack() {
		hi=0;
		score=0;
		lives=5;
		level=1;
		myJack=new Jack();
		myJack.jackFrame=Jack.Frame.Quiet;
		myJack.jackPhase=Jack.Phase.Quiet;
		myJack.setXY(0,144);
		myJack.setNPlatform(0);
		createBars();
		setLayout(new BorderLayout());
		scoreLabel=new JLabel("Level=01   Score=00000   Lives=05   HI=00000");
		creditsLabel=new JLabel("JumpingJack (c)2021 Marco Bruti");
		jumpButton= new JButton("Jump!");
		startButton= new JButton("Start!");
	    add(scoreLabel, BorderLayout.PAGE_START);
		add(creditsLabel, BorderLayout.PAGE_END);
		add(jumpButton,BorderLayout.LINE_START);
		jumpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gamePhase==GamePhase.Play) {
					if (myJack.jackPhase==Jack.Phase.Quiet) {
						myJack.jackPhase=Jack.Phase.Jumping;
						myJack.setJumpStep(0);
						myJack.setJumpDir(-1);
						myJack.jackFrame=Jack.Frame.Jump1;
					}
				}
			}
			
		});
		add(startButton,BorderLayout.LINE_END);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				score=0;
				lives=5;
				level=1;
				myJack.jackPhase=Jack.Phase.Quiet;
				myJack.setXY(0,144);
				myJack.setNPlatform(0);
				createBars();
				gamePhase=GamePhase.Play; 
			}
			
		});
		jjCanvas = new JJCanvas(this);
	    jjCanvas.setPreferredSize(new Dimension(320,320));
		add(jjCanvas,BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			@Override  
			public void windowClosing(WindowEvent e) {			
				System.exit(0);
			};
		});
		pack();	
		setVisible(true);
	}
	
	public void createBars() {
		myBars=null;
		myBars=new Bar[5];
		for(int i=0;i<5;i++) { 
			myBars[i]=new Bar();
			if (i==0) {
				myBars[0].setXY(0, 176);
				myBars[i].setSpeed(0);
			} else {
				myBars[i].setXY(i*72, (int)(Math.random()*316));
				if ((i % 2)==0) 
					myBars[i].setSpeed(-(int)(Math.random()*3+1));
				else
					myBars[i].setSpeed((int)(Math.random()*3+1));	
			}
		}
	}
	
	public GamePhase getGamePhase() {
		return gamePhase;
	}
	public void setScore(int score) {
		this.score=score;
	}
	public int getScore() {
		return score;
	}
	public void setHi(int hi) {
		this.hi=hi;
	}
	public int getHi() {
		return hi;
	}
	public void setLevel(int level) {
		this.level=level;
	}
	public int getLevel() {
		return level;
	}
	public void setLives(int lives) {
		this.lives=lives;
	}
	public int getLives() {
		return lives;
	}
	public static void main(String[] args) {
		JumpingJack JJ = new JumpingJack();
	}
}
