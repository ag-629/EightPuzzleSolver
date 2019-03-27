import java.io.*;
import java.util.Stack;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;//only allows one copy to be added which prevents duplicate states
import java.util.LinkedList;
import java.util.PriorityQueue;

class Solver {

	void readInputFileAndSolve(String searchType, String fileName) throws IOException {
		String[][] sm = new String[3][3];
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int rowCount = 0;
		String s;
		while ((s = reader.readLine()) != null) {
			String[] l = s.split(" ");
			sm[rowCount] = l;
			rowCount = rowCount + 1;
		}
		State initial = new State(sm);
		reader.close();
		solve(initial, searchType);
	}

	void solve(State initial, String searchType) {
		// Move this to each search method to reflect the difference in metrics
		if (initial.isGoal()) {
			initial.printMatrix();
			System.out.println("Path Cost: 0");
			System.out.println("Frontier: 0");
			System.out.println("Expanded: 0");
		} else {
			if (searchType.equals("bfs")) {
				Queue<State> frontier = new LinkedList<State>();
				frontier.add(initial);
				State solution = solveBFS(frontier);
				if (solution != null) {
					printSolution(solution, "Breadth-First Search");
				}
			} else if (searchType.equals("ucost")) {
				PriorityQueue<State> frontierPQ = new PriorityQueue<State>(new StateComparator());
				frontierPQ.add(initial);
				State solution = solveUniform(frontierPQ);
				if (solution != null) {
					printSolution(solution, "Uniform-Cost Search");
				}
			} else if (searchType.contains("greedy")) {
				PriorityQueue<State> frontierPQ = new PriorityQueue<State>(new StateComparator());
				frontierPQ.add(initial);
				State solution = solveGreedy(frontierPQ);
				if (solution != null) {
					printSolution(solution, "Greedy Best-First Search");
				}
			} else if (searchType.contains("astar")) {
				PriorityQueue<State> frontierPQ = new PriorityQueue<State>(new StateComparator());
				frontierPQ.add(initial);
				State solution = solveAStar(frontierPQ);
				if (solution != null) {
					printSolution(solution, "A* Search");
				}
			} else {
				System.out.println(
						"Invalid Format. The input should formatted as: java EightPuzzle <Search Strategy> <File path>");
				System.out.println("The valid search types are: bfs, ucost, greedy, and astar.");
				System.out.println("Please ensure the file path is correct.");
			}
		}
	}

	/*
	 * Breadth-First Search Parameters; FIFO Queue frontier Returns the solution
	 * state which stores the path's metrics
	 */
	public State solveBFS(Queue<State> frontier) {
		if (frontier.size() == 0) {
			// If the code reaches this point, there's no solution
			// Print "No Solution" and the metrics
			System.out.println("No Solution");
			System.out.println("Path Cost: ");
			System.out.println("Frontier: 0");
			System.out.println("Expanded: 0");
			return null;
		} else {
			// A hash set that stores the string representations of the states that are
			// added to the frontier
			Set<String> frontierStrings = new HashSet<String>();
			// A hash set that stores the string representations of the states that have
			// been explored
			Set<String> explored = new HashSet<String>();
			frontierStrings.add(frontier.peek().createStringRepresentation());
			int expanded = 0;
			int frontierTotal = 1;
			int pathCost = 0;
			while (!frontier.isEmpty()) {
				State current = frontier.remove();
				expanded = expanded + 1;
				pathCost = pathCost + current.getCost();
				frontierStrings.remove(current.createStringRepresentation());
				explored.add(current.createStringRepresentation());
				LinkedList<State> successors = expand(current, "bfs");
				for (State s : successors) {
					if (!frontierStrings.contains(s.createStringRepresentation())
							&& !explored.contains(s.createStringRepresentation())) {
						if (s.isGoal()) {
							explored.add(s.createStringRepresentation());
							s.setExpanded(expanded);
							s.setFrontierTotal(frontierTotal);
							return s;
						} else {
							frontier.add(s);
							frontierTotal = frontierTotal + 1;
							frontierStrings.add(s.createStringRepresentation());
						}
					}
				}
			}
			// If the code reaches this point, there's no solution
			// Print "No Solution" and the metrics
			System.out.println("No Solution");
			System.out.println("Path Cost: " + pathCost);
			System.out.println("Frontier: " + frontierTotal);
			System.out.println("Expanded: " + expanded);
			return null;
		}
	}

