package sanguine;

import java.util.List;
import sanguine.controller.DeckConfiguration;
import sanguine.controller.SanguineController;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.HumanPlayer;
import sanguine.strategy.MachinePlayer;
import sanguine.strategy.MaximizeRowScoreStrategy;
import sanguine.strategy.PlayerType;
import sanguine.view.MainGui;

/**
 * Main entry point for the Sanguine game application.
 */
public final class SanguineGame {

  /**
   * Main method for sanguine game.
   * Generates the two views/guis for the blue/red player.
   *
   * @param args the arguments we took in which were: java -jar SanguineJarHW7.jar 5 7
   *             docs/deck.config docs/deck.config human human
   */
  public static void main(String[] args) {
    if (args.length < 6) {
      System.err.println("Usage: rows cols redDeck blueDeck redPlayer bluePlayer");
      System.err.println("Player types: human, strategy1, strategy2");
      System.err.println(
              "Example: java -jar SanguineJarHW7.jar 5 7 docs/deck.config"
                  + "docs/deck.config human human");
      return;
    }

    try {
      int rows = Integer.parseInt(args[0]);
      int cols = Integer.parseInt(args[1]);
      String redDeckPath = args[2];
      String blueDeckPath = args[3];
      final String redPlayerType = args[4];
      final String bluePlayerType = args[5];

      // Validate board
      if (rows <= 0) {
        throw new IllegalArgumentException("rows must be positive");
      }
      if (cols <= 1 || cols % 2 == 0) {
        throw new IllegalArgumentException("cols must be > 1 and odd");
      }

      // Read deck files
      List<Card> redDeck = DeckConfiguration.readDeckFile(redDeckPath);
      List<Card> blueDeck = DeckConfiguration.readDeckFile(blueDeckPath);

      // Hand size
      int handSize = Math.max(1, Math.min(redDeck.size(), blueDeck.size()) / 3);

      // Create model
      SanguineModel model = new BasicSanguine();
      model.startGame(redDeck, blueDeck, rows, cols, handSize);

      // Create views
      MainGui redView = new MainGui(model, Player.RED);
      MainGui blueView = new MainGui(model, Player.BLUE);

      // Position windows
      redView.setLocation(50, 50);
      blueView.setLocation(redView.getX() + redView.getWidth() + 20, 50);

      // Create players
      PlayerType redPlayer = createPlayer(redPlayerType, Player.RED, model);
      PlayerType bluePlayer = createPlayer(bluePlayerType, Player.BLUE, model);

      // Create controllers (they register themselves)
      new SanguineController(model, Player.RED, redPlayer, redView);
      new SanguineController(model, Player.BLUE, bluePlayer, blueView);

      // Show views
      redView.setVisible(true);
      blueView.setVisible(true);

      // Start game
      model.startGame();

      System.out.println("Sanguine game started!");
      System.out.println("RED: " + redPlayerType + " | BLUE: " + bluePlayerType);

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static PlayerType createPlayer(String type, Player player, SanguineModel model) {
    switch (type.toLowerCase()) {
      case "human":
        return new HumanPlayer(player);
      case "strategy1":
        return new MachinePlayer(player, new FillFirstStrategy(), model);
      case "strategy2":
        return new MachinePlayer(player, new MaximizeRowScoreStrategy(), model);
      default:
        throw new IllegalArgumentException("Unknown player type: " + type);
    }
  }
}