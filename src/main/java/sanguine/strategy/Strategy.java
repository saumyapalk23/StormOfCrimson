package sanguine.strategy;

import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents a strategy for choosing moves in Sanguine.
 * A strategy examines the game state and returns the best move.
 */
public interface Strategy {

  /**
   * Chooses a move for the given player.
   *
   * @param model the game model to analyze
   * @param player the player to choose a move for
   * @return an array or null if invalid -> [handInex, row, col]
   */
  int[] moveChoice(ReadOnlySanguineModel model, Player player);
}