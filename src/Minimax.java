import java.util.Arrays;
import java.util.HashMap;

public class Minimax {
    public static int[] columnOrder = new int[] { 3, 2, 4, 1, 5, 0, 6 };

    public static int runMinimax(Board board, boolean isMax, int alpha, int beta, int depth) {
        
        /*
         * Minimax algorithm with alpha-beta pruning, move ordering and some sort of
         * iterative deepining(?)
         * -> assumes player 1 is always a maximizer and player 2 is always a minimizer
         * The algorithm uses depth to distinguish between winning moves, i.e. a winning
         * move that requires less moves is better.
         */
        Move lastMove = board.moves.peek();
        // check for terminal states, win/loss
       
        if (board.checkGameWon(lastMove.row, lastMove.col)) {
            if (board.moveCount % 2 == 0) {
                // player 2 will always win on an even move count.
                return -1 * depth;
            } else {
                return depth;
            }
        } else if (board.moveCount > 42) // check for draw.
        {
            return 0;
        }
        // at each call for minimax at a depth < some threshold, check moves in some
        // estimated sequence.
        int deepeningThreshold = 20;
        int[] moveSeq = columnOrder;
        if (depth > deepeningThreshold) {
            moveSeq = board.getSortedMoves(isMax ? 1 : -1);
        }
        if (isMax) {
            // check if depth has exceeded alpha (any move encountered after this will be
            // worse than the winning alternative).
            if (depth <= alpha) {
                // if alpha has been established at a lower depth, then any winning state at a
                // lower depth will be less valuble than alpha
                // therefore skip.
                return depth;
            }

            for (int i : moveSeq) {
                Move possibleMove = Move.createMove(i + 1, board.players[0]);
                if (board.checkAvailableMove(possibleMove) == -1) {
                    continue;
                }
                board.makeMove(possibleMove);
                alpha = Math.max(alpha, runMinimax(board, false, alpha, beta, depth - 1));
                board.undoMove();
                if (alpha >= beta) {
                    return beta; // ignore value.
                }
            }
            return alpha;
        } else {
            // check if depth has exceeded beta depth (any move encountered after this will
            // be worse than the winning alternative)
            if (-1 * depth >= beta) {
                // if beta has been established at a lower depth, then any winning state at a
                // lower depth will be less valuble than beta
                // therefore skip.
                return -1 * depth;
            }
            for (int i : moveSeq) {
                Move possibleMove = Move.createMove(i + 1, board.players[1]);
                if (board.checkAvailableMove(possibleMove) == -1) {
                    continue;
                }
                board.makeMove(possibleMove);
                beta = Math.min(beta, runMinimax(board, true, alpha, beta, depth - 1));
                board.undoMove();
                if (beta <= alpha) {
                    return alpha; // ignore value
                }
            }
            return beta;
        }
    }

    public static int getBestMove(Board board) {
        int bestCol = -1;
        boolean isMax = board.players[0] instanceof AIPlayer;
        int bestScore = isMax ? -100000 : 100000;
        // int[] scores = new int[7]; // if you want to see the evaluated scores for
        // each available col.
        for (int col : columnOrder) {
            Move possibleMove = new Move(col, board.getCurrentPlayer());
            if (board.checkAvailableMove(possibleMove) == -1) {
                continue;
            }
            board.makeMove(possibleMove);
            int score = runMinimax(board, !isMax, -100000, 100000, 42 - board.moveCount);
            // scores[col] = score;

            board.undoMove();
            if (isMax) {
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
            else {
                if (score < bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }

        // // test where move is.
        // Move bestMove = new Move(bestCol, players[1]);
        // board.checkAvailableMove(bestMove);
        // board.makeMove(bestMove);
        // System.out.println("Best Score: " + bestScore);
        // System.out.println("Best move: " + bestCol);
        // board.printBoard();

        // // test to see scores for each available col.
        // for (int i = 0; i < scores.length; i++) {
        // System.out.println("For column " + i + " score is: " + scores[i]);
        // }

        return bestCol;
    }
}