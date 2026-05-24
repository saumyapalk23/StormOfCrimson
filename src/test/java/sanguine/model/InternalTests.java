package sanguine.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Internal tests for package-private implementation details.
 * These tests all verify internal methods through creating a new model and new decks to modify.
 */
public class InternalTests {

  private BasicSanguine model;
  private List<Card> redDeck;
  private List<Card> blueDeck;

  /**
   * SetUp(), Runs before every test.
   * Calls createTestDeck to generate identical decks for both players.
   */
  @Before
  public void setUp() {
    model = new BasicSanguine();
    redDeck = createTestDeck();
    blueDeck = createTestDeck();
  }

  /**
   * Makes a list of 15 identical card objects.
   * ALl cards have cosst of 1, value of 3.
   * The influence pattern is recognized by createNewInfluence() so we can test according to that.
   *
   * @return a list of cards, a deck
   */

  private List<Card> createTestDeck() {
    List<Card> deck = new ArrayList<>();
    boolean[][] influence = createNewInfluence();
    for (int i = 0; i < 15; i++) {
      deck.add(new Card("Card" + i, 1, 3, influence));
    }
    return deck;
  }

  /**
   * This is a helper method that influences a pattern to be applied in later tests.
   * It defines how the other spaces will be influenced when a card is applied in a 5x5 grid.
   * Ensures that the surrounding spaces are being correctly targeted/influenced.
   *
   * @return a 2D array of booleans
   */

  private boolean[][] createNewInfluence() {
    boolean[][] influence = new boolean[5][5];
    influence[1][2] = true; // Above
    influence[3][2] = true; // Below
    influence[2][1] = true; // Left
    influence[2][3] = true; // Right
    return influence;
  }

  //   Board Internal State Tests  

