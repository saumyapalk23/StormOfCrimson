package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the Sanguine card game model.
 * Manages game state, enforces rules, and handles player turns.
 */
public class BasicSanguine implements SanguineModel {
  private Board board;
  private Deck redDeck;
  private Deck blueDeck;
  private PlayerHand redHand;
  private PlayerHand blueHand;
  private Player currentPlayer;
  private boolean gameStarted;
  private boolean redPassed;
  private boolean bluePassed;

  private final List<ModelStatusListener> modelListeners;
  private boolean gameStartedNotified;

  //INVARIANT: The board dimensions are valid (rows > 0, cols > 1, cols is odd)
  //INVARIANT: If gameStarted is true, board, decks, and hands are non-null
  //INVARIANT: currentPlayer alternates between RED and BLUE after each turn

  /**
   * Constructs a new BasicSanguine game.
   * Game must be started with startGame before playing.
   */
  public BasicSanguine() {
    this.gameStarted = false;
    this.redPassed = false;
    this.bluePassed = false;
    this.modelListeners = new ArrayList<>();
    this.gameStartedNotified = false;
  }

  @Override
  public void startGame(List<Card> redDeck, List<Card> blueDeck,
                        int rows, int cols, int handSize) {
    if (gameStarted) {
      throw new IllegalStateException("Game has already been started");
    }
    if (redDeck == null || blueDeck == null) {
      throw new IllegalArgumentException("Decks cannot be null");
    }
    if (handSize > redDeck.size() / 3 || handSize > blueDeck.size() / 3) {
      throw new IllegalArgumentException("Hand size cannot exceed deck size / 3");
    }

    // create board
    this.board = new BoardImpl(rows, cols);

    // Create decks
    this.redDeck = new Deck(Player.RED, new ArrayList<>(redDeck));
    this.blueDeck = new Deck(Player.BLUE, new ArrayList<>(blueDeck));

    // create hands and deal initial cards
    this.redHand = new PlayerHand(Player.RED);
    this.blueHand = new PlayerHand(Player.BLUE);

    dealInitialHands(handSize);

    // red always goes first
    this.currentPlayer = Player.RED;
    this.gameStarted = true;
  }

  @Override
  public void startGame() {
    if (!gameStarted) {
      throw new IllegalStateException("Call startGame(decks...) first");
    }
    if (!gameStartedNotified) {
      notifyPlayerTurn(currentPlayer);
      gameStartedNotified = true;
    }
  }

  @Override
  public void addModelStatusListener(ModelStatusListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    this.modelListeners.add(listener);
  }

  /**
   * Notifies all listeners that it's a specific player's turn.
   */
  private void notifyPlayerTurn(Player player) {
    for (ModelStatusListener listener : modelListeners) {
      listener.onPlayerTurn(player);
    }
  }

  /**
   * Notifies all listeners that the game is over.
   */
  private void notifyGameOver() {
    Player winner = getWinner();
    for (ModelStatusListener listener : modelListeners) {
      listener.onGameOver(winner);
    }
  }

  /**
   * Deals the initial cards to both players' hands.
   *
   * @param handSize number of cards to deal to each player
   */
  private void dealInitialHands(int handSize) {
    for (int i = 0; i < handSize; i++) {
      Card redCard = redDeck.drawCard();
      Card blueCard = blueDeck.drawCard();
      if (redCard != null) {
        redHand.addCard(redCard);
      }
      if (blueCard != null) {
        blueHand.addCard(blueCard);
      }
    }
  }

  @Override
  public void placeCard(int handIndex, int row, int col) {
    ensureGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("Game is over");
    }

    PlayerHand currentHand = getCurrentHand();

    if (handIndex < 0 || handIndex >= currentHand.size()) {
      throw new IllegalArgumentException("Invalid hand index");
    }

    Card card = currentHand.getCard(handIndex);
    Cell cell = board.getCell(row, col);

    // Check if move is legal
    if (!isLegalMove(cell, card)) {
      throw new IllegalStateException("Cannot place card at this position");
    }

    // Remove card from hand and place on board
    currentHand.removeCard(handIndex);
    board.placeCard(card, currentPlayer, row, col);

    // Apply card influence
    applyInfluence(card, row, col);

    // Reset pass tracking - a card was played
    redPassed = false;
    bluePassed = false;

