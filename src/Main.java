import java.util.Scanner;

public class Main {
    // static members to be accessed by all app methods.
    private static Scanner scanner = new Scanner(System.in);
    private static int screenWidth = 40;
    
    private static void clearScreen() {
        // helper function to clear screen.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void app() {
        // method to dictate main flow of the application.
        clearScreen();

        splashScreen();
        int gameMode;
        while (true) 
        {
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
            checkFour();
        }
        else if (gameMode == 2) {
            // user selected Single-Player
            int difficulty;
            while (true) {
                // get difficulty of AI.
                selectDifficulty();
                System.out.print("Choose an option: ");
                Integer choice = getChoice(1, 3);
                if (choice == null) {
                    clearScreen();
                    continue;
                }
                difficulty = choice;
                break;
            }            
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
        // validates user input to return value within a provided range. Returns null if invalid input.
        String choice = scanner.nextLine();
        if (choice.matches("^[0-9]$")) {
            int chosen = Integer.parseInt(choice);
            if (chosen >= min && chosen <= max) {
                return chosen;
            }
        }       
        return null;
    }

    public static void selectDifficulty() {
        // app screen to display AI difficulty options.
        System.out.println("=".repeat(screenWidth));
        System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        String easy = formatMenuTextLjust("1. Easy");
        System.out.println(easy);
        String med = formatMenuTextLjust("2. Medium");
        System.out.println(med);
        String hard = formatMenuTextLjust("3. Hard");
        System.out.println(hard);
        System.out.println("||" + " ".repeat(screenWidth - 4) + "||");
        System.out.println("=".repeat(screenWidth));
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
            // Clear the console
            System.out.print("\033[H\033[2J");
            System.out.flush();
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
        // checkFour();
        app();
    }
}