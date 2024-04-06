public class AIPlayer extends Player {

    public AIPlayer(Colour colour) {
        super("MiniMax", colour);
    }

    public Move getMove(Board board) {
        int bestCol = Minimax.getBestMove(board);
        Move move = new Move(bestCol, this);
        board.checkAvailableMove(move);
        return move;
    }
}