package game.SY.model;


public enum Colour {
	BLACK, BLUE, GREEN, RED, WHITE, YELLOW;


	public boolean isMrX() {
		return this == BLACK;
	}


	public boolean isDetective() {
		return !isMrX();
	}

}
