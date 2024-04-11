# Minimax Algorithm for the Connect Four Game

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

## Description
This repository features a Java application of Connect Four, using a Minimax algorithm with alpha-beta pruning for the AI. The Minimax algorithm is employed to forecast possible moves and their outcomes, enabling the AI to make strategic decisions. Alpha-beta pruning is integrated to trim down the search tree, reducing the number of nodes evaluated and speeding up the decision process. This results in a more challenging and efficient AI opponent.

## Example
<img width="359" alt="Screenshot 2024-04-11 at 9 40 24 AM" src="https://github.com/tyleroneil72/connect-four-mini-max/assets/43754564/0afbb453-dcb3-425b-8964-05fc8ff3c8c5">


### Terminal States
For Connect 4, the terminal states for the game are as follows:
- Horizontal chain of 4 - Player Won
- Vertical chain of 4 - Player Won
- Diagonal chain of 4 - Player Won
- All cells filled - Draw
- - -
### Static Game State Evaluation
\* *Potentially unnecessary for evaluating the best possible move*.

**Open Chains:** One possible way of evaluating the game state at a given non-terminal state could be track or calculate the all *open chains* for a given player. An open chain is a continuous horizontal, vertical or diagonal sequence of tokens that is not blocked by a token of the opponent player. That is, a chain that can be added to in a subsequent move is termed as an open chain.
Having the greatest number of open chains increases the possibility of winning as any of those chains may be developed later on to become a winning chain.

**Open Chain Weighting:** This idea builds on top of Open Chain evaluation by stating that not all open chains are of equal value. This is fairly intuitive as an open chain with only 1 token is less likely to become a winning chain than an open chain with 2 or 3 tokens. Thus a chain's value may be evaluated by virtue of its length.

~~**Considering possible additions around a chain~~:** A chain may be more valuable if it has more space around it. Consider the following board state
``` 
[[- - - - - - -]
 [x - - - - - -]
 [x - x - - - o]
 [x - o o o - o]]
```
In this scenario, `o`'s horizontal chain of 3 in the center is much more valuable than `x`'s vertical chain of 3 on the edge as the vertical chain is easily blocked by one move, but the horizontal chain possesses potential expandability in both the left and the right direction allowing `o` to win regardless of a blocking move played by `x`.
The center columns may be strategically more advantageous to play in as compared to ones nearer to the edge as there is greater chaining possibility when playing near the center.

- - -
### Terminal state evaluation?
If instead of implementing a solution which dives to a given depth to evaluate the static game state at that position, we determine a heuristic which will allow us to determine the outcome of the game (with perfect play) before a chain of four has been completed, it will greatly reduce the amount of calculations done as the determined terminal state will prohibit the algorithm to perform any further calculations.

**OR**
Distinguishing between two terminal states with the same outcome.
A win that is reached in a lower amount of moves is a better outcome than a win that takes more amount of moves. That is, if it is possible to win within the next 3 moves with perfect play, that sequence of moves is preferable over the sequence that takes 7 moves. 
Could use the depth parameter to track which move is better. At terminal state, instead of returning `+1, 0, -1`, we could  `return isMax ? depth : -1 * depth` **!!**
**\ * ~~This might improve alpha-beta pruning**~~.
**\*\* This does improve alpha-beta pruning!!**
- - -
### Move Ordering
At each recursive call to the minimax function, sorting the list of possible moves based on some heuristic so that the moves which have the greatest likelihood of being winning moves are evaluated first. This leads to a narrower window of the alpha-beta bounds which results in greater pruning.
The **open-chain** heuristic may be a valid move scoring technique to determine the weight of each move to be played. We will look at the columns who have the greatest amount of weighted open chains and evaluate subsequent moves in that order. This can be done even without having to track open chains in some sort of collection by simply evaluating it for each state with the following implementation.

***Implementing Column Scoring***
One possible strategy of implementing open-chain scoring is to go through all empty slots in a given column at the maximum available height for that column.
For a given empty slot, we look in a unit-square (all adjacent cells) around that gap and check for chains in every direction (except possibly directly upwards), adding to the score for each direction. The score is evaluated keeping in mind the following criteria.
1. A chain of a greater length should have a greater score. This is intuitive as chains of length 3 are winning chains and should be prioritized over chains of length 2 or 1, similarly chains of length 2 are potentially better than chains of length 1.
2. A chain of tokens of the same color as the player making the move should have a higher weight than a chain of the same length of the opponent player.
Some other important points to keep in mind are:
- A chain of a greater length must never be *overpowered* by multiple chains of some smaller lengths.
- The score for a column must always be a **scalar** quantity. That is, it must be the same regardless of whether it is a maximizing move or a minimizing move.

Considering all of that, the following formula has been devised:
$$S_{net} = \sum_{d \in D} S_d$$
where $S_d$ is
$$S_d = \sum\limits_{\substack{0\le n\le4 \\ G_{i,j} = G_{{i\pm 1},{j\pm 1}}}} m \times n \times a^n$$
	where $S_d$ is the score in a given direction (vertical, horizontal, diagonal),
	$n$ signifies which occurrence of the token is being evaluated (1st in chain, 2nd in chain, 3rd in chain)
	$m$ is a factor to prioritize whether the the chain is a player's or the opponents
	$a$ is an amplifying factor which is exponentiated by $n$ in order to have a greater score gap between chains of differing lengths. Using an exponential polynomial mitigates the possibility that a chain of a greater length will be overpowered by a chain of a smaller length.
	and the condition $G_{i,j} = G_{{i+\Delta i},{j+\Delta j}}$ is used to only evaluate the summation when the element at location $(i,j)$ in the grid $G$ is the same token as the next element $(i\pm 1, j\pm 1)$ being checked in a given direction.
 - - - 

