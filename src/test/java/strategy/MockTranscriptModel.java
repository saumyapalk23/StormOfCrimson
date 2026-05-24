package strategy;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.Board;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Mock model that logs all coordinate accesses for transcript generation.
 * This wraps a real model and logs method calls related to move checking
 * and score evaluation (the core logic of the strategies).
 */
public class MockTranscriptModel implements ReadOnlySanguineModel {
  private final List<String> transcript = new ArrayList<>();
  private final ReadOnlySanguineModel delegate;

  /**
   * A constructor for the MockTranscriptModel.
   * Takes in the ReadOnly model as a delegate object.
   *
   * @param delegate the real model to delegate to.
   */
  public MockTranscriptModel(ReadOnlySanguineModel delegate) {
    this.delegate = delegate;
  }

  /**
   * Returns the accumulated list of move-checks made by the strategy.
   */
  public List<String> getTranscript() {
    return new ArrayList<>(this.transcript);
  }

  /**
   * Logs legality check attempts. We fetch the card value for detailed logging.
   */
  @Override
  public boolean moveCard(int handIndex, int row, int col) {
    Card card = null;
    try {
      // Get the card being considered for better logging detail
      card = delegate.getHand(delegate.getCurrentPlayer()).get(handIndex);
    } catch (Exception e) {
      // Ignore if card index is invalid, the delegate.moveCard will handle legality.
    }

    // Delegate the move check
    boolean isLegal = delegate.moveCard(handIndex, row, col);

    String cardDetail = (card != null)
        ? String.format("Card %d (Value %d)", handIndex, card.getValue())
        : String.format("Card %d", handIndex);

    // Log the move check with detailed information
    transcript.add(String.format("CHECKING MOVE: %s at (%d, %d). Legal? %b",
        cardDetail, row, col, isLegal));

    return isLegal;
  }

  /**
   * Logs score evaluation attempts.
   */
  @Override
  public int getRowScore(int row, Player player) {
    // Delegate the score calculation
    int score = delegate.getRowScore(row, player);

    // Log the score check
    transcript.add(String.format("EVALUATING SCORE: Row %d for %s. Score: %d",
        row, player, score));

    return score;
  }

  @Override
  public Player getCurrentPlayer() {
    return delegate.getCurrentPlayer();
  }

  /**
   * Calculates total score for the player.
   *
   * @param player the player playing
   * @return the total score
   */
  public int getTotalScore(Player player) {
    // Assuming delegate has a getTotalScore method as per ReadOnlySanguineModel
    return delegate.getScore(player);
  }

  @Override // Added @Override based on your correction that this is a model method
  public int getScore(Player player) {
    // Assuming delegate implements getScore()
    return delegate.getScore(player);
  }

  @Override
  public List<Card> getHand(Player player) {
    return delegate.getHand(player);
  }

  @Override // Added @Override based on your correction that this is a model method
  public int getDeckSize(Player player) {
    // Assuming delegate implements getDeckSize()
    return delegate.getDeckSize(player);
  }

  @Override
  public Cell getCell(int row, int col) {
    return delegate.getCell(row, col);
  }

  @Override
  public int getRows() {
    return delegate.getRows();
  }

  @Override
  public int getCols() {
    return delegate.getCols();
  }

  @Override
  public boolean isGameOver() {
    return delegate.isGameOver();
  }

  @Override
  public Player getWinner() {
    return delegate.getWinner();
  }

  @Override
  public Board getBoard() {
    return null;
  }
}