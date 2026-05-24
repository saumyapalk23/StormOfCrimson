package sanguine.model;

import java.util.List;

/**
 * Represents the model for the Sanguine card game.
 * This interface defines all operations needed to play the game,
 * including starting the game, making moves, and querying game state.
 */
public interface SanguineModel extends ReadOnlySanguineModel {

  /**
   * Places a card from the current player's hand onto the board.
   * The card is removed from the hand and placed at the specified position.
   * Card influence is then applied to the board.
   *
   * @param handIndex the index of the card in the current player's hand
   * @param row       the row where the card should be placed
   * @param col       the column where the card should be placed
   * @throws IllegalArgumentException if handIndex is invalid or position is invalid
   * @throws IllegalStateException    if game hasn't started or is over
   * @throws IllegalStateException    if the move is illegal (insufficient pawns, etc.)
   */
  void placeCard(int handIndex, int row, int col);

  /**
   * Current player passes their turn without placing a card.
   * If both players pass consecutively, the game ends.
   *
   * @throws IllegalStateException if game hasn't started or is already over
   */
  void passTurn();

  /**
   * Adds a listener for model status events (turn changes, game over).
   * Controllers register themselves to be notified when the active player changes.
   *
   * @param listener the listener to add
   */
  void addModelStatusListener(ModelStatusListener listener);


  /**
   * Starts a new game of Sanguine with the given decks and board configuration.
   * Both players are dealt cards from their respective decks.
   * The board is initialized with pawns in the first and last columns.
   *
   * @param redDeck  the deck of cards for the red player
   * @param blueDeck the deck of cards for the blue player
   * @param rows     the number of rows on the board (must be positive)
   * @param cols     the number of columns on the board (must be > 1 and odd)
   * @param handSize the number of cards each player starts with
   * @throws IllegalArgumentException if any parameter is invalid
   * @throws IllegalStateException    if game has already been started
   */
  void startGame(List<Card> redDeck, List<Card> blueDeck, int rows, int cols, int handSize);

  /**
   * Starts the game and notifies the first player it's their turn.
   * This must be called AFTER all listeners are registered via addModelStatusListener.
   * Ensures controllers are ready before the first player is notified.
   *
   * @throws IllegalStateException if startGame(decks...) hasn't been called first
   */
  void startGame();

}
