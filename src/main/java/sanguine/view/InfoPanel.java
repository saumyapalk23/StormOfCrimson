package sanguine.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * Shows current player and scores at the top.
 */
public class InfoPanel extends JPanel {

  private final ReadOnlySanguineModel model;

  /**
   * Constructor for InfoPanel.
   *
   * @param model read only model
   */
  public InfoPanel(ReadOnlySanguineModel model) {
    this.model = model;
    this.setPreferredSize(new Dimension(800, 60));
    this.setBackground(Color.LIGHT_GRAY);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    Player current = model.getCurrentPlayer();
    g2d.setFont(new Font("Arial", Font.BOLD, 20));
    g2d.setColor(current == Player.RED ? Color.RED : Color.BLUE);
    g2d.drawString("Current Player: " + current, 20, 35);

    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
    g2d.setColor(Color.BLACK);
    int redScore = model.getScore(Player.RED);
    int blueScore = model.getScore(Player.BLUE);
    g2d.drawString("Scores - RED: " + redScore + " | BLUE: " + blueScore, 300, 35);

    g2d.setFont(new Font("Arial", Font.ITALIC, 12));
    g2d.drawString("[ENTER: Confirm | P: Pass]", 700, 35);
  }
}
