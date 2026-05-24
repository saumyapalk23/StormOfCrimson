package sanguine.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import sanguine.controller.Features;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Displays the current player's hand of cards.
 * Publishes card click events to subscribed Features listeners.
 */
public class HandPanel extends JPanel {

  private final ReadOnlySanguineModel model;
  private final Player thisPlayer;
  private final List<Features> featuresListeners;
  private int selectedCardIndex = -1;

  private static final int CARD_WIDTH = 100;
  private static final int CARD_HEIGHT = 140;
  private static final int CARD_SPACING = 10;

  /**
   * Constructor for Hand panel.
   *
   * @param model read only model
   * @param thisPlayer which player this hand belongs to
   */
  public HandPanel(ReadOnlySanguineModel model, Player thisPlayer) {
    this.model = model;
    this.thisPlayer = thisPlayer;
    this.featuresListeners = new ArrayList<>();
    this.setBackground(new Color(240, 240, 240));

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleMouseClick(e.getX(), e.getY());
      }
    });
  }

  @Override
  public Dimension getPreferredSize() {
    int handSize = 5;

    try {
      handSize = model.getHand(thisPlayer).size();
    } catch (IllegalStateException e) {
      // empty catch block, game hasn't begun
    }

    int width = (handSize * (CARD_WIDTH + CARD_SPACING)) + 40;
    return new Dimension(width, CARD_HEIGHT + 40);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    final List<Card> hand = model.getHand(thisPlayer);

    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    g2d.setColor(Color.BLACK);
    g2d.drawString(thisPlayer + "'s Hand:", 20, 20);

    int startX = 20;
    int startY = 30;

    for (int i = 0; i < hand.size(); i++) {
      int x = startX + (i * (CARD_WIDTH + CARD_SPACING));
      drawCard(g2d, hand.get(i), x, startY, i, thisPlayer);
    }
  }

  private void drawCard(Graphics2D g2d, Card card, int x, int y,
                        int index, Player player) {
    // Highlight if selected
    if (index == selectedCardIndex) {
      g2d.setColor(Color.CYAN);
      g2d.fillRect(x - 2, y - 2, CARD_WIDTH + 4, CARD_HEIGHT + 4);
    }

    g2d.setColor(Color.WHITE);
    g2d.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);

    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString(card.getName(), x + 5, y + 15);
    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
    g2d.drawString("Cost: " + card.getCost(), x + 5, y + 30);
    g2d.drawString("Value: " + card.getValue(), x + 5, y + 45);

    drawInfluenceGrid(g2d, card, player, x + 5, y + 55);
  }

  private void drawInfluenceGrid(Graphics2D g2d, Card card, Player player,
                                 int x, int y) {
    boolean[][] grid = player == Player.BLUE
        ? card.getMirroredInfluenceGrid() : card.getInfluenceGrid();

    int cellSize = 16;
    g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        int cellX = x + (col * cellSize);
        int cellY = y + (row * cellSize);

        if (row == 2 && col == 2) {
          g2d.setColor(Color.ORANGE);
          g2d.fillRect(cellX, cellY, cellSize, cellSize);
          g2d.setColor(Color.BLACK);
          g2d.drawString("C", cellX + 4, cellY + 12);
        } else if (grid[row][col]) {
          g2d.setColor(new Color(100, 200, 255));
          g2d.fillRect(cellX, cellY, cellSize, cellSize);
          g2d.setColor(Color.BLACK);
          g2d.drawString("I", cellX + 5, cellY + 12);
        } else {
          g2d.setColor(Color.LIGHT_GRAY);
          g2d.fillRect(cellX, cellY, cellSize, cellSize);
          g2d.setColor(Color.GRAY);
          g2d.drawString("X", cellX + 4, cellY + 12);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawRect(cellX, cellY, cellSize, cellSize);
      }
    }
  }

  /**
   * Handles mouse clicks and notifies all subscribers.
   */
  private void handleMouseClick(int mouseX, int mouseY) {
    List<Card> hand = model.getHand(thisPlayer);
    int startX = 20;
    int startY = 30;

    for (int i = 0; i < hand.size(); i++) {
      int x = startX + (i * (CARD_WIDTH + CARD_SPACING));

      if (mouseX >= x && mouseX <= x + CARD_WIDTH
          && mouseY >= startY && mouseY <= startY + CARD_HEIGHT) {

        // Toggle
        if (i == selectedCardIndex) {
          selectedCardIndex = -1;
        } else {
          selectedCardIndex = i;
        }

        // Notify all subscribers/pubsub
        notifyCardClick(i, thisPlayer);
        repaint();
        return;
      }
    }
  }

  /**
   * Notifies all subscribed Features listeners about a card click.
   * This is the "publish" part of pub-sub.
   */
  private void notifyCardClick(int handIndex, Player player) {
    for (Features listener : featuresListeners) {
      listener.handleCardClick(handIndex, player);
    }
  }

  /**
   * Adds a Features listener (subscriber).
   * This is how controllers subscribe to this panel's events.
   */
  public void addFeaturesListener(Features features) {
    this.featuresListeners.add(features);
  }

  /**
   * Removes a Features listener (unsubscribe).
   */
  public void removeFeaturesListener(Features features) {
    this.featuresListeners.remove(features);
  }
}