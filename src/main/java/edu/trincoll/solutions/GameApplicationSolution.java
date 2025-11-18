package edu.trincoll.solutions;

import edu.trincoll.game.factory.CharacterFactory;
import edu.trincoll.game.model.Character;
import edu.trincoll.game.player.HumanPlayer;
import edu.trincoll.game.player.Player;
import edu.trincoll.game.player.RuleBasedPlayer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * SOLUTION: Complete Spring Boot application with all TODOs solved.
 *
 * This demonstrates:
 * - TODO 6: Complete team configuration with all player types
 * - Integration of all design patterns
 * - Spring AI configuration with multiple providers
 *
 * To run this solution version:
 *   1. Set environment variables for API keys
 *   2. Run: ./gradlew bootRun -Pmain=edu.trincoll.solutions.GameApplicationSolution
 *
 * Or use the main method directly from your IDE.
 */
@SpringBootApplication(scanBasePackages = "edu.trincoll.game.config")
public class GameApplicationSolution {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GameApplicationSolution.class, args);
        // Exit after CommandLineRunner completes
        SpringApplication.exit(context, () -> 0);
    }

    /**
     * SOLUTION for TODO 6: Complete team configuration.
     *
     * This sets up a game with:
     * - Team 1: Mixed human and rule-based AI
     * - Team 2: All three LLM models (GPT-5, Claude Sonnet 4.5, Gemini 2.5 Pro)
     */
    @Bean
    public CommandLineRunner runSolution(
            @Qualifier("openAiChatClient") ChatClient openAiClient,
            @Qualifier("anthropicChatClient") ChatClient anthropicClient) {

        return args -> {
            displayWelcome();

            // Let user choose configuration
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nChoose game mode:");
            System.out.println("1. Demo Mode (Human + AI vs 2 LLMs)");
            System.out.println("2. All AI Mode (Watch LLMs battle each other)");
            System.out.println("3. Human Mode (You control Team 1)");
            System.out.print("Select mode (1-3): ");

            String choice = scanner.nextLine().trim();

            GameControllerSolution controller = switch (choice) {
                case "2" -> createAllAIConfiguration(openAiClient, anthropicClient);
                case "3" -> createHumanConfiguration(openAiClient, anthropicClient);
                default -> createDemoConfiguration(openAiClient, anthropicClient);
            };

            controller.playGame();
        };
    }

    /**
     * Demo configuration: Human + RuleBasedAI vs 2 LLMs
     */
    private GameControllerSolution createDemoConfiguration(
            ChatClient openAiClient,
            ChatClient anthropicClient) {

        System.out.println("\n🎮 Demo Mode: Human + AI vs 2 LLMs");

        // Team 1: Human Warrior + RuleBasedAI Mage
        Character humanWarrior = CharacterFactory.createWarrior("Conan");
        Character aiMage = CharacterFactory.createMage("Gandalf");
        List<Character> team1 = List.of(humanWarrior, aiMage);

        // Team 2: Two LLM players (GPT-5 and Claude)
        Character gptArcher = CharacterFactory.createArcher("Legolas");
        Character claudeRogue = CharacterFactory.createRogue("Shadow");
        List<Character> team2 = List.of(gptArcher, claudeRogue);

        // Map characters to players
        Map<Character, Player> playerMap = new HashMap<>();
        playerMap.put(humanWarrior, new HumanPlayer());
        playerMap.put(aiMage, new RuleBasedPlayer());
        playerMap.put(gptArcher, new LLMPlayerSolution(openAiClient, "GPT-5"));
        playerMap.put(claudeRogue, new LLMPlayerSolution(anthropicClient, "Claude-Sonnet-4.5"));

        return new GameControllerSolution(team1, team2, playerMap);
    }

    /**
     * All AI configuration: Watch LLMs battle each other
     */
    private GameControllerSolution createAllAIConfiguration(
            ChatClient openAiClient,
            ChatClient anthropicClient) {

        System.out.println("\n🤖 All AI Mode: Watch LLMs battle!");

        // Team 1: GPT + Claude
        Character gpt1 = CharacterFactory.createWarrior("GPT-Warrior");
        Character claude1 = CharacterFactory.createMage("Claude-Mage");
        List<Character> team1 = List.of(gpt1, claude1);

        // Team 2: Different character types with same models
        Character gpt2 = CharacterFactory.createArcher("GPT-Archer");
        Character claude2 = CharacterFactory.createRogue("Claude-Rogue");
        List<Character> team2 = List.of(gpt2, claude2);

        // All controlled by LLMs
        Map<Character, Player> playerMap = new HashMap<>();
        playerMap.put(gpt1, new LLMPlayerSolution(openAiClient, "GPT-5-T1"));
        playerMap.put(claude1, new LLMPlayerSolution(anthropicClient, "Claude-4.5-T1"));
        playerMap.put(gpt2, new LLMPlayerSolution(openAiClient, "GPT-5-T2"));
        playerMap.put(claude2, new LLMPlayerSolution(anthropicClient, "Claude-4.5-T2"));

        return new GameControllerSolution(team1, team2, playerMap);
    }

    /**
     * Human configuration: Player controls entire Team 1
     */
    private GameControllerSolution createHumanConfiguration(
            ChatClient openAiClient,
            ChatClient anthropicClient) {

        System.out.println("\n👤 Human Mode: You control Team 1!");

        // Team 1: All human controlled
        Character warrior = CharacterFactory.createWarrior("Hero");
        Character mage = CharacterFactory.createMage("Merlin");
        List<Character> team1 = List.of(warrior, mage);

        // Team 2: Two LLM players
        Character gptWarrior = CharacterFactory.createWarrior("AI-Tank");
        Character claudeRogue = CharacterFactory.createRogue("AI-Assassin");
        List<Character> team2 = List.of(gptWarrior, claudeRogue);

        // Human controls team1, LLMs control team2
        Map<Character, Player> playerMap = new HashMap<>();
        playerMap.put(warrior, new HumanPlayer());
        playerMap.put(mage, new HumanPlayer());
        playerMap.put(gptWarrior, new LLMPlayerSolution(openAiClient, "GPT-5"));
        playerMap.put(claudeRogue, new LLMPlayerSolution(anthropicClient, "Claude-Sonnet-4.5"));

        return new GameControllerSolution(team1, team2, playerMap);
    }

    /**
     * Displays welcome message.
     */
    private void displayWelcome() {
        System.out.println("""
            ============================================================
            AI-POWERED RPG GAME - SOLUTION VERSION
            ============================================================

            This is the complete reference implementation demonstrating:

            DESIGN PATTERNS:
            ✓ Strategy Pattern: Different AI decision-making algorithms
            ✓ Command Pattern: Undoable game actions
            ✓ Factory Pattern: Character creation
            ✓ Builder Pattern: Complex object construction
            ✓ Adapter Pattern: LLM text → Game commands
            ✓ Facade Pattern: Simplified game loop
            ✓ Mediator Pattern: Component coordination

            PLAYERS:
            ✓ Human: You control via console
            ✓ LLM-based: GPT-5, Claude Sonnet 4.5
            ✓ Rule-based AI: Simple if-then logic

            SPRING AI INTEGRATION:
            ✓ ChatClient abstraction
            ✓ Multiple LLM providers (OpenAI & Anthropic)
            ✓ Prompt engineering
            ✓ Robust error handling
            ============================================================
            """);
    }
}
