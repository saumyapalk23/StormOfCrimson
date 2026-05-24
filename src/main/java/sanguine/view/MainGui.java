package sanguine.view;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import sanguine.controller.Features;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Main GUI view for Sanguine game.
 */
public class MainGui extends JFrame implements SanguineView {

  private final ReadOnlySanguineModel model;
  private final Player thisPlayer;
  private final BoardPanel boardPanel;
  private final HandPanel handPanel;
  private final InfoPanel infoPanel;

  /**
   * Constructor for MainGUI.
   *
   * @param model read only model
   * @param thisPlayer which player this view represents
   */
  public MainGui(ReadOnlySanguineModel model, Player thisPlayer) {
    super("Sanguine - " + thisPlayer + " Player");

    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;
    this.thisPlayer = thisPlayer;

    // Initialize panels
    this.infoPanel = new InfoPanel(model);
    this.boardPanel = new BoardPanel(model);
    this.handPanel = new HandPanel(model, thisPlayer);

    // Setup frame
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());

    // Add panels
    this.add(infoPanel, BorderLayout.NORTH);
    this.add(boardPanel, BorderLayout.CENTER);
    this.add(handPanel, BorderLayout.SOUTH);

    // Pack and make visible
    this.pack();
  }

  @Override
  public void refresh() {
    if (infoPanel != null) {
      infoPanel.repaint();
    }
    if (boardPanel != null) {
      boardPanel.repaint();
    }
    if (handPanel != null) {
      handPanel.repaint();
    }
  }

  @Override
  public void addFeaturesListener(Features features) {
    boardPanel.addFeaturesListener(features);
    handPanel.addFeaturesListener(features);

    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          features.handleConfirm();
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
          features.handlePass();
        }
      }
    });

    this.setFocusable(true);
    this.requestFocus();
  }
}