package sanguine.model;

import java.util.List;

/**
 * Read only Sanguine model.
 */
public interface ReadOnlySanguineModel {

  /**
   * Gets the player whose turn it currently is.
   *
   * @return the current player (RED or BLUE)
   * @throws IllegalStateException if game hasn't started
   */
  Player getCurrentPlayer();

  /**
   * Checks if the game is over.
   * The game ends when both players pass consecutively.
   *
   * @return true if the game is over, false otherwise
   * @throws IllegalStateException if game hasn't started
   */
  boolean isGameOver();

  /**
   * Gets the winner of the game based on total scores.
   * If scores are tied, returns null.
   *
   * @return the winning player, or null if tied
   * @throws IllegalStateException if game hasn't started or isn't over
   */
  Player getWinner();

  /**
   * Calculates the total score for the specified player.
   * For each row, if a player has the higher row-score, they gain that many points.
   * If row-scores are tied, neither player gains points for that row.
   *
   * @param player the player whose score to calculate
   * @return the total score
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException    if game hasn't started
   */
  int getScore(Player player);

  /**
   * Gets the game board.
   *
   * @return the board
   * @throws IllegalStateException if game hasn't started
   */
  Board getBoard();

  /**
   * Gets a copy of the specified player's hand.
   *
   * @param player the player whose hand to get
   * @return a list of cards in that player's hand
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException    if game hasn't started
   */
  List<Card> getHand(Player player);

  /**
   * Gets the number of cards remaining in the specified player's deck.
   *
   * @param player the player whose deck size to check
   * @return the number of cards remaining
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException    if game hasn't started
   */
  int getDeckSize(Player player);

  /**
   * Checks if a card can legally be placed at the specified position.
   * A card can be placed if:
   * - The cell has pawns owned by the current player
   * - The cell has at least as many pawns as the card's cost
   *
   * @param handIndex the index of the card in current player's hand
   * @param row       the row to check
   * @param col       the column to check
   * @return true if the card can be placed, false otherwise
   * @throws IllegalArgumentException if handIndex, row, or col is invalid
   * @throws IllegalStateException    if game hasn't started or is over
   */
  boolean moveCard(int handIndex, int row, int col);

  /**
   * Gets the cell at the specified position on the board.
   * This allows inspection of cell contents and ownership.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @return the cell at that position
   * @throws IllegalArgumentException if position is out of bounds
   * @throws IllegalStateException if game hasn't started
   */
  Cell getCell(int row, int col);

  /**
   * Calculates the row score for a specific player in a specific row.
   * The row score is the sum of all card values owned by that player in the row.
   *
   * @param row the row index
   * @param player the player whose row score to calculate
   * @return the row score for that player
   * @throws IllegalArgumentException if row is out of bounds or player is null
   * @throws IllegalStateException if game hasn't started
   */
  int getRowScore(int row, Player player);

  /**
   * Gets the number of rows in the game board.
   *
   * @return the number of rows
   * @throws IllegalStateException if game hasn't started
   */
  int getRows();

  /**
   * Gets the number of columns in the game board.
   *
   * @return the number of columns
   * @throws IllegalStateException if game hasn't started
   */
  int getCols();

}

