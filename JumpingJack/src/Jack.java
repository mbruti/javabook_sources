public class Jack {
	public enum Frame {
		Quiet(0),Jump1(1),Jump2(2);
		private final int value;
		private Frame(int value) {
			this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
	public enum Phase {
		Quiet(0),Jumping(1);
		private final int value;
		private Phase(int value) {
			this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
	Frame jackFrame;
	Phase jackPhase;
	private int x,y;
	private int nPlatform=0;
	private int speed;
	private int jumpStep=0;
	private int jumpDir=-1;
	public void setXY(int x, int y) {
		this.x=x;
		this.y=y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getSpeed() {
		return speed;
	}
	public int getNPlatform() {
		return nPlatform;
	}
	public void setNPlatform(int nPlatform) {
		this.nPlatform=nPlatform;
	}
	public void setSpeed(int speed) {
		this.speed=speed;
	}
	public int getJumpStep() {
		return jumpStep;
	}
	public int getJumpDir() {
		return jumpDir;
	}
	public void setJumpStep(int jumpStep) {
		this.jumpStep=jumpStep;
	}
	public void setJumpDir(int jumpDir) {
		this.jumpDir=jumpDir;
	}
}
