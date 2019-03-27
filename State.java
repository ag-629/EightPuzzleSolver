/*
 * State Class Representing a State of the board
 * 
 * Andrew Gerlach
 * 2/14/2019
 * 
 * Instance Variables:
 * 	private String[][] stateMatrix, 3x3 matrix storing input values
	private State predecessor, The state succeeded from
	private int cost, The cost for a given state
	private int expanded, Count of successor states
	private int frontierTotal, Assigned to the Goal state before returning
	private int manhattanDistance, Used for A* search, the sum of each misplaced tile's distance from where it should be
	private final String GOAL = ".12345678", String representation of the goal state
 */
public class State {

	private String[][] stateMatrix;
	private State predecessor;
	private int cost;
	private int expanded;
	private int frontierTotal;
	private int manhattanDistance;
	private final String GOAL = ".12345678";
	
	// Constructor initialize state matrix
	public State(String[][] stateMatrix) {
		this.stateMatrix = stateMatrix;
	}
/*
 * This method returns the single string representation of the stateMatrix
 */
	public String createStringRepresentation() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 3; i = i + 1) {
			for (int j = 0; j < 3; j += 1) {
				sb.append(this.stateMatrix[i][j]);
			}
		}
		String output = sb.toString();
		return output;
	}
/*
 * Compare a state's string to the goal string
 */
	boolean isGoal() {
		if (this.createStringRepresentation().equals(GOAL)) {
			return true;
		} else {
			return false;
		}
	}

	public String[][] getStateMatrix() {
		return this.stateMatrix;
	}

	public String getStateMatrixEntry(int i, int j) {
		return this.stateMatrix[i][j];
	}

	public void setPredecessor(State s) {
		this.predecessor = s;
	}

	public State getPredecessor() {
		return this.predecessor;
	}
/*
 * Prints, on 3 lines, a state of the puzzle followed by some separating lines
 */
	void printMatrix() {
		for (String[] l : this.stateMatrix) {
			for (String num : l) {
				System.out.print(num + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void setCost(int cost) {
		//The arguments depend on which type of search is being run
		//It will be either 0, or the cost of the previous state
		this.cost = cost;
	}

	public int getCost() {
		return this.cost;
	}

	public void setExpanded(int expanded) {
		this.expanded = expanded;
	}

	public int getExpanded() {
		return this.expanded;
	}

	public void setFrontierTotal(int frontierTotal) {
		this.frontierTotal = frontierTotal;

	}

	public int getFrontierTotal() {
		return this.frontierTotal;
	}

/*
 * Returns the sum of the distances each number in a state 
 * is from where it should be.
 * I used this post as a guide
 * https://stackoverflow.com/questions/12526792/manhattan-distance-in-a
 */
	public int getManhattanDistance() {
		String stateString = this.createStringRepresentation();
		int manhattanDistance = 0;
		for (int i = 0; i < 9; i = i + 1) {
			for (int j = 0; j < 9; j = j + 1) {
				if(stateString.charAt(i) == GOAL.charAt(j)){
					manhattanDistance = manhattanDistance + (Math.abs(i % 3 - j % 3) + Math.abs(i / 3 + j / 3));
				}
			}
		}
		this.manhattanDistance = manhattanDistance;
		return this.manhattanDistance;
	}
}