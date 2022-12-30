import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class IntroScene implements SceneInterface {
	StarMiner sm;
	Game game;
	P1 p1;
	
	public IntroScene(StarMiner sm, Game game) {
		this.sm=sm;
		this.game=game;
		Helper.playSound("fire");
	}
	
	public class P1 extends JPanel {
		String title="Star Miner";
		String copyright="(c)2022 Texasoft Reloaded";
		String pressAnyKey="Press Space to Begin";
		int w=sm.getW();
		int h=sm.getH();
		P1(StarMiner sm) {
			setDoubleBuffered(true);
			setPreferredSize(new Dimension(w,h));
			sm.add(this);
	        sm.pack();
	        setVisible(true);
	        sm.setVisible(true);
			setFocusable(true);
			requestFocusInWindow();	
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(Color.BLACK);
			g2D.fillRect(0, 0, w, h);
			Image moonTitle=sm.getImage("moontitle");
			g2D.drawImage(moonTitle,w/2-moonTitle.getWidth(null)/2,h/2-moonTitle.getHeight(null)/2,sm); 
			g2D.setFont(sm.getFont().deriveFont(24f));
			g2D.setColor(Color.YELLOW);
			g2D.drawString(title, w/2-g2D.getFontMetrics().stringWidth(title)/2, 32);
			g2D.setFont(sm.getFont());
			g2D.setColor(Color.RED);
			g2D.drawString(copyright, w/2-g2D.getFontMetrics().stringWidth(copyright)/2, 344);
			g2D.setColor(Color.WHITE);
			g2D.drawString(pressAnyKey, w/2-g2D.getFontMetrics().stringWidth(pressAnyKey)/2, 368);	
		}
	}
	
	@Override
	public void createScene() {
		p1=new P1(sm);
		AbstractAction spaceAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				game.setGameScene(Game.GameScene.Play);
			}
		};
		Helper.assignKeyActionBinding(p1,"SPACE", spaceAction);
	}

	@Override
	public void playScene() {
	}
	
	@Override
	public void drawScene() {
		p1.repaint();
	}

	@Override
	public void destroyScene() {
		sm.remove(p1);
		p1=null;
	}
	
}
