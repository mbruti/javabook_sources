import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
public class PlayScene implements SceneInterface, Runnable {
	StarMiner sm;
	Game game;
	PlayScene ps;
	P1 p1;
	int xGun,yGun,xCrew,yCrew;
	int fireStep,fireX,fireY;
	int fireX1,fireX2,fireY1,fireY2;
	int currentMonster=0;
	int monsterCounter=0;
	int monsterAdvance=0;
	float monsterScale=0;
	int monsterDirX,monsterDirY=0;
	int monsterX,monsterY;
	int temperature=0;
	int water=600;
	int objX,objY;
	float objectScale=0;
	boolean waterOK=true;
	int ammo=12;
	long actionMessageTimer;
	int trackFrame=0;
	int crewFrame=0;
	AtomicBoolean running=new AtomicBoolean(false);
	Color colorTunnelLight=new Color(252,227,114);
	Color colorTunnelDark=new Color(224,192,64);
	Color[] levelColor;
	int[] monsterSpeed= {1,1,2,2,2,2,2,3,3,3,3,3,3,3,4,4,4,4,4,4};
	ScenePhase scenePhase=ScenePhase.OnShip;
	FireStatus fireStatus=FireStatus.noFire;
	ObjectStatus objectStatus=ObjectStatus.noObject;
	ObjectStatus pickedObject=ObjectStatus.noObject;
	MessageStatus messageStatus=MessageStatus.noMessage;
	boolean explosion=false;
	boolean extraCrewMember=false;
	StringBuffer message,actionMessage;
	int tunnelDrawStep=0;
	
	enum Direction {
		STOP,UP, DOWN,LEFT,RIGHT
	};
	
	Direction direction=Direction.STOP;
	Direction lastDirection=Direction.STOP;
	
	public enum ScenePhase {
		OnShip(0),Man(1);
		private int value;
		private ScenePhase(int value) {
		this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
	
	public enum FireStatus {
		noFire(0),hFire(1),vFire(2),crewFire(3);
		private int value;
		private FireStatus(int value) {
		this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
	
	public enum MessageStatus {
		noMessage(0),actionMessage(1),bonusMessage(2),changeLevelMessage(3),waterMessage(4),hitMessage(5),temperatureMessage(6),outofwaterMessage(7),hitCrewMessage(8),gameOverMessage(9);
		private int value;
		private MessageStatus(int value) {
		this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
	
	public enum ObjectStatus {
		noObject(0),water(1),treasure(2);
		private int value;
		private ObjectStatus(int value) {
		this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
		
	public PlayScene(StarMiner sm, Game game) {
		this.sm=sm;
		this.game=game;
		ps=this;
		xGun=248;
		yGun=120;
		message=new StringBuffer(40);
		levelColor=new Color[100];
		for(int i=0;i<=99;i++)
			levelColor[i]=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255),255);
	}
	
	public class P1 extends JPanel {
		String tempString="TEMP.";
		String waterString="WATER";
		String levelString="LEVEL";
		String crewString="CREW";
		String scoreString="SCORE:";
		String highString="HIGH :";
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
		
		private void drawFire(Graphics2D g2D) {
			if (fireStatus==FireStatus.noFire) return;
			g2D.setColor(Color.RED);
			switch (fireStatus) {
			case hFire:
				fireX1=48+fireStep*8;
				fireX2=448-fireStep*8;
				fireY1=fireY+7;
				fireY2=fireY1;
				g2D.fillRect(fireX1,fireY1, 16, 2);
				g2D.fillRect(fireX2,fireY2, 16, 2);
				break;
			case vFire:
				fireY1=24+fireStep*8;
				fireY2=220-fireStep*8;
				fireX1=fireX+7;
				fireX2=fireX1;
				g2D.fillRect(fireX1,fireY1,  2, 16);
				g2D.fillRect(fireX2,fireY2, 2, 16);
				break;
			case crewFire:
				g2D.fillRect(fireX, fireY, 2, 16);
				break;
			}
		}
		
		private void drawObject(Graphics2D g2D) {
			switch (objectStatus) {
			case noObject:
				return;
			case water:
				objectScale=(objY-128.0f)/96.0f;
				g2D.drawImage(sm.getImage("water"),objX, objY, (int)(objectScale*32), (int)(objectScale*16), this);
				break;
			case treasure:
				objectScale=(objY-128.0f)/96.0f;
				g2D.drawImage(sm.getImage("treasure"),objX, objY, (int)(objectScale*32), (int)(objectScale*16), this);
				break;
			}
		}
		
		private void drawTunnel(Graphics2D g2D) {
			g2D.setColor(levelColor[(game.getLevel()-1) % 100] );
			g2D.fillRect(32, 0, 448, 255);
			if (explosion) {
			float explosionRedComp=(float)Math.random();
				g2D.setColor(new Color(explosionRedComp,0.5f,0.5f,1.0f));
				g2D.fillRect(48,16,416,224);
				g2D.setColor(new Color(explosionRedComp,0.25f,0.25f,1.0f));
				g2D.fillRect(80,48,352,160);
				g2D.setColor(new Color(explosionRedComp,0.0f,0.0f,1.0f));
				g2D.fillRect(112,80,288,96);			
			} else {
				g2D.setColor(colorTunnelLight);
				g2D.fillRect(48,16,416,224);
				g2D.fillRect(80,48,352,160);
				g2D.setColor(colorTunnelDark);
				g2D.fillRect(112,80,288,96);
			}
			g2D.setColor(Color.BLACK);
			g2D.fillRect(144,112,224,32);
			g2D.drawRect(32, 0, 448,256);
			g2D.drawLine(32,0, 144, 112);
			g2D.drawLine(480,0, 368, 112);
			g2D.drawLine(32,256, 144, 144);
			g2D.drawLine(480,256, 368, 144);
			g2D.setColor(Color.BLACK);
			g2D.setStroke(new BasicStroke(4));
			g2D.drawRect(80-tunnelDrawStep,48-tunnelDrawStep,352+tunnelDrawStep*2,160+tunnelDrawStep*2);
			g2D.setStroke(new BasicStroke(3));
			g2D.drawRect(112-tunnelDrawStep,80-tunnelDrawStep,288+tunnelDrawStep*2,96+tunnelDrawStep*2);
			g2D.setStroke(new BasicStroke(2));
			g2D.drawRect(144-tunnelDrawStep,112-tunnelDrawStep,224+tunnelDrawStep*2,32+tunnelDrawStep*2);
		}
		
		private void drawInfoPanel(Graphics2D g2D) {
			g2D.setColor(Color.WHITE);
			g2D.fillRect(32, 256, 448, 128);
			g2D.setColor(Color.BLUE);
			g2D.fillRect(48, 272, 416, 96);
			g2D.setFont(sm.getFont().deriveFont(16f));
			g2D.setColor(Color.WHITE);
			g2D.drawString(tempString, 64,288);
			g2D.drawString(waterString, 64,304);
			g2D.fillRect(150,272,168,32);
			g2D.setColor(Color.red);
			g2D.fillRect(154, 275,(int)(temperature/100.0*160), 12);
			g2D.setColor(Color.CYAN);
			g2D.fillRect(154, 290, (int)(water/600.0*160), 12);
			g2D.setColor(Color.WHITE);
			g2D.drawString(levelString, 64,320);
			g2D.drawString(String.format("%02d", game.getLevel()), 152, 320);
			g2D.drawString(crewString, 64,336);
			for(int i=1;i<=game.getCrew();i++)
				g2D.drawImage(sm.getImage("crew"), 64+(i-1)*32,336,this);
			g2D.drawString(scoreString, 320,288);
			g2D.drawString(String.format("%06d", game.getScore()), 320, 304);
			g2D.drawString(highString, 320,336);
			g2D.drawString(String.format("%06d", game.getHi()), 320, 352);
			g2D.setColor(Color.BLACK);
			g2D.drawRect(32, 256, 448,127);
			g2D.drawRect(48, 272, 416,96);
			g2D.drawLine(32,256, 48, 272);
			g2D.drawLine(480,256, 464, 272);
			g2D.drawLine(32,384, 48, 368);
			g2D.drawLine(480,384, 464, 368);
		}
		
		private void drawShip(Graphics2D g2D) {
			BufferedImage tracksImg=(BufferedImage)(sm.getImage("tracks"));
			g2D.drawImage(tracksImg.getSubimage(0,16*(int)(trackFrame/2),80,16),48,224,this);
			g2D.drawImage(tracksImg.getSubimage(80,16*(int)(trackFrame/2),80,16),384,224,this);
			if (scenePhase!=ScenePhase.OnShip) return;
			g2D.drawImage(sm.getImage("topgun"),xGun,16,this);
			g2D.drawImage(sm.getImage("downgun"),xGun,224,this);
			g2D.drawImage(sm.getImage("leftgun"),48,yGun,this);
			g2D.drawImage(sm.getImage("rightgun"),448,yGun,this);
		}
		
		private void drawMonster(Graphics2D g2D) {
			if (currentMonster==0) return;
			BufferedImage monstersImg=(BufferedImage)(sm.getImage("monsters"));
			g2D.drawImage(monstersImg.getSubimage(32*(currentMonster-1),0,32,32),monsterX, monsterY,(int)(32*monsterScale)+1,(int)(32*monsterScale)+1,this);
		}
		
		private void drawCrew(Graphics2D g2D) {
			if (scenePhase!=ScenePhase.Man) return;
			BufferedImage crewImg=(BufferedImage)(sm.getImage("crew_anim"));
			if ((messageStatus==MessageStatus.bonusMessage) || (messageStatus==MessageStatus.waterMessage)) {
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(Math.random()));
				g2D.setComposite(ac);
			}
			switch (direction) {
			case LEFT:
				g2D.drawImage(crewImg.getSubimage(0,32*(int)(crewFrame/2),32,32),xCrew,yCrew,this);
				break;
			case RIGHT:
				g2D.drawImage(crewImg.getSubimage(32,32*(int)(crewFrame/2),32,32),xCrew,yCrew,this);
				break;
			case UP:
				g2D.drawImage(crewImg.getSubimage(64,32*(int)(crewFrame/2),32,32),xCrew,yCrew,this);
				break;
			case DOWN:
				g2D.drawImage(crewImg.getSubimage(96,32*(int)(crewFrame/2),32,32),xCrew,yCrew,this);
				break;
			case STOP:
				g2D.drawImage(sm.getImage("crew_still"),xCrew,yCrew,this);
				break;
			}
			if (direction!=Direction.STOP) 
				crewFrame=(crewFrame<5 ? crewFrame+1 : 0);
			if ((messageStatus==MessageStatus.bonusMessage) || (messageStatus==MessageStatus.waterMessage)) {
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
				g2D.setComposite(ac);
			}
		}
		
		private void drawMessage(Graphics2D g2D, String msg) {
			if (messageStatus==MessageStatus.gameOverMessage) {
				g2D.setColor(Color.WHITE);
				g2D.drawString(msg, 144,136);
			} else {
				g2D.setColor(Color.blue);
				g2D.fillRect(48,240,416,16);
				g2D.setColor(Color.WHITE);
				g2D.drawString(msg, 64,254);
			}
		}
			
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(Color.BLUE);
			g2D.fillRect(0, 0, w, h);
			drawTunnel(g2D);
			drawShip(g2D);
			if ((messageStatus==MessageStatus.noMessage) || (messageStatus==MessageStatus.actionMessage)) {
				drawMonster(g2D);
				drawObject(g2D);
				drawFire(g2D);
			}
			if ((messageStatus==MessageStatus.noMessage) || (messageStatus==MessageStatus.actionMessage) || (messageStatus==MessageStatus.bonusMessage) || (messageStatus==MessageStatus.waterMessage)) 
				drawCrew(g2D);
			
			drawInfoPanel(g2D);
			if (messageStatus==MessageStatus.actionMessage)
				drawMessage(g2D,actionMessage.toString());
			else if (messageStatus!=MessageStatus.noMessage) {
				drawMessage(g2D,message.toString());
			}
		}
	}
	
	private boolean checkFireCollision() {
		if ((fireStatus==FireStatus.noFire) || (currentMonster==0))
			return false;
		switch (fireStatus) {
		case hFire:
			if ((fireY1>=monsterY) && ((fireY1-monsterY)<(32*monsterScale)) && ((Math.abs(fireX1-monsterX)<(32*monsterScale+8)) || (Math.abs(fireX2-monsterX)<(32*monsterScale+8)))) {
				fireStatus=FireStatus.noFire;
				return true;
			}
			break;
		case vFire:
			if ((fireX1>=monsterX) && ((fireX1-monsterX)<(32*monsterScale)) && ((Math.abs(fireY1-monsterY)<(32*monsterScale+8)) || (Math.abs(fireY2-monsterY)<(32*monsterScale+8)))){
				fireStatus=FireStatus.noFire;
				return true;
			}
			break;
		case crewFire:
			if ((fireX>=monsterX) && ((fireX-monsterX)<(32*monsterScale)) && (Math.abs(fireY-monsterY)<(32*monsterScale+8))){
				fireStatus=FireStatus.noFire;
				return true;
			}
			break;
		}
		return false;
	}
	
	private boolean checkCrewCollisionMonster() {
		if (scenePhase!=ScenePhase.Man)
			return false;
		if (currentMonster!=0)
			if ((xCrew>=monsterX) && (yCrew>=monsterY)) {
				if (((xCrew-monsterX)<=(28*monsterScale)) && ((yCrew-monsterY)<=(28*monsterScale))) 
					return true;
			} else if ((xCrew>=monsterX) && (yCrew<monsterY)) {
				if (((xCrew-monsterX)<=(28*monsterScale)) && ((monsterY-yCrew)<=28)) 
					return true;
			} else if ((xCrew<monsterX) && (yCrew>=monsterY)) {
				if (((monsterX-xCrew)<=28) && ((yCrew-monsterY)<=(28*monsterScale))) 
					return true;
			} else if ((xCrew<monsterX) && (yCrew<monsterY)) {
				if (((monsterX-xCrew)<=28) && ((monsterY-yCrew)<=28)) 
					return true;
			}
		return false;	
	}
	
	private boolean checkCrewCollisionObject() {
		if (scenePhase!=ScenePhase.Man)
			return false;
		if (objectStatus!=ObjectStatus.noObject) {
			if ((yCrew<=objY) && ((objY-yCrew)<=32) && ((objY-yCrew)>=(32-16*objectScale))) {
				if ((xCrew<=objX) && (objX-xCrew)<=16)
					return true;
				else if ((xCrew>objX) && (xCrew-objX)<=(16*objectScale)) 
					return true;
			}
		}
		return false;	
	}
	
	@Override
	public void createScene() {
		p1=new P1(sm);
		AbstractAction stopAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				direction=Direction.STOP;
			}
		};
		AbstractAction upPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.actionMessage) && (messageStatus!=MessageStatus.noMessage))
					return;
				direction=Direction.UP;
				lastDirection=direction;
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed UP", upPressedAction);
		Helper.assignKeyActionBinding(p1,"released UP", stopAction);
			
		AbstractAction downPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.actionMessage) && (messageStatus!=MessageStatus.noMessage))
					return;
				direction=Direction.DOWN;
				lastDirection=direction;
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed DOWN", downPressedAction);
		Helper.assignKeyActionBinding(p1,"released DOWN", stopAction);
		
		AbstractAction leftPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.actionMessage) && (messageStatus!=MessageStatus.noMessage))
					return;
				direction=Direction.LEFT;
				lastDirection=direction;
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed LEFT", leftPressedAction);
		Helper.assignKeyActionBinding(p1,"released LEFT", stopAction);
			
		AbstractAction rightPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.actionMessage) && (messageStatus!=MessageStatus.noMessage))
					return;
				direction=Direction.RIGHT; 
				lastDirection=direction;
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed RIGHT", rightPressedAction);
		Helper.assignKeyActionBinding(p1,"released RIGHT", stopAction);
		
		AbstractAction firePressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.noMessage) && (messageStatus!=MessageStatus.actionMessage))
					return;
				if (scenePhase==ScenePhase.OnShip) {
					temperature+=10;
					water-=4;
					if (temperature>=100) {
						temperature=100;
						messageStatus=MessageStatus.temperatureMessage;
						Thread messageThread= new Thread(ps);
						messageThread.start();
						return;
					}
					if (water<=0) {
						water=0;	
						messageStatus=MessageStatus.outofwaterMessage;
						Thread messageThread= new Thread(ps);
						messageThread.start();
						return;
					} else if (water<100) {
						actionMessage=Helper.centerString("COOLANT IS RUNNING LOW!", 26);
						messageStatus=MessageStatus.actionMessage;
						actionMessageTimer=System.currentTimeMillis();
					}
				}
				if (fireStatus==FireStatus.noFire) {
					fireStep=0;
					if (scenePhase==ScenePhase.OnShip) {
						Helper.playSound("fire");
						if ((lastDirection==Direction.STOP) || (lastDirection==Direction.LEFT) || (lastDirection==Direction.RIGHT)) {
							fireStatus=FireStatus.vFire;
							fireX=xGun;
						} else {
							fireStatus=FireStatus.hFire;
							fireY=yGun;
						}
					} else if (scenePhase==ScenePhase.Man) {
						if (checkCrewCollisionObject()) {
							if (objectStatus==ObjectStatus.water) {
								objectStatus=ObjectStatus.noObject;
								pickedObject=ObjectStatus.water;
							} else {
								objectStatus=ObjectStatus.noObject;
								pickedObject=ObjectStatus.treasure;
							}
						} else {
							if (ammo>0) {
								ammo--;
								Helper.playSound("fire");
								fireStatus=FireStatus.crewFire;
								fireX=xCrew+16;
								fireY=yCrew-8;
							}
						}
					}
				}
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed Q", firePressedAction);
		
		AbstractAction spacePressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((messageStatus!=MessageStatus.noMessage) && (messageStatus!=MessageStatus.actionMessage))
					return;
				if ((scenePhase==ScenePhase.OnShip) && (objectStatus!=ObjectStatus.noObject)) {
					scenePhase=ScenePhase.Man;
					xCrew=240;
					yCrew=206;
					crewFrame=0;
					ammo=12;
				} else if ((scenePhase==ScenePhase.Man) && (currentMonster==0)) {
					direction=Direction.STOP;
					if (pickedObject!=ObjectStatus.noObject) {
						if (pickedObject==ObjectStatus.water)
							messageStatus=MessageStatus.waterMessage;
						else
							messageStatus=MessageStatus.bonusMessage;
						Thread messageThread= new Thread(ps);
						messageThread.start();
					}
				}
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed SPACE", spacePressedAction);
		
		AbstractAction quitPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if (messageStatus!=MessageStatus.gameOverMessage) {
					return;
				}
				game.setGameScene(Game.GameScene.Over);
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed X", quitPressedAction);
		
		AbstractAction redoPressedAction = new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if (messageStatus!=MessageStatus.gameOverMessage) 
					return;
				running.set(false);
				currentMonster=0;
				temperature=0;
				water=600;
				game.setLevel(1);
				game.setScore(0);
				game.resetCrew();
				extraCrewMember=false;
				objectStatus=ObjectStatus.noObject;
				pickedObject=ObjectStatus.noObject;
				messageStatus=MessageStatus.noMessage;
				scenePhase=ScenePhase.OnShip; 
				game.setGameScene(Game.GameScene.Intro);
			}
		};
		Helper.assignKeyActionBinding(p1,"pressed R", redoPressedAction);
	}
	
	@Override
	public void playScene() {
		if ((messageStatus!=MessageStatus.noMessage) && (messageStatus!=MessageStatus.actionMessage))
			return;
		if (messageStatus==MessageStatus.actionMessage)
			if (System.currentTimeMillis()-actionMessageTimer>3000)
				messageStatus=MessageStatus.noMessage;
		if ((currentMonster==0) && ((scenePhase==ScenePhase.OnShip) || (pickedObject==ObjectStatus.noObject))) {
			currentMonster=(int)(Math.random()*12)+1;
			monsterY=128;
			monsterX=(int)(Math.random()*224)+144;
			monsterAdvance=0;
			monsterScale=0.2f;
			monsterDirY=Helper.randDir();
			monsterDirX=Helper.randDir();
		} else if (currentMonster!=0) {
			monsterAdvance++;
			monsterScale=(float)(monsterAdvance/224.0);
			if ((Math.random()*100)<=game.getLevel()) {
				monsterDirY=Helper.randDir();
				monsterDirX=Helper.randDir();
			}
			if (monsterAdvance>224) {
				currentMonster=0;
				pickedObject=ObjectStatus.noObject;
				messageStatus=MessageStatus.hitMessage;
				Thread messageThread= new Thread(this);
				messageThread.start();
			}
			int mSpeed;
			if (game.getLevel()>=21) 
				mSpeed=5;
			else
				mSpeed=monsterSpeed[game.getLevel()-1];
			if (monsterDirX==-1) {
				monsterX-=mSpeed;
				if (monsterX<(144-monsterAdvance/2))
					monsterDirX=1;
			} else {
				monsterX+=mSpeed;
				if (monsterX>((360+monsterAdvance/2)-32*monsterScale))
					monsterDirX=-1;
			}
			if (monsterDirY==-1) {
				monsterY-=mSpeed;
				if (monsterY<(128-monsterAdvance/2))
					monsterDirY=1;
			} else {
				monsterY+=mSpeed;
				if (monsterY>((128+monsterAdvance/2)-32*monsterScale))
					monsterDirY=-1;
			}
			if (checkFireCollision()) {
				Helper.playSound("hit");
				currentMonster=0;
				game.setScore(game.getScore()+50);
				monsterCounter++;
				if (monsterCounter==12) {
					monsterCounter=0;
					game.setLevel(game.getLevel()+1);
					actionMessage=Helper.centerString("ADVANCE TO NEXT LEVEL!", 26);
					messageStatus=MessageStatus.actionMessage;
					actionMessageTimer=System.currentTimeMillis();
				}
			}
		}
		if (checkCrewCollisionMonster()) {
			currentMonster=0;
			pickedObject=ObjectStatus.noObject;
			messageStatus=MessageStatus.hitCrewMessage;
			Thread messageThread= new Thread(ps);
			messageThread.start();
		}
		if (scenePhase==ScenePhase.OnShip) {
			tunnelDrawStep+=1;
			if (tunnelDrawStep>32) 
				tunnelDrawStep=0;
			trackFrame=(trackFrame<11 ? trackFrame+1 : 0);
			if (objectStatus==ObjectStatus.noObject) {
				if ((water<100) && (waterOK)) {
					objectStatus=ObjectStatus.water;
					objX=(int)(Math.random()*232+124);
					objY=152;
					actionMessage=Helper.centerString("WATER AHEAD", 26);
					messageStatus=MessageStatus.actionMessage;
					actionMessageTimer=System.currentTimeMillis();
				} else if (Math.random()<0.001) {
					objectStatus=ObjectStatus.treasure;
					objX=(int)(Math.random()*232+124);
					objY=152;
					actionMessage=Helper.centerString("DETECTING UNKNOWN OBJECT", 26);
					messageStatus=MessageStatus.actionMessage;
					actionMessageTimer=System.currentTimeMillis();
				}
			}
			if (objectStatus!=ObjectStatus.noObject) {
				objY++;
				if (objY>224) {
					if (objectStatus==ObjectStatus.water)
						waterOK=false;
					objectStatus=ObjectStatus.noObject;
					messageStatus=MessageStatus.noMessage;
				}
			}
		}
		if ((messageStatus==MessageStatus.noMessage) || (messageStatus==MessageStatus.actionMessage))
			switch (direction) {
			case UP:
				if (scenePhase==ScenePhase.OnShip) {
					if (yGun>32)
						yGun=yGun-4;
				} else if (scenePhase==ScenePhase.Man) {
					if ((yCrew>124) && (xCrew>(60+192-yCrew)) && (xCrew<(420-192+yCrew)))
						yCrew-=2;
				}
				break;
			case DOWN:
				if (scenePhase==ScenePhase.OnShip) {
					if (yGun<206) 
						yGun=yGun+4;
				} else if (scenePhase==ScenePhase.Man) {
					if ((xCrew<120) || (xCrew>360)) {
						if (yCrew<192)	
						yCrew+=2;
					} else if (yCrew<206)
						yCrew+=2;
				}
				break;
			case LEFT: 
				if (scenePhase==ScenePhase.OnShip) {
					if (xGun>128)
						xGun=xGun-4;
				} else if (scenePhase==ScenePhase.Man) {
					if (yCrew>192) {
						if (xCrew>120) 
							xCrew-=4;
					} else if (xCrew>(60+192-yCrew))
						xCrew-=4;
				}
			break;
			case RIGHT: 
				if (scenePhase==ScenePhase.OnShip) {
					if (xGun<368)
						xGun=xGun+4;
				} else if (scenePhase==ScenePhase.Man) {
					if (yCrew>192) {
						if (xCrew<360) 
							xCrew+=4;
					} else if (xCrew<(420-192+yCrew))
						xCrew+=4;
				}
				break;
			case STOP: break;
			}
		if (fireStatus!=FireStatus.noFire) {
			switch (fireStatus) {
				case hFire,vFire:
					fireStep++;
					if (fireStep>12)
						fireStatus=FireStatus.noFire;
					break;
				case crewFire:
					fireY-=8;
					if (fireY<16)
						fireStatus=FireStatus.noFire;
					break;
				case noFire:
			}
		} else
			temperature=(temperature>=2 ? temperature-2: 0);
		if ((!extraCrewMember) && (game.getScore()>=5000)) {
			game.setCrew(game.getCrew()+1);
			actionMessage=Helper.centerString("EXTRA CREW MEMBER!", 26);
			messageStatus=MessageStatus.actionMessage;
			actionMessageTimer=System.currentTimeMillis();
			extraCrewMember=true;
		} 
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
	
	@Override
	public void run() {
		int messageStep=0;
		long start=System.currentTimeMillis();
		long diffTime;
		running.set(true);
		switch(messageStatus) {
		case hitMessage:
			message=Helper.centerString("MONSTER DAMAGED SHIP!",26);
			messageStep=1;
			explosion=true;
			Helper.playSound("explosion");
			while((System.currentTimeMillis()-start)<4000) {
				diffTime=System.currentTimeMillis()-start;
				if (diffTime>2000) {
					if (messageStep==1) {
						explosion=false;
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("CREW MEMBER LOST",26);
						game.setCrew(game.getCrew()-1);
					}
				}
			}
			water=600;
			waterOK=true;
			break;
		case hitCrewMessage:
			message=Helper.centerString("MONSTER ATTACKED CREW!",26);
			messageStep=1;
			explosion=true;
			Helper.playSound("explosion");
			while((System.currentTimeMillis()-start)<4000) {
				diffTime=System.currentTimeMillis()-start;
				if (diffTime>2000) {
					if (messageStep==1) {
						explosion=false;
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("CREW MEMBER LOST",26);
						game.setCrew(game.getCrew()-1);
					}
				}
			}
			water=600;
			waterOK=true;
			break;	
		case temperatureMessage:
			message=Helper.centerString("LASER OVERHEATED",26);
			messageStep=1;
			explosion=true;
			Helper.playSound("explosion");
			while((System.currentTimeMillis()-start)<4000) {
				diffTime=System.currentTimeMillis()-start;
				if (diffTime>2000) {
					if (messageStep==1) {
						explosion=false;
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("CREW MEMBER LOST",26);
						game.setCrew(game.getCrew()-1);
					}
				}
			}
			water=600;
			waterOK=true;
			break;	
		case waterMessage:
			message.setLength(0);
			message.append("CONGRATULATIONS CAPTAIN!");
			messageStep=1;
			while((System.currentTimeMillis()-start)<6000) {
				diffTime=System.currentTimeMillis()-start;
			    if (diffTime>4000) {
					if (messageStep==2) {
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("1000 BONUS POINTS GAINED!",26);
						game.setScore(game.getScore()+1000);
					} 
				} else if (diffTime>2000) {
					if (messageStep==1) {
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("WATER TANKS FULL",26);
						water=600;
					}
				}
			}
			waterOK=true;
			break;
		case bonusMessage:
			message.setLength(0);
			message.append("CONGRATULATIONS CAPTAIN!");
			messageStep=1;
			while((System.currentTimeMillis()-start)<4000) {
				diffTime=System.currentTimeMillis()-start;
			    if (diffTime>2000) {
					if (messageStep==1) {
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("500 BONUS POINTS GAINED!",26);
						game.setScore(game.getScore()+500);
					} 
				}
			}
			objectStatus=ObjectStatus.noObject;
			break;	
		case outofwaterMessage:
			message=Helper.centerString("OUT OF WATER, CAPTAIN",26);
			messageStep=1;
			explosion=true;
			Helper.playSound("explosion");
			while((System.currentTimeMillis()-start)<4000) {
				diffTime=System.currentTimeMillis()-start;
				if (diffTime>2000) {
					if (messageStep==1) {
						explosion=false;
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("CREW MEMBER LOST",26);
						game.setCrew(game.getCrew()-1);
					}
				}
			}
			water=600;
			waterOK=true;
			break;	
		}
		if (game.getCrew()==0) {
			messageStatus=MessageStatus.gameOverMessage;
			explosion=false;
			start=System.currentTimeMillis();
			messageStep=1;
			message=Helper.centerString("GAME OVER",14);
			while(running.get()) {
				diffTime=System.currentTimeMillis()-start;
				if (diffTime>4000) {
					if (messageStep==1) {
						messageStep++;
						message.setLength(0);
						message=Helper.centerString("<R>REDO<X>QUIT",14);
						if (game.getScore()>game.getHi()) 
							game.setHi(game.getScore());
					} 
				}
			}	
		} else {
			message.setLength(0);
			message=Helper.centerString("CONTINUE GAME, CAPTAIN",26);
			start=System.currentTimeMillis();
			while((System.currentTimeMillis()-start)<2000) {
			};
			currentMonster=0;
			temperature=0;
			objectStatus=ObjectStatus.noObject;
			pickedObject=ObjectStatus.noObject;
			messageStatus=MessageStatus.noMessage;
			scenePhase=ScenePhase.OnShip;
		}
	}
}