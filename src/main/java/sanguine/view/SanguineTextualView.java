package sanguine.view;

import sanguine.model.Board;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.SanguineModel;

/**
 * Provides a textual rendering of the Sanguine game board and scores.
 * Follows the format specified in the project requirements.
 * Generates the correct format for each card object.
 */
public class SanguineTextualView {

  private final SanguineModel model;

  /**
   * Constructs a SanguineTextualView with the model to be rendered.
   *
   * @param model the game model
   * @throws IllegalArgumentException if the model is null
   */
  public SanguineTextualView(SanguineModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }
    this.model = model;
  }

  /**
   * Generates the textual representation of the current game state.
   *
   * @return a String representing the current board, scores, and turn info.
   */
  @Override
  public String toString() {
    Board board = model.getBoard();
    if (board == null) {
      return "Game not started. Call startGame() first.";
    }

    StringBuilder output = new StringBuilder();

    // append turn info
    Player currentPlayer = model.getCurrentPlayer();
    output.append("Current Player: ").append(currentPlayer).append(" (");
    output.append(currentPlayer == Player.RED ? "Red" : "Blue").append(")\n");

    // append hand sizes and deck sizes
    output.append("RED Hand Size: ").append(model.getHand(Player.RED).size());
    output.append(", Deck: ").append(model.getDeckSize(Player.RED)).append(" | ");
    output.append("BLUE Hand Size: ").append(model.getHand(Player.BLUE).size());
    output.append(", Deck: ").append(model.getDeckSize(Player.BLUE)).append("\n");

    // append the board/scoring view
    output.append(renderBoard(board));

    // append game over/winner info (if applicable)
    if (model.isGameOver()) {
      Player winner = model.getWinner();
      output.append("\nGame Over! Result: ");
      if (winner == null) {
        output.append("TIE");
      } else {
        output.append("Winner: ").append(winner);
      }
      output.append("\nRED Total Score: ").append(model.getScore(Player.RED));
      output.append("\nBLUE Total Score: ").append(model.getScore(Player.BLUE));
    }

    return output.toString();
  }

  /**
   * Helper method to generate the required board and row-score textual output.
   */
  private String renderBoard(Board board) {
    int rows = board.getRows();
    int cols = board.getCols();
    StringBuilder boardStringing = new StringBuilder();

    for (int r = 0; r < rows; r++) {
      // calculate row scores using the Board method and Card method (getValue)
      int redRowScore = board.getCardsInRow(r, Player.RED).stream()
          .mapToInt(Card::getValue)
          .sum();
      int blueRowScore = board.getCardsInRow(r, Player.BLUE).stream()
          .mapToInt(Card::getValue)
          .sum();

      // print the Red row Score (left side)
      boardStringing.append(redRowScore).append(" ");

      // print the cells
      for (int c = 0; c < cols; c++) {
        Cell cell = board.getCell(r, c);
        if (cell.hasCard()) {
          // Card: 'R' or 'B'
          boardStringing.append(cell.getOwner() == Player.RED ? "R" : "B");
        } else if (cell.hasPawns()) {
          // Pawns: 1, 2, or 3
          boardStringing.append(cell.getPawnCount());
        } else {
          // Empty
          boardStringing.append("_");
        }
      }

      // Print the Blue row score (right side)
      boardStringing.append(" ").append(blueRowScore).append("\n");
    }
    return boardStringing.toString();
  }
}