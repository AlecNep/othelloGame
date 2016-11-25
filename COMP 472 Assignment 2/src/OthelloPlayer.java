public class OthelloPlayer {
	public boolean isHuman; // true if human controlled, false if AI
	public char color; // 'B' for black, 'W' for white 
	public int heuristic; // 0 for none, 1 for greedy, 2 for michael's, 3 for alec's 
	
	public OthelloPlayer(boolean human, char color) {
		this.isHuman = human;
		this.color = color;
		this.heuristic = 0;
	}
	
	public OthelloPlayer(boolean human, char color, int heuristic) {
		this.isHuman = human;
		this.color = color;
		this.heuristic = heuristic;
	}
	
	public OthelloPlayer(OthelloPlayer player) {
		this.isHuman = player.isHuman;
		this.color = player.color;
		this.heuristic = player.heuristic;
	}
}