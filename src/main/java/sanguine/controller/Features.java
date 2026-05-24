package sanguine.controller;

import sanguine.model.Player;

/**
 * Stub controller interface for handling user input from the view.
 * Prints messages to System.out for now.
 */
public interface Features {
  /**
   * Called when a card in the hand is clicked.
   *
   * @param handIndex the index of the card clicked
   * @param player the player whose hand it is
   */
  void handleCardClick(int handIndex, sanguine.model.Player player);

  /**
   * Called when a cell on the board is clickd.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   */
  void handleCellClick(int row, int col);

  /**
   * Called when user presses key to confirm their move.
   */
  void handleConfirm();

  /**
   * Called when user presses a key to pass their turn.
   */
  void handlePass();
}