	/*
	 * Uniform Cost Search Parameters: A Priority Queue frontier The priority is the
	 * path cost so far + the cost of the current state Returns solution state with
	 * the path's metrics
	 * 
	 */
	public State solveUniform(PriorityQueue<State> frontierPQ) {
		// A hash set that stores the string representations of the states that are
		// added to the frontier
		Set<String> frontierStrings = new HashSet<String>();
		// A hash set that stores the string representations of the states that have
		// been explored
		Set<String> explored = new HashSet<String>();
		frontierStrings.add(frontierPQ.peek().createStringRepresentation());
		int expanded = 0;
		int frontierTotal = 1;
		int pathCost = 0;
		while (!frontierPQ.isEmpty()) {
			State current = frontierPQ.poll();
			expanded = expanded + 1;
			pathCost = pathCost + current.getCost();
			frontierStrings.remove(current.createStringRepresentation());
			if (current.isGoal()) {
				current.setExpanded(expanded);
				current.setFrontierTotal(frontierTotal);
				return current;
			}
			explored.add(current.createStringRepresentation());
			LinkedList<State> successors = expand(current, "unif");
			for (State s : successors) {
				if (!frontierStrings.contains(s.createStringRepresentation())
						&& !explored.contains(s.createStringRepresentation())) {
					frontierPQ.add(s);
					frontierTotal = frontierTotal + 1;
					frontierStrings.add(s.createStringRepresentation());
				}
			}
		}
		// If the code reaches this point, there's no solution
		// Print "No Solution" and the metrics
		System.out.println("No Solution");
		System.out.println("Path Cost: " + pathCost);
		System.out.println("Frontier: " + frontierTotal);
		System.out.println("Expanded: " + expanded);
		return null;
	}

	private State solveGreedy(PriorityQueue<State> frontierPQ) {
		// A hash set that stores the string representations of the states that have
		// been explored
		Set<String> explored = new HashSet<String>();
		// A hash set that stores the string representations of the states that are
		// added to the frontier
		Set<String> frontierStrings = new HashSet<String>();
		frontierStrings.add(frontierPQ.peek().createStringRepresentation());
		int expanded = 0;
		int frontierTotal = 1;
		int pathCost = 0;
		while (!frontierPQ.isEmpty()) {
			State current = frontierPQ.poll();
			expanded = expanded + 1;
			pathCost = pathCost + current.getCost();
			frontierStrings.remove(current.createStringRepresentation());
			if (current.isGoal()) {
				current.setExpanded(expanded);
				current.setFrontierTotal(frontierTotal);
				return current;
			}
			explored.add(current.createStringRepresentation());
			LinkedList<State> successors = expand(current, "greedy");

			for (State s : successors) {
				if (!frontierStrings.contains(s.createStringRepresentation())
						&& !explored.contains(s.createStringRepresentation())) {
					frontierPQ.add(s);
					frontierTotal = frontierTotal + 1;
					frontierStrings.add(s.createStringRepresentation());
				}
			}
		}
		// If the code reaches this point, there's no solution
		// Print "No Solution" and the metrics
		System.out.println("No Solution");
		System.out.println("Path Cost: " + pathCost);
		System.out.println("Frontier: " + frontierTotal);
		System.out.println("Expanded: " + expanded);
		return null;
	}

	private State solveAStar(PriorityQueue<State> frontierPQ) {
		// A hash set that stores the string representations of the states that have
		// been explored
		Set<String> explored = new HashSet<String>();
		// A hash set that stores the string representations of the states that are
		// added to the frontier
		Set<String> frontierStrings = new HashSet<String>();
		frontierStrings.add(frontierPQ.peek().createStringRepresentation());
		int expanded = 0;
		int frontierTotal = 1;
		int pathCost = 0;
		while (!frontierPQ.isEmpty()) {
			State current = frontierPQ.poll();
			expanded = expanded + 1;
			pathCost = pathCost + current.getCost();
			frontierStrings.remove(current.createStringRepresentation());
			if (current.isGoal()) {
				current.setExpanded(expanded);
				current.setFrontierTotal(frontierTotal);
				return current;
			}
			explored.add(current.createStringRepresentation());
			LinkedList<State> successors = expand(current, "astar");
			for (State s : successors) {
				if (!frontierStrings.contains(s.createStringRepresentation())
						&& !explored.contains(s.createStringRepresentation())) {
					frontierPQ.add(s);
					frontierTotal = frontierTotal + 1;
					frontierStrings.add(s.createStringRepresentation());
				}
			}
		}
		// If the code reaches this point, there's no solution
		// Print "No Solution" and the metrics
		System.out.println("No Solution");
		System.out.println("Path Cost: " + pathCost);
		System.out.println("Frontier: " + frontierTotal);
		System.out.println("Expanded: " + expanded);
		return null;
	}

