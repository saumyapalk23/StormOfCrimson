package sanguine.model;

import java.util.List;

/**
 * Interface for game Board which is a rectangular grid
 * that contains pawns or cards.
 *
 */
public interface Board {
  /**
   * Gets the number of rows in the board.
   *
   * @return the number of rows
   */
  int getRows();

  /**
   * Gets the number of columns in the board.
   *
   * @return the number of columns
   */
  int getCols();

  /**
   * Gets the cell at the specified position.
   *
   * @param row the row index
   * @param col the column index
   * @return the cell at that position
   * @throws IllegalArgumentException if position is out of bounds
   */
  Cell getCell(int row, int col);

  /**
   * Gets all cards in a specific row owned by a specific player.
   * Used for calculating row scores.
   *
   * @param row    the row index
   * @param player the player whose cards to retrieve
   * @return a list of cards owned by that player in that row
   * @throws IllegalArgumentException if row is out of bounds
   */
  List<Card> getCardsInRow(int row, Player player);

  /**
   * Places a card on the board at the specified position.
   * This removes all pawns at that location and replaces them with the card.
   *
   * @param card  the card to place
   * @param owner the player who owns the card
   * @param row   the row position
   * @param col   the column position
   * @throws IllegalArgumentException if position is invalid or cell already has a card
   */
  void placeCard(Card card, Player owner, int row, int col);

  /**
   * Adds one pawn to the specified cell.
   * Maximum 3 pawns per cell.
   *
   * @param owner the player who owns the pawn
   * @param row   the row position
   * @param col   the column position
   * @throws IllegalArgumentException if position is invalid or cell already at max pawns
   */
  void addPawn(Player owner, int row, int col);

  /**
   * Converts the ownership of pawns at the specified cell to a new owner.
   *
   * @param newOwner the new owner of the pawns
   * @param row      the row position
   * @param col      the column position
   * @throws IllegalArgumentException if position is invalid or cell has no pawns
   */
  void setPawnOwner(Player newOwner, int row, int col);
}
