package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Strategy that tries to win rows from top to bottom.
 * For each row where current player's score <= opponent's score,
 * finds the first move that would make current player's score > opponent's score.
 */
public class MaximizeRowScoreStrategy implements Strategy {

  @Override
  public int[] moveChoice(ReadOnlySanguineModel model, Player player) {
    Player opponent = player.switchColor();
    List<Card> hand = model.getHand(player);

    // Visit rows from top to bottom
    for (int row = 0; row < model.getRows(); row++) {
      int ourScore = model.getRowScore(row, player);
      int theirScore = model.getRowScore(row, opponent);

      // Only consider rows where loss or tied
      if (ourScore <= theirScore) {
        for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
          Card card = hand.get(handIndex);

          for (int col = 0; col < model.getCols(); col++) {
            if (model.moveCard(handIndex, row, col)) {
              // Calculate what our new score would be
              int newScore = ourScore + card.getValue();

              if (newScore > theirScore) {
                return new int[]{handIndex, row, col};
              }
            }
          }
        }
      }
    }

    return null;
  }
}