import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class ColorPanel extends JPanel implements ActionListener {
	Color colorBrown=new Color(128,64,0);
	Color colorLightYellow=new Color(255,255,128);
	Color colorDarkYellow=new Color(255,255,0);
	Color colorDarkGreen=new Color(0,128,0);
	Color[] colorList={Color.BLACK,Color.WHITE,Color.BLUE,Color.CYAN,Color.DARK_GRAY,Color.GRAY,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,
			Color.PINK,Color.RED,colorDarkYellow,colorBrown,colorLightYellow,colorDarkGreen};
	JRadioButton[] colorRadioButtons;
    ButtonGroup colorRadioGroup;
    JPicture container;
    JButton clearButton, setBkgButton,startButton,stopButton;
    JButton lineButton,rectButton,ovalButton,moveButton,fillModeButton, writeModeButton;
	public ColorPanel(JPicture container) {
		setLayout(null);
		setSize(new Dimension(208,512));
		this.container=container;
		container.setCurrentColor(Color.BLACK);
	    container.setBkgColor(Color.WHITE);
	    colorRadioButtons=new JRadioButton[colorList.length];
		colorRadioGroup=new ButtonGroup();
	    for(int i=0;i<colorList.length;i++) {
			colorRadioButtons[i]=new JRadioButton();
			colorRadioButtons[i].setActionCommand(Integer.toString(i));
			colorRadioGroup.add(colorRadioButtons[i]);
			colorRadioButtons[i].setBounds(0,16*i,32,16);
			colorRadioButtons[i].setVisible(true);
			add(colorRadioButtons[i]);
			colorRadioButtons[i].setVisible(true);
			colorRadioButtons[i].addActionListener(this);
		}
	    colorRadioButtons[0].setSelected(true);
	    clearButton=new JButton("Clear");
	    clearButton.setBounds(8, 272, 80, 32);
	    add(clearButton);
	    clearButton.setVisible(true);
	    clearButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		int opt=JOptionPane.showConfirmDialog(container,"Are you sure?","Clear Grid",JOptionPane.OK_CANCEL_OPTION );
	    		if (opt!=0) return;
	    		container.saveUndo();
	    		container.setBkgColor(container.getCurrentColor());
	    		container.clearGrid();
	    	}
	    });
	    setBkgButton=new JButton("Bgk");
	    setBkgButton.setBounds(104, 272, 80, 32);
	    add(setBkgButton);
	    setBkgButton.setVisible(true);
	    setBkgButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		int opt=JOptionPane.showConfirmDialog(container,"Are you sure?","Set Bkg Color",JOptionPane.OK_CANCEL_OPTION );
	    		if (opt!=0) return;
	    		container.saveUndo();
	    		Color currentBkgColor=container.getBkgColor();
	    		Color newBkgColor=container.getCurrentColor();
	    		int w=container.getW();
	    		int h=container.getH();
	    		for(int y=0;y<h;y++) {
	    			for(int x=0;x<w;x++) {
	    				Color currentPixelColor=container.getGrid(x, y);
	    				if (currentPixelColor.equals(currentBkgColor)) {
	    					container.setGrid(x, y, newBkgColor);
	    				}
	    			}
	    		}
	    		container.setBkgColor(newBkgColor);
	    	}
	    });
	    startButton=new JButton("Start");
	    startButton.setBounds(8, 320, 80, 32);
	    add(startButton);
	    startButton.setVisible(true);
	    startButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.setStartPos();
	    	}
	    });
	    stopButton=new JButton("Stop");
	    stopButton.setBounds(104, 320, 80, 32);
	    add(stopButton);
	    stopButton.setVisible(true);
	    stopButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.setStopPos();
	    	}
	    });
	    lineButton=new JButton("Line");
	    lineButton.setBounds(8, 368, 80, 32);
	    add(lineButton);
	    lineButton.setVisible(true);
	    lineButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.saveUndo();
	    		container.drawLine();
	    	}
	    });
	    rectButton=new JButton("Rect");
	    rectButton.setBounds(104, 368, 80, 32);
	    add(rectButton);
	    rectButton.setVisible(true);
	    rectButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.saveUndo();
	    		container.drawRect();
	    	}
	    });
	    ovalButton=new JButton("Oval");
	    ovalButton.setBounds(8, 416, 80, 32);
	    add(ovalButton);
	    ovalButton.setVisible(true);
	    ovalButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.saveUndo();
	    		container.drawOval();
	    	}
	    });
	    moveButton=new JButton("Move");
	    moveButton.setBounds(104, 416, 80, 32);
	    add(moveButton);
	    moveButton.setVisible(true);
	    moveButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		container.saveUndo();
	    		container.moveArea();
	    	}
	    });
	    fillModeButton=new JButton("Border");
	    fillModeButton.setBounds(8, 464, 80, 32);
	    add(fillModeButton);
	    fillModeButton.setVisible(true);
	    fillModeButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (container.getFillMode()==true) {
	    			container.setFillMode(false);
	    			fillModeButton.setText("Border");	
	    		} else {
	    			container.setFillMode(true);
	    			fillModeButton.setText("Fill");
	    		}
	    	}
	    });
	    writeModeButton=new JButton("PenUp");
	    writeModeButton.setBounds(104, 464, 80, 32);
	    add(writeModeButton);
	    writeModeButton.setVisible(true);
	    writeModeButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (container.getWriteMode()==true) {
	    			container.setWriteMode(false);
	    			writeModeButton.setText("PenUp");	
	    		} else {
	    			container.setWriteMode(true);
	    			writeModeButton.setText("Write");
	    		}
	    	}
	    });
	}
	public void actionPerformed(ActionEvent e) {
		int colorButtonNum=Integer.parseInt(e.getActionCommand());
		if ((colorButtonNum<0) || (colorButtonNum>=colorList.length))
			return;
		container.setCurrentColor(colorList[colorButtonNum]);
	}
	public int mapColor(Color c) {
		int colorCode=0;
		if (c==Color.BLACK)
			colorCode=0;
		else if (c==Color.WHITE)
			colorCode=1;
		else if (c==Color.BLUE)
			colorCode=2;
		else if (c==Color.CYAN)
			colorCode=3;
		else if (c==Color.DARK_GRAY)
			colorCode=4;
		else if (c==Color.GRAY)
			colorCode=5;
		else if (c==Color.GREEN)
			colorCode=6;
		else if (c==Color.LIGHT_GRAY)
			colorCode=7;
		else if (c==Color.MAGENTA)
			colorCode=8;
		else if (c==Color.ORANGE)
			colorCode=9;
		else if (c==Color.PINK)
			colorCode=10;
		else if (c==Color.RED)
			colorCode=11;
		else if (c==colorDarkYellow)
			colorCode=12;
		else if (c==colorBrown)
			colorCode=13;
		else if (c==colorLightYellow)
			colorCode=14;
		else if (c==colorDarkGreen)
			colorCode=15;
		return colorCode;
	}
	public Color getColorFromCode(int colorCode) {
		return colorList[colorCode];
	}
	public void paint(Graphics g) {
		Graphics2D g2=(Graphics2D) g;
		g2.setColor(Color.CYAN);
		g2.fillRect(0, 0, 208, 512);
		for(int i=0;i<colorList.length;i++) {
			g2.setColor(colorList[i]);
			g2.fillRect(32, 16*i, 176, 16);
		}
		for(int i=0;i<colorList.length;i++) {
			colorRadioButtons[i].repaint();
		}
		clearButton.repaint();
		setBkgButton.repaint();
		startButton.repaint();
		stopButton.repaint();
		lineButton.repaint();
		rectButton.repaint();
		ovalButton.repaint();
		moveButton.repaint();
		writeModeButton.repaint();
		fillModeButton.repaint();
	}
}
