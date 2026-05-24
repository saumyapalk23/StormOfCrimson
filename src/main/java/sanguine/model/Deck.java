package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a deck of cards for a player in Sanguine.
 * Cards are drawn from the top of the deck.
 */
public class Deck {
  private final List<Card> cards;
  private final Player owner;

  /**
   * Constructs a deck with the given cards for the specified player.
   *
   * @param owner the player who owns this deck
   * @param cards the cards in the deck
   * @throws IllegalArgumentException if owner or cards is null
   */
  public Deck(Player owner, List<Card> cards) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    if (cards == null) {
      throw new IllegalArgumentException("Cards cannot be null");
    }
    this.owner = owner;
    this.cards = new ArrayList<>(cards);
  }

  /**
   * Draws a card from the top of the deck.
   *
   * @return the drawn card, or null if deck is empty
   */
  public Card drawCard() {
    if (cards.isEmpty()) {
      return null;
    }
    return cards.remove(0);
  }

  /**
   * Gets the number of cards remaining in the deck.
   *
   * @return the deck size
   */
  public int size() {
    return cards.size();
  }

  /**
   * Checks if the deck is empty.
   *
   * @return true if no cards remain, false otherwise
   */
  public boolean isEmpty() {
    return cards.isEmpty();
  }

  /**
   * Gets the owner of this deck.
   *
   * @return the player who owns this deck
   */
  public Player getOwner() {
    return owner;
  }
}