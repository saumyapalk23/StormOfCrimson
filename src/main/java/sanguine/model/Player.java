package sanguine.model;

/**
 * A defined enumeration for player.
 * Allows us to define the colors included for each player; red/blue.
 */
public enum Player {
  RED,
  BLUE;

  /**
   * Switches to the alternate player than the current if the boolean returns true, for gaming.
   *
   * @return the respective player whose turn it is
   */
  public Player switchColor() {
    return this == RED ? BLUE : RED;
  }
}