import java.io.*;
import java.util.*;

public class PathGame
{
	private static boolean animationEnabled = false;

	private static double frameRate = 4.0;

	public static void enableAnimation() { PathGame.animationEnabled = true; }
	public static void disableAnimation() { PathGame.animationEnabled = false; }
	public static void setFrameRate(double fps) { PathGame.frameRate = frameRate; }

	private static final char WALL       = '#';
	private static final char PERSON     = '@';
	private static final char EXIT       = 'e';
	private static final char BREADCRUMB = '.';
	private static final char SPACE      = ' ';

	private static final char LEFT       = 'l';
	private static final char RIGHT      = 'r';
	private static final char UP         = 'u';
	private static final char DOWN       = 'd';

	public static HashSet<String> findPaths(char [][] maze)
	{

		// HashSet to contain the list of solutions to the maze
		HashSet<String> solutionSet = new HashSet<>();

		// StringBuilder puts together the characters that form a single path to the exit
		StringBuilder path = new StringBuilder();

		int height = maze.length;
		int width = maze[0].length;

		// The visited array keeps track of visited positions and the exit
		char [][] visited = new char[height][width];
		for (int i = 0; i < height; i++)
			Arrays.fill(visited[i], SPACE);

		// Find starting position (location of the '@' character).
		int startRow = -1;
		int startCol = -1;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		return findPaths(maze, visited, startRow, startCol, height, width, path, solutionSet);

	}

	// Method to run through a ACII maze and return all paths to exit as a HashSet of Strings
	private static HashSet<String> findPaths(char [][] maze, char [][] visited,
	                                         int currentRow, int currentCol,
	                                         int height, int width, StringBuilder path, HashSet<String> solutionSet)
	{

		// This conditional block prints the maze when a new move is made.
		if (PathGame.animationEnabled)
		{
			printAndWait(maze, height, width, "Searching...", PathGame.frameRate);
		}

		// If the exit is found in the maze then the StringBuilder now holds a list of all moves made to get there
		if (visited[currentRow][currentCol] == 'e')
		{

			// Run the animation for finding the exit
			if (PathGame.animationEnabled)
			{
				for (int i = 0; i < 3; i++)
				{
					maze[currentRow][currentCol] = '*';
					printAndWait(maze, height, width, "Hooray!", PathGame.frameRate);

					maze[currentRow][currentCol] = PERSON;
					printAndWait(maze, height, width, "Hooray!", PathGame.frameRate);
				}
			}

			// There is an extra space to be deleted in the String (FORMATTING STUFF)
			path.deleteCharAt(path.length() - 1);

			// Add the formatted string to the hashset
			solutionSet.add(path.toString());

			// I add the space back in to keep formatting during the function
			path.append(' ');

			maze[currentRow][currentCol] = EXIT;

			return solutionSet;
		}

		// Moves: left, right, up, down
		int [][] moves = new int[][] {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

		for (int i = 0; i < moves.length; i++)
		{
			int newRow = currentRow + moves[i][0];
			int newCol = currentCol + moves[i][1];

			// Check move is in bounds, not a wall, and not marked as visited.
			if (!isLegalMove(maze, visited, newRow, newCol, height, width))
				continue;



			// Change state. we need to check whether we're overwriting the exit.
			if (maze[newRow][newCol] == EXIT)
				visited[newRow][newCol] = EXIT;

			maze[currentRow][currentCol] = BREADCRUMB;
			visited[currentRow][currentCol] = BREADCRUMB;
			maze[newRow][newCol] = PERSON;


			// the direction that is chosen is saved as a character denoting the direction taken
			char move = ' ';

			if (i == 0)
				move = LEFT;
			if (i == 1)
				move = RIGHT;
			if (i == 2)
				move = UP;
			if (i == 3)
				move = DOWN;

			// As moves are chosen they are added to our exit Path
			path.append(move);
			path.append(' ');

			// Perform recursive descent.
			findPaths(maze, visited, newRow, newCol, height, width, path, solutionSet);

			// Undo state change
			if (maze[newRow][newCol] == EXIT)
				maze[newRow][newCol] = EXIT;
			else
				maze[newRow][newCol] = BREADCRUMB;

			// In order to find all possible paths the visited array needs to only contain the current path taken
			// so it is updated as we backtrack to no longer contain positions that were tracked on the prior search
			if (!(visited[newRow][newCol] == EXIT))
				visited[newRow][newCol] = SPACE;

			maze[currentRow][currentCol] = PERSON;

			// We only want to delete from an empty StringBuilder (there has to be a path to delete the path)
			if (path.length() > 1)
			{
				path.deleteCharAt(path.length() - 1);
				path.deleteCharAt(path.length() - 1);
			}

			// This conditional block prints the maze when a move gets undone
			// (which is effectively another kind of move).
			if (PathGame.animationEnabled)
			{
				printAndWait(maze, height, width, "Backtracking...", frameRate);
			}

			// There is only a BreadCrumb at the position that was most recently backtracked from to avoid bread crumbs interfearing with other potential paths
			if (maze[newRow][newCol] == EXIT)
				maze[newRow][newCol] = EXIT;
			else
				maze[newRow][newCol] = SPACE;

		}

		return solutionSet;
	}



	public static boolean solveMaze(char [][] maze)
	{
		int height = maze.length;
		int width = maze[0].length;

		char [][] visited = new char[height][width];
		for (int i = 0; i < height; i++)
			Arrays.fill(visited[i], SPACE);

		// Find starting position (location of the '@' character).
		int startRow = -1;
		int startCol = -1;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		return solveMaze(maze, visited, startRow, startCol, height, width);
	}

	private static boolean solveMaze(char [][] maze, char [][] visited,
	                                 int currentRow, int currentCol,
	                                 int height, int width)
	{
		// This conditional block prints the maze when a new move is made.
		if (PathGame.animationEnabled)
		{
			printAndWait(maze, height, width, "Searching...", PathGame.frameRate);
		}

		// Hooray!
		if (visited[currentRow][currentCol] == 'e')
		{
			if (PathGame.animationEnabled)
			{
				for (int i = 0; i < 3; i++)
				{
					maze[currentRow][currentCol] = '*';
					printAndWait(maze, height, width, "Hooray!", PathGame.frameRate);

					maze[currentRow][currentCol] = PERSON;
					printAndWait(maze, height, width, "Hooray!", PathGame.frameRate);
				}
			}

			return true;
		}

		// Moves: left, right, up, down
		int [][] moves = new int[][] {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

		for (int i = 0; i < moves.length; i++)
		{
			int newRow = currentRow + moves[i][0];
			int newCol = currentCol + moves[i][1];

			if (!isLegalMove(maze, visited, newRow, newCol, height, width))
				continue;

			if (maze[newRow][newCol] == EXIT)
				visited[newRow][newCol] = EXIT;

			maze[currentRow][currentCol] = BREADCRUMB;
			visited[currentRow][currentCol] = BREADCRUMB;
			maze[newRow][newCol] = PERSON;

			if (solveMaze(maze, visited, newRow, newCol, height, width))
				return true;

			maze[newRow][newCol] = BREADCRUMB;
			maze[currentRow][currentCol] = PERSON;

			if (PathGame.animationEnabled)
			{
				printAndWait(maze, height, width, "Backtracking...", frameRate);
			}
		}

		return false;
	}


	private static boolean isLegalMove(char [][] maze, char [][] visited,
	                                   int row, int col, int height, int width)
	{
		if (row < 0 || col < 0)
			return false;

		if (col > (maze[0].length - 1) || row > (maze.length - 1))
			return false;

		if (maze[row][col] == WALL || visited[row][col] == BREADCRUMB)
			return false;

		return true;
	}

	private static boolean isLegalPlayMove(char [][] maze, int row, int col)
	{
		if (row < 0 || col < 0)
			return false;

		if (col > (maze[0].length - 1) || row > (maze.length - 1))
			return false;

		if (maze[row][col] == WALL)
			return false;

		return true;
	}

	private static void wait(double waitTimeInSeconds)
	{
		long startTime = System.nanoTime();
		long endTime = startTime + (long)(waitTimeInSeconds * 1e9);

		while (System.nanoTime() < endTime)
			;
	}

	private static void printAndWait(char [][] maze, int height, int width,
	                                 String header, double frameRate)
	{
		if (header != null && !header.equals(""))
			System.out.println(header);

		if (height < 1 || width < 1)
			return;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				System.out.print(maze[i][j]);
			}

			System.out.println();
		}

		System.out.println();
		wait(1.0 / frameRate);
	}

	public static char [][] defaultMaze()
	{
		char[][] maze = new char[][]{
			{'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
			{'#', '@', '#', ' ', '#', ' ', ' ', ' ', '#', ' ', '#', ' ', '#'},
			{'#', ' ', ' ', ' ', '#', ' ', '#', ' ', '#', ' ', '#', ' ', '#'},
			{'#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', ' ', '#'},
			{'#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', '#'},
			{'#', ' ', '#', '#', '#', '#', '#', ' ', '#', '#', '#', '#', '#'},
			{'#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'e', '#'},
			{'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
		};

		return maze;
	}

	private static char [][] readMaze(String filename) throws IOException
	{
		Scanner in = new Scanner(new File(filename));

		int height = in.nextInt();
		int width = in.nextInt();

		char [][] maze = new char[height][];

		in.nextLine();

		for (int i = 0; i < height; i++)
			maze[i] = in.nextLine().toCharArray();

		return maze;
	}

	private static void printMaze(char [][] maze)
	{
		for (int i = 0; i < maze.length; i++)
		{
			for (int j = 0; j < maze[0].length; j++)
				System.out.print(maze[i][j]);
			
			System.out.println();
		}

	}

	private static void play(char [][] maze)
	{
		printMaze(maze);

		int startRow = -1;
		int startCol = -1;
		int currentRow = -1;
		int currentCol = -1;

		for (int i = 0; i < maze.length; i++)
		{
			for (int j = 0; j < maze[0].length; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		currentRow = startRow;
		currentCol = startCol;

		Scanner input = new Scanner(System.in);

		while (maze[currentRow][currentCol] != EXIT)
		{
			char move = input.next().charAt(0);

			

			switch(move)
			{
				case 's':
					solveMaze(maze);
					return;
				case 'f':
					System.out.println(findPaths(maze));
					return;
				case LEFT:
					if (!isLegalPlayMove(maze, currentRow, currentCol-1))
						continue;

					maze[currentRow][currentCol] = ' ';
					currentCol -= 1;
					break;
					
				case RIGHT:
					if (!isLegalPlayMove(maze, currentRow, currentCol+1))
						continue;

					maze[currentRow][currentCol] = ' ';
					currentCol += 1;
					break;
					
				case UP:
					if (!isLegalPlayMove(maze, currentRow-1, currentCol))
						continue;

					maze[currentRow][currentCol] = ' ';
					currentRow -= 1;
					break;
					
				case DOWN:
					if (!isLegalPlayMove(maze, currentRow+1, currentCol))
						continue;

					maze[currentRow][currentCol] = ' ';
					currentRow += 1;
					break;
					
				default:
					printMaze(maze);

			}

			if (maze[currentRow][currentCol] == 'e')
			{
				if (PathGame.animationEnabled)
				{
					for (int i = 0; i < 3; i++)
					{
						maze[currentRow][currentCol] = '*';
						printAndWait(maze, maze.length, maze[0].length, "Hooray!", PathGame.frameRate);

						maze[currentRow][currentCol] = PERSON;
						printAndWait(maze, maze.length, maze[0].length, "Hooray!", PathGame.frameRate);
					}
				}

			return;
			}

			maze[currentRow][currentCol] = PERSON;
			printMaze(maze);
		}

		return;
	}

	public static void main(String [] args) throws IOException
	{
		System.out.println("Press 's' to solve the maze.\nPress 'f' to find all paths for the maze.\nPress 'p' to play!\nPress any other key to quit.");
		Scanner scan = new Scanner(System.in);
		char c = scan.next().charAt(0);
		char [][] maze; 

			
		System.out.println(args.length);

		if (args.length == 0)
			maze = defaultMaze();
		else
			maze = readMaze(args[0]);


		PathGame.enableAnimation();

		// Go!!
		if (c == 's')
		{
			if (PathGame.solveMaze(maze))
				System.out.println("Found path to exit!");
			else
				System.out.println("There doesn't appear to be a path to the exit.");
		}

		else if (c == 'f')
		{
			System.out.println(PathGame.findPaths(maze));
		}

		else if (c == 'p')
		{
			play(maze);
		}

		else
		{
			System.out.println("Closing PathGame!");
		}

	}

}
