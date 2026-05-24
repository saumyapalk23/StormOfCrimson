package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Board;
import sanguine.model.BoardImpl;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Deck;
import sanguine.model.Player;
import sanguine.model.PlayerHand;
import sanguine.model.SanguineModel;

/**
 * Comprehensive testing for the Sanguine game model.
 * Here, we test all the aspects of our model class that are public-facing.
 *
 */
public class ModelTests {

  private SanguineModel model;
  private List<Card> redDeck;
  private List<Card> blueDeck;

  /**
   * Our setUp() function which creates a new implementation of Sanguine impl.
   * We also create copies of a new deck in order to test red/blue before running each function.
   */

  @Before
  public void setUp() {
    model = new BasicSanguine();
    redDeck = createTestDeck();
    blueDeck = createTestDeck();
  }

  /**
   * This is a helper method that makes a test deck so we can easily apply it to tests.
   * It initializes deck objects for red/blue players.
   * Ensures that there are 15 cards: it's a valid deck.
   *
   * @return a list of cards that is a deck
   */

  private List<Card> createTestDeck() {
    List<Card> deck = new ArrayList<>();
    boolean[][] newInfluence = createNewInfluence();

    for (int i = 0; i < 15; i++) {
      deck.add(new Card("Card" + i, 1, 3, newInfluence));
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

  // Card Tests

  @Test
  public void testCardCreation() {
    boolean[][] influence = createNewInfluence();
    Card card = new Card("TestCard", 2, 5, influence);

    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(5, card.getValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCardWithNullName() {
    new Card(null, 1, 1, createNewInfluence());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCardWithInvalidCost() {
    new Card("Test", 0, 1, createNewInfluence());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCardWithInvalidValue() {
    new Card("Test", 1, 0, createNewInfluence());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCardWithInvalidInfluenceGrid() {
    boolean[][] invalid = new boolean[4][4];
    new Card("Test", 1, 1, invalid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCardWithCenterInfluence() {
    boolean[][] influence = createNewInfluence();
    influence[2][2] = true; // Invalid: center must be false
    new Card("Test", 1, 1, influence);
  }

  @Test
  public void testCardInfluenceAt() {
    boolean[][] influence = createNewInfluence();
    Card card = new Card("Test", 1, 1, influence);

    assertTrue(card.hasInfluenceAt(1, 2));
    assertFalse(card.hasInfluenceAt(2, 2));
    assertFalse(card.hasInfluenceAt(0, 0));
  }

  @Test
  public void testCardMirroredInfluence() {
    boolean[][] influence = createNewInfluence();
    Card card = new Card("Test", 1, 1, influence);
    boolean[][] mirrored = card.getMirroredInfluenceGrid();
    // Left becomes right
    assertTrue(mirrored[2][3]);
    // Right becomes left
    assertTrue(mirrored[2][1]);
  }

  @Test
  public void testCardEquality() {
    boolean[][] influence = createNewInfluence();
    Card card1 = new Card("Test", 1, 1, influence);
    Card card2 = new Card("Test", 1, 1, influence);
    Card card3 = new Card("Different", 1, 1, influence);

    assertEquals(card1, card2);
    assertNotEquals(card1, card3);
  }

  // Cell Tests

  @Test
  public void testEmptyCell() {
    Cell cell = new Cell();
    assertTrue(cell.isEmpty());
    assertFalse(cell.hasPawns());
    assertFalse(cell.hasCard());
    assertNull(cell.getOwner());
  }

  @Test
  public void testCellWithPawns() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 2);

    assertFalse(cell.isEmpty());
    assertTrue(cell.hasPawns());
    assertFalse(cell.hasCard());
    assertEquals(Player.RED, cell.getOwner());
    assertEquals(2, cell.getPawnCount());
  }

  @Test
  public void testCellWithCard() {
    Cell cell = new Cell();
    Card card = new Card("Test", 1, 1, createNewInfluence());
    cell.setCard(Player.BLUE, card);

    assertFalse(cell.isEmpty());
    assertFalse(cell.hasPawns());
    assertTrue(cell.hasCard());
    assertEquals(Player.BLUE, cell.getOwner());
    assertEquals(card, cell.getCard());
  }

  @Test
  public void testAddPawnToCell() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 1);
    cell.addPawn();

    assertEquals(2, cell.getPawnCount());
    cell.addPawn();
    assertEquals(3, cell.getPawnCount());
  }

  @Test(expected = IllegalStateException.class)
  public void testAddPawnToMaxPawns() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 3);
    cell.addPawn(); // Should throw
  }

  @Test
  public void testChangeOwner() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 2);
    cell.changeOwner(Player.BLUE);

    assertEquals(Player.BLUE, cell.getOwner());
    assertEquals(2, cell.getPawnCount());
  }

  @Test
  public void testClearCell() {
    Cell cell = new Cell();
    cell.setPawns(Player.RED, 2);
    cell.clearCell();

    assertTrue(cell.isEmpty());
    assertNull(cell.getOwner());
  }

  //Board Tests

  @Test
  public void testBoardCreation() {
    Board board = new BoardImpl(3, 5);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getCols());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBoardWithInvalidRows() {
    new BoardImpl(0, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBoardWithEvenColumns() {
    new BoardImpl(3, 4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBoardWithOneColumn() {
    new BoardImpl(3, 1);
  }

  @Test
  public void testBoardInitialization() {
    Board board = new BoardImpl(3, 5);

    // Ensuring that the first column should have red pawns
    for (int row = 0; row < 3; row++) {
      Cell cell = board.getCell(row, 0);
      assertTrue(cell.hasPawns());
      assertEquals(Player.RED, cell.getOwner());
      assertEquals(1, cell.getPawnCount());
    }

    // Ensuring that the last column should have blue pawns
    for (int row = 0; row < 3; row++) {
      Cell cell = board.getCell(row, 4);
      assertTrue(cell.hasPawns());
      assertEquals(Player.BLUE, cell.getOwner());
      assertEquals(1, cell.getPawnCount());
    }

    // Ensuring that the middle columns should be empty
    for (int row = 0; row < 3; row++) {
      for (int col = 1; col < 4; col++) {
        Cell cell = board.getCell(row, col);
        assertTrue(cell.isEmpty());
      }
    }
  }

  @Test
  public void testPlaceCardOnBoard() {
    Board board = new BoardImpl(3, 5);
    Card card = new Card("Test", 1, 1, createNewInfluence());

    board.placeCard(card, Player.RED, 1, 2);
    Cell cell = board.getCell(1, 2);

    assertTrue(cell.hasCard());
    assertEquals(card, cell.getCard());
    assertEquals(Player.RED, cell.getOwner());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardOnOccupiedCell() {
    Board board = new BoardImpl(3, 5);
    Card card1 = new Card("Test1", 1, 1, createNewInfluence());
    Card card2 = new Card("Test2", 1, 1, createNewInfluence());

    board.placeCard(card1, Player.RED, 1, 2);
    board.placeCard(card2, Player.RED, 1, 2); // Should throw
  }

  @Test
  public void testAddPawnToBoard() {
    Board board = new BoardImpl(3, 5);
    board.addPawn(Player.RED, 1, 2);

    Cell cell = board.getCell(1, 2);
    assertTrue(cell.hasPawns());
    assertEquals(Player.RED, cell.getOwner());
    assertEquals(1, cell.getPawnCount());
  }

  @Test
  public void testSetPawnOwner() {
    Board board = new BoardImpl(3, 5);
    // First column has RED pawns by default
    board.setPawnOwner(Player.BLUE, 0, 0);

    Cell cell = board.getCell(0, 0);
    assertEquals(Player.BLUE, cell.getOwner());
    assertEquals(1, cell.getPawnCount());
  }

  @Test
  public void testGetCardsInRow() {
    Board board = new BoardImpl(3, 5);
    Card card1 = new Card("Test1", 1, 1, createNewInfluence());
    Card card2 = new Card("Test2", 1, 1, createNewInfluence());

    board.addPawn(Player.RED, 1, 1);
    board.placeCard(card1, Player.RED, 1, 1);
    board.addPawn(Player.RED, 1, 2);
    board.placeCard(card2, Player.RED, 1, 2);

    List<Card> cards = board.getCardsInRow(1, Player.RED);
    assertEquals(2, cards.size());
    assertTrue(cards.contains(card1));
    assertTrue(cards.contains(card2));
  }

  // Deck Tests

  @Test
  public void testDeckCreation() {
    List<Card> cards = createTestDeck();
    Deck deck = new Deck(Player.RED, cards);

    assertEquals(Player.RED, deck.getOwner());
    assertEquals(15, deck.size());
    assertFalse(deck.isEmpty());
  }

  @Test
  public void testDrawCard() {
    List<Card> cards = createTestDeck();
    Deck deck = new Deck(Player.RED, cards);

    Card drawn = deck.drawCard();
    assertNotNull(drawn);
    assertEquals(14, deck.size());
  }

  @Test
  public void testDrawFromEmptyDeck() {
    Deck deck = new Deck(Player.RED, new ArrayList<>());
    assertNull(deck.drawCard());
  }

  // PlayerHand Tests

  @Test
  public void testPlayerHandCreation() {
    PlayerHand hand = new PlayerHand(Player.RED);
    assertEquals(Player.RED, hand.getOwner());
    assertTrue(hand.isEmpty());
    assertEquals(0, hand.size());
  }

  @Test
  public void testAddCardToHand() {
    PlayerHand hand = new PlayerHand(Player.RED);
    Card card = new Card("Test", 1, 1, createNewInfluence());

    hand.addCard(card);
    assertEquals(1, hand.size());
    assertFalse(hand.isEmpty());
  }

  @Test
  public void testRemoveCardFromHand() {
    PlayerHand hand = new PlayerHand(Player.RED);
    Card card = new Card("Test", 1, 1, createNewInfluence());

    hand.addCard(card);
    Card removed = hand.removeCard(0);

    assertEquals(card, removed);
    assertEquals(0, hand.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveInvalidCardIndex() {
    PlayerHand hand = new PlayerHand(Player.RED);
    hand.removeCard(0);
  }

  // BasicSanguine Game Tests

  @Test
  public void testStartGame() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    assertEquals(Player.RED, model.getCurrentPlayer());
    assertFalse(model.isGameOver());
    assertEquals(5, model.getHand(Player.RED).size());
    assertEquals(5, model.getHand(Player.BLUE).size());
    assertEquals(10, model.getDeckSize(Player.RED));
    assertEquals(10, model.getDeckSize(Player.BLUE));
  }

  @Test(expected = IllegalStateException.class)
  public void testStartGameTwice() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    model.startGame(redDeck, blueDeck, 3, 5, 5); // Should throw
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNullDeck() {
    model.startGame(null, blueDeck, 3, 5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithLargeHandSize() {
    model.startGame(redDeck, blueDeck, 3, 5, 10);
  }

  @Test
  public void testPlaceCard() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // RED can place on their pawns in first column
    model.placeCard(0, 0, 0);

    assertEquals(Player.BLUE, model.getCurrentPlayer());
    assertEquals(4, model.getHand(Player.RED).size());
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardBeforeGameStarts() {
    model.placeCard(0, 0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardWithInvalidHandIndex() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    model.placeCard(10, 0, 0);
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardOnEmptyCell() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    model.placeCard(0, 1, 2);
  }

  @Test
  public void testPassTurn() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    model.passTurn();
    assertEquals(Player.BLUE, model.getCurrentPlayer());
    assertFalse(model.isGameOver());

    model.passTurn();
    assertEquals(Player.RED, model.getCurrentPlayer());
    assertTrue(model.isGameOver());
  }

  @Test
  public void testGameOverAfterBothPass() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    assertFalse(model.isGameOver());
    model.passTurn();
    assertFalse(model.isGameOver());
    model.passTurn();
    assertTrue(model.isGameOver());
  }

  @Test
  public void testPassResetAfterCardPlayed() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    model.passTurn(); // RED passes
    model.placeCard(0, 0, 4); // BLUE plays
    model.passTurn(); // RED passes again
    assertFalse(model.isGameOver()); // Game not over since BLUE played between passes
  }

  @Test
  public void testGetScore() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // Initially, no cards placed, so scores should be 0
    assertEquals(0, model.getScore(Player.RED));
    assertEquals(0, model.getScore(Player.BLUE));
  }

  @Test
  public void testGetWinner() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // Place cards to create a score difference
    model.placeCard(0, 0, 0); // RED plays
    model.passTurn(); // BLUE passes
    model.passTurn(); // RED passes - game over

    assertTrue(model.isGameOver());

    int redScore = model.getScore(Player.RED);
    int blueScore = model.getScore(Player.BLUE);

    if (redScore > blueScore) {
      assertEquals(Player.RED, model.getWinner());
    } else if (blueScore > redScore) {
      assertEquals(Player.BLUE, model.getWinner());
    } else {
      assertNull(model.getWinner()); // Tie
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWinnerBeforeGameOver() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    model.getWinner(); // Should throw
  }

  @Test
  public void testMoveCard() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    // RED should be able to place on their pawns
    assertTrue(model.moveCard(0, 0, 0));

    // RED should not be able to place on empty cells
    assertFalse(model.moveCard(0, 1, 2));

    // RED should not be able to place on BLUE's pawns
    assertFalse(model.moveCard(0, 0, 4));
  }

  @Test
  public void testCardInfluenceApplication() {
    // Create a deck with cards that have specific influence
    boolean[][] influence = new boolean[5][5];
    influence[1][2] = true; // Above card position

    List<Card> customRedDeck = new ArrayList<>();
    List<Card> customBlueDeck = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      customRedDeck.add(new Card("RedCard" + i, 1, 3, influence));
      customBlueDeck.add(new Card("BlueCard" + i, 1, 3, influence));
    }

    model.startGame(customRedDeck, customBlueDeck, 3, 5, 3);

    // Place a card and check influence
    model.placeCard(0, 1, 0); // RED plays at (1, 0)

    Board board = model.getBoard();

    // Check that card was placed
    assertTrue(board.getCell(1, 0).hasCard());

    // Check influence at position above (0, 0)
    Cell influencedCell = board.getCell(0, 0);
    // The cell at (0, 0) had RED pawns, so RED should have gained pawns there
    assertTrue(influencedCell.hasPawns());
    assertEquals(Player.RED, influencedCell.getOwner());
  }

  @Test
  public void testAlternatingTurns() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    assertEquals(Player.RED, model.getCurrentPlayer());
    model.placeCard(0, 0, 0);

    assertEquals(Player.BLUE, model.getCurrentPlayer());
    model.placeCard(0, 0, 4);

    assertEquals(Player.RED, model.getCurrentPlayer());
  }

  @Test
  public void testGetBoard() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    Board board = model.getBoard();

    assertNotNull(board);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getCols());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetBoardBeforeGameStarts() {
    model.getBoard();
  }

  @Test
  public void testGetHand() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);

    List<Card> redHand = model.getHand(Player.RED);
    List<Card> blueHand = model.getHand(Player.BLUE);

    assertEquals(5, redHand.size());
    assertEquals(5, blueHand.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetHandWithNullPlayer() {
    model.startGame(redDeck, blueDeck, 3, 5, 5);
    model.getHand(null);
  }

  @Test
  public void testDeckDrawing() {
    model.startGame(redDeck, blueDeck, 3, 5, 3);

    int initialRedDeckSize = model.getDeckSize(Player.RED);
    int initialBlueDeckSize = model.getDeckSize(Player.BLUE);
    model.placeCard(0, 0, 0); // RED plays, turn ends, BLUE draw
    // RED's deck unchanged, BLUE draw a card
    assertEquals(initialRedDeckSize, model.getDeckSize(Player.RED));
    assertEquals(initialBlueDeckSize - 1, model.getDeckSize(Player.BLUE));
    model.placeCard(0, 0, 4); // BLUE plays, turn ends, RED draws
    // Now RED draw a card
    assertEquals(initialRedDeckSize - 1, model.getDeckSize(Player.RED));
  }

  // Player Enum Tests

  @Test
  public void testPlayerSwitchColor() {
    assertEquals(Player.BLUE, Player.RED.switchColor());
    assertEquals(Player.RED, Player.BLUE.switchColor());
  }

  // New test game initializer

  @Test
  public void testGameInitialization() {
    int rows = 3;
    int cols = 5;
    int handSize = 3;

    model.startGame(redDeck, blueDeck, rows, cols, handSize);

    assertEquals(Player.RED, model.getCurrentPlayer());
    assertFalse(model.isGameOver());
    assertEquals(handSize, model.getHand(Player.RED).size());
    assertEquals(handSize, model.getHand(Player.BLUE).size());
    assertEquals(model.getBoard().getRows(), rows);
    assertEquals(model.getBoard().getCols(), cols);
  }
}