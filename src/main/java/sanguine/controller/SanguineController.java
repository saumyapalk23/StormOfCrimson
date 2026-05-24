package sanguine.controller;

import javax.swing.JOptionPane;
import sanguine.model.ModelStatusListener;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.strategy.PlayerType;
import sanguine.view.SanguineView;

/**
 * Controller for a single player in the Sanguine game.
 * Coordinates between the model, view, and player implementation.
 * Each player has their own controller instance.
 */
public class SanguineController implements Features, ModelStatusListener {
  private final SanguineModel model;
  private final Player thisPlayer;
  private final PlayerType player;
  private final SanguineView view;

  private int selectedCardIndex = -1;
  private int selectedRow = -1;
  private int selectedCol = -1;
  private boolean isMyTurn = false;

  /**
   * Constructs a controller for a single player.
   *
   * @param model      the game model
   * @param thisPlayer which player this controller represents
   * @param player     the player implementation (human or machine)
   * @param view       the view for this player
   */
  public SanguineController(SanguineModel model, Player thisPlayer,
                            PlayerType player, SanguineView view) {
    if (model == null || thisPlayer == null || player == null || view == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }

    this.model = model;
    this.thisPlayer = thisPlayer;
    this.player = player;
    this.view = view;

    // Register as listener for view events (human clicks)
    view.addFeaturesListener(this);

    // Register as listener for player events (machine emits moves here)
    player.addFeaturesListener(this);

    model.addModelStatusListener(this);
  }

  @Override
  public void handleCardClick(int handIndex, Player clickedPlayer) {
    // Ignore if not our turn
    if (!isMyTurn) {
      return;
    }

    // Ensure player can only select their own cards
    if (clickedPlayer != thisPlayer) {
      return;
    }

    // Store the selected card
    selectedCardIndex = handIndex;
    System.out.println(thisPlayer + " selected card at index " + handIndex);
  }

  @Override
  public void handleCellClick(int row, int col) {
    // Ignore if not our turn
    if (!isMyTurn) {
      return;
    }

    // Store the selected cell
    selectedRow = row;
    selectedCol = col;
    System.out.println(thisPlayer + " selected cell (" + row + ", " + col + ")");
  }

  @Override
  public void handleConfirm() {
    System.out.println(">>> CONFIRM pressed by " + thisPlayer + " controller");
    System.out.println("    isMyTurn = " + isMyTurn);
    System.out.println("    selectedCard = " + selectedCardIndex);
    System.out.println("    selectedCell = (" + selectedRow + ", " + selectedCol + ")");

    if (!isMyTurn) {
      System.out.println("    BLOCKED: Not my turn!");
      return;
    }

    // Ensure both card and cell are selected
    if (selectedCardIndex == -1 || selectedRow == -1 || selectedCol == -1) {
      showError("Please select both a card and a cell before confirming.");
      return;
    }

    try {
      if (!model.moveCard(selectedCardIndex, selectedRow, selectedCol)) {
        showError("That move is not legal. Please choose a valid card and cell.");
        clearSelections();
        return;
      }

      // Execute the move (this also switches turns in the model)
      System.out.println("    Executing placeCard...");
      model.placeCard(selectedCardIndex, selectedRow, selectedCol);
      System.out.println(thisPlayer + " placed card at (" + selectedRow + ", "
          + selectedCol + ")");

      // Clear selections
      clearSelections();

      // No need to call view.refresh() here if views listen to model

    } catch (IllegalStateException e) {
      showError("Invalid move: " + e.getMessage());
      clearSelections();
    } catch (Exception e) {
      showError("Error making move: " + e.getMessage());
      clearSelections();
    }
  }

  @Override
  public void handlePass() {
    // Ignore if not our turn
    if (!isMyTurn) {
      return;
    }

    try {
      // Clear any selections
      clearSelections();

      // Pass turn (this switches turns in the model)
      model.passTurn();
      System.out.println(thisPlayer + " passed their turn");

    } catch (Exception e) {
      showError("Error passing turn: " + e.getMessage());
    }
  }

  @Override
  public void onPlayerTurn(Player player) {
    isMyTurn = (player == thisPlayer);

    System.out.println("==================================================");
    System.out.println("TURN NOTIFICATION: " + thisPlayer + " controller");
    System.out.println("  Current player is: " + player);
    System.out.println("  This controller is for: " + thisPlayer);
    System.out.println("  isMyTurn = " + isMyTurn);
    System.out.println("==================================================");

    if (isMyTurn) {
      clearSelections();
      view.refresh();
      this.player.onTurnStart();
    } else {
      view.refresh();
    }
  }

  @Override
  public void onGameOver(Player winner) {
    // Refresh view one final time
    view.refresh();

    // Display game over message
    String message;
    if (winner == null) {
      message = "Game Over!\n\nIt's a TIE!\n\n"
          + "RED Score: " + model.getScore(Player.RED) + "\n"
          + "BLUE Score: " + model.getScore(Player.BLUE);
    } else {
      message = "Game Over!\n\nWinner: " + winner + "!\n\n"
          + "RED Score: " + model.getScore(Player.RED) + "\n"
          + "BLUE Score: " + model.getScore(Player.BLUE);
    }

    JOptionPane.showMessageDialog(null, message, "Game Over",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Clears all current selections.
   */
  private void clearSelections() {
    selectedCardIndex = -1;
    selectedRow = -1;
    selectedCol = -1;
  }

  /**
   * Displays an error message to the user.
   *
   * @param message the error message to display
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Invalid Move",
        JOptionPane.ERROR_MESSAGE);
  }
}