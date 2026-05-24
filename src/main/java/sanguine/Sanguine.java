package sanguine;

import java.io.File;
import java.util.List;
import sanguine.controller.DeckConfiguration;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.SanguineModel;
import sanguine.view.SanguineTextualView;

/**
 * Main class for demonstrating the Sanguine game.
 * Reads a deck configuration file and plays through a game automatically.
 */
public class Sanguine {

  /**
   * Main method to run a Sanguine game demo.
   *
   * @param args command line arguments, expects deck config file path at args[0]
   */
  public static void main(String[] args) {
    // Check for arguments
    if (args.length < 1) {
      System.out.println("Usage: java sanguine.Sanguine <deck-config-file>");
      System.out.println("Please provide a deck configuration file path.");
      return;
    }

    String filepath = args[0];
    File configFile = new File(filepath);

    // check if file exists
    if (!configFile.exists()) {
      System.out.println("Error: File '" + filepath + "' does not exist.");
      System.out.println("Please provide a valid deck configuration file.");
      return;
    }

    try {
      // read deck from file
      List<Card> deck = DeckConfiguration.readDeckFile(filepath);

      // check if deck has enough cards for 3x5 board (15 cells minimum)
      if (deck.size() < 15) {
        System.out.println("Error: Deck must contain at least 15 cards for a 3x5 board.");
        System.out.println("This deck only has " + deck.size() + " cards.");
        return;
      }

      // initialize model and start game
      SanguineModel model = new BasicSanguine();
      model.startGame(deck, deck, 3, 5, 5);

      // Create view
      SanguineTextualView view = new SanguineTextualView(model);

      System.out.println("Starting Sanguine Game");
      System.out.println("Board: 3 rows x 5 columns");
      System.out.println("Hand size: 5 cards each\n");

      System.out.println("Initial State:");
      System.out.println(view.toString());
      System.out.println("\n");

      // play game automatically
      playGame(model, view);

      // display final results
      System.out.println("\n FINAL RESULTS");
      System.out.println(view.toString());

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Plays the game automatically until no more cards can be placed.
   * Both players try to place cards; if unable, they pass.
   *
   * @param model the game model
   * @param view  the view to render game state
   */
  private static void playGame(SanguineModel model, SanguineTextualView view) {
    while (!model.isGameOver()) {
      sanguine.model.Player currentPlayer = model.getCurrentPlayer();
      boolean cardPlaced = tryToPlaceCard(model);

      if (cardPlaced) {
        System.out.println(currentPlayer + " placed a card.\n");
      } else {
        model.passTurn();
        System.out.println(currentPlayer + " passed.\n");
      }

      // print board after each action
      System.out.println(view.toString());
      System.out.println("\n");
    }
  }

  /**
   * Attempts to place a card from the current player's hand.
   * Tries each card in hand at each board position.
   *
   * @param model the game model
   * @return true if a card was placed, false if no legal moves
   */
  private static boolean tryToPlaceCard(SanguineModel model) {
    List<Card> hand = model.getHand(model.getCurrentPlayer());
    int rows = model.getBoard().getRows();
    int cols = model.getBoard().getCols();

    // try each card in hand
    for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
      // try each position on board
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          if (model.moveCard(handIndex, row, col)) {
            model.placeCard(handIndex, row, col);
            return true;
          }
        }
      }
    }

    return false; // no legal moves werr found
  }
}