	private LinkedList<State> expand(State state, String searchType) {
		LinkedList<State> possibleSuccessors = new LinkedList<State>();
		int row = -1, col = -1;
		for (int i = 0; i < 3; i = i + 1) {
			for (int j = 0; j < 3; j = j + 1) {
				if (state.getStateMatrixEntry(i, j).equals(".")) {
					// Look around for possible successor states
					// switch with a number BELOW, if possible
					row = i;
					col = j;
				}
			}
		}
		// Switch with a number BELOW, if possible
		if ((row + 1 <= 2)) {
			State belowState = swap(state, row, col, row + 1, col);
			belowState.setPredecessor(state);
			if (searchType.equals("bfs")) {
				belowState.setCost(Integer.parseInt(belowState.getStateMatrixEntry(row, col)));
			}
			if (searchType.equals("unif")) {
				belowState.setCost(belowState.getPredecessor().getCost()
						+ Integer.parseInt(belowState.getStateMatrixEntry(row, col)));
			}
			if (searchType.contentEquals("greedy")) {
				// change to manhattan
				belowState.setCost(belowState.getManhattanDistance());
			}
			if (searchType.equals("astar")) {
				// Same as uniform + manhattan
				belowState.setCost(Integer.parseInt(belowState.getStateMatrixEntry(row, col))
						+ belowState.getPredecessor().getCost() + belowState.getManhattanDistance());
			}

			possibleSuccessors.add(belowState);

		}
		// Switch with a number ABOVE, if possible
		if ((row - 1 >= 0)) {
			State aboveState = swap(state, row, col, row - 1, col);
			aboveState.setPredecessor(state);
			if (searchType.equals("bfs")) {
				aboveState.setCost(Integer.parseInt(aboveState.getStateMatrixEntry(row, col)));
			}
			if (searchType.equals("unif")) {
				aboveState.setCost(aboveState.getPredecessor().getCost()
						+ Integer.parseInt(aboveState.getStateMatrixEntry(row, col)));
			}
			if (searchType.contentEquals("greedy")) {
				aboveState.setCost(aboveState.getManhattanDistance());
			}
			if (searchType.equals("astar")) {
				aboveState.setCost(Integer.parseInt(aboveState.getStateMatrixEntry(row, col))
						+ aboveState.getPredecessor().getCost() + aboveState.getManhattanDistance());
			}

			possibleSuccessors.add(aboveState);
		}
		// Switch with a number to the LEFT, if possible
		if ((col - 1 >= 0)) {
			State leftState = swap(state, row, col, row, col - 1);
			leftState.setPredecessor(state);
			if (searchType.equals("bfs")) {
				leftState.setCost(Integer.parseInt(leftState.getStateMatrixEntry(row, col)));
			}
			if (searchType.equals("unif")) {
				leftState.setCost(leftState.getPredecessor().getCost()
						+ Integer.parseInt(leftState.getStateMatrixEntry(row, col)));
			}
			if (searchType.contentEquals("greedy")) {
				leftState.setCost(leftState.getManhattanDistance());
			}
			if (searchType.equals("astar")) {
				leftState.setCost(Integer.parseInt(leftState.getStateMatrixEntry(row, col))
						+ leftState.getPredecessor().getCost() + leftState.getManhattanDistance());
			}

			possibleSuccessors.add(leftState);
		}
		// Switch with a number to the RIGHT, if possible
		if ((col + 1 <= 2)) {
			State rightState = swap(state, row, col, row, col + 1);
			rightState.setPredecessor(state);
			if (searchType.equals("bfs")) {
				rightState.setCost(Integer.parseInt(rightState.getStateMatrixEntry(row, col)));
			}
			if (searchType.equals("unif")) {
				rightState.setCost(rightState.getPredecessor().getCost()
						+ Integer.parseInt(rightState.getStateMatrixEntry(row, col)));
			}
			if (searchType.contentEquals("greedy")) {
				rightState.setCost(rightState.getManhattanDistance());
			}
			if (searchType.equals("astar")) {
				rightState.setCost(Integer.parseInt(rightState.getStateMatrixEntry(row, col))
						+ rightState.getPredecessor().getCost() + rightState.getManhattanDistance());
			}

			possibleSuccessors.add(rightState);
		}

		return possibleSuccessors;
	}

	State swap(State state, int row, int col, int newRow, int newCol) {
		String[][] newStateMatrix = duplicateStatematrix(state.getStateMatrix());
		String temp = state.getStateMatrixEntry(row, col);
		newStateMatrix[row][col] = state.getStateMatrixEntry(newRow, newCol);
		newStateMatrix[newRow][newCol] = temp;
		State swappedState = new State(newStateMatrix);
		return swappedState;
	}

	public String[][] duplicateStatematrix(String[][] from) {
		String[][] copy = new String[3][3];
		for (int i = 0; i < 3; i = i + 1) {
			for (int j = 0; j < 3; j = j + 1) {
				copy[i][j] = from[i][j];
			}
		}
		return copy;
	}

	/*
	 * Display the solution from Starting (Input) State to the final state Followed
	 * my the metrics of the
	 */
	void printSolution(State solution, String searchType) {
		int pathCost = solution.getCost();
		int frontierSize = solution.getFrontierTotal();
		int exp = solution.getExpanded();
		Stack<State> solutionStack = new Stack<State>();
		while (solution.getPredecessor() != null) {
			solutionStack.push(solution);
			solution = solution.getPredecessor();
			//Get tthe sum of all state costs for BFS
			if (searchType.equals("Breadth-First Search")) {
				pathCost = pathCost + solution.getCost();
			}
		}
		solution.printMatrix();
		while (solutionStack.size() != 0) {
			solutionStack.pop().printMatrix();
		}
		System.out.println("path cost: " + pathCost);
		System.out.println("frontier: " + frontierSize);
		System.out.println("expanded: " + exp);
	}

	public static void main(String args[]) {
		try {
			new Solver().readInputFileAndSolve(args[0], args[1]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}