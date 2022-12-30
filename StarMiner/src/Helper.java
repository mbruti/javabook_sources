import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Scanner;

public class Helper {
    static HashMap<String,Image> loadImages() {
    	HashMap<String,Image> imageMap=new HashMap<String,Image>();
    	String localDir = System.getProperty("user.dir");
    	try {
    		Scanner imageScanner=new Scanner(new File(localDir+"/res/images.res"));
    		while (imageScanner.hasNextLine()) {
    			String nextImageFileName=imageScanner.nextLine();
    			Image im=ImageIO.read(new File(localDir+"/res/"+nextImageFileName));
    			String[] s=nextImageFileName.split("[.]");
    			imageMap.put(s[0], im);
    		}
    	} catch (Exception e) { System.out.println("Cannot load images!"); }
    	return imageMap;
    }
    
    static Font loadFont(String fontFile, float fontSize) {
    	String localDir = System.getProperty("user.dir");
    	try {
    		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    		Font fnt1=Font.createFont(Font.TRUETYPE_FONT, new File(localDir+"/res/"+fontFile));
    		Font font=fnt1.deriveFont(fontSize);
    		ge.registerFont(font);
    		return font;
    	} catch (Exception e) {
    		System.out.println("Cannot load font!");
    		return null;
    	}
    }
    
    public static void pause(long r) {
    	Date d=new Date();
    	long mil=d.getTime();
    	long save=mil;
    	while (mil<save+r) {
    		d=new Date();
    		mil=d.getTime();
    	}
    }
    
    public static void assignKeyActionBinding(JComponent comp, String key,Action action) {
    	comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
    	comp.getActionMap().put(key, action);
    }
    
    public static int randDir() {
    	if (Math.random()<0.5) 
    		return -1;
    	else return 1;
    }
    
    public static StringBuffer centerString(String s,int len) {
    	StringBuffer sb=new StringBuffer(len);
    	sb.append(" ".repeat((len-s.length()-1)/2)).append(s);
    	return sb;
    }
    
    public static void playSound(String soundName) {
    	Clip clip;
    	AudioInputStream audioStream;
    	String localDir = System.getProperty("user.dir");
		try {
			audioStream = AudioSystem.getAudioInputStream(new File(localDir+"/res/"+soundName+".wav")); 
			clip = AudioSystem.getClip(); 
			clip.open(audioStream); 
			clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	    }
	}
    
	public Helper() {
	}
}