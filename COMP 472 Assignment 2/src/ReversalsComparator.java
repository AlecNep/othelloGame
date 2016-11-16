import java.util.Comparator;

public class ReversalsComparator implements Comparator<OthelloGameBoard> {

	public int compare(OthelloGameBoard o1, OthelloGameBoard o2) {
		if (o1.numberOfReveralsHeuristic > o2.numberOfReveralsHeuristic) {
			return -1;
		}
		else if (o1.numberOfReveralsHeuristic == o2.numberOfReveralsHeuristic) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
}