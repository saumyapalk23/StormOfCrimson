package sanguine.strategy;

import sanguine.controller.Features;

/**
 * Represents a player in the Sanguine game.
 * Players can be human (awaiting GUI input) or machine (using strategies).
 * Players publish their actions through the Features interface.
 */
public interface PlayerType {
  /**
   * Adds a Features listener to this player.
   * When the player makes a decision (human via GUI or machine via strategy),
   * they notify listeners through this interface.
   *
   * @param features the listener to add
   */
  void addFeaturesListener(Features features);

  /**
   * Notifies this player that it is now their turn.
   * Human players do nothing (they wait for GUI input).
   * Machine players compute their move and emit it via Features listeners.
   */
  void onTurnStart();
}