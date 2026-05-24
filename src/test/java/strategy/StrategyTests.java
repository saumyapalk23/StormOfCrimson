package strategy;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaximizeRowScoreStrategy;

/**
 * Tests for strategy implementations.
 */
public class StrategyTests {
  private Card makeCard(String name, int cost, int value) {
    boolean[][] inf = new boolean[5][5];
    inf[2][3] = true; // influence to the right
    return new Card(name, cost, value, inf);
  }

  private List<Card> makeDeck() {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(makeCard("Card" + i, 1, i + 1));
    }
    return deck;
  }

  @Test
  public void fillFirstChoosesUpperLeftFirst() {
    List<Card> deck = makeDeck();
    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    FillFirstStrategy strategy = new FillFirstStrategy();
    int[] move = strategy.moveChoice(model, Player.RED);

    Assert.assertNotNull(move);
    Assert.assertEquals(0, move[0]); // first card
    Assert.assertEquals(0, move[1]); // row 0
    Assert.assertEquals(0, move[2]); // col 0
  }

  @Test
  public void maxRowScoreTriesToWinRow() {
    List<Card> deck = new ArrayList<>();
    Card highValue = makeCard("High", 1, 5);
    for (int i = 0; i < 20; i++) {
      deck.add(i == 0 ? highValue : makeCard("Low" + i, 1, 1));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(1, 0, 0);
    MaximizeRowScoreStrategy strategy = new MaximizeRowScoreStrategy();
    int[] move = strategy.moveChoice(model, Player.BLUE);

    Assert.assertNotNull(move);
    Assert.assertEquals(0, move[0]); // high-value card
    Assert.assertEquals(0, move[1]); // row 0 to compete
  }

  @Test
  public void strategyReturnsNullWhenNoMoves() {
    List<Card> deck = new ArrayList<>();
    // Expensive cards that can't be played with 1 pawn
    Card expensive = makeCard("Exp", 3, 5);
    for (int i = 0; i < 20; i++) {
      deck.add(expensive);
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    FillFirstStrategy strategy = new FillFirstStrategy();
    int[] move = strategy.moveChoice(model, Player.RED);

    Assert.assertNull(move); // can't afford any cards
  }

}