package sanguine.strategy;

import java.util.ArrayList;
import java.util.List;
import sanguine.controller.Features;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents a machine (AI) player that uses a strategy to choose moves.
 * When notified of their turn, computes a move and emits it via Features.
 */
public class MachinePlayer implements PlayerType {
  private final Player player;
  private final Strategy strategy;
  private final ReadOnlySanguineModel model;
  private final List<Features> listeners;

  /**
   * Constructs a machine player.
   *
   * @param player   which player this is (RED or BLUE)
   * @param strategy the strategy to use for choosing moves
   * @param model    the game model to analyze
   */
  public MachinePlayer(Player player, Strategy strategy, ReadOnlySanguineModel model) {
    if (player == null || strategy == null || model == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }
    this.player = player;
    this.strategy = strategy;
    this.model = model;
    this.listeners = new ArrayList<>();
  }

  @Override
  public void addFeaturesListener(Features features) {
    this.listeners.add(features);
  }

  @Override
  public void onTurnStart() {
    int[] move = strategy.moveChoice(model, player);

    if (move == null) {
      notifyPass();
    } else {
      int handIndex = move[0];
      int row = move[1];
      int col = move[2];

      notifyCardClick(handIndex);
      notifyCellClick(row, col);
      notifyConfirm();
    }
  }

  /**
   * Notifies all listeners of a card click.
   */
  private void notifyCardClick(int handIndex) {
    for (Features listener : listeners) {
      listener.handleCardClick(handIndex, player);
    }
  }

  /**
   * Notifies all listeners of a cell click.
   */
  private void notifyCellClick(int row, int col) {
    for (Features listener : listeners) {
      listener.handleCellClick(row, col);
    }
  }

  /**
   * Notifies all listeners of move confirmation.
   */
  private void notifyConfirm() {
    for (Features listener : listeners) {
      listener.handleConfirm();
    }
  }

  /**
   * Notifies all listeners of passing turn.
   */
  private void notifyPass() {
    for (Features listener : listeners) {
      listener.handlePass();
    }
  }
}