package sanguine.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents an immutable card in the Sanguine game.
 * Each card has a name, cost, value, and a 5x5 influence grid that determines
 * how the card affects the board when placed.
 */
public class Card {
  private final String name;
  private final int cost;
  private final int value;
  private final boolean[][] influenceGrid;

  //INVARIANT: cost is 1, 2, or 3
  //INVARIANT: value > 0
  //INVARIANT: influenceGrid is 5x5 and influenceGrid[2][2] == false (center is card position)

  /**
   * Constructs a Card with the specified properties.
   *
   * @param name          the name of the card
   * @param cost          the cost to place this card
   * @param value         the point value of this card
   * @param influenceGrid the 5x5 grid showing where this card has influence
   *                      (must be 5x5, center must be false)
   * @throws IllegalArgumentException if any parameter is invalid
   */

  public Card(String name, int cost, int value, boolean[][] influenceGrid) {
    if (name == null) {
      throw new IllegalArgumentException("Card name cannot be null");
    }
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Card cost must be 1, 2, or 3");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("Card value must be positive");
    }
    if (!isValidInfluenceGrid(influenceGrid)) {
      throw new IllegalArgumentException("Invalid influence grid");
    }

    this.name = name;
    this.cost = cost;
    this.value = value;
    this.influenceGrid = copyGrid(influenceGrid);
  }

  //checks if the influenceGrid is valid by ensuring grid length and row length are equal to 5.
  private boolean isValidInfluenceGrid(boolean[][] grid) {
    if (grid == null || grid.length != 5) {
      return false;
    }
    for (boolean[] row : grid) {
      if (row == null || row.length != 5) {
        return false;
      }
    }
    return !grid[2][2]; // center must be false
  }

  /**
   * Creates a copy of a 2D boolean array.
   *
   * @param grid the grid to copy
   * @return a new grid with the same values
   */
  private boolean[][] copyGrid(boolean[][] grid) {
    boolean[][] copy = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      copy[i] = Arrays.copyOf(grid[i], 5);
    }
    return copy;
  }

  /**
   * Gets the name of this card.
   *
   * @return the card name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the cost to place this card.
   *
   * @return the cost
   */
  public int getCost() {
    return this.cost;
  }

  /**
   * Gets the point value of this card.
   *
   * @return the value
   */
  public int getValue() {
    return this.value;
  }

  /**
   * Gets a copy of the influence grid for this card.
   * The grid is 5x5 where true means the card has influence at that position
   * relative to where it's placed. The center (2,2) is always false.
   *
   * @return a copy of the influence grid
   */
  public boolean[][] getInfluenceGrid() {
    return copyGrid(this.influenceGrid);
  }

  /**
   * Checks if this card has influence at a specific position in its grid.
   *
   * @param gridRow the row in the influence grid (0-4)
   * @param gridCol the column in the influence grid (0-4)
   * @return true if the card influences that position, else false
   * @throws IllegalArgumentException if row or col is out of bounds
   */
  public boolean hasInfluenceAt(int gridRow, int gridCol) {
    if (gridRow < 0 || gridRow >= 5 || gridCol < 0 || gridCol >= 5) {
      throw new IllegalArgumentException("Grid position out of bounds");
    }
    return this.influenceGrid[gridRow][gridCol];
  }

  /**
   * Gets a mirrored version of the influence grid for the blue player.
   * The grid is mirrored across the vertical axis since blue player is
   * on the right side of the board.
   *
   * @return a mirrored copy of the influence grid
   */
  public boolean[][] getMirroredInfluenceGrid() {
    boolean[][] mirrored = new boolean[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        mirrored[row][col] = this.influenceGrid[row][4 - col];
      }
    }
    return mirrored;
  }

  /**
   * Determines if this card is equal to another object.
   *
   * @param other the object to compare to
   * @return true if the objects are equal, else false
   */

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Card)) {
      return false;
    }
    Card otherCard = (Card) other;

    if (this.cost != otherCard.cost || this.value != otherCard.value) {
      return false;
    }
    if (!this.name.equals(otherCard.name)) {
      return false;
    }

    // Uses a nested for loop to create copy of influenceGrid
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (this.influenceGrid[row][col] != otherCard.influenceGrid[row][col]) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Computes a hash code for this card based on its fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    int result = Objects.hash(name, cost, value);

    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        result = 31 * result + (influenceGrid[row][col] ? 1 : 0);
      }
    }

    return result;
  }
}