    // End turn
    endTurn();
  }

  /**
   * Checks if placing a card at a cell is legal.
   *
   * @param cell the cell to place on
   * @param card the card to place
   * @return true if legal, false otherwise
   */
  private boolean isLegalMove(Cell cell, Card card) {
    return cell.hasPawns()
            && cell.getOwner() == currentPlayer
            && cell.getPawnCount() >= card.getCost();
  }

  /**
   * Applies the influence of a placed card to the board.
   *
   * @param card    the card that was placed
   * @param cardRow the row where the card was placed
   * @param cardCol the column where the card was placed
   */
  private void applyInfluence(Card card, int cardRow, int cardCol) {
    boolean[][] influenceGrid = getInfluenceForPlayer(card);

    // Grid center is at (2, 2), card is at (cardRow, cardCol)
    for (int gridRow = 0; gridRow < 5; gridRow++) {
      for (int gridCol = 0; gridCol < 5; gridCol++) {
        if (influenceGrid[gridRow][gridCol]) {
          // Calculate board position relative to card
          int boardRow = cardRow + (gridRow - 2);
          int boardCol = cardCol + (gridCol - 2);

          applyInfluenceToCell(boardRow, boardCol);
        }
      }
    }
  }

  /**
   * Gets the influence grid for the current player.
   * Blue player's influence is mirrored.
   *
   * @param card the card
   * @return the influence grid
   */
  private boolean[][] getInfluenceForPlayer(Card card) {
    if (currentPlayer == Player.BLUE) {
      return card.getMirroredInfluenceGrid();
    }
    return card.getInfluenceGrid();
  }

  /**
   * Applies influence to a single cell on the board.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   */
  private void applyInfluenceToCell(int row, int col) {
    // Check if position is on the board
    if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getCols()) {
      return;
    }

    Cell cell = board.getCell(row, col);

    if (cell.hasCard()) {
      // Cards are not affected by influence
      return;
    } else if (cell.isEmpty()) {
      // Empty cell gains a pawn
      board.addPawn(currentPlayer, row, col);
    } else if (cell.hasPawns()) {
      if (cell.getOwner() == currentPlayer) {
        // Same owner, add pawn (up to max 3)
        board.addPawn(currentPlayer, row, col);
      } else {
        // Different owner, convert ownership
        board.setPawnOwner(currentPlayer, row, col);
      }
    }
  }

  @Override
  public void passTurn() {
    ensureGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("Game is already over");
    }

    // Track which player passed
    if (currentPlayer == Player.RED) {
      redPassed = true;
    } else {
      bluePassed = true;
    }

    endTurn();

    // Check if game just ended
    if (isGameOver()) {
      notifyGameOver();
    }
  }

  /**
   * Ends the current turn and switches to the other player.
   * Also draws a card for the new current player if available.
   */
  private void endTurn() {
    currentPlayer = currentPlayer.switchColor();

    // Draw a card for the new current player
    Card drawnCard = getCurrentDeck().drawCard();
    if (drawnCard != null) {
      getCurrentHand().addCard(drawnCard);
    }

    // Notify listeners of turn change
    notifyPlayerTurn(currentPlayer);
  }

  @Override
  public Player getCurrentPlayer() {
    ensureGameStarted();
    return this.currentPlayer;
  }

  @Override
  public boolean isGameOver() {
    ensureGameStarted();
    return redPassed && bluePassed;
  }

  @Override
  public Player getWinner() {
    if (!isGameOver()) {
      throw new IllegalStateException("Game is not over");
    }

    int redScore = getScore(Player.RED);
    int blueScore = getScore(Player.BLUE);

    if (redScore > blueScore) {
      return Player.RED;
    } else if (blueScore > redScore) {
      return Player.BLUE;
    }
    return null; // tie between red and blue
  }

  @Override
  public int getScore(Player player) {
    ensureGameStarted();
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    int totalScore = 0;

    for (int row = 0; row < board.getRows(); row++) {
      int rowScore = getRowScore(row, player);
      int opponentRowScore = getRowScore(row, player.switchColor());

      // only add score if this player wins the row
      if (rowScore > opponentRowScore) {
        totalScore += rowScore;
      }
    }

    return totalScore;
  }

  /**
   * Calculates the row score for a player in a specific row.
   * Specifies paramters and then executes.
   *
   * @param row the row to calculate (specified)
   * @param player the player whose turn it is
   * @return the row score
   */
  @Override
  public int getRowScore(int row, Player player) {
    ensureGameStarted();
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (row < 0 || row >= board.getRows()) {
      throw new IllegalArgumentException("Row index out of bounds");
    }

    List<Card> cards = board.getCardsInRow(row, player);
    int score = 0;
    for (Card card : cards) {
      score += card.getValue();
    }
    return score;
  }

  @Override
  public Board getBoard() {
    ensureGameStarted();
    return this.board;
  }

  @Override
  public List<Card> getHand(Player player) {
    ensureGameStarted();
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    if (player == Player.RED) {
      return redHand.getCards();
    } else {
      return blueHand.getCards();
    }
  }

  @Override
  public int getDeckSize(Player player) {
    ensureGameStarted();
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    return player == Player.RED ? redDeck.size() : blueDeck.size();
  }

  @Override
  public boolean moveCard(int handIndex, int row, int col) {
    ensureGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("Game is over");
    }

    PlayerHand currentHand = getCurrentHand();

    if (handIndex < 0 || handIndex >= currentHand.size()) {
      throw new IllegalArgumentException("Invalid hand index");
    }

    try {
      Card card = currentHand.getCard(handIndex);
      Cell cell = board.getCell(row, col);
      return isLegalMove(cell, card);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Gets the hand for the current player.
   *
   * @return the current player's hand
   */
  private PlayerHand getCurrentHand() {
    return currentPlayer == Player.RED ? redHand : blueHand;
  }

  /**
   * Gets the deck for the current player.
   *
   * @return the current player's deck
   */
  private Deck getCurrentDeck() {
    return currentPlayer == Player.RED ? redDeck : blueDeck;
  }

  /**
   * Ensures the game has been started.
   *
   * @throws IllegalStateException if game hasn't started
   */
  private void ensureGameStarted() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
  }

  @Override
  public Cell getCell(int row, int col) {
    ensureGameStarted();
    return board.getCell(row, col);
  }

  @Override
  public int getRows() {
    ensureGameStarted();
    return board.getRows();
  }

  @Override
  public int getCols() {
    ensureGameStarted();
    return board.getCols();
  }
}