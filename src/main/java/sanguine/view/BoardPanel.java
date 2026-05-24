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
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * Displays the game board with cells.
 * Publishes cell click events to subscribed Features listeners.
 */
public class BoardPanel extends JPanel {

  private final ReadOnlySanguineModel model;
  private final List<Features> featuresListeners;  // List of subscribers
  private int selectedRow = -1;
  private int selectedCol = -1;

  private static final int CELL_SIZE = 70;

  /**
   * Board panel constructor.
   *
   * @param model read only model
   */
  public BoardPanel(ReadOnlySanguineModel model) {
    this.model = model;
    this.featuresListeners = new ArrayList<>();  // Initialize subscriber list
    this.setBackground(Color.WHITE);

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleMouseClick(e.getX(), e.getY());
      }
    });
  }

  @Override
  public Dimension getPreferredSize() {
    int rows = model.getRows();
    int cols = model.getCols();
    int width = (cols * CELL_SIZE) + 160;
    int height = (rows * CELL_SIZE) + 40;
    return new Dimension(width, height);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    int rows = model.getRows();
    int cols = model.getCols();
    int startX = 80;
    int startY = 20;

    for (int row = 0; row < rows; row++) {
      int y = startY + (row * CELL_SIZE);

      // Red row score (left)
      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.BOLD, 18));
      g2d.drawString(String.valueOf(model.getRowScore(row, Player.RED)),
              30, y + CELL_SIZE / 2 + 5);

      for (int col = 0; col < cols; col++) {
        int x = startX + (col * CELL_SIZE);
        drawCell(g2d, row, col, x, y);
      }

      // Blue row score (right)
      int rightX = startX + (cols * CELL_SIZE) + 10;
      g2d.drawString(String.valueOf(model.getRowScore(row, Player.BLUE)),
              rightX, y + CELL_SIZE / 2 + 5);
    }
  }

  private void drawCell(Graphics2D g2d, int row, int col, int x, int y) {
    final Cell cell = model.getCell(row, col);

    // highlight
    if (row == selectedRow && col == selectedCol) {
      g2d.setColor(Color.CYAN);
      g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
    }

    // border
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);

    // contents
    if (cell.hasCard()) {
      drawCardOnBoard(g2d, cell, x, y);
    } else if (cell.hasPawns()) {
      drawPawns(g2d, cell, x, y);
    } else {
      g2d.setColor(Color.LIGHT_GRAY);
      g2d.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
    }
  }

  private void drawCardOnBoard(Graphics2D g2d, Cell cell, int x, int y) {
    Player owner = cell.getOwner();
    Card card = cell.getCard();

    g2d.setColor(owner == Player.RED
            ?
            new Color(255, 200, 200) : new Color(200, 200, 255));
    g2d.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 20));
    String valueStr = String.valueOf(card.getValue());
    int strWidth = g2d.getFontMetrics().stringWidth(valueStr);
    g2d.drawString(valueStr, x + (CELL_SIZE - strWidth) / 2, y + CELL_SIZE / 2 + 7);
  }

  private void drawPawns(Graphics2D g2d, Cell cell, int x, int y) {
    Player owner = cell.getOwner();
    int count = cell.getPawnCount();

    g2d.setColor(owner == Player.RED
            ?
            new Color(255, 230, 230) : new Color(230, 230, 255));
    g2d.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

    g2d.setColor(owner == Player.RED ? Color.RED : Color.BLUE);
    g2d.setFont(new Font("Arial", Font.BOLD, 30));
    String countStr = String.valueOf(count);
    int strWidth = g2d.getFontMetrics().stringWidth(countStr);
    g2d.drawString(countStr, x + (CELL_SIZE - strWidth) / 2, y + CELL_SIZE / 2 + 10);
  }

  /**
   * Handles mouse clicks and notifies all subscribers.
   */
  private void handleMouseClick(int mouseX, int mouseY) {
    int startX = 80;
    int startY = 20;
    int col = (mouseX - startX) / CELL_SIZE;
    int row = (mouseY - startY) / CELL_SIZE;

    // Check bounds
    if (row >= 0 && row < model.getRows() && col >= 0 && col < model.getCols()) {
      // Toggle selection
      if (row == selectedRow && col == selectedCol) {
        selectedRow = -1;
        selectedCol = -1;
      } else {
        selectedRow = row;
        selectedCol = col;
      }

      // Notify all subscribers (pub-sub pattern)
      notifyCellClick(row, col);
      repaint();
    }
  }

  /**
   * Notifies all subscribed Features listeners about a cell click.
   */
  private void notifyCellClick(int row, int col) {
    for (Features listener : featuresListeners) {
      listener.handleCellClick(row, col);
    }
  }

  /**
   * Adds a Features listener (subscriber).
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