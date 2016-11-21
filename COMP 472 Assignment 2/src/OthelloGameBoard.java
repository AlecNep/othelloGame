import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class OthelloGameBoard {
	public char[][] board;
	public int numberOfReveralsHeuristic;
	public int tileXPlayed;
	public int tileYPlayed;
	public OthelloPlayer player1;
	public OthelloPlayer player2;
	private Scanner kb;
	
	public OthelloGameBoard(OthelloPlayer player1, OthelloPlayer player2) {
		board = new char[8][8];
		// initialze the empty board 
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = '0';
			}
		}
		// initialize the middle tiles 
		board[3][3] = 'W';
		board[3][4] = 'B';
		board[4][3] = 'B';
		board[4][4] = 'W';
		// initial state has 0 reversals 
		numberOfReveralsHeuristic = 0;
		this.player1 = new OthelloPlayer(player1);
		this.player2 = new OthelloPlayer(player2);
	}
	
	public OthelloGameBoard(OthelloGameBoard b) {
		board = new char[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = b.board[i][j];
			}
		}
		numberOfReveralsHeuristic = b.numberOfReveralsHeuristic;
		player1 = new OthelloPlayer(b.player1);
		player2 = new OthelloPlayer(b.player2);
		tileXPlayed = b.tileXPlayed;
		tileYPlayed = b.tileYPlayed;
	}
	
	public OthelloPlayer getPlayer(char turn) {
		if (player1.color == turn) {
			return player1;
		}
		else { 
			return player2;
		}
	}
	
	public boolean gameOver() {
		// check if both players can make a move 
		PriorityQueue<OthelloGameBoard> blackMoves = greedyGenerateAllPossibleMoves('B');
		PriorityQueue<OthelloGameBoard> whiteMoves = greedyGenerateAllPossibleMoves('W');
		
		if (blackMoves.size() == 0 && whiteMoves.size() == 0) {
			// no possible moves left - count number of tiles and declare the winner
			int countWhite = 0;
			int countBlack = 0;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board[i][j] == 'W') {
						countWhite++;
					}
					else if (board[i][j] == 'B') {
						countBlack++;
					}
				}
			}
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
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasAvailableMoves(char turn) {
		PriorityQueue<OthelloGameBoard> moves = greedyGenerateAllPossibleMoves(turn);
		
		return (moves.size() != 0);
	}
	
	public void printBoard() {
		System.out.println("  1 2 3 4 5 6 7 8");
		for (int i = 0; i < 8; i++) {
			System.out.print((i + 1) + " ");
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != '0') {
					System.out.print(board[i][j] + " ");
				}
				else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
		//System.out.println();
	}
	
	public static boolean checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol) {
		// method that checks whether there is the specified type of tile
		// OR a series of opponent's tiles followed by the specified tile 
		// in a specified direction from pos x y 
		
		// check the next x y position of the board depending on the direction you are searching in 
		row += dirRow;
		col += dirCol;
		
		// if the tile is the target tile, return true 
		if (b.board[row][col] == targetTile) {
			return true;
		}
		
		// if tile belongs to your opponent, and you are not in the first row, first col, last row, last col  
		// recursively check the next tile that is in the specified direction
		else if (b.board[row][col] == opponent && row != 0 && col != 0 && row != 7 && col != 7) {
			return checkForTile(b, targetTile, opponent, row, col, dirRow, dirCol);
		}
		
		// otherwise, the tile is either not found, or you are in the first row/first col/last row/last col
		else {
			return false;
		}
	}
	
	public OthelloGameBoard placePlayerTile(char player, int row, int col, int numberOfReveralsHeuristic) {
		char opponent;
		if (player == 'B') {
			opponent = 'W';
		}
		else {
			opponent = 'B';
		}		
		
		// copy the current state of the board to a new object 
		OthelloGameBoard newState = new OthelloGameBoard(this);
		newState.numberOfReveralsHeuristic = numberOfReveralsHeuristic;
		boolean playerTileFound;
		int dirRow;
		int dirCol;
		newState.board[row][col] = player; 
		//newState.numberOfReveralsHeuristic++;
		newState.tileXPlayed = row;
		newState.tileYPlayed = col;
				
		// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
		// check up left
		// dont need to check if you are either in the first row or first col 
		if (row != 0 && col != 0) {
			if (checkForTile(newState, player, opponent, row, col, -1, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check up 
		// dont check if you are in first row 
		if (row != 0) {
			if (checkForTile(newState, player, opponent, row, col, -1, 0)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = 0;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check up right
		// dont check if you are in first row or last col 
		if (row != 0 && col != 7) {
			if (checkForTile(newState, player, opponent, row, col, -1, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check right
		// dont check if you are in last col
		if (col != 7) {
			if (checkForTile(newState, player, opponent, row, col, 0, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 0;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down right 
		// dont check if you are in last row or last col 
		if (row != 7 && col != 7) {
			if (checkForTile(newState, player, opponent, row, col, 1, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down 
		// dont check if you are in last row 
		if (row != 7) {
			if (checkForTile(newState, player, opponent, row, col, 1, 0)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = 0;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down left 
		// dont check if you are in last row or first col 
		if (row != 7 && col != 0) {
			if (checkForTile(newState, player, opponent, row, col, 1, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check left 
		// dont check if you are in first col 
		if (col != 0) {
			if (checkForTile(newState, player, opponent, row, col, 0, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 0;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// print number of reversals (for debugging purposes)
		//System.out.println("Number of Reversals: " + newState.numberOfReveralsHeuristic);
		// return this new state 
		return newState;
	}
	
	public static OthelloGameBoard generateState(OthelloGameBoard b, char player, int row, int col, int dirRow, int dirCol) {
		
		char opponent;
		if (player == 'B') {
			opponent = 'W';
		}
		else {
			opponent = 'B';
		}
		
		// copy the current state of the board to a new object 
		OthelloGameBoard newState = new OthelloGameBoard(b);
		newState.numberOfReveralsHeuristic = 0;
		
		// search for the blank tile in the specified direction 
		boolean blankFound = false;
		while (!blankFound) {
			// check the next x y position of the board depending on the direction you are searching in 
			row += dirRow;
			col += dirCol;
			
			if (newState.board[row][col] == '0') {
				blankFound = true;
				newState.board[row][col] = player;
				newState.tileXPlayed = row;
				newState.tileYPlayed = col;
			}
		}
		
		// next, search in the opposite direction for your tile, flipping all of your opponents tiles in the process 
		boolean playerTileFound = false;
		dirRow *= -1;
		dirCol *= -1;
		while (!playerTileFound){
			row += dirRow;
			col += dirCol;
			
			if (newState.board[row][col] == player) {
				playerTileFound = true;
			}
			else {
				newState.board[row][col] = player;
				newState.numberOfReveralsHeuristic++;
			}
		}
		
		// return this new state 
		//return newState.placePlayerTile(player, newState.tileXPlayed, newState.tileYPlayed, newState.numberOfReveralsHeuristic);
		// next, starting from the tile you played (tileXPlayed, tileYPlayed), check the other directions for 1 or more of the opponents tiles 
		// followed by a player's tile and reverse them too 
		row = newState.tileXPlayed;
		col = newState.tileYPlayed;
		
		// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
		// check up left
		// dont need to check if you are either in the first row or first col 
		if (row != 0 && col != 0) {
			if (checkForTile(newState, player, opponent, row, col, -1, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check up 
		// dont check if you are in first row 
		if (row != 0) {
			if (checkForTile(newState, player, opponent, row, col, -1, 0)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = 0;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check up right
		// dont check if you are in first row or last col 
		if (row != 0 && col != 7) {
			if (checkForTile(newState, player, opponent, row, col, -1, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = -1;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check right
		// dont check if you are in last col
		if (col != 7) {
			if (checkForTile(newState, player, opponent, row, col, 0, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 0;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down right 
		// dont check if you are in last row or last col 
		if (row != 7 && col != 7) {
			if (checkForTile(newState, player, opponent, row, col, 1, 1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = 1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down 
		// dont check if you are in last row 
		if (row != 7) {
			if (checkForTile(newState, player, opponent, row, col, 1, 0)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = 0;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check down left 
		// dont check if you are in last row or first col 
		if (row != 7 && col != 0) {
			if (checkForTile(newState, player, opponent, row, col, 1, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 1;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// check left 
		// dont check if you are in first col 
		if (col != 0) {
			if (checkForTile(newState, player, opponent, row, col, 0, -1)) {
				// if you do find a player tile followed by 1 or more opponent tiles, flip them 
				playerTileFound = false;
				dirRow = 0;
				dirCol = -1;
				while (!playerTileFound) {
					row += dirRow;
					col += dirCol;
					
					if (newState.board[row][col] == player) {
						playerTileFound = true;
					}
					else {
						newState.board[row][col] = player;
						newState.numberOfReveralsHeuristic++;
					}
				}
				row = newState.tileXPlayed;
				col = newState.tileYPlayed;
			}
		}
		
		// print number of reversals (for debugging purposes)
		//System.out.println("Number of Reversals: " + newState.numberOfReveralsHeuristic);
		return newState;
	}
	
	public PriorityQueue<OthelloGameBoard> greedyGenerateAllPossibleMoves(char player) {
		
		//ArrayList<OthelloGameBoard> states  = new ArrayList<OthelloGameBoard>();
      	// comparator for priority queue 
      	Comparator<OthelloGameBoard> comparator = new ReversalsComparator();
      	// stores the open list for Best-First Search and A* as a priority queue
      	PriorityQueue<OthelloGameBoard> states = new PriorityQueue<OthelloGameBoard>(10, comparator);
		
		// black always has the first move
		// algorithm: scan the board sequentially for a white tile		
		// once you find the white tile, check if there is a black piece horizontally, vertically, or diagonally 
		// next to the white tile 
		// if the black tile is to the right of the white tile, place the new tile on to the left of the white tile
		// etc..
		
		// used to store the opponent's color 
		char opponent; 
		
		// if its black's turn, opponent is white 
		if (player == 'B') {
			opponent = 'W';
		}
		// otherwise, its white's turn; opponent is black 
		else {
			opponent = 'B';
		}
		
		rows: for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				// search the board for a tile with opponent's color on it 
				if (board[i][j] == opponent) {
					
					// check diagonal up-left for blank tile 
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, -1, -1)) {
						// check diagonal down-right for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 1, 1)) {
							// if found, player can play their color in diagonal up-left 
							// generateState(OthelloGameBoard b, char player, int row, int col, int dirRow, int dirCol)
							// generate this state and add it to the list of all possible moves 
							states.add(generateState(this, player, i, j, -1, -1));
						}
					}
					
					// check up for blank tile 
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, -1, 0)) {
						// check down for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 1, 0)) {
							states.add(generateState(this, player, i, j, -1, 0));
						}
					}
					
					// check up-right
					if (checkForTile(this, '0', opponent, i, j, -1, 1)) {
						// check down-left for player  color's tile
						if (checkForTile(this, player, opponent, i, j, 1, -1)) {
							states.add(generateState(this, player, i, j, -1, 1));
						}
					}
					
					// check right
					if (checkForTile(this, '0', opponent, i, j, 0, 1)) {
						// check left for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 0, -1)) {
							states.add(generateState(this, player, i, j, 0, 1));
						}
					}
					
					// check down-right
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, 1, 1)) {
						// check up-left for player color's tile
						if (checkForTile(this, player, opponent, i, j, -1, -1)) {
							states.add(generateState(this, player, i, j, 1, 1));
						}
					}
					
					// check down
					if (checkForTile(this, '0', opponent, i, j, 1, 0)) {
						// check up for player color's tile 
						if (checkForTile(this, player, opponent, i, j, -1, 0)) {
							states.add(generateState(this, player, i, j, 1, 0));
						}
					}
					
					// check down-left
					if (checkForTile(this, '0', opponent, i, j, 1, -1)) {
						if (checkForTile(this, player, opponent, i, j, -1, 1)) {
							states.add(generateState(this, player, i, j, 1, -1));
						}
					}
					
					// check left 
					if (checkForTile(this, '0', opponent, i, j, 0, -1)) {
						if (checkForTile(this, player, opponent, i, j, 0, 1)) {
							states.add(generateState(this, player, i, j, 0, -1));
						}
					}
				}
			}
		}
		return states;
	}
	
public ArrayList<OthelloGameBoard> generateAllPossibleMoves(char player) {
		
      	// stores the open list for Best-First Search and A* as a priority queue
        ArrayList<OthelloGameBoard> states = new ArrayList<OthelloGameBoard>();
		
		// black always has the first move
		// algorithm: scan the board sequentially for a white tile		
		// once you find the white tile, check if there is a black piece horizontally, vertically, or diagonally 
		// next to the white tile 
		// if the black tile is to the right of the white tile, place the new tile on to the left of the white tile
		// etc..
		
		// used to store the opponent's color 
		char opponent; 
		
		// if its black's turn, opponent is white 
		if (player == 'B') {
			opponent = 'W';
		}
		// otherwise, its white's turn; opponent is black 
		else {
			opponent = 'B';
		}
		
		rows: for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				// search the board for a tile with opponent's color on it 
				if (board[i][j] == opponent) {
					
					// check diagonal up-left for blank tile 
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, -1, -1)) {
						// check diagonal down-right for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 1, 1)) {
							// if found, player can play their color in diagonal up-left 
							// generateState(OthelloGameBoard b, char player, int row, int col, int dirRow, int dirCol)
							// generate this state and add it to the list of all possible moves 
							states.add(generateState(this, player, i, j, -1, -1));
						}
					}
					
					// check up for blank tile 
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, -1, 0)) {
						// check down for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 1, 0)) {
							states.add(generateState(this, player, i, j, -1, 0));
						}
					}
					
					// check up-right
					if (checkForTile(this, '0', opponent, i, j, -1, 1)) {
						// check down-left for player  color's tile
						if (checkForTile(this, player, opponent, i, j, 1, -1)) {
							states.add(generateState(this, player, i, j, -1, 1));
						}
					}
					
					// check right
					if (checkForTile(this, '0', opponent, i, j, 0, 1)) {
						// check left for player color's tile 
						if (checkForTile(this, player, opponent, i, j, 0, -1)) {
							states.add(generateState(this, player, i, j, 0, 1));
						}
					}
					
					// check down-right
					// checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol)
					if (checkForTile(this, '0', opponent, i, j, 1, 1)) {
						// check up-left for player color's tile
						if (checkForTile(this, player, opponent, i, j, -1, -1)) {
							states.add(generateState(this, player, i, j, 1, 1));
						}
					}
					
					// check down
					if (checkForTile(this, '0', opponent, i, j, 1, 0)) {
						// check up for player color's tile 
						if (checkForTile(this, player, opponent, i, j, -1, 0)) {
							states.add(generateState(this, player, i, j, 1, 0));
						}
					}
					
					// check down-left
					if (checkForTile(this, '0', opponent, i, j, 1, -1)) {
						if (checkForTile(this, player, opponent, i, j, -1, 1)) {
							states.add(generateState(this, player, i, j, 1, -1));
						}
					}
					
					// check left 
					if (checkForTile(this, '0', opponent, i, j, 0, -1)) {
						if (checkForTile(this, player, opponent, i, j, 0, 1)) {
							states.add(generateState(this, player, i, j, 0, -1));
						}
					}
				}
			}
		}
		return states;
	}	

	/*public OthelloGameBoard miniMax(ArrayList<OthelloGameBoard> curMoves){
		boolean max = true; //min if false
		OthelloGameBoard bestMove;
		for(int i=0; i<curMoves.size(); i++){
			if(max){
				int val = (int)Double.NEGATIVE_INFINITY;
				
			}
			else{
				int val = (int)Double.POSITIVE_INFINITY;
				
			}
		}
		return bestMove;
	}*/

	public int miniMax(OthelloGameBoard curBoard, int maxDepth, char startingPlayer){
		return miniMax(curBoard, maxDepth, startingPlayer, startingPlayer);
		//return miniMax(curBoard, startingPlayer, startingPlayer);
	}

	private int miniMax(OthelloGameBoard curBoard, int depth, char original, char current){
		if (curBoard.gameOver() || depth == 0) {
			return curBoard.heuristic();
		}
		else {
			ArrayList<OthelloGameBoard> nextMoves = curBoard.generateAllPossibleMoves(current);

			if(original == current){ //maximizing
				int val = (int)Double.NEGATIVE_INFINITY;
				for(int i=0; i<nextMoves.size(); i++){
					val = Math.max(val, miniMax(nextMoves.get(i), depth-1, original, oppositePlayer(current)));
				}
				return val;
			}
			else{ // minimizing
				int val = (int)Double.POSITIVE_INFINITY;
				for(int i=0; i<nextMoves.size(); i++){
					val = Math.min(val, miniMax(nextMoves.get(i), depth-1, original, oppositePlayer(current)));
				}
				return val;
			}
		}
	}
	
	/*private int miniMax(OthelloGameBoard curBoard, char original, char current){
		if (curBoard.gameOver()) {
			return curBoard.heuristic();
		}
		else {
			ArrayList<OthelloGameBoard> nextMoves = curBoard.generateAllPossibleMoves(current);

			if(original == current){ //maximizing
				int val = (int)Double.NEGATIVE_INFINITY;
				for(int i=0; i<nextMoves.size(); i++){
					val = Math.max(val, miniMax(nextMoves.get(i), original, oppositePlayer(current)));
				}
				return val;
			}
			else{ // minimizing
				int val = (int)Double.POSITIVE_INFINITY;
				for(int i=0; i<nextMoves.size(); i++){
					val = Math.min(val, miniMax(nextMoves.get(i), original, oppositePlayer(current)));
				}
				return val;
			}
		}
	}*/
	
	public char oppositePlayer(char player) {
		char opponent;
		// if its black's turn, opponent is white 
		if (player == 'B') {
			opponent = 'W';
		}
		// otherwise, its white's turn; opponent is black 
		else {
			opponent = 'B';
		}
		return opponent;
	}

	public int heuristic(){
		return 0;
	}
	
	public OthelloGameBoard humanPlayerTurn(char turn) {
		kb = new Scanner(System.in);
		boolean validMove = false;
		int row = 0;
		int col = 0;
		
		do {
			row = 0;
			col = 0;
			// get input in range 1-8 for row and col 
			while(row <= 0 || row > 8) {
				// ask for input
				System.out.println("Enter row [1, 8]: ");
				row = kb.nextInt(); 
				if (row <= 0 || row > 8) {
					System.out.println("Out of range [1, 8]");
				}
			}
			while(col <= 0 || col > 8) {
				// ask for input
				System.out.println("Enter col [1, 8]: ");
				col = kb.nextInt();
				if (col <= 0 || col > 8) {
					System.out.println("Out of range [1, 8]");
				}
			}
			// convert input to range 0-7
			row--;
			col--;
			
			// check if placing player's color on that tile is a valid move
			if (isValidMove(row, col, turn)) {
				validMove = true;
			}						
		} while (!validMove);
		
		// do the move 
		return new OthelloGameBoard(placePlayerTile(turn, row, col, 0));
	}
	
	public boolean isValidMove(int row, int col, char player) {
		char opponent;
		if (player == 'B') {
			opponent = 'W';
		}
		else {
			opponent = 'B';
		}
		// check if the tile is empty, if not, you know right away that its not a valid move
		if (board[row][col] != '0') {
			System.out.println("Move invalid: Tile not empty.");
			return false;
		}
		
		// if it is empty, check if it sandwiches 1 or more opponent tiles with another player tile
		// public static boolean checkForTile(OthelloGameBoard b, char targetTile, char opponent, int row, int col, int dirRow, int dirCol) {
		// for all cases, check if the adjacent tile is an opponent tile, if yes, call checkForTile to check for player's tile after it 
		
		// check up left 
		if (row != 0 && col != 0) {
			if (board[row - 1][col - 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, -1, -1)) {
					return true;
				}
			}
		}
		
		// check up 
		if (row != 0) {
			if (board[row - 1][col] == opponent) {
				if (checkForTile(this, player, opponent, row, col, -1, 0)) {
					return true;
				}
			}
		}
		
		// check up right
		if (row != 0 && col != 7) {
			if (board[row - 1][col + 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, -1, 1)) {
					return true;
				}
			}
		}
		
		// check right
		if (col != 7) {
			if (board[row][col + 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, 0, 1)) {
					return true;
				}
			}
		}
		
		// check down right
		if (row != 7 && col != 7) {
			if (board[row + 1][col + 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, 1, 1)) {
					return true;					
				}
			}
		}
		
		// check down 
		if (row != 7) {
			if (board[row + 1][col] == opponent) {
				if (checkForTile(this, player, opponent, row, col, 1, 0)) {
					return true;
				}
			}
		}
		
		// check down left 
		if (row != 7 && col != 0) {
			if (board[row + 1][col - 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, 1, -1)) {
					return true;					
				}
			}
		}
		
		// check left 
		if (col != 0) {
			if (board[row][col - 1] == opponent) {
				if (checkForTile(this, player, opponent, row, col, 0, -1)) {
					return true;					
				}
			}
		}
		
		// otherwise, move is not valid
		System.out.println("Move is not valid.");
		return false;
	}
}