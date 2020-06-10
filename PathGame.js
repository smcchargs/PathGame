		var animationEnabled = true;
		var frameRate = 4.0;

		var WALL       = '#';
		var PERSON     = '@';
		var EXIT       = 'e';
		var BREADCRUMB = '.';
		var SPACE      = ' ';

		var LEFT       = 'l';
		var RIGHT      = 'r';
		var UP         = 'u';
		var DOWN       = 'd';

		var defaultMaze = [
				['#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'],
				['#', '@', '#', ' ', '#', ' ', ' ', ' ', '#', ' ', '#', ' ', '#'],
				['#', ' ', ' ', ' ', '#', ' ', '#', ' ', '#', ' ', '#', ' ', '#'],
				['#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', ' ', '#'],
				['#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', '#'],
				['#', ' ', '#', '#', '#', '#', '#', ' ', '#', '#', '#', '#', '#'],
				['#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'e', '#'],
				['#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'],
			];



class PathGame
{

	static enableAnimation() 
	{ 
		animationEnabled = true; 
	}
	static disableAnimation() 
	{ 
		animationEnabled = false;
	}
	static setFrameRate(fps) 
	{
		frameRate = fps; 
	}

	static findPaths(maze)
	{
		/*if (maze == undefined)
		{
			console.log(this.defaultMaze);
			maze = this.defaultMaze;
		}*/

		var solutionSet = new Set();

		var path = "";
		console.log(SPACE);
		var height = maze.length;
		var width = maze[0].length;

		var visited = new Array(height);

		for(let i = 0; i < height; i++) 
		{
    		visited[i] = new Array(width);
		}

		for (let i = 0; i < height; i++)
			for (let j = 0; j < width; j++)
				visited[i][j] = SPACE;


		var startRow = -1;
		var startCol = -1;

		for (let i = 0; i < height; i++)
		{
			for (let j = 0; j < width; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		return this.findPathsInner(maze, visited, startRow, startCol, height, width, path, solutionSet);

	}

	static findPathsInner(maze, visited, currentRow, currentCol, height, width, path, solutionSet)
	{
		if (animationEnabled)
		{
			this.printAndWait(maze, height, width, "Searching...", frameRate);
		}

		if (visited[currentRow][currentCol] == EXIT)
		{
			if (animationEnabled)
			{
				for (let i = 0; i < 3; i++)
				{
					maze[currentRow][currentCol] = '*';
					this.printAndWait(maze, height, width, "Hooray!", frameRate);

					maze[currentRow][currentCol] = PERSON;
					this.printAndWait(maze, height, width, "Hooray!", frameRate);
				}
			}

			path = path.substr(0, path.length - 1);
			solutionSet.add(path);
			path = path.concat(' ');

			maze[currentRow][currentCol] = EXIT;

			return solutionSet;
		}

		var moves = [
						[0, -1], // i = 0   left
						[0, 1],  // i = 1   right
						[-1, 0], // i = 2   up
						[1, 0]   // i = 3   down
					];

		for (let i = 0; i < moves.length; i++)
		{
			var newRow = currentRow + moves[i][0];
			var newCol = currentCol + moves[i][1];

			if (!this.isLegalMove(maze, visited, newRow, newCol, height, width))
				continue;

			if (maze[newRow][newCol] == EXIT)
				visited[newRow][newCol] = EXIT;

			maze[currentRow][currentCol] = BREADCRUMB;
			visited[currentRow][currentCol] = BREADCRUMB;
			maze[newRow][newCol] = PERSON;


			var move = ' ';

			if (i == 0)
				move = LEFT;
			if (i == 1)
				move = RIGHT;
			if (i == 2)
				move = UP;
			if (i == 3)
				move = DOWN;

			// As moves are chosen they are added to our exit Path
			path = path.concat(move);
			path = path.concat(' ');

			this.findPathsInner(maze, visited, newRow, newCol, height, width, path, solutionSet);


			if (maze[newRow][newCol] == EXIT)
				maze[newRow][newCol] = EXIT;
			else
				maze[newRow][newCol] = BREADCRUMB;

			if (!(visited[newRow][newCol] == EXIT))
				visited[newRow][newCol] = SPACE;

			maze[currentRow][currentCol] = PERSON;

			if (path.length > 1)
			{
				path = path.substr(0, path.length - 1);
				path = path.substr(0, path.length - 1);
			}

			// This conditional block prints the maze when a move gets undone
			// (which is effectively another kind of move).
			if (animationEnabled)
			{
				this.printAndWait(maze, height, width, "Backtracking...", frameRate);
			}

			// There is only a BreadCrumb at the position that was most recently backtracked from to avoid bread crumbs interfearing with other potential paths
			if (maze[newRow][newCol] == EXIT)
				maze[newRow][newCol] = EXIT;
			else
				maze[newRow][newCol] = SPACE;
		}

		return solutionSet;
	}

	static solveMaze(maze)
	{
		var height = maze.length;
		var width = maze[0].length;

		var visited = new Array(height);

		for(let i = 0; i < height; i++) 
		{
    		visited[i] = new Array(width);
		}

		for (let i = 0; i < height; i++)
			for (let j = 0; j < width; j++)
				visited[i][j] = SPACE;

		var startRow = -1;
		var startCol = -1;

		for (let i = 0; i < height; i++)
		{
			for (let j = 0; j < width; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		return this.solveMazeInner(maze, visited, height, width, startRow, startCol);
	}

	static solveMazeInner(maze, visited, height, width, currentRow, currentCol)
	{

		if (animationEnabled)
		{
			this.printAndWait(maze, height, width, "Searching...", frameRate);
		}

		if (visited[currentRow][currentCol] == EXIT)
		{
			if (animationEnabled)
			{
				for (let i = 0; i < 3; i++)
				{
					maze[currentRow][currentCol] = '*';
					this.printAndWait(maze, height, width, "Hooray!", frameRate);

					maze[currentRow][currentCol] = PERSON;
					this.printAndWait(maze, height, width, "Hooray!", frameRate);
				}
			}

			return true;
		}

		var moves = [
						[0, -1], // i = 0   left
						[0, 1],  // i = 1   right
						[-1, 0], // i = 2   up
						[1, 0]   // i = 3   down
					];

		for (let i = 0; i < moves.length; i++)
		{
			var newRow = currentRow + moves[i][0];
			var newCol = currentCol + moves[i][1];

			if (!this.isLegalMove(maze, visited, newRow, newCol, height, width))
				continue;

			if (maze[newRow][newCol] == EXIT)
				visited[newRow][newCol] = EXIT;

			maze[currentRow][currentCol] = BREADCRUMB;
			visited[currentRow][currentCol] = BREADCRUMB;
			maze[newRow][newCol] = PERSON;

			if (this.solveMazeInner(maze, visited, height, width, newRow, newCol))
				return true;

			maze[newRow][newCol] = BREADCRUMB;
			maze[currentRow][currentCol] = PERSON;

			if (this.animationEnabled)
			{
				this.printAndWait(maze, height, width, "Backtracking...", frameRate);
			}
		}

		return false;

	}

	static isLegalMove(maze, visited, row, col, height, width)
	{
		if (row < 0 || col < 0)
			return false;

		if (col > (maze[0].length - 1) || row > (maze.length - 1))
			return false;

		if (maze[row][col] == WALL || visited[row][col] == BREADCRUMB)
			return false;

		return true;
	}

	static isLegalPlayMove(maze, row, col)
	{
		if (row < 0 || col < 0)
			return false;

		if (col > (maze[0].length - 1) || row > (maze.length - 1))
			return false;

		if (maze[row][col] == WALL)
			return false;

		return true;
	}

	static wait(waitTimeInSeconds)
	{
		var ms = waitTimeInSeconds*1000;

		var start = new Date().getTime();
   		var end = start;
   		while(end < start + ms)
     		end = new Date().getTime();
	}


	static printAndWait(maze, height, width, header, frameRate)
	{
		if (header != null)
			console.log(header);

		if (height < 1 || width < 1)
			return;

		for (let i = 0; i < height; i++)
		{
			for (let j = 0; j < width; j++)
				process.stdout.write(maze[i][j]);

			console.log();
		}

		console.log();
		this.wait(1.0 / frameRate);
	}

	static printMaze(maze)
	{
		for (let i = 0; i < maze.length; i++)
		{
			for (let j = 0; j < maze[0].length; j++)
				process.stdout.write(maze[i][j]);
			
			console.log();
		}

	}
}

console.log(PathGame.findPaths(defaultMaze));