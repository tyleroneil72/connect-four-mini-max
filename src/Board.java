import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Board {
    public final static int WIDTH = 7;
    public final static int HEIGHT = 6;
    public int[][] grid;
    public int moveCount;
    public String moveHistory;

    // stack of moves to track and undo moves.
    public Stack<Move> moves = new Stack<Move>();

    // incorporating players withing the board class to make minimax integration
    // easier.
    public Player[] players = new Player[2];
    private int currPlayer;

    public Board() {
        this.grid = new int[HEIGHT][WIDTH];
        this.moveCount = 0;
        this.moveHistory = "";
    }

    public Board(String moveHistory, Player[] players) {
        this.grid = new int[HEIGHT][WIDTH];
        this.moveCount = 0; // load board will handle that
        this.moveHistory = moveHistory;
        this.players = players;
        this.currPlayer = 0; // p1 is current player in the beginning.
        loadBoard();
    }

    // create deep copy of board
    public Board copyBoard() {
        Board duplicate = new Board();
        for (int i = 0; i < HEIGHT; i++)
            duplicate.grid[i] = Arrays.copyOf(grid[i], grid[i].length);
        duplicate.moveCount = moveCount;
        return duplicate;
    }

    public int[] possibleMoves() {
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for (int i = 0; i < WIDTH; i++) {
            if (grid[5][i] == 0)
                moveList.add(i);
        }

        int[] result = new int[moveList.size()];
        for (int i = 0; i < moveList.size(); i++) {
            result[i] = moveList.get(i);
        }
        return result;
    }

    // takes Move object, if column is not full, insert according to player color
    public int checkAvailableMove(Move move) {
        for (int i = 0; i < HEIGHT; i++) {
            if (grid[i][move.col] == 0) {
                move.row = i;
                return i;
            }
        }
        return -1;
    }

    public int checkAvailableMove(int col) {
        for (int i = 0; i < HEIGHT; i++) {
            if (grid[i][col] == 0) {
                return i;
            }
        }
        return -1;
    }

    public void makeMove(Move move) {
        int value = move.player.colour == Colour.RED ? 1 : -1;
        grid[move.row][move.col] = value;
        moveHistory += move.col;
        moves.push(move);
        moveCount++;
        // removed the printing because it was slowing down minimax,
        // print after move has been made outside the board class or in another
        // function.
    }

    public void undoMove() {
        Move lastMove = moves.pop();
        grid[lastMove.row][lastMove.col] = 0;
        moveCount--;
    }

    public boolean checkGameWon(int row, int col) {
        return checkVertical(row, col) || checkHorizontal(row, col) ||
                checkDiagonalRight(row, col) || checkDiagonalLeft(row, col);
    }

    public boolean checkVertical(int row, int col) {
        int target = grid[row][col];
        if (target == 0)
            return false;
        int counter = 0;

        for (int i = row; i >= 0; i--) {
            if (grid[i][col] == target)
                counter++;
            else
                counter = 0;
            if (counter == 4)
                return true;
        }
        return false;
    }

    /*
     * Two pointer approach used to check horizontal and diagonal wins. Replacing
     * checking entire rows and entire diags.
     * 2 direction pointers are initialized along with a counter, check until you
     * have same token at the directional pointer
     * or if you've reached a win threshold. (counter = 4)
     */
    public boolean checkHorizontal(int row, int col) {
        int target = grid[row][col];
        int left = col - 1;
        int right = col + 1;
        if (target == 0)
            return false;
        int counter = 1;
        // check left
        while (left >= 0 && grid[row][left] == target && counter <= 3) {
            left--;
            counter++;
        }
        // check right
        while (right < Board.WIDTH && grid[row][right] == target && counter <= 4) {
            right++;
            counter++;
        }
        if (counter >= 4)
            return true;
        return false;

    }

    public boolean checkDiagonalRight(int row, int col) {
        // checking for '/' chains
        // only checks a total of 4 times. O(1).
        int target = grid[row][col];
        if (target == 0)
            return false;
        int counter = 1;
        int x = col + 1, y = row + 1;
        while (x < WIDTH && y < HEIGHT && grid[y][x] == target && counter <= 3) {
            counter++;
            x++;
            y++;
        }
        x = col - 1;
        y = row - 1;
        while (x >= 0 && y >= 0 && grid[y][x] == target && counter <= 3) {
            counter++;
            x--;
            y--;
        }
        return counter == 4;
    }

    public boolean checkDiagonalLeft(int row, int col) {
        // checking for '\' chains
        // only checks a total of 4 times. O(1).
        int target = grid[row][col];
        if (target == 0)
            return false;
        int counter = 1;
        int x = col - 1, y = row + 1;
        while (x >= 0 && y < HEIGHT && grid[y][x] == target && counter <= 3) {
            counter++;
            x--;
            y++;
        }
        x = col + 1;
        y = row - 1;
        while (x < WIDTH && y >= 0 && grid[y][x] == target && counter <= 3) {
            counter++;
            x++;
            y--;
        }
        return counter == 4;
    }

    // double check returning column / cell
    private void loadBoard() {
        for (int i = 0; i < moveHistory.length(); i++) {
            try {
                int col = (int) moveHistory.charAt(i) - '0'; // get int value from char (non ASCII)
                int value = 1 + (moveCount % 2 * -2);
                Move move = new Move(col, players[currPlayer]);
                if (checkAvailableMove(move) != -1) {
                    moves.push(move);
                    grid[move.row][move.col] = value;
                }
                currPlayer = (currPlayer + 1) % 2; // switch player.
                moveCount++;

            } catch (Exception e) {
                System.out
                        .println("Load board failed, invalid value or winning condition met before completely loading");
                return;
            }
        }
    }

    public void printBoard() {
        // Color codes for red, yellow, and reset.
        String ANSI_RED = "\u001B[31m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        String TOKEN = "O";
        System.out.print("   ");
        for (int j = 0; j < WIDTH; j++) {
            System.out.print("| " + (j + 1) + " ");
        }
        System.out.println("|");

        for (int i = HEIGHT - 1; i >= 0; i--) {
            System.out.print((i + 1) + "  ");
            for (int j = 0; j < WIDTH; j++) {
                if (grid[i][j] == 1)
                    System.out.print("| " + ANSI_RED + TOKEN + ANSI_RESET + " ");
                else if (grid[i][j] == -1)
                    System.out.print("| " + ANSI_GREEN + TOKEN + ANSI_RESET + " ");
                else
                    System.out.print("|   ");
            }
            System.out.println("|");
        }
        System.out.println();
    }

    public int evalMoveScore(int col, int target) {
        int score = 0;
        int row = checkAvailableMove(col);
        if (row == -1) {
            return score;
        }
        /*
         * score for a column should be a static value regardless of whether a maximizer
         * or minimizer is playing.
         * weight of a token/coin (w) is evaluated using the following exponential
         * polynomial.
         * w += m * n * (a ^ n)
         * 'n' (counter) is the position of the token in the current sequence being
         * evaluated, (1 in chain, 2nd in chain and so on)
         * 'a' and 'm' are amplifying factors, where m emphasizes greater value if the
         * token being checked is of the same color as the player making the move.
         * and a is used to provide a greater distinction in value between chains of 2
         * and chains of 3.
         */

        int counter = 0;
        int a = 10;
        // get vertical score
        for (int i = row - 1; i >= 0 && grid[i][col] != 0 && i >= row - 4; i--) {
            int m = grid[i][col] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            counter++;
            if (i - 1 >= 0 && grid[i][col] != grid[i - 1][col])
                break;
        }
        counter = 0;
        // get horizontal score
        // check left
        for (int hL = col - 1; hL >= 0 && hL >= col - 4 && grid[row][hL] != 0; hL--) {
            int m = grid[row][hL] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            counter++;
            if (hL - 1 >= 0 && grid[row][hL] != grid[row][hL - 1])
                break;
        }
        // check right
        for (int hR = col + 1; hR < WIDTH && hR <= col + 4 && grid[row][hR] != 0; hR++) {
            int m = grid[row][hR] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            counter++;
            if (hR + 1 < WIDTH && grid[row][hR] != grid[row][hR + 1])
                break;
        }

        // get diagonal score
        // check '/'
        // up and right
        int drx = col + 1, dry = row + 1;
        for (counter = 0; drx < WIDTH && dry < HEIGHT && counter < 4; counter++, drx++, dry++) {
            int m = grid[dry][drx] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            if (drx + 1 < WIDTH && dry + 1 < HEIGHT && grid[dry][drx] != grid[dry + 1][drx + 1])
                break;
        }

        // down and left
        drx = col - 1;
        dry = row - 1;
        for (counter = 0; drx >= 0 && dry >= 0 && counter < 4; counter++, drx--, dry--) {
            int m = grid[dry][drx] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            if (drx - 1 >= 0 && dry - 1 >= 0 && grid[dry][drx] != grid[dry - 1][drx - 1])
                break;
        }

        // check '\'
        // up and left
        int dlx = col - 1, dly = row + 1;
        for (counter = 0; dlx >= 0 && dly < HEIGHT && counter < 4; counter++, dlx--, dly++) {
            int m = grid[dly][dlx] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            if (dlx - 1 >= 0 && dly + 1 < HEIGHT && grid[dly][dlx] != grid[dly + 1][dlx - 1])
                break;
        }
        // down and right
        dlx = col + 1;
        dly = row - 1;
        for (counter = 0; dlx < WIDTH && dly >= 0 && counter < 4; counter++, dlx++, dly--) {
            int m = grid[dly][dlx] == target ? 2 : 1;
            score += (counter * m * (Math.pow(a, counter)));
            if (dlx + 1 < WIDTH && dly - 1 >= 0 && grid[dly][dlx] != grid[dly - 1][dlx + 1])
                break;
        }
        return score;
    }

    public int[] getScoresForMoves(int target) // returns an array where element at each index corresponds to the score
                                               // for that column.
    {
        int[] scores = new int[WIDTH];
        for (int i : Minimax.columnOrder) {
            scores[i] = evalMoveScore(i, target);
        }
        return scores;
    }

    public int[] getSortedMoves(int target) // returns an array of column indices sorted w.r.t scores.
    {
        // sorting using insertion sort alg so that most of the base sequence is still
        // maintained even if scores are 0.
        // that is, if no significant evaluation can be made based on the current state,
        // check in order of center to edge.
        int[] baseSeq = Minimax.columnOrder.clone();
        int[] scores = getScoresForMoves(target);
        for (int i = 1; i < WIDTH; i++) {
            int pulled = baseSeq[i];
            int j = i - 1;
            while (j >= 0 && scores[pulled] > scores[baseSeq[j]]) {
                baseSeq[j + 1] = baseSeq[j];
                j--;
            }
            baseSeq[j + 1] = pulled;
        }
        return baseSeq;
    }
}
