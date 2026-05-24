package sanguine.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.strategy.HumanPlayer;
import sanguine.strategy.PlayerType;
import sanguine.view.SanguineView;

/**
 * Tests for the SanguineController class.
 */
public class SanguineControllerTest {

  private SanguineModel model;
  private MockView mockView;
  private PlayerType humanPlayer;
  private SanguineController controller;

  /**
   * Mock view for testing.
   */
  private static class MockView implements SanguineView {
    private boolean visible = false;
    private int refreshCount = 0;
    private Features listener = null;

    @Override
    public void setVisible(boolean visible) {
      this.visible = visible;
    }

    @Override
    public void refresh() {
      this.refreshCount++;
    }

    @Override
    public void addFeaturesListener(Features features) {
      this.listener = features;
    }

    public int getRefreshCount() {
      return refreshCount;
    }

    public Features getListener() {
      return listener;
    }
  }

  /**
   * Before setup that runs prior to all tests.
   * Creates the deck, the controllers, the views, etc to be tested (mocks).
   */
  @Before
  public void setUp() {
    // Creat deck for testing
    final List<Card> deck = createTestDeck();

    // model
    model = new BasicSanguine();

    // mock view
    mockView = new MockView();

    // human player
    humanPlayer = new HumanPlayer(Player.RED);

    // controller
    controller = new SanguineController(model, Player.RED, humanPlayer, mockView);

    //controller as model listener
    model.addModelStatusListener(controller);

    //start game
    model.startGame(deck, deck, 3, 5, 3);
  }

  private List<Card> createTestDeck() {
    List<Card> deck = new ArrayList<>();
    boolean[][] grid = new boolean[5][5];
    grid[2][1] = true;

    for (int i = 0; i < 20; i++) {
      deck.add(new Card("Card" + i, 1, 5, grid));
    }
    return deck;
  }

  @Test
  public void testControllerRegistersAsListener() {
    assertNotNull(mockView.getListener());
  }

  @Test
  public void testRedPlayerCanMoveOnFirstTurn() {
    controller.handleCardClick(0, Player.RED);
    controller.handleCellClick(0, 0);
    try {
      controller.handleConfirm();
    } catch (Exception e) {
      fail("RED player should be able to move on their turn");
    }
  }

  @Test
  public void testCannotMoveWithoutSelectingCard() {
    controller.handleCellClick(0, 0);
    int refreshesBefore = mockView.getRefreshCount();
    controller.handleConfirm();
    assertEquals(refreshesBefore, mockView.getRefreshCount());
  }

  @Test
  public void testCannotMoveWithoutSelectingCell() {
    controller.handleCardClick(0, Player.RED);
    int refreshesBefore = mockView.getRefreshCount();
    controller.handleConfirm();
    assertEquals(refreshesBefore, mockView.getRefreshCount());
  }

  @Test
  public void testPassClearsSelections() {
    controller.handleCardClick(0, Player.RED);
    controller.handleCellClick(0, 0);
    controller.handlePass();
    int refreshesBefore = mockView.getRefreshCount();
    controller.handleConfirm();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerRejectsNullModel() {
    new SanguineController(null, Player.RED, humanPlayer, mockView);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerRejectsNullPlayer() {
    new SanguineController(model, Player.RED, null, mockView);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerRejectsNullView() {
    new SanguineController(model, Player.RED, humanPlayer, null);
  }

  @Test
  public void testControllerIgnoresOpponentCardClicks() {
    controller.handleCardClick(0, Player.BLUE);
    controller.handleCellClick(0, 0);
    int refreshesBefore = mockView.getRefreshCount();
    controller.handleConfirm();
    assertEquals(refreshesBefore, mockView.getRefreshCount());
  }
}