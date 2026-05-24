package sanguine.model;

/**
 * Listener interface for model status events.
 * The model publishes notifications about game state changes,
 * particularly when the active player changes.
 */
public interface ModelStatusListener {
  /**
   * Called when it becomes a specific player's turn.
   * Controllers should register for this event to know when their player can act.
   *
   * @param player the player whose turn it now is
   */
  void onPlayerTurn(Player player);

  /**
   * Called when the game ends.
   * Controllers should register for this event to display final results.
   *
   * @param winner the winning player, or null if tied
   */
  void onGameOver(Player winner);
}