package sanguine.model;

/**
 * Represents a single cell on the Sanguine game board.
 * A cell can be empty, contain pawns (1-3), or contain a card.
 * If a cell has content, it is owned by a player (RED or BLUE).
 */
public class Cell {

  private Player owner;        // null if empty
  private Integer countPawns;  // null if no pawns, 1-3 if has pawns
  private Card card;           // null if no card

  //INVARIANT: At most one of countPawns or card is non-null
  //INVARIANT: If countPawns is non-null, it is between 1 and 3 inclusive
  //INVARIANT: If owner is null, both countPawns and card are null (empty cell)
  //INVARIANT: If owner is non-null, exactly one of countPawns or card is non-null

  /**
   * Constructs an empty cell with no owner, pawns, or card.
   */
  public Cell() {
    this.owner = null;
    this.countPawns = null;
    this.card = null;
  }

  /**
   * Checks if this cell is empty (contains neither pawns nor a card).
   *
   * @return true if the cell is empty, false otherwise
   */
  public boolean isEmpty() {
    return this.owner == null;
  }

  /**
   * Checks if this cell contains pawns.
   *
   * @return true if the cell has pawns, false otherwise
   */
  public boolean hasPawns() {
    return this.countPawns != null;
  }

  /**
   * Checks if this cell contains a card.
   *
   * @return true if the cell has a card, false otherwise
   */
  public boolean hasCard() {
    return this.card != null;
  }

  /**
   * Gets the number of pawns in this cell.
   *
   * @return the pawn count (1-3) if cell has pawns, 0 if no pawns
   */
  public int getPawnCount() {
    return this.countPawns == null ? 0 : this.countPawns;
  }

  /**
   * Gets the card in this cell.
   *
   * @return the card if present, null if no card
   */
  public Card getCard() {
    return this.card;
  }

  /**
   * Gets the owner of this cell's content.
   *
   * @return the player who owns this cell's content (RED or BLUE), or null if the cell is empty
   */
  public Player getOwner() {
    return this.owner;
  }

  /**
   * Sets this cell to contain pawns owned by the specified player.
   *
   * @param owner the player who owns the pawns
   * @param count the number of pawns (must be 1-3)
   * @throws IllegalArgumentException if owner is null or count is not 1-3
   */
  public void setPawns(Player owner, int count) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    if (count < 1 || count > 3) {
      throw new IllegalArgumentException("Pawn count must be between 1 and 3");
    }
    this.owner = owner;
    this.countPawns = count;
    this.card = null; // Clear any existing card
  }

  /**
   * Sets this cell to contain a card owned by the specified player.
   *
   * @param owner the player who owns the card
   * @param card  the card to place in this cell
   * @throws IllegalArgumentException if owner or card is null
   */
  public void setCard(Player owner, Card card) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    this.owner = owner;
    this.card = card;
    this.countPawns = null; // Clear any existing pawns
  }

  /**
   * Adds one pawn to this cell (up to a maximum of 3).
   * The cell must already contain pawns.
   *
   * @throws IllegalStateException if cell doesn't have pawns or already has 3 pawns
   */
  public void addPawn() {
    if (!hasPawns()) {
      throw new IllegalStateException("Cannot add pawn to cell without pawns");
    }
    if (this.countPawns >= 3) {
      throw new IllegalStateException("Cell already has maximum pawns (3)");
    }
    this.countPawns++;
  }

  /**
   * Changes the owner of the pawns in this cell without changing the count.
   * The cell must currently contain pawns.
   *
   * @param newOwner the new owner of the pawns
   * @throws IllegalStateException    if cell doesn't contain pawns
   * @throws IllegalArgumentException if newOwner is null
   */
  public void changeOwner(Player newOwner) {
    if (!hasPawns()) {
      throw new IllegalStateException("Cannot change owner of cell without pawns");
    }
    if (newOwner == null) {
      throw new IllegalArgumentException("New owner cannot be null");
    }
    this.owner = newOwner;
  }

  /**
   * Clears this cell, making it empty with no owner, pawns, or card.
   */
  public void clearCell() {
    this.owner = null;
    this.countPawns = null;
    this.card = null;
  }

  /**
   * Returns a string representation of this cell's contents.
   *
   * @return a string describing what's in the cell
   */
  @Override
  public String toString() {
    if (isEmpty()) {
      return "Empty";
    }
    if (hasPawns()) {
      return owner + " pawns: " + countPawns;
    }
    return owner + " card: " + card.getName();
  }
}