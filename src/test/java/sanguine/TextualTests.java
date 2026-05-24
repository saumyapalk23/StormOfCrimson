package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.view.SanguineTextualView;

/**
 * Testing for SanguineTextualView.
 * Includes the specifics on how to generate card objects using textual rendering.
 * How to use textual view:
 * 1. Create a SanguineModel and start the game
 * 2. To create a view, type SanguineTextualView view = new SanguineTextualView(model);
 * 3. To visualize, print System.out.println(view.toString());
 */


public class TextualTests {

  private BasicSanguine model;
  private SanguineTextualView view;
  private List<Card> redDeck;
  private List<Card> blueDeck;

  /**
   * C reates a new model which is a new instantiation of basicSanguine.
   * Creates new test decks with 10 cards for easier implementation.
   */

  @Before
  public void setUp() {
    model = new BasicSanguine();
    // Create simple test decks with cards
    redDeck = createTestDeck(10);
    blueDeck = createTestDeck(10);
  }

  /**
   * Helper method to create a test deck with simple cards.
   * Method creates cards with cost 1, varying values, and simple influence patterns
   */
  private List<Card> createTestDeck(int size) {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      boolean[][] influence = createNewInfluence();
      deck.add(new Card("Card" + i, 1, i + 1, influence));
    }
    return deck;
  }

  /**
   * Creates a simple influence grid for testing.
   * Has influence in cardinal directions from center.
   */
  private boolean[][] createNewInfluence() {
    boolean[][] grid = new boolean[5][5];
    grid[1][2] = true; // up
    grid[3][2] = true; // down
    grid[2][1] = true; // left
    grid[2][3] = true; // right
    return grid;
  }


  @Test
  public void testInitialGameState() {
    model.startGame(redDeck, blueDeck, 3, 3, 3);

    view = new SanguineTextualView(model);
    String output = view.toString();

    System.out.println("Initial Game State:");
    System.out.println(output);
    System.out.println();

    // Verify key elements
    assertTrue(output.contains("Current Player: RED"));
    assertTrue(output.contains("RED Hand Size: 3"));
    assertTrue(output.contains("BLUE Hand Size: 3"));

    assertTrue(output.contains("1_1")); // Each row: RED pawn, empty, BLUE pawn
  }

  @Test
  public void testAfterCardPlacement() {
    // Start game
    model.startGame(redDeck, blueDeck, 3, 3, 3);

    // Place a card from RED's hand at position (0, 0)
    model.placeCard(0, 0, 0);

    view = new SanguineTextualView(model);
    String output = view.toString();

    System.out.println("After Card Placement:");
    System.out.println(output);
    System.out.println();

    assertTrue(output.contains("R")); // RED card on board

    assertTrue(output.contains("Current Player: BLUE"));
  }

  @Test
  public void testGameOver() {
    // Start game
    model.startGame(redDeck, blueDeck, 3, 3, 3);

    // Both players pass to end game
    model.passTurn(); // red passes
    model.passTurn(); // Blue passes

    view = new SanguineTextualView(model);
    String output = view.toString();

    System.out.println("Game Over State:");
    System.out.println(output);
    System.out.println();

    // Verify game over message appears
    assertTrue(output.contains("Game Over!"));
    assertTrue(output.contains("Total Score"));
  }

  @Test
  public void testRowScores() {
    // Start game
    model.startGame(redDeck, blueDeck, 3, 3, 3);

    model.placeCard(0, 0, 0);
    model.placeCard(0, 0, 2);

    view = new SanguineTextualView(model);
    String output = view.toString();

    System.out.println("With Row Scores:");
    System.out.println(output);
    System.out.println();

    // Output should contain row scores on both sides
    // Checks digits, matching spaces, and matching characters
    assertTrue(output.matches("(?s).*\\d+ [RB_123]+ \\d+.*"));
  }

  @Test
  public void testLargerBoard() {
    // Create larger decks for bigger game
    List<Card> bigRedDeck = createTestDeck(20);
    List<Card> bigBlueDeck = createTestDeck(20);

    // Start a 5x5 game
    model.startGame(bigRedDeck, bigBlueDeck, 5, 5, 5);

    view = new SanguineTextualView(model);
    String output = view.toString();

    System.out.println("5x5 Board:");
    System.out.println(output);
    System.out.println();

    // Should have 5 rows
    String[] lines = output.split("\n");
    int matching = 0;
    for (String line : lines) {
      if (line.matches("\\d+ [RB_123]+ \\d+")) {
        // Again, here it checks for digits, matching spaces, and matching characters
        matching++;
      }
    }
    assertEquals(5, matching);
  }
}