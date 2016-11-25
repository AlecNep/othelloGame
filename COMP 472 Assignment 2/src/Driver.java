import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		// initialize the two players
		Scanner kb = new Scanner(System.in);
		int choice = 0;
		OthelloPlayer player1;
		OthelloPlayer player2;
		
		System.out.println("Black player: Human or AI?");		
		System.out.println("Enter 1 for Human");
		System.out.println("Enter 2 for AI");
		choice = kb.nextInt();
		
		if (choice == 2) {
			System.out.println("What heuristic do you want to use? ");
			System.out.println("Enter 1 for greedy algorithm ");
			System.out.println("Enter 2 for Michael's heuristic ");
			System.out.println("Enter 3 for Alec's heuristic ");	
			choice = kb.nextInt();
			player1 = new OthelloPlayer(false, 'B', choice);
		}
		else {
			player1 = new OthelloPlayer(true, 'B');
		}
		
		System.out.println("White player: Human or AI?");		
		System.out.println("Enter 1 for Human");
		System.out.println("Enter 2 for AI");
		choice = kb.nextInt();
		
		if (choice == 2) {
			System.out.println("What heuristic do you want to use? ");
			System.out.println("Enter 1 for greedy algorithm ");
			System.out.println("Enter 2 for Michael's heuristic ");
			System.out.println("Enter 3 for Alec's heuristic ");	
			choice = kb.nextInt();
			player2 = new OthelloPlayer(false, 'W', choice);
		}
		else {
			player2 = new OthelloPlayer(true, 'W');
		}
		
		OthelloGameBoard b = new OthelloGameBoard(player1, player2);
		b.printBoard();
		System.out.println("Count white: "  + b.countWhite);
		System.out.println("Count black: "  + b.countBlack);
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
					if (player.heuristic == 1) { // greedy algorithm
						System.out.println("Greedy Algorithm");
						b = new OthelloGameBoard(moves.poll());
					}
					else {
						long lStartTime = System.currentTimeMillis();
						b = new OthelloGameBoard(b.miniMax(b, 4, turn, player.heuristic));
				        long lEndTime = System.currentTimeMillis();
				        long output = lEndTime - lStartTime;
				        if (output < 1000) {
					        System.out.println("Elapsed time in milliseconds (minimax): " + output);
				        }
				        else {
				        	double outDbl = (double) output;			        	
					        System.out.println("Elapsed time in seconds (minimax): " + (outDbl / 1000));
				        }
						b.heuristic(turn, turn, player.heuristic);
						System.out.println("Heuristic: " + b.heuristic);
					}
				}
				b.printBoard();
				//System.out.println("Heuristic: " + b.heuristic);
				System.out.println("Count white: "  + b.countWhite);
				System.out.println("Count black: "  + b.countBlack);
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

	private static Scanner Scanner(InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}
}