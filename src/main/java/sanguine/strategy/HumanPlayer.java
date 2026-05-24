package sanguine.strategy;

import java.util.ArrayList;
import java.util.List;
import sanguine.controller.Features;
import sanguine.model.Player;

/**
 * Represents a human player who makes moves via GUI interaction.
 * This player doesn't emit events directly; the view emits them instead.
 * However, it implements PlayerType to maintain a uniform interface.
 */
public class HumanPlayer implements PlayerType {
  private final Player player;
  private final List<Features> listeners;

  /**
   * Constructs a human player.
   *
   * @param player which player this is (RED or BLUE)
   */
  public HumanPlayer(Player player) {
    this.player = player;
    this.listeners = new ArrayList<>();
  }

  @Override
  public void addFeaturesListener(Features features) {
    this.listeners.add(features);
  }

  @Override
  public void onTurnStart() {
    // Human players wait for GUI input
  }
}