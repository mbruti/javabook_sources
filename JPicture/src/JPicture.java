import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Scanner;
import java.io.IOException;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
public class JPicture extends JFrame {
    Color currentColor;
    Color bkgColor;
	Color[][] gridPicture;
	Color[][] undoGridPicture;
    ColorPanel colorPanel;
    int startX=0,startY=0,stopX=0,stopY=0,lastX=0,lastY=0;
    boolean writeMode=false;
    boolean fillMode=false;
    int w,h,y_insets,x_insets;
    MenuBar menuBar;
    Menu menuActions;
    MenuItem menuItemUndo,menuItemResize, menuItemSave, menuItemLoad, menuItemExport,menuItemQuit;
    JLabel coordLabel;
    boolean selection=false;
    JPicture mainWindow;
	public JPicture() {
		w=16;
		h=16;
		gridPicture = new Color[64][64];
		undoGridPicture = new Color[64][64];
		for(int i=0;i<64;i++)
			for(int j=0;j<64;j++)
				gridPicture[i][j]=Color.WHITE;
		mainWindow=this;
		setLayout(null);
		setBackground(Color.WHITE);
		setTitle("JPicture (c)2021 Texasoft Reloaded");
		colorPanel=new ColorPanel(this);
	    colorPanel.setBounds(513, 0, 208, 512);
		add(colorPanel);
		colorPanel.setVisible(true);
		menuBar = new MenuBar();
		menuActions=new Menu("Actions");
		menuItemUndo=new MenuItem("Undo");
		menuItemUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<64;i++)
					System.arraycopy(undoGridPicture[i],0,gridPicture[i],0,64);
				repaint();
			};
		});
		menuItemResize=new MenuItem("Resize");
		menuItemResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean errorFlag=false;
				int new_w=0;
				int new_h=0;
				try {
					String ws=(String) JOptionPane.showInputDialog(mainWindow,"Input width (max 64):");
					new_w=Integer.parseInt(ws);
				} catch (Exception e1) {
					errorFlag=true;
				}
				try {
					String hs=(String) JOptionPane.showInputDialog(mainWindow, "Input height (max 64):");
					new_h=Integer.parseInt(hs);	
				} catch (Exception e2) {
					errorFlag=true;
				}
				if ((new_w<1) || (new_w>64) || (new_h<1) || (new_h>64))
					errorFlag=true;
				if (errorFlag) {
					JOptionPane.showMessageDialog(mainWindow, "Input Error!");
					return;
				} else {
					w=new_w;
					h=new_h;
				}
				repaint();
			}
		});
		menuItemQuit=new MenuItem("Quit");
		menuItemQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			};
		});
		menuItemSave=new MenuItem("Save");
		menuItemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String jPictureFileName=JOptionPane.showInputDialog("File Name?")+".jpic";
					File jPictureFile=new File(jPictureFileName);
					jPictureFile.delete();
					if (jPictureFile.createNewFile()) {
						FileWriter jPictureFileWriter = new FileWriter(jPictureFileName);
						jPictureFileWriter.write(w);
						jPictureFileWriter.write(h);
						for (int j=0;j<w;j++)
							for (int i=0;i<h;i++)
								jPictureFileWriter.write(colorPanel.mapColor(gridPicture[i][j]));
						jPictureFileWriter.close();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,"Cannot save the file!");
				}
			};
		});
		menuItemLoad=new MenuItem("Load");
		menuItemLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String jPictureFileName=JOptionPane.showInputDialog("File Name?")+".jpic";
					File jPictureFile=new File(jPictureFileName);
					FileReader jPictureFileReader=new FileReader(jPictureFile);
					w=jPictureFileReader.read();
					h=jPictureFileReader.read();
					for (int j=0;j<w;j++)
						for (int i=0;i<h;i++) {
							int colorCode=jPictureFileReader.read();
							gridPicture[i][j]=colorPanel.getColorFromCode(colorCode);
						}
					jPictureFileReader.close();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,"Cannot Load the file!");
				}
				repaint();
			};
		});
		menuItemExport=new MenuItem("Export");
		menuItemExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage bi;
				try {
					String imageName=JOptionPane.showInputDialog("Image Name?");
					Robot r=new Robot();
					bi=r.createScreenCapture(new Rectangle(256+x_insets,548+y_insets,w,h));
					System.out.println(32+x_insets);
					System.out.println(548+y_insets);
					System.out.println(w);
					System.out.println(h);
					ImageIO.write(bi, "png", new File(imageName+".png"));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,"Cannot save the image!");
				}
			};
		});
		menuActions.add(menuItemUndo);
		menuActions.add(menuItemResize);
		menuActions.add(menuItemSave);
		menuActions.add(menuItemLoad);
		menuActions.add(menuItemExport);
		menuActions.add(menuItemQuit);
		menuBar.add(menuActions);
		setMenuBar(menuBar);
		setVisible(true);
		setSize(new Dimension(720,720));
		x_insets=getInsets().left;
		y_insets=getInsets().top;
		coordLabel=new JLabel("0,0");
		coordLabel.setBounds(0, 720-y_insets-getInsets().bottom-16, 48, 16);
		add(coordLabel);
		coordLabel.setVisible(true);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int mouseX=e.getX()-x_insets;
				int mouseY=e.getY()-y_insets;
				int pixelX=mouseX/8;
				int pixelY=mouseY/8;
				if ((pixelX<0) || (pixelX>=w) || (pixelY<0) || (pixelY>=h))
					return;
				if (writeMode) {
					saveUndo();
					gridPicture[pixelY][pixelX]=currentColor;
					setGrid(pixelX,pixelY,currentColor);
				}
				lastX=pixelX;
				lastY=pixelY;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int mouseX=e.getX()-x_insets;
				int mouseY=e.getY()-y_insets;
				int pixelX=mouseX/8;
				int pixelY=mouseY/8;
				writeCoord(pixelX,pixelY);
			}
			public void mouseDragged(MouseEvent e) {
				int mouseX=e.getX()-x_insets;
				int mouseY=e.getY()-y_insets;
				int pixelX=mouseX/8;
				int pixelY=mouseY/8;
				if ((pixelX<0) || (pixelX>=w) || (pixelY<0) || (pixelY>=h))
					return;
				writeCoord(pixelX,pixelY);
				if (writeMode) {
					gridPicture[pixelY][pixelX]=currentColor;
					setGrid(pixelX,pixelY,currentColor);
				}
				lastX=pixelX;
				lastY=pixelY;
			}
		});
	}
	
	public void writeCoord(int x,int y) {
		if ((x<0) || (x>=w) || (y<0) || (y>=h)) {
			coordLabel.setText("out,out");
			return;
		}
		coordLabel.setText(x+","+y);
	}
	
	public void clearGrid() {
		for(int y=0;y<h;y++) {
			for(int x=0;x<w;x++) {
				gridPicture[y][x]=currentColor;
			}
		}
		repaint();
	}
	
	public void setGrid(int pixelX, int pixelY, Color c) {
		gridPicture[pixelY][pixelX]=c;
		Graphics2D g2=(Graphics2D) getGraphics();
		g2.setColor(c);
		g2.fillRect(pixelX*8+x_insets+1, pixelY*8+y_insets+1,7,7 );
		g2.fillRect(545+x_insets+pixelX*2,521+y_insets+pixelY*2,2,2);
		g2.fillRect(256+x_insets+pixelX,548+y_insets+pixelY,1,1);
	}
	
	public Color getGrid(int pixelX, int pixelY) {
		return gridPicture[pixelY][pixelX];
	}
	
	public void paint(Graphics g) {
		Graphics2D g2=(Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0,0,720,720);
		for(int y=0;y<h;y++) {
			for(int x=0;x<w;x++) {
				g2.setColor(Color.BLACK);
				g2.drawRect(x*8+x_insets, y*8+y_insets,8,8 );
				setGrid(x,y,gridPicture[y][x]);
			}
		}
		g2.setColor(Color.BLACK);
		g2.drawRect(544+x_insets, 520+y_insets, w*2+2, h*2+2);
		colorPanel.repaint();
		coordLabel.repaint();
		selection=false;
	}
	
	public int getW() {
		return this.w;
	}
	
	public int getH() {
		return this.h;
	}
	
	public Color getCurrentColor() {
		return this.currentColor;
	}
	
	public void setCurrentColor(Color c) {
		this.currentColor=c;
	}
	
	public Color getBkgColor() {
		return this.bkgColor;
	}
	
	public void setBkgColor(Color bkgColor) {
		this.bkgColor=bkgColor;
	}
	
	public boolean getWriteMode() {
		return this.writeMode;
	}
	
	public void setWriteMode(boolean writeMode) {
		this.writeMode=writeMode;
	}
	
	public boolean getFillMode() {
		return this.fillMode;
	}
	
	public void setFillMode(boolean fillMode) {
		this.fillMode=fillMode;
	}
	
	public void setStartPos() {
		int from_x=Math.min(startX, stopX);
		int to_x=Math.max(startX, stopX);
		int from_y=Math.min(startY, stopY);
		int to_y=Math.max(startY, stopY);
		Graphics g2D=(Graphics2D)getGraphics();
		g2D.setXORMode(Color.white);
		if (selection)
			g2D.draw3DRect(from_x*8+x_insets-2, from_y*8+y_insets-2, (to_x-from_x+1)*8+2, (to_y-from_y+1)*8+2, true);
		startX=lastX;
		startY=lastY;
		from_x=Math.min(startX, stopX);
		to_x=Math.max(startX, stopX);
		from_y=Math.min(startY, stopY);
		to_y=Math.max(startY, stopY);
		g2D.draw3DRect(from_x*8+x_insets-2, from_y*8+y_insets-2, (to_x-from_x+1)*8+2, (to_y-from_y+1)*8+2, true);
		selection=true;
	}
	
	public void setStopPos() {
		int from_x=Math.min(startX, stopX);
		int to_x=Math.max(startX, stopX);
		int from_y=Math.min(startY, stopY);
		int to_y=Math.max(startY, stopY);
		Graphics g2D=(Graphics2D)getGraphics();
		g2D.setXORMode(Color.white);
		if (selection)
			g2D.draw3DRect(from_x*8+x_insets-2, from_y*8+y_insets-2, (to_x-from_x+1)*8+2, (to_y-from_y+1)*8+2, true);
		stopX=lastX;
		stopY=lastY;
		from_x=Math.min(startX, stopX);
		to_x=Math.max(startX, stopX);
		from_y=Math.min(startY, stopY);
		to_y=Math.max(startY, stopY);
		g2D.draw3DRect(from_x*8+x_insets-2, from_y*8+y_insets-2, (to_x-from_x+1)*8+2, (to_y-from_y+1)*8+2, true);
		selection=true;
	}
	
	public void drawLine() {
		float line_w=Math.abs(stopX-startX);
		float line_h=Math.abs(stopY-startY);
		setGrid(startX,startY,currentColor);
		setGrid(stopX,stopY,currentColor);
		if (line_w>=line_h) {
			float currentY=startY;
			float stepY=(startY>stopY ? -line_h/line_w : line_h/line_w);
	        if (startX>stopX) {
	        	for (int x=startX-1;x>stopX;x--) {
					currentY+=stepY;
					setGrid(x,Math.round(currentY),currentColor);	
				}	
	        } else {
	        	for (int x=startX+1;x<stopX;x++) {
					currentY+=stepY;
					setGrid(x,Math.round(currentY),currentColor);	
				}
	        }
		} else {
			float currentX=startX;
			float stepX=(startX>stopX ? -line_w/line_h : line_w/line_h);
	        if (startY>stopY) {
	        	for (int y=startY-1;y>stopY;y--) {
					currentX+=stepX;
					setGrid(Math.round(currentX),y,currentColor);	
				}	
	        } else {
	        	for (int y=startY+1;y<stopY;y++) {
					currentX+=stepX;
					setGrid(Math.round(currentX),y,currentColor);	
				}
	        }
		}
	}
	
	public void drawRect() {
		int saveStartX=startX;
		int saveStartY=startY;
		int saveStopX=stopX;
		int saveStopY=stopY;
		if (!getFillMode()) {
			stopY=startY;
			drawLine();
			stopX=startX;
			stopY=saveStopY;
			drawLine();
			startX=saveStopX;
			stopX=startX;
			drawLine();
			startX=saveStartX;
			startY=stopY;
			stopX=saveStopX;
			drawLine();	
		} else {
			int from_x=Math.min(startX, stopX);
			int to_x=Math.max(startX, stopX);
			int from_y=Math.min(startY, stopY);
			int to_y=Math.max(startY, stopY);
			startX=from_x;
			stopX=to_x;
			for (int y=from_y;y<=to_y;y++) {
				startY=y;
				stopY=y;
				drawLine();
			}
		}
		startX=saveStartX;
		startY=saveStartY;
		stopX=saveStopX;
		stopY=saveStopY;
	}
	
	public void drawOvalBorder(double centerX, double centerY, double radiusX, double radiusY) {
		int x,y;
		for (double angle=0;angle<=360;angle++) {
			x=(int)Math.round(centerX+radiusX*Math.cos(angle*Math.PI/180));
			y=(int)Math.round(centerY+radiusY*Math.sin(angle*Math.PI/180));
				if ((x>=0) && (x<=(w-1)) && (y>=0) && (y<=(w-1)))
					setGrid(x,y,currentColor);
		}
	}
	
	public void drawOval() {
		double from_x=Math.min(startX, stopX);
		double to_x=Math.max(startX, stopX);
		double from_y=Math.min(startY, stopY);
		double to_y=Math.max(startY, stopY);
		double centerX=from_x+(to_x-from_x)/2;
		double centerY=from_y+(to_y-from_y)/2;
		double radiusX=(to_x-from_x)/2;
		double radiusY=(to_y-from_y)/2;
		drawOvalBorder(centerX,centerY,radiusX,radiusY);
		if (getFillMode()) {
			if (radiusX>radiusY) {
				for (double rX=0;rX<=radiusX;rX++) 
					drawOvalBorder(centerX,centerY,rX,radiusY);
			} else {		
				for (double rY=0;rY<=radiusY;rY++)
					drawOvalBorder(centerX,centerY,radiusX,rY);
			}
		}
	}
	
	public void moveArea() {
		int from_x=Math.min(startX, stopX);
		int to_x=Math.max(startX, stopX);
		int from_y=Math.min(startY, stopY);
		int to_y=Math.max(startY, stopY);
		int lenCopy=to_x-from_x+1;
		if ((lastX+lenCopy)>64)
			lenCopy=64-lastX;
		for(int i=from_y;(i<=to_y) && ((lastY+(i-from_y))<64);i++) {
			System.arraycopy(gridPicture[i],from_x,gridPicture[lastY+(i-from_y)],lastX,lenCopy);
		}
		repaint();
	}
	
	public void saveUndo() {
		for(int i=0;i<64;i++) 
			System.arraycopy(gridPicture[i],0,undoGridPicture[i],0,64);
	}
	
	public static void main(String[] args) {
		JPicture mainWindow=new JPicture();
	}
}