import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
public class JJCanvas extends Canvas implements ActionListener {
	private Image jackImg[];
	private Image barImg;
	private JumpingJack jjack;
	private Timer timer;
	public JJCanvas(JumpingJack jjack) {
		    this.jjack=jjack;
			jackImg=new Image[3];
			setSize(320,320);
			setBackground (Color.WHITE); 
			for(int i=0;i<3;i++) 
				jackImg[i]=Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"/bin/jack"+i+".png");
			barImg=Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"/bin/bar.png");
			timer=new Timer(60,this);
			timer.start();
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
	    g2D.drawImage(jackImg[jjack.myJack.jackFrame.getValue()], jjack.myJack.getX(), jjack.myJack.getY(), this);
	    for(int i=0;i<5;i++) 
	    	g2D.drawImage(barImg,jjack.myBars[i].getX(),jjack.myBars[i].getY(),this);
	    jjack.scoreLabel.setText(String.format("Level=%02d   Score=%05d   Lives=%02d   HI=%05d",jjack.getLevel(),jjack.getScore(),jjack.getLives(),jjack.getHi()));
	}
    public void actionPerformed(ActionEvent e) {
    	if (jjack.getGamePhase()==GamePhase.Play) {
			if  (jjack.myJack.jackPhase==Jack.Phase.Jumping) {	
				if (jjack.myJack.jackFrame==Jack.Frame.Jump1) {
					jjack.myJack.jackFrame=Jack.Frame.Jump2;
				} else if (jjack.myJack.jackFrame==Jack.Frame.Jump2) {
					jjack.myJack.jackFrame=Jack.Frame.Jump1;
				}
				jjack.myJack.setJumpStep(jjack.myJack.getJumpStep()+4);
				jjack.myJack.setXY(jjack.myJack.getX()+4,jjack.myJack.getY()+jjack.myJack.getJumpDir()*4);
				if (jjack.myJack.getJumpStep()>=36) {
					if (jjack.myJack.getJumpDir()==-1) {
						jjack.myJack.setJumpStep(0);
						jjack.myJack.setJumpDir(1);
					} else {
						jjack.myJack.jackPhase=Jack.Phase.Quiet;
						jjack.myJack.jackFrame=Jack.Frame.Quiet;
						if (jjack.myJack.getNPlatform()==4) {
							jjack.myJack.setXY(0,144);
							jjack.myJack.setNPlatform(0);
							jjack.setScore(jjack.getScore()+100);
							jjack.setLevel(jjack.getLevel()+1);
							jjack.setLives(jjack.getLives()+1);
							for(int i=1;i<5;i++) {	
								int speed=jjack.myBars[i].getSpeed();
								if (speed<0)
									jjack.myBars[i].setSpeed(speed-1);
								else 
									jjack.myBars[i].setSpeed(speed+1);
							}
							return;
						}
						jjack.myJack.setNPlatform(jjack.myJack.getNPlatform()+1);
						int diffY=jjack.myBars[jjack.myJack.getNPlatform()].getY()-32-jjack.myJack.getY();
						if ((diffY>-8) && (diffY<16)) {
							jjack.myJack.setXY(jjack.myJack.getX(),jjack.myBars[jjack.myJack.getNPlatform()].getY()-32);
							jjack.setScore(jjack.getScore()+50);
						} else {
							jjack.myJack.setXY(0,144);
							jjack.myJack.setNPlatform(0);
							jjack.setLives(jjack.getLives()-1);
							if (jjack.getLives()==0) {
								jjack.gamePhase=GamePhase.Over;
								if (jjack.getHi()<jjack.getScore())
									jjack.setHi(jjack.getScore());
							}
						}
							
					}
				}
				
			} else if  (jjack.myJack.jackPhase==Jack.Phase.Quiet) {
				jjack.myJack.setXY(jjack.myJack.getX(), jjack.myJack.getY()+jjack.myBars[jjack.myJack.getNPlatform()].getSpeed());
			}
			for(int i=0;i<5;i++) {	
				jjack.myBars[i].setXY(jjack.myBars[i].getX(), jjack.myBars[i].getY()+jjack.myBars[i].getSpeed());
				if (jjack.myBars[i].getY()<0) {
					jjack.myBars[i].setSpeed(Math.abs(jjack.myBars[i].getSpeed()));
				} else if (jjack.myBars[i].getY()>320) {
					jjack.myBars[i].setSpeed(-Math.abs(jjack.myBars[i].getSpeed()));
				}
			}
    	}
    	repaint();
    }
}
