import java.util.Arrays;
import java.util.HashMap;

public class Minimax
{

    public static int[] columnOrder = new int[] {3,2,4,1,5,0,6};
    
    public static int runMinimax(Board board, Player[] players, boolean isMax, int alpha, int beta, int depth) 
    {
        Move lastMove = board.moves.peek();

        // check for terminal states, win/loss
        if (board.checkGameWon(lastMove.row, lastMove.col))
        {
            if (board.moveCount % 2 == 0) {
                return -1 * depth;
            }
            else {
                return depth;
            }
        }
        else if (board.moveCount > 42)  // check for draw.
        {
            return 0;
        }
        // at each call for minimax at a depth < some threshold, check moves in some estimated sequence.
        int deepeningThreshold = 20;
        int[] moveSeq = columnOrder;
        if (depth > deepeningThreshold)
        {
           moveSeq = board.getSortedMoves(isMax ? 1 : -1); 
        }
        if (isMax)
        {
            // check if depth has exceeded alpha  depth.
            if (depth <= alpha) 
            {
                // if alpha has been established at a lower depth, then any winning state at a lower depth will be less valuble than alpha
                // therefore skip and return alpha.
                return depth;
            }
            
            for (int i : moveSeq) {
                Move possibleMove = Move.createMove(i + 1, players[0]);
                if (board.checkAvailableMove(possibleMove) == -1)
                {
                    continue;
                }
                board.makeMove(possibleMove);
                alpha = Math.max(alpha, runMinimax(board, players, false, alpha, beta, depth - 1));
                board.undoMove();
                if (alpha >= beta)
                {
                    return beta;
                }
            }
            return alpha;
        }
        else
        {
            // check if depth has exceeded beta depth.
            if (-1 * depth >= beta) 
            {
                // if beta has been established at a lower depth, then any winning state at a lower depth will be less valuble than beta
                // therefore skip and return beta.
                return -1 * depth;
            }
            for (int i : moveSeq) {
                Move possibleMove = Move.createMove(i + 1, players[1]);
                if (board.checkAvailableMove(possibleMove) == -1)
                {
                    continue;
                }
                board.makeMove(possibleMove);
                beta = Math.min(beta, runMinimax(board, players, true, alpha, beta, depth - 1));
                board.undoMove();
                if (beta <= alpha) {
                    return beta;
                }
            }
            return beta;
        }
    }

    public static void getBestMove(Board board, Player[] players)
    {
        int bestCol = -1;
        int bestScore = 1000;
        int[] scores = new int[7];
        for (int col : columnOrder) {
            Move possibleMove = new Move(col, players[1]);
            if (board.checkAvailableMove(possibleMove) == -1)
            {
                continue;
            }
            board.makeMove(possibleMove);
            int score = runMinimax(board, players, true, -1000, 1000, 42 - board.moveCount);            
            scores[col] = score;
            board.undoMove();
            if (score < bestScore)
            {
                bestScore = score;
                bestCol = col;
            }
        }
        Move bestMove = new Move(bestCol, players[1]);
        board.checkAvailableMove(bestMove);
        board.makeMove(bestMove);
        System.out.println("Best Score: " + bestScore);
        System.out.println("Best move: " + bestCol);
        board.printBoard();
        for (int i = 0; i < scores.length; i++) {
            System.out.println("For column " + i + " score is: " + scores[i]);
        }
        
    }
    

}
