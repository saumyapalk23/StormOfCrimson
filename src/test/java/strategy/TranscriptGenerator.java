package strategy;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaximizeRowScoreStrategy;


/**
 * Generates transcript files showing which coordinates strategies check.
 */
public class TranscriptGenerator {

  private static Card makeCard(String name, int cost, int value) {
    boolean[][] inf = new boolean[5][5];
    inf[2][3] = true; // influence to the right, as defined in tests
    return new Card(name, cost, value, inf);
  }

  private static List<Card> makeDeck() {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(makeCard("Card" + i, 1, i + 1));
    }
    return deck;
  }
  /**
   * Generates transcript files for strategies.
   *
   * @param args command line arguments (unused)
   */

  public static void main(String[] args) {
    try {
      List<Card> deck = makeDeck();


      BasicSanguine model = new BasicSanguine();
      model.startGame(deck, deck, 3, 5, 5);

      System.out.println("Generating FillFirst transcript...");
      MockTranscriptModel mock1 = new MockTranscriptModel(model);
      FillFirstStrategy fillFirst = new FillFirstStrategy();
      int[] move1 = fillFirst.moveChoice(mock1, Player.RED);


      writeTranscript("strategy-transcript-first.txt",
          mock1.getTranscript(), move1);
      System.out.println("Created: strategy-transcript-first.txt");


      System.out.println("Generating MaximizeRowScore transcript...");
      MockTranscriptModel mock2 = new MockTranscriptModel(model);
      MaximizeRowScoreStrategy maxRow = new MaximizeRowScoreStrategy();
      int[] move2 = maxRow.moveChoice(mock2, Player.RED);


      writeTranscript("strategy-transcript-score.txt",
          mock2.getTranscript(), move2);
      System.out.println("Created: strategy-transcript-score.txt");


      System.out.println("\nTranscripts generated successfully!");


    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }


  private static void writeTranscript(String filename,
                                      List<String> transcript,
                                      int[] move) throws IOException {
    try (FileWriter writer = new FileWriter(filename)) {
      writer.write("Strategy Transcript\n");
      writer.write("===================\n\n");


      for (String line : transcript) {
        writer.write(line + "\n");
      }


      writer.write("\n===================\n");
      if (move != null) {
        writer.write("Chosen move: card " + move[0]
            + " at (" + move[1] + ", " + move[2] + ")\n");
      } else {
        writer.write("Chosen move: null (no valid moves)\n");
      }
    }
  }
}