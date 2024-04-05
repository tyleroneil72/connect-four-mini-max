import java.util.Scanner;

public class Player {
    public String name;
    public Colour colour;

    public Player(String name, Colour colour) {
        this.name = name;
        this.colour = colour;
    }

    public Move getMove(Scanner scanner) {
        return Move.createMove(scanner, this);
    }
}