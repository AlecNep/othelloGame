public class OthelloPlayer {
	public boolean isHuman; // true if human controlled, false if AI
	public char color; // 'B' for black, 'W' for white 
	
	public OthelloPlayer(boolean human, char color) {
		this.isHuman = human;
		this.color = color;
	}
	
	public OthelloPlayer(OthelloPlayer player) {
		this.isHuman = player.isHuman;
		this.color = player.color;
	}
}