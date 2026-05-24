package sanguine.view;

import sanguine.controller.Features;

/**
 * Interface for Sanguine view.
 */
public interface SanguineView {

  /**
   * Makes the view visible.
   */
  void setVisible(boolean visible);

  /**
   * Refreshes the view to reflect current game state.
   */
  void refresh();

  /**
   * Adds a features listener to handle user actions.
   *
   * @param features the controller that will handle events
   */
  void addFeaturesListener(Features features);
}
