public enum GamePhase {
		Play(1),Over(2);
		private final int value;
		private GamePhase(int value) {
			this.value=value;
		}
		public int getValue() {
			return value;
		}
	};
