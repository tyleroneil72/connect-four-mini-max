public class Main {
    public static void checkFour(){

        Board b = new Board();

        Player p1 = new Player("P1", Colour.RED);
        Player p2 = new Player("P2", Colour.YELLOW);
        Player[] players = new Player[]{p1, p2};

        int y = 0;
        int x = 0;
        Move nextMove = null;

        b.printBoard();

        while(!b.checkGameWon(y, x) && b.moveCount < 42){

            nextMove = Move.createMove(players[b.moveCount % 2]);
            if(b.checkAvailableMove(nextMove) == -1){
                System.out.println("Invalid move.");
                continue;
            }

            b.makeMove(nextMove);
            b.printBoard();

            y = nextMove.row;
            x = nextMove.col;
        }
        if(b.moveCount < 42) {
            assert nextMove != null;
            System.out.println("Game over, " + nextMove.player.colour + " won!");
        }
        else
            System.out.println("Game over, Draw!");
    }

    public static void main(String[] args) {
        checkFour();
    }
}