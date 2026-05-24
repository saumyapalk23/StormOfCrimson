package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
/**
 * Strategy that chooses the first legal move found.
 * Iterates through cards left-to-right, then positions top-to-bottom, left-to-right.
 */

public class FillFirstStrategy implements Strategy {

  @Override
  public int[] moveChoice(ReadOnlySanguineModel model, Player player) {
    List<Card> hand = model.getHand(player);

    // Tries each card in hand order (left to right)
    for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
      // Tries all positions (top-to-bottom, left-to-right)
      for (int row = 0; row < model.getRows(); row++) {
        for (int col = 0; col < model.getCols(); col++) {
          if (model.moveCard(handIndex, row, col)) {
            return new int[]{handIndex, row, col};
          }
        }
      }
    }

    return null; // neaning there are no valid moves
  }
}