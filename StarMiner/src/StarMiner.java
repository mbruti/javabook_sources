import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
public class StarMiner extends JFrame implements ActionListener { 
	Game game;
	Game.GameScene currentGameScene;
	HashMap<String,Image> imageMap;
	String localDir;
	int w,h;
	SceneInterface currentScene;
	Font font;
	Timer timer;
	StarMiner() {
		localDir=System.getProperty("user.dir");
		w=512;
		h=384;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		imageMap=Helper.loadImages();
		font=Helper.loadFont("kongtext.ttf",16f);
		setFont(font);
		setLayout(new FlowLayout());
		setBackground(Color.BLACK);
		setLocationRelativeTo(null);
		game=new Game();
		game.setGameScene(Game.GameScene.Intro);
		currentGameScene=null;
		currentScene=null;
		setVisible(true);
		timer=new Timer(17,this);
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev){
	    if(ev.getSource()==timer){
	    	if (currentGameScene!=game.getGameScene()) {
	    		if (currentScene==null) {
	    			currentScene=new IntroScene(this,game);
	    			currentScene.createScene();
	    			currentGameScene=Game.GameScene.Intro;
	    		} else {
	    			currentScene.destroyScene();
	    			currentScene=null;
	    			currentGameScene=game.getGameScene();
	    			switch(currentGameScene) {
	    			case Intro: 
	    				currentScene=new IntroScene(this,game);
	    				currentScene.createScene();
	    				game.setGameScene(Game.GameScene.Intro);
	    				break;
	    			case Play:
	    				currentScene=new PlayScene(this,game);
    					currentScene.createScene();
    					game.setGameScene(Game.GameScene.Play);
    					break;
	    			case Over:
	    				System.exit(0);
	    			}
	    		} 
	    	}
	    	currentScene.playScene();
	    	currentScene.drawScene();
	    }
	}
	
	public static void main(String[] args) {
		StarMiner starMiner=new StarMiner();
	}
	
	public HashMap<String,Image> getImagesHashMap() {
		return imageMap;
	}
	
	public Image getImage(String imageName) {
		return imageMap.get(imageName);
	}

	public Font getFont() {
		return font;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
}
