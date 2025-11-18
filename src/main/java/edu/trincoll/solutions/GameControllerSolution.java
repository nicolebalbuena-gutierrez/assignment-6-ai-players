package edu.trincoll.solutions;

import edu.trincoll.game.command.CommandInvoker;
import edu.trincoll.game.command.GameCommand;
import edu.trincoll.game.model.Character;
import edu.trincoll.game.player.GameState;
import edu.trincoll.game.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SOLUTION: Complete implementation of GameController with all TODOs solved.
 *
 * This demonstrates:
 * - TODO 4: Main game loop with turn alternation
 * - TODO 5: Individual turn processing
 *
 * Design Patterns:
 * - FACADE: Simplifies complex game loop interactions
 * - MEDIATOR: Coordinates between players, characters, and commands
 * - ITERATOR: Manages turn order
 */
public class GameControllerSolution {
    private final List<Character> team1;
    private final List<Character> team2;
    private final Map<Character, Player> playerMap;
    private final CommandInvoker invoker;
    private GameState gameState;

    public GameControllerSolution(List<Character> team1,
                                 List<Character> team2,
                                 Map<Character, Player> playerMap) {
        this.team1 = new ArrayList<>(team1);
        this.team2 = new ArrayList<>(team2);
        this.playerMap = new HashMap<>(playerMap);
        this.invoker = new CommandInvoker();
        this.gameState = GameState.initial();
    }

    /**
     * SOLUTION for TODO 4: Main game loop implementation.
     *
     * The game loop:
     * 1. Processes all team1 characters' turns
     * 2. Checks for game over
     * 3. Processes all team2 characters' turns
     * 4. Checks for game over
     * 5. Advances to next round
     * 6. Repeats until one team is defeated
     */
    public void playGame() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("GAME START!");
        System.out.println("=".repeat(60));

        displayTeamSetup();

        while (!isGameOver()) {
            displayRoundHeader();

            // Team 1's turn
            for (Character character : team1) {
                if (isGameOver()) break;
                if (character.getStats().health() > 0) {
                    processTurn(character, team1, team2);
                }
            }

            if (isGameOver()) break;

            // Team 2's turn
            for (Character character : team2) {
                if (isGameOver()) break;
                if (character.getStats().health() > 0) {
                    processTurn(character, team2, team1);
                }
            }

            // Advance to next round
            gameState = gameState.nextRound();

            if (!isGameOver()) {
                displayRoundSummary();
            }
        }

        displayResult();
    }

    /**
     * SOLUTION for TODO 5: Process a single character's turn.
     *
     * Steps:
     * 1. Get the player controlling this character
     * 2. Ask player to decide action
     * 3. Execute the command
     * 4. Display result
     * 5. Update game state
     */
    private void processTurn(Character character,
                            List<Character> allies,
                            List<Character> enemies) {
        // Skip defeated characters
        if (character.getStats().health() <= 0) {
            return;
        }

        System.out.println("\n" + character.getName() + "'s turn...");

        // Get the player controlling this character
        Player player = playerMap.get(character);
        if (player == null) {
            System.out.println("ERROR: No player assigned to " + character.getName());
            return;
        }

        // Get the player's decision
        GameCommand command = player.decideAction(character, allies, enemies, gameState);

        // Execute the command
        invoker.executeCommand(command);

        // Display the result
        displayActionResult(command, character);

        // Update game state
        gameState = gameState.nextTurn()
            .withUndo(true, invoker.getCommandHistory().size());
    }

    /**
     * Checks if the game is over.
     */
    private boolean isGameOver() {
        boolean team1Alive = team1.stream()
            .anyMatch(c -> c.getStats().health() > 0);
        boolean team2Alive = team2.stream()
            .anyMatch(c -> c.getStats().health() > 0);

        return !team1Alive || !team2Alive;
    }

    /**
     * Displays the initial team setup.
     */
    private void displayTeamSetup() {
        System.out.println("\n=== Team Setup ===");
        System.out.println("\nTeam 1:");
        for (Character c : team1) {
            Player p = playerMap.get(c);
            String playerType = p.getClass().getSimpleName();
            System.out.printf("  - %s (%s) - %s%n",
                c.getName(), c.getType(), playerType);
        }

        System.out.println("\nTeam 2:");
        for (Character c : team2) {
            Player p = playerMap.get(c);
            String playerType = p.getClass().getSimpleName();
            System.out.printf("  - %s (%s) - %s%n",
                c.getName(), c.getType(), playerType);
        }
    }

    /**
     * Displays round header.
     */
    private void displayRoundHeader() {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("TURN %d - ROUND %d%n",
            gameState.turnNumber(), gameState.roundNumber());
        System.out.println("=".repeat(60));

        // Display current team status
        System.out.println("\nTeam 1 Status:");
        for (Character c : team1) {
            displayCharacterStatus(c);
        }

        System.out.println("\nTeam 2 Status:");
        for (Character c : team2) {
            displayCharacterStatus(c);
        }
    }

    /**
     * Displays round summary.
     */
    private void displayRoundSummary() {
        System.out.println("\n--- Round " + gameState.roundNumber() + " Complete ---");
        System.out.println("Team 1: " + countAlive(team1) + "/" + team1.size() + " alive");
        System.out.println("Team 2: " + countAlive(team2) + "/" + team2.size() + " alive");
    }

    /**
     * Displays the game result.
     */
    public void displayResult() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("GAME OVER");
        System.out.println("=".repeat(60));

        boolean team1Wins = team1.stream().anyMatch(c -> c.getStats().health() > 0);

        if (team1Wins) {
            System.out.println("🏆 Team 1 wins!");
        } else {
            System.out.println("🏆 Team 2 wins!");
        }

        System.out.println("\nFinal Status:");
        System.out.println("\nTeam 1:");
        for (Character c : team1) {
            displayCharacterStatus(c);
        }

        System.out.println("\nTeam 2:");
        for (Character c : team2) {
            displayCharacterStatus(c);
        }

        System.out.println("\nGame Statistics:");
        System.out.println("Total turns played: " + gameState.turnNumber());
        System.out.println("Total rounds: " + gameState.roundNumber());
        System.out.println("Total commands executed: " + gameState.commandHistorySize());
    }

    /**
     * Displays a character's current status.
     */
    private void displayCharacterStatus(Character c) {
        String status = c.getStats().health() > 0 ? "Alive" : "💀 Defeated";
        double healthPercent = c.getStats().maxHealth() > 0 ?
            (double) c.getStats().health() / c.getStats().maxHealth() * 100 : 0;

        System.out.printf("  %s (%s): %d/%d HP (%.0f%%) - %s%n",
            c.getName(),
            c.getType(),
            Math.max(0, c.getStats().health()),
            c.getStats().maxHealth(),
            Math.max(0, healthPercent),
            status);
    }

    /**
     * Displays the result of an action.
     */
    private void displayActionResult(GameCommand command, Character actor) {
        // The command's execute method should have already printed details
        // This is just a separator for clarity
        System.out.println("---");
    }

    /**
     * Counts alive characters in a team.
     */
    private int countAlive(List<Character> team) {
        return (int) team.stream()
            .filter(c -> c.getStats().health() > 0)
            .count();
    }

    // Getters for testing
    public GameState getGameState() {
        return gameState;
    }

    public List<Character> getTeam1() {
        return new ArrayList<>(team1);
    }

    public List<Character> getTeam2() {
        return new ArrayList<>(team2);
    }

    public CommandInvoker getInvoker() {
        return invoker;
    }
}
