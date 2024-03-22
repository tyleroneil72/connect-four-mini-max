import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Board {
    public final static int WIDTH = 7;
    public final static int HEIGHT = 6;
    public int[][] grid;
    public int moveCount;
    public String moveHistory;


    public Board(){
        this.grid = new int[HEIGHT][WIDTH];
        this.moveCount = 0;
        this.moveHistory = "";
    }

    public Board(String moveHistory){
        this.grid = new int[HEIGHT][WIDTH];
        this.moveCount = 0; // load board will handle that
        this.moveHistory = moveHistory;
        loadBoard();
    }

    // create deep copy of board
    public Board copyBoard(){
        Board duplicate  = new Board();
        for(int i = 0; i < HEIGHT; i++)
           duplicate.grid[i] = Arrays.copyOf(grid[i], grid[i].length);
        duplicate.moveCount = moveCount;
        return duplicate;
    }

    public int[] possibleMoves(){
        ArrayList<Integer> moveList = new ArrayList<Integer>();
        for(int i = 0; i < WIDTH; i++){
            if(grid[5][i] == 0)
                moveList.add(i);
        }

        int[] result = new int[moveList.size()];
        for(int i = 0; i < moveList.size(); i++){
            result[i] = moveList.get(i);
        }
        return result;
    }

    // takes Move object, if column is not full, insert according to player color
    public int checkAvailableMove(Move move){
        for(int i = 0; i < HEIGHT; i++){
            if(grid[i][move.col] == 0){
                move.row = i;
                return i;
            }
        }
        return -1;
    }

    public int checkAvailableMove(int col){
        for(int i = 0; i < HEIGHT; i++){
            if(grid[i][col] == 0){
                return i;
            }
        }
        return -1;
    }

    public void makeMove(Move move){
        int value = move.player.colour == Colour.RED? 1 : -1;
        grid[move.row][move.col] = value;
        moveHistory += move.col;
        moveCount++;
        System.out.println(move.player.colour + " placed at column " + (move.col+1));
    }

    public boolean checkGameWon(int row, int col){
        return checkVertical(row, col) || checkHorizontal(row, col) ||
                checkDiagonalUp(row, col) || checkDiagonalDown(row, col);
    }

    public boolean checkVertical(int row, int col){
        int target = grid[row][col];
        if(target == 0) return false;
        int counter = 0;

        for(int i = row; i >= 0; i--){
//            System.out.println("(" + i + ","+ col + ") = " + grid[i][col]);
            if(grid[i][col] == target)
                counter++;
            else
                counter = 0;
            if(counter == 4)
                return true;
        }
        return false;
    }

    public boolean checkHorizontal(int row, int col){
        int target = grid[row][col];
        if(target == 0) return false;
        int counter = 0;

        for(int i = 0; i < WIDTH; i++){
//            System.out.println("(" + row + "," + i + ") = " + grid[row][i]);
            if(grid[row][i] == target)
                counter++;
            else
                counter = 0;
            if(counter == 4)
                return true;
        }
        return false;
    }

    public boolean checkDiagonalUp(int row, int col){
        int target = grid[row][col];
        if(target == 0) return false;
        int counter = 0;

        while(row > 0 && col > 0){
            row--;
            col--;
        }

        while(row < HEIGHT && col < WIDTH){
            if(grid[row][col] == target)
                counter++;
            else
                counter = 0;
            if(counter == 4) return true;
            row++;
            col++;
        }
        return false;
    }

    public boolean checkDiagonalDown(int row, int col){
        int target = grid[row][col];
        if(target == 0) return false;
        int counter = 0;

        while(row > 0 && col < 6){
            row--;
            col++;
        }

        while(row < HEIGHT && col >= 0){
            if(grid[row][col] == target)
                counter++;
            else
                counter = 0;
            if(counter == 4) return true;
            row++;
            col--;
        }
        return false;
    }

    // double check returning column / cell
    private void loadBoard(){
        for(int i = 0; i < moveHistory.length(); i++){
            try {
                int col = (int) moveHistory.charAt(i) - '0'; // get int value from char (non ASCII)
                int value = 1 + (moveCount % 2 * -2);
                int row = checkAvailableMove(col);

                grid[row][col] = value;
                moveCount++;

            } catch (Exception e){
                System.out.println("Load board failed, invalid value or winning condition met before completely loading");
                return;
            }
        }
    }

    public void printBoard(){
        System.out.print("   ");
        for(int j = 0; j < WIDTH; j++){
            System.out.print("| " + (j+1) + " ");
        }
        System.out.println("|");

        for(int i = HEIGHT - 1; i >= 0; i--){
            System.out.print((i+1) + "  ");
            for(int j = 0; j < WIDTH; j++){
                if(grid[i][j] == 1)
                    System.out.print("| R ");
                else if(grid[i][j] == -1)
                    System.out.print("| Y ");
                else
                    System.out.print("|   ");
            }
            System.out.println("|");
        }
        System.out.println();
    }




}