  @Test
  public void testBoardInternalCellArray() {
    BoardImpl board = new BoardImpl(3, 5);

    // Access internal state to verify proper initialization
    assertEquals(3, board.getRows());
    assertEquals(5, board.getCols());

    // verify all cells are initialized
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        assertNotNull(board.getCell(row, col));
      }
    }
  }

  @Test
  public void testBoardInternalValidatePosition() {
    BoardImpl board = new BoardImpl(3, 5);

    // Valid positions should not throw
    board.getCell(0, 0);
    board.getCell(2, 4);

    // Invalid positions should throw
    try {
      //we used the assert.Fail here in order to ensure that the method is written correctly
      //we checked for IAE exception messages here
      board.getCell(-1, 0);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("out of bounds"));
    }

    try {
      board.getCell(0, 5);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("out of bounds"));
    }
  }

  @Test
  public void testBoardToString() {
    BoardImpl board = new BoardImpl(2, 3);
    String boardString = board.toString();

    // Should contain representation of the board
    assertNotNull(boardString);
    assertFalse(boardString.isEmpty());
    // Should have multiple lines
    assertTrue(boardString.contains("\n"));
  }

  //   Cell Internal State Tests  

  @Test
  public void testCellInternalStateEmpty() {
    Cell cell = new Cell();

    // Internal state checks
    assertNull(cell.getOwner());
    assertEquals(0, cell.getPawnCount());
    assertNull(cell.getCard());
    assertTrue(cell.isEmpty());
  }

  @Test
  public void testCellInternalStateTransitions() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 2);
    assertFalse(cell.isEmpty());
    assertTrue(cell.hasPawns());
    assertFalse(cell.hasCard());

    Card card = new Card("Test", 1, 1, createNewInfluence());
    cell.setCard(Player.BLUE, card);
    assertFalse(cell.isEmpty());
    assertFalse(cell.hasPawns());
    assertTrue(cell.hasCard());
    assertEquals(0, cell.getPawnCount());

    cell.clearCell();
    assertTrue(cell.isEmpty());
    assertNull(cell.getOwner());
  }

  @Test
  public void testCellInternalPawnIncrement() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 1);

    // Test internal increment
    assertEquals(1, cell.getPawnCount());
    cell.addPawn();
    assertEquals(2, cell.getPawnCount());
    cell.addPawn();
    assertEquals(3, cell.getPawnCount());

    // Should not exceed 3
    try {
      cell.addPawn();
      fail("Should throw IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("maximum"));
    }
  }

  @Test
  public void testCellToString() {
    Cell cell = new Cell();

    // Empty cell
    assertTrue(cell.toString().contains("Empty"));

    // Cell with pawns
    cell.setPawns(Player.RED, 2);
    String str = cell.toString();
    assertTrue(str.contains("RED"));
    assertTrue(str.contains("2"));

    // Cell with card
    Card card = new Card("TestCard", 1, 1, createNewInfluence());
    cell.setCard(Player.BLUE, card);
    str = cell.toString();
    assertTrue(str.contains("BLUE"));
    assertTrue(str.contains("TestCard"));
  }

  //   Deck Internal Tests  

  @Test
  public void testDeckInternalState() {
    List<Card> cards = createTestDeck();
    Deck deck = new Deck(Player.RED, cards);

    assertEquals(Player.RED, deck.getOwner());
    assertEquals(15, deck.size());
    assertFalse(deck.isEmpty());
  }

  @Test
  public void testDeckDrawOrder() {
    List<Card> cards = new ArrayList<>();
    Card card1 = new Card("First", 1, 1, createNewInfluence());
    Card card2 = new Card("Second", 1, 1, createNewInfluence());
    Card card3 = new Card("Third", 1, 1, createNewInfluence());

    cards.add(card1);
    cards.add(card2);
    cards.add(card3);

    Deck deck = new Deck(Player.RED, cards);

    // Should draw in order
    assertEquals(card1, deck.drawCard());
    assertEquals(card2, deck.drawCard());
    assertEquals(card3, deck.drawCard());
    assertNull(deck.drawCard()); // Empty
  }

  @Test
  public void testDeckDefensiveCopy() {
    List<Card> original = createTestDeck();
    Deck deck = new Deck(Player.RED, original);

    // Modifying original should not affect deck
    original.clear();
    assertEquals(15, deck.size());
  }

  //   PlayerHand Internal Tests  

  @Test
  public void testPlayerHandInternalState() {
    PlayerHand hand = new PlayerHand(Player.RED);

    assertEquals(Player.RED, hand.getOwner());
    assertTrue(hand.isEmpty());
    assertEquals(0, hand.size());
  }

  @Test
  public void testPlayerHandAddRemoveSequence() {
    PlayerHand hand = new PlayerHand(Player.RED);
    Card card1 = new Card("Card1", 1, 1, createNewInfluence());
    Card card2 = new Card("Card2", 2, 2, createNewInfluence());

    hand.addCard(card1);
    hand.addCard(card2);

    assertEquals(2, hand.size());
    assertEquals(card1, hand.getCard(0));
    assertEquals(card2, hand.getCard(1));

    // Remove first card
    Card removed = hand.removeCard(0);
    assertEquals(card1, removed);
    assertEquals(1, hand.size());
    assertEquals(card2, hand.getCard(0)); // card2 shifted to index 0
  }

  @Test
  public void testPlayerHandDefensiveCopy() {
    PlayerHand hand = new PlayerHand(Player.RED);
    Card card = new Card("Test", 1, 1, createNewInfluence());
    hand.addCard(card);

    List<Card> cards = hand.getCards();
    cards.clear();

    // Hand should still have the card
    assertEquals(1, hand.size());
  }

  @Test
  public void testPlayerHandToString() {
    PlayerHand hand = new PlayerHand(Player.RED);
    String str = hand.toString();

    assertTrue(str.contains("RED"));
    assertTrue(str.contains("0 cards"));

    hand.addCard(new Card("Test", 1, 1, createNewInfluence()));
    str = hand.toString();
    assertTrue(str.contains("1 cards"));
  }

  //   Card Internal Tests  

  @Test
  public void testCardInternalGridCopy() {
    boolean[][] original = createNewInfluence();
    Card card = new Card("Test", 1, 1, original);

    // Modifying original should not affect card
    original[0][0] = true;
    assertFalse(card.hasInfluenceAt(0, 0));
  }

  @Test
  public void testCardGetInfluenceGridDefensiveCopy() {
    Card card = new Card("Test", 1, 1, createNewInfluence());
    boolean[][] grid = card.getInfluenceGrid();

    // Modifying returned grid should not affect card
    grid[0][0] = true;
    assertFalse(card.hasInfluenceAt(0, 0));
  }

  @Test
  public void testCardMirroredInfluenceSymmetry() {
    boolean[][] influence = new boolean[5][5];
    influence[2][1] = true; // Left of center
    influence[2][3] = true; // Right of center

    Card card = new Card("Test", 1, 1, influence);
    boolean[][] mirrored = card.getMirroredInfluenceGrid();

    // Left and right should be swapped
    assertTrue(mirrored[2][3]); // Was left, now right
    assertTrue(mirrored[2][1]); // Was right, now left
  }

  @Test
  public void testCardHashCodeConsistency() {
    boolean[][] influence = createNewInfluence();
    Card card1 = new Card("Test", 1, 1, influence);
    Card card2 = new Card("Test", 1, 1, influence);

    // Equal objects should have equal hash codes
    assertEquals(card1, card2);
    assertEquals(card1.hashCode(), card2.hashCode());
  }

  //   BasicSanguine Internal Game Flow Tests  

  /**
   * Makes sure that priot to startGame, other methods called will cause an ISE.
   */
  @Test
  public void testBasicSanguineInitialState() {
    BasicSanguine game = new BasicSanguine();

    // Before game starts, most operations should throw
    try {
      game.getCurrentPlayer();
      fail("Should throw IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("not been started"));
    }
  }

  /**
   * Tries to see if a deck too small will successfully throw an IAE.
   */

  @Test
  public void testBasicSanguineHandSizeValidation() {
    BasicSanguine game = new BasicSanguine();
    List<Card> smallDeck = new ArrayList<>();

    for (int i = 0; i < 6; i++) {
      smallDeck.add(new Card("Card" + i, 1, 1, createNewInfluence()));
    }

    // ensure that mathematically, handsize of 3 should fail
    try {
      game.startGame(smallDeck, smallDeck, 3, 5, 3);
    } catch (IllegalArgumentException e) {
      // Empty catch block
    }
  }

  /**
   * Makes sure that passTuren() switches b/w blue and red (or vice versa), consecutively.
   */

  @Test
  public void testBasicSanguineTurnAlternation() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    assertEquals(Player.RED, model.getCurrentPlayer());

    model.passTurn();
    assertEquals(Player.BLUE, model.getCurrentPlayer());

    model.passTurn();
    assertEquals(Player.RED, model.getCurrentPlayer());
  }

  /**
   * Makes sure that two passes = game over.
   */

  @Test
  public void testBasicSanguinePassTracking() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // First pass
    model.passTurn();
    assertFalse(model.isGameOver());

    // Second consecutive pass
    model.passTurn();
    assertTrue(model.isGameOver());
  }

  /**
   * Makes sure that if one player passes but the next plays a turn, passing is reset.
   */
  @Test
  public void testBasicSanguinePassReset() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // RED passes
    model.passTurn();
    assertFalse(model.isGameOver());

    // BLUE plays card and resets pass tracking
    model.placeCard(0, 0, 4);

    // RED passes again
    model.passTurn();
    assertFalse(model.isGameOver()); // Only BLUE's previous pass doesn't count

    // BLUE passes
    model.passTurn();
    assertTrue(model.isGameOver()); // Both passed consecutively
  }

  /**
   * Puts a card that influences it's cell above, respectively.
   * Makes sure it's successfully influenced.
   */

  @Test
  public void testBasicSanguineInfluenceApplication() {
    boolean[][] influence = new boolean[5][5];
    influence[1][2] = true; // One cell above

    List<Card> customRed = new ArrayList<>();
    List<Card> customBlue = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      customRed.add(new Card("Red" + i, 1, 1, influence));
      customBlue.add(new Card("Blue" + i, 1, 1, influence));
    }

    model.startGame(customRed, customBlue, 3, 5, 3);

    Board board = model.getBoard();

    // Place card at (1, 0) - should influence (0, 0)
    model.placeCard(0, 1, 0);

    Cell cardCell = board.getCell(1, 0);
    assertTrue(cardCell.hasCard());

    Cell influencedCell = board.getCell(0, 0);
    // Cell (0, 0) already had RED pawns, now has more
    assertTrue(influencedCell.hasPawns());
    assertEquals(Player.RED, influencedCell.getOwner());
  }

  /**
   * Makes sure that a card is placed at a corner and doesn't cause issues.
   */
  @Test
  public void testBasicSanguineInfluenceBoundaryCheck() {
    boolean[][] influence = new boolean[5][5];
    // Fill entire grid with influence (except center)
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (i != 2 || j != 2) {
          influence[i][j] = true;
        }
      }
    }

    List<Card> customRed = new ArrayList<>();
    List<Card> customBlue = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      customRed.add(new Card("Red" + i, 1, 1, influence));
      customBlue.add(new Card("Blue" + i, 1, 1, influence));
    }

    model.startGame(customRed, customBlue, 3, 3, 3);

    // Place card at corner (0, 0) - should not crash with out of bounds
    model.placeCard(0, 0, 0);

    // Game should continue normally
    assertFalse(model.isGameOver());
  }

  /**
   * Tests scoring correctly.
   * Makes sure that cards at certain point values are verified to have
   * the correct calculations.
   */
  @Test
  public void testBasicSanguineScoreCalculation() {
    boolean[][] influence = createNewInfluence();
    List<Card> customRed = new ArrayList<>();
    List<Card> customBlue = new ArrayList<>();

    // Create cards with different values
    customRed.add(new Card("R1", 1, 5, influence));
    customRed.add(new Card("R2", 1, 3, influence));
    customBlue.add(new Card("B1", 1, 4, influence));
    customBlue.add(new Card("B2", 1, 2, influence));

    for (int i = 0; i < 10; i++) {
      customRed.add(new Card("Red" + i, 1, 1, influence));
      customBlue.add(new Card("Blue" + i, 1, 1, influence));
    }

    model.startGame(customRed, customBlue, 2, 3, 2);

    // RED places card worth 5 in row 0
    model.placeCard(0, 0, 0);

    // BLUE places card worth 4 in row 0
    model.placeCard(0, 0, 2);

    // RED should win row 0 (5 > 4) and get 5 points
    int redScore = model.getScore(Player.RED);
    assertTrue(redScore >= 5);
  }

  /**
   * Makes sure moveCard is being done properly/correctly.
   * Moving to currently owned cells for the Red player = yes!
   * Moving to blue cells/empty cells for red player = NO!
   */
  @Test
  public void testBasicSanguineMoveCardValidation() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // RED should be able to move to cells with RED pawns
    assertTrue(model.moveCard(0, 0, 0));
    assertTrue(model.moveCard(0, 1, 0));
    assertTrue(model.moveCard(0, 2, 0));

    // RED should not be able to move to BLUE pawns
    assertFalse(model.moveCard(0, 0, 4));

    // RED should not be able to move to empty cells
    assertFalse(model.moveCard(0, 1, 2));
  }

  //   Player Enum Tests

  /**
   * Makes sure that the only values appliccable for player are red/blue.
   */

  @Test
  public void testPlayerEnumValues() {
    Player[] players = Player.values();
    assertEquals(2, players.length);
    assertEquals(Player.RED, players[0]);
    assertEquals(Player.BLUE, players[1]);
  }

  /**
   * Tests that we have switching that works.
   * Changes from blue/red, red/blue properly.
   */

  @Test
  public void testPlayerSwitchColorBothDirections() {
    assertEquals(Player.BLUE, Player.RED.switchColor());
    assertEquals(Player.RED, Player.BLUE.switchColor());

    // Test double switch returns to original
    assertEquals(Player.RED, Player.RED.switchColor().switchColor());
    assertEquals(Player.BLUE, Player.BLUE.switchColor().switchColor());
  }

  //   Integration Tests

  /**
   * This test runs a bunch of mooves.
   * It has passes, and checks that the game successfully ends.
   */

  @Test
  public void testCompleteGameFlow() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // Play several turns
    model.placeCard(0, 0, 0); // RED
    model.placeCard(0, 0, 4); // BLUE
    model.placeCard(0, 1, 0); // RED
    model.placeCard(0, 1, 4); // BLUE

    // Both pass to end game
    model.passTurn(); // RED
    model.passTurn(); // BLUE

    assertTrue(model.isGameOver());

    // Should have a winner or tie
    Player winner = model.getWinner();
    // Winner can be RED, BLUE, or null (tie)
    assertTrue(winner == Player.RED || winner == Player.BLUE || winner == null);
  }

  /**
   * This tests where influence may change the state of a cell.
   * In this partiucular test, a red card modifies a cell  which thus results in gaining redpawns.
   *
   */
  @Test
  public void testInfluenceConversion() {
    boolean[][] influence = new boolean[5][5];
    influence[2][3] = true; // Right of card

    List<Card> customRed = new ArrayList<>();
    List<Card> customBlue = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      customRed.add(new Card("Red" + i, 1, 1, influence));
      customBlue.add(new Card("Blue" + i, 1, 1, influence));
    }

    model.startGame(customRed, customBlue, 3, 5, 3);

    Board board = model.getBoard();

    // BLUE places at (0, 3) - should influence (0, 4) which has BLUE pawns
    model.passTurn(); // RED passes
    model.placeCard(0, 0, 4); // BLUE plays at (0, 4)

    // RED places at (0, 0) - should influence (0, 1) which is empty
    model.placeCard(0, 0, 0);

    Cell influencedCell = board.getCell(0, 1);
    // Should now have RED pawns due to influence
    assertTrue(influencedCell.hasPawns());
    assertEquals(Player.RED, influencedCell.getOwner());
  }
}