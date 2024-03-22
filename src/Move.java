import java.util.InputMismatchException;
import java.util.Scanner;

public class Move {
    public int col;
    public int row;
    public Player player;

    public Move(int col, Player player){
        this.col = col;
        this.row = 0;
        this.player = player;
    }

    // create move object, only allows column count of 1-7
    // may return NULL

    public static Move createMove(Player player){
        Scanner scanner = new Scanner(System.in);
        System.out.println(player.colour + "'s turn. Enter column: ");
        while(true){
            try {
                int col = scanner.nextInt();
                if(col >= 1 && col <= 7) {
                    return new Move(col - 1, player);
                }
                System.out.println("Invalid column. Please enter a valid column (1-7).");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid column (1-7).");
                scanner.nextLine(); // Clear the input buffer
            }
        } 
    }

    public static Move createMove(int col, Player player){
        if(col >= 1 && col <= 7)
            return new Move(col - 1, player);
        return null;
    }
}
