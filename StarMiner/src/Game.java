public class Game {
	public enum GameScene {
		Intro(0),Play(1),Over(2);
		private int value;
		private GameScene(int value) {
		this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
    private GameScene gameScene;
    int startCrew=4;
    int crew=startCrew;
    int score=0;
    int hi=0;
    int level=1;

	public Game() {
		gameScene=GameScene.Intro;
	}
	
	public GameScene getGameScene() {
		return gameScene;
	}
	
	public void setGameScene(GameScene gameScene) {
		this.gameScene=gameScene;
	}
	
	public void resetCrew() {
		crew=startCrew;
	}
	
	public int getCrew() {
		return crew;
	}
	
	public void setCrew(int crew) {
		this.crew=crew;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score=score;
	}
	
	public int getHi() {
		return hi;
	}
	
	public void setHi(int hi) {
		this.hi=hi;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level=level;
	}
}