### Implementation

#### Board & Evaluation:
The Board class contains the following attributes:
- `static int WIDTH`: Number of columns, 7.
- `static int HEIGHT`: Number of rows, 6.
- `int[][] grid`: Actual board represented as a 2D int array.
- `int moveCount`: Number of moves played so far.
- `Stack<Move> moves`: Collection of moves played so far.
And the following methods:
- `bool validMove(Move move)`: Returns whether the given move is a valid move that may be played.
- `void makeMove(Move move)`: Updates the `grid` with the player value (`-1`, `+1`) at the given move location. Pushes the latest move into the `move` stack.
- `void undoMove()`: Pops from the `move` stack and decrements `moveCount`.
- `bool checkGameWon()`: Returns whether a player won or not checking all possibilities.
- `int evalMoveScore()`: Returns the static evaluation for the given current board.
- `int[] getSortedMoves()`: Returns an array of column numbers sorted in the order of best estimated move to worst.
**Evaluating the Game State:**
- Tracking Open chains: 

```
public int evalMoveScore(int col, int target)
{
	int score = 0;
	if (unavailable move) { return score; }
	else { row = highestAvailableRow; }
	int counter = 0;
	int a = 10
	for the next 4 tokens below this row
	{
		m = G[row][col] == target ? 2 : 1;
		score += m * n * (a^n);
		if G[row - 1][col] != G[row][col] { break; }
	}
	// reset counter
	counter = 0;
	for the next 4 tokens to the right
	{
		m = G[row][col] == target ? 2 : 1;
		score += m * n * (a^n);
		if G[row][col + 1] != G[row][col] { break; }
	}
	for the next 4 tokens to the left
	{
		m = G[row][col] == target ? 2 : 1;
		score += m * n * (a^n);
		if G[row][col - 1] != G[row][col] { break; }
	}
	// reset counter
	and the same for diagonals...

	return score;
}
```
This method is used to evaluate the score for one given move. 
It is also known through strategy that a move made in the center is typically better than a move made towards the edge. Thus, with no evaluation, our program should prioritize moving in the following base column order `[3,2,4,1,5,0,6]`.
A function must be created that will sort the column order in such a way that it the ones with the highest score are ordered first, but if multiple columns have the same score (most likely 0), then they will be searched in the order of the base column order. An insertion sort-like algorithm can be used to do that by using an another array where the item at each index corresponds to the score of that column. In the code, this is done using the `getSortedMoves()` method.
#### The Minimax Method:
The Minimax method would take in the following parameters:
- `board`: The state of the board at the current evaluation.
- `players[]`: The collection of players playing the game. 
- `isMax`: A boolean value declaring whether the current player is a *maximizer* or *minimizer*.
- `alpha`: The best alternative for the maximizer
- `beta`: The best alternative for the minimizer
- `depth`: The maximum depth the algorithm may run till.
The Minimax method would return a number that would be the static evaluation for the given game state.

```
int runMinimax(Board board, Player[] players, bool isMax,int alpha, int beta, int depth) 
{
	// check win or loss
	if (board.checkGameWon())
	{
		return maximizerWin ? depth : -1 * depth;
	}
	// check draw
	else if (board.moveCount > 42) { return 0 }
	threshold = 20;
	int[] moveSeq = depth > threshold ? board.getSortedMoves() : baseColumnOrder
	if (isMax)
	{		
		if (depth <= alpha) { return depth; }
		
		// check all possible moves (all possible columns).
		for (int col in moveSeq)
		{
			// assuming p1 is maximizer.
			Move possibleMove = new Move(col, players[0]);
			if (invalidMove(possibleMove))
			{
				// skip evaluation if invalid.
				continue;
			}
			board.makeMove(possibleMove);
			
			// evaluate score for move.
			alpha = max(alpha, Minimax(board, players, false, depth -  1);
			// undo move to not manipulate board reference.
			board.undoMove();
			if (alpha >= beta) { return beta; }
		}
		return alpha
	}
	else
	{
		// check all possible moves (all possible columns).
		for (int col in moveSeq)
		{
			if (-1 * depth >= beta) { return -1 * depth; }
			
			// assuming p2 is minimizer.
			Move possibleMove = new Move(col, players[1]);
			if (invalidMove(possibleMove))
			{
				// skip evaluation if invalid.
				continue;
			}
			// if valid move.
			board.makeMove(possibleMove);
			
			// evaluate score for move.
			beta = min(beta, Minimax(board, players, true, depth - 1))
			
			// undo move to not manipulate board reference.
			board.undoMove();
			if (beta <= alpha) { return alpha; }
		}
		return beta;
	}
}
```

## Attribution
This Project was a group project for a data structures and algorithms course. 
Other Group Members Include:
@aadi219
@cheukman196
@Haley-K

## Contact
For any inquiries or questions, you can reach me at tyleroneildev@gmail.com
or on my linkedin at https://ca.linkedin.com/in/tyler-oneil-dev
