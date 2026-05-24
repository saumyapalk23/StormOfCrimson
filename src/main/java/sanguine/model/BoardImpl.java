package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Board interface.
 * Represents a rectangular grid of cells with game state.
 */
public class BoardImpl implements Board {

  private final Cell[][] cells;
  private final int rows;
  private final int cols;


  //INVARIANT: cells.length == rows && cells[0].length == cols
  //INVARIANT: cols > 1 && cols % 2 == 1 (columns must be odd and > 1)
  //INVARIANT: rows > 0
  //INVARIANT: All cells in first column owned by RED, last column owned by BLUE (at start)

  /**
   * Constructs a board with the specified dimensions.
   * Initializes the board with starting pawns in the first and last columns.
   *
   * @param rows the number of rows (must be > 0)
   * @param cols the number of columns (must be > 1 and odd)
   * @throws IllegalArgumentException if dimensions are invalid
   */
  public BoardImpl(int rows, int cols) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Rows must be greater than 0");
    }
    if (cols <= 1 || cols % 2 == 0) {
      throw new IllegalArgumentException("Columns must be greater than 1 and odd");
    }


    this.cells = new Cell[rows][cols];
    this.rows = rows;
    this.cols = cols;
    this.initializeCells();
  }

  /**
   * Initializes all cells on the board.
   * Sets up starting pawns in first column (RED) and last column (BLUE).
   */
  private void initializeCells() {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        cells[row][col] = new Cell();

        if (col == 0) {
          cells[row][col].setPawns(Player.RED, 1);
        } else if (col == cols - 1) {
          cells[row][col].setPawns(Player.BLUE, 1);
        }
      }
    }
  }

  @Override
  public int getRows() {
    return this.rows;
  }

  @Override
  public int getCols() {
    return this.cols;
  }

  @Override
  public Cell getCell(int row, int col) {
    validatePosition(row, col);
    return this.cells[row][col];
  }

  /**
   * Validates that a position is within the board bounds.
   *
   * @param row the row to check
   * @param col the column to check
   * @throws IllegalArgumentException if position is out of bounds
   */
  private void validatePosition(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IllegalArgumentException(
          String.format("Position (%d, %d) is out of bounds", row, col));
    }
  }

  @Override
  public List<Card> getCardsInRow(int row, Player player) {
    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("Row out of bounds");
    }

    List<Card> cards = new ArrayList<>();
    for (int col = 0; col < cols; col++) {
      Cell cell = cells[row][col];
      if (cell.hasCard() && cell.getOwner() == player) {
        cards.add(cell.getCard());
      }
    }
    return cards;
  }

  @Override
  public void placeCard(Card card, Player owner, int row, int col) {
    validatePosition(row, col);

    Cell cell = cells[row][col];
    if (cell.hasCard()) {
      throw new IllegalArgumentException("Cell already contains a card");
    }

    cell.setCard(owner, card);
  }

  @Override
  public void addPawn(Player owner, int row, int col) {
    validatePosition(row, col);

    Cell cell = cells[row][col];

    if (cell.hasCard()) {
      return;
    }
    if (cell.isEmpty()) {
      cell.setPawns(owner, 1);
    } else if (cell.hasPawns()) {
      if (cell.getOwner() == owner) {
        // Same owner, try to increment (max 3)
        if (cell.getPawnCount() < 3) {
          cell.addPawn();
        }
        // If already at 3, do nothing (max reached)
      } else {
        // setPawnOwner should be called instead
        throw new IllegalStateException(
            "Cannot add pawn - cell owned by different player. Use setPawnOwner instead.");
      }
    }
  }

  @Override
  public void setPawnOwner(Player newOwner, int row, int col) {
    validatePosition(row, col);

    Cell cell = cells[row][col];

    if (!cell.hasPawns()) {
      throw new IllegalArgumentException("Cell has no pawns to convert");
    }
    cell.changeOwner(newOwner);
  }

  /**
   * Returns a string representation of the board for debugging.
   *
   * @return string representation
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = cells[row][col];
        if (cell.isEmpty()) {
          sb.append("_");
        } else if (cell.hasCard()) {
          sb.append(cell.getOwner() == Player.RED ? "R" : "B");
        } else {
          sb.append(cell.getPawnCount());
        }
        sb.append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}



