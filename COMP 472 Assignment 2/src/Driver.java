import java.util.ArrayList;
import java.util.PriorityQueue;

public class Driver {
	public static void main(String[] args) {
		// initialize the two players
		// public OthelloPlayer(boolean human, char color)
		// human player
		OthelloPlayer player1 = new OthelloPlayer(false, 'B');
		// AI player 
		OthelloPlayer player2 = new OthelloPlayer(true, 'W');
		
		OthelloGameBoard b = new OthelloGameBoard(player1, player2);
		b.printBoard();
		System.out.println();
		
		// keeps track of whose turn it is 
		char turn = 'B';
		
		while (!b.gameOver()) {
			char opponent;
			if (turn == 'B') {
				System.out.println("Black's turn ");
				opponent = 'W';
			}
			else {
				System.out.println("White's turn ");
				opponent = 'B';
			}
			
			OthelloPlayer player = b.getPlayer(turn);
			
			PriorityQueue<OthelloGameBoard> moves = b.greedyGenerateAllPossibleMoves(turn);
			
			if (!moves.isEmpty()) {
				if (player.isHuman) {
					// ask for input and play the move 
					b = new OthelloGameBoard(b.humanPlayerTurn(turn));					
				}
				else {
					//b = new OthelloGameBoard(moves.poll());
					b = new OthelloGameBoard(b.miniMax(b, 5, turn));
				}
				b.printBoard();
				System.out.println("Number of reversals: " + b.numberOfReveralsHeuristic);
				System.out.println(turn + " tile placed at: (" + (b.tileXPlayed + 1) + ", " + (b.tileYPlayed + 1) + ")");
				System.out.println();	
			}
			else {
				System.out.println(turn + " has no available moves. Skipping turn.");
			}
			
			// change turn to other player 
			if (turn == 'B') {
				turn = 'W';
			}
			else {
				turn = 'B';
			}			
		}
		System.out.println("Game over!");
		/*
		if (countWhite > countBlack) {
			System.out.println("White wins!");
			System.out.println("White count: " + countWhite);
			System.out.println("Black count: " + countBlack);
		}
		else if (countWhite < countBlack) {
			System.out.println("Black wins!");
			System.out.println("White count: " + countWhite);
			System.out.println("Black count: " + countBlack);
		}
		else {
			System.out.println("Tie!");
			System.out.println("White count: " + countWhite);
			System.out.println("Black count: " + countBlack);
		}
		*/
	}
}