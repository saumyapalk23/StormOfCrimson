package sanguine.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sanguine.model.Card;

/**
 * Deck configuration file that reads in the deck.config file and parses it into cards.
 * Used in order to properly perform run configuration and textual view.
 */
public class DeckConfiguration {

  /**
   * Reads a deck configuration file and returns a list of Card objects.
   *
   * @param filePath The path to the configuration file.
   * @return A list of Card objects created from the file.
   * @throws IOException              if the file cannot be read.
   * @throws IllegalArgumentException if the file format is invalid.
   */
  public static List<Card> readDeckFile(String filePath) throws IOException {
    List<Card> deck = new ArrayList<>();
    File configFile = new File(filePath);

    try (Scanner scanner = new Scanner(configFile)) {
      while (scanner.hasNextLine()) {
        String cardLine = scanner.nextLine();
        Scanner lineScanner = new Scanner(cardLine);
        String name = lineScanner.next();
        int cost = lineScanner.nextInt();
        int value = lineScanner.nextInt();

        // read the 5x5 Influence grid using a nested for-loop
        boolean[][] influenceGrid = new boolean[5][5];
        for (int row = 0; row < 5; row++) {
          String rowLine = scanner.nextLine();
          if (rowLine.length() != 5) {
            throw new IllegalArgumentException("Influence row must be 5 characters.");
          }
          for (int col = 0; col < 5; col++) {
            char influenceChar = rowLine.charAt(col);
            if (influenceChar == 'I') {
              influenceGrid[row][col] = true;
            } else if (influenceChar == 'X') {
              influenceGrid[row][col] = false;
            } else if (influenceChar == 'C') {
              // C is the center and must be false for influence
              influenceGrid[row][col] = false;
              if (row != 2 || col != 2) {
                throw new IllegalArgumentException("Card center 'C' must only be at [2][2].");
              }
            } else {
              throw new IllegalArgumentException("Invalid influence character: " + influenceChar);
            }
          }
        }

        // Create and add the Card
        deck.add(new Card(name, cost, value, influenceGrid));
      }
    }
    return deck;
  }
}