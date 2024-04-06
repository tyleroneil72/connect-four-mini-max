import java.util.HashMap;
import java.util.Scanner;

public class Main {
    // static members to be accessed by all app methods.
    private static Scanner scanner = new Scanner(System.in);
    private static int screenWidth = 40;

    public static void clearScreen() {
        // helper function to clear screen.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void app() {
        // method to dictate main flow of the application.
        clearScreen();

        splashScreen();
        int gameMode;
        while (true) {
            // get valid choice for main menu options.
            mainMenu();
            System.out.print("Choose an option: ");
            Integer choice = getChoice(1, 2);
            if (choice == null) {
                clearScreen();
                continue;
            }
            gameMode = choice;
            break;
        }
        clearScreen();
        if (gameMode == 1) {
            // user selected Multiplayer.
            System.out.print("Player 1 - Enter name: ");
            String p1Name = scanner.nextLine();
            while (p1Name.isEmpty()) {
                clearScreen();
                System.out.print("Player 1 - Enter name: ");
                p1Name = scanner.nextLine();
            }
            System.out.print("Choose your color [R | Y]: ");
            String p1Color = scanner.nextLine();
            while (!p1Color.matches("(^R$|^Y$)")) {
                System.out.print("Choose your color [R | Y]: ");
                p1Color = scanner.nextLine();
            }

            System.out.print("Player 2 - Enter name: ");
            String p2Name = scanner.nextLine();
            while (p2Name.isEmpty()) {
                clearScreen();
                System.out.print("Player 2 - Enter name: ");
                p2Name = scanner.nextLine();
            }
            Player p1 = new Player(p1Name, p1Color.equals("R") ? Colour.RED : Colour.YELLOW);
            Player p2 = new Player(p2Name, p1Color.equals("R") ? Colour.YELLOW : Colour.RED);

            playGame(p1, p2);

        } else if (gameMode == 2) {
            // user selected Single-Player
            // get user name
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            while (name.isEmpty()) {
                clearScreen();
                System.out.print("Enter name: ");
                name = scanner.nextLine();
            }

            // get user color
            System.out.print("Enter color [R | Y]: ");
            String color = scanner.nextLine();
            while (!color.matches("(^R$|^Y$)")) {
                System.out.print("Enter color [R | Y]: ");
                color = scanner.nextLine();
            }

            Player human = new Player(name, color.equals("R") ? Colour.RED : Colour.YELLOW);
            Player AI = new AIPlayer(color.equals("R") ? Colour.YELLOW : Colour.RED);

            // get player 1
            System.out.print("Would you like to go first? [Y | N]: ");
            String choice = scanner.nextLine();
            while (!choice.matches("(^Y$|^N$)")) {
                System.out.print("Would you like to go first? [Y | N]: ");
                choice = scanner.nextLine();
            }

            clearScreen();
            if (choice.equals("Y"))
                playGame(human, AI);
            else
                playGame(AI, human);

            
        }
    }

    private static String formatMenuTextCenter(String text) {
        // helper method to write center aligned text in the format of the menu.
        StringBuilder textFormatted = new StringBuilder();
        textFormatted.append("||");
        textFormatted.append(" ".repeat((screenWidth - text.length() - 4) / 2));
        textFormatted.append(text);
        textFormatted.append(" ".repeat((screenWidth - text.length() - 4) / 2));
        textFormatted.append("||");
        return textFormatted.toString();
    }

    private static String formatMenuTextLjust(String text) {
        // helper method to write Left Justified aligned text in the format of the menu.
        StringBuilder textFormatted = new StringBuilder();
        int padLeft = 12;
        textFormatted.append("||");
        textFormatted.append(" ".repeat(padLeft));
        textFormatted.append(text);
        textFormatted.append(" ".repeat(screenWidth - text.length() - padLeft - 4));
        textFormatted.append("||");
        return textFormatted.toString();

    }

    private static Integer getChoice(int min, int max) {
        // validates user input to return value within a provided range. Returns null if
        // invalid input.
        String choice = scanner.nextLine();
        if (choice.matches("^[0-9]$")) {
            int chosen = Integer.parseInt(choice);
            if (chosen >= min && chosen <= max) {
                return chosen;
            }
        }
        return null;
    }

    public static void splashScreen() {
        // welcome screen displayed on start of application.
        System.out.println("=".repeat(screenWidth));
        for (int i = 0; i < 2; i++) {
            System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        }

        String title = formatMenuTextCenter("CONNECT FOUR");
        System.out.println(title);

        System.out.println("||" + " ".repeat(screenWidth - 4) + "||");

        String toMainMenu = formatMenuTextCenter("Press Enter to Continue...");
        System.out.println(toMainMenu);

        System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        System.out.println("=".repeat(screenWidth));
        scanner.nextLine();
        clearScreen();
    }

    public static void mainMenu() {
        // app screen to display game modes.
        System.out.println("=".repeat(screenWidth));
        for (int i = 0; i < 2; i++) {
            System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        }
        String option1, option2;
        option1 = formatMenuTextLjust("1. VS Human");
        option2 = formatMenuTextLjust("2. VS AI");
        System.out.println(option1);
        System.out.println(option2);
        for (int i = 0; i < 2; i++) {
            System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        }
        System.out.println("=".repeat(screenWidth));
    }

    public static void playGame(Player p1, Player p2) {
        Board board = new Board(p1, p2);
        board.printBoard();  
        int x = 0, y = 0;
        Move nextMove = null;      
        while (board.moveCount < 42 && !board.checkGameWon(y, x)) {
            if (board.getCurrentPlayer() instanceof AIPlayer) {
                nextMove = ((AIPlayer) board.getCurrentPlayer()).getMove(board);
            }
            else {
                if (nextMove != null && nextMove.player instanceof AIPlayer)
                    System.out.println("MiniMax moved at: " + (nextMove.col + 1));
                nextMove = board.getCurrentPlayer().getMove(scanner);
            }

            if (board.checkAvailableMove(nextMove) == -1) {
                System.out.println("Invalid Move!");
                continue;
            }

            board.makeMove(nextMove);
            board.switchPlayer();

            clearScreen();
            board.printBoard();
    
            y = nextMove.row;
            x = nextMove.col;
            
        }
        if (board.moveCount < 42) {
            assert nextMove != null;
            System.out.println("Game Over! " + nextMove.player.name + " wins!");
        }
        else {
            System.out.println("Game Over! Draw!");
        }
    }
    public static void main(String[] args) {
        app();
    }
     
}