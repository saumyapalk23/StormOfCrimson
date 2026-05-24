package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's hand of cards in the Sanguine game.
 * A hand contains the cards currently available for the player to play.
 */
public class PlayerHand {
  private final List<Card> cards;
  private final Player owner;

  /**
   * Constructs an empty hand for the specified player.
   *
   * @param owner the player who owns this hand
   * @throws IllegalArgumentException if owner is null
   */
  public PlayerHand(Player owner) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    this.owner = owner;
    this.cards = new ArrayList<>();
  }

  /**
   * Adds a card to this hand.
   *
   * @param card the card to add
   * @throws IllegalArgumentException if card is null
   */
  public void addCard(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    this.cards.add(card);
  }

  /**
   * Removes and returns the card at the specified index.
   *
   * @param index the index of the card to remove (0-based)
   * @return the card that was removed
   * @throws IllegalArgumentException if index is out of bounds
   */
  public Card removeCard(int index) {
    if (index < 0 || index >= cards.size()) {
      throw new IllegalArgumentException("Invalid card index: " + index);
    }
    return this.cards.remove(index);
  }

  /**
   * Gets the card at the specified index without removing it.
   *
   * @param index the index of the card to view (0-based)
   * @return the card at that index
   * @throws IllegalArgumentException if index is out of bounds
   */
  public Card getCard(int index) {
    if (index < 0 || index >= cards.size()) {
      throw new IllegalArgumentException("Invalid card index: " + index);
    }
    return this.cards.get(index);
  }

  /**
   * Gets the number of cards currently in this hand.
   *
   * @return the size of the hand
   */
  public int size() {
    return this.cards.size();
  }

  /**
   * Gets a defensive copy of all cards in this hand.
   * Modifications to the returned list will not affect the actual hand.
   *
   * @return a new list containing all cards in this hand
   */
  public List<Card> getCards() {
    return new ArrayList<>(this.cards);
  }

  /**
   * Gets the player who owns this hand.
   *
   * @return the owner of this hand
   */
  public Player getOwner() {
    return this.owner;
  }

  /**
   * Checks if this hand is empty.
   *
   * @return true if the hand has no cards, false otherwise
   */
  public boolean isEmpty() {
    return this.cards.isEmpty();
  }

  @Override
  public String toString() {
    return String.format("%s's hand (%d cards)", owner, cards.size());
  }
}