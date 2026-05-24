# Overview -- Part 1

Sanguine is a game played on a grid, by two players. It involves playing cards through
placing variously cost-applied cards that have levels of influence over the grid and steer the game.
When both players pass, the game ends, and calculations based on cost make up the scores.

High-level assumptions:

- should be designed so we can include AI/human interaction without refactoring code
- two players are playing
- columns # is always odd
- 5x5 grid for the game to be played

## Quick Start

- Here is a small code snippet which is a test representing a rudimentary gameplay:

```
@Test
  public void testGameInitialization() {
  int rows = 3;
  int cols = 5;
  int handSize = 3;

  model.startGame(redDeck, blueDeck, rows, cols, handSize);

  assertEquals(Player.RED, model.getCurrentPlayer());
  assertFalse(model.isGameOver());
  assertEquals(handSize, model.getHand(Player.RED).size());
  assertEquals(handSize, model.getHand(Player.BLUE).size());
  assertEquals(model.getBoard().getRows(), rows);
  assertEquals(model.getBoard().getCols(), cols);
}
```

## Key Components

- The model, aka sanguine.model which is the collective production of the model classes working together.
- The view, which includes all the textual rendering of the game.
- The controller, which includes deck configurations and runs the collection of card objects.
- The main, which drives the entirety of the run configurations through the command line.

## Key Subcomponents

- The Sanguine Model interface and the Basic Sanguine implementation:
    - Interface interacts with the game and has method outlines for moves and other actions.
    - Implementation maintains all inner functions like board, player situations, and order of operations.
- Board Interface and the Board Implementation:
    - Interface represents the board and possible methods/functions.
    - Implementation maintains card possibilities, cells, pawns, etc.
- Cell and Card
    - Cell is a box on a board (a single space) with different functions
        - Can be empty, 1-3 pawns, be owned by a player, or have a card.
    - Card is a game card with attributes (name, cost, value, influence)
        - different for Red/Blue players
- Player/PlayerHand
    - Player enum defines functions for red/blue players
- Deck
    - Manages draw pile for players
    - Collection of card objects
- TextualView
    - View component of MVC
    - Does a rudimentary display of cards
- DeckConfiguration
    - Reads deck.config, parses into cards

## Source Organization

This is a map for you, a user, to navigate our codebase:
![Outline](outline.png)
Structure:

src/main/java/sanguine/Sanguine.Java (main method file)
/sanguine/controller/DeckConfiguration.java (deck file parser)
/sanguine/model/BasicSanguine.java (model impl)
/sanguine/model/Board.java (board interface)
/sanguine/model/BoardImpl.java (model impl)
/sanguine/model/Card.java (card class)
/sanguine/model/Cell.java (board spaces)
/sanguine/model/Deck.java (draw piles)
/sanguine/model/Player.java (color enum)
/sanguine/model/PlayerHand.java (hand piles)
/sanguine/model/SanguineModel.java (model interface)
/sanguine/view/SanguineTextualView.java (textual rendering)
/test/java/sanguine/model/InternalTests.java (internal impl tests)
/test/java/sanguine/model/ModelTests.java (public interface tests)


# Changes for Part 2
Unfortunately, we were held up and struggled to get all of part four of assignment two out. Therefore, the strategy classes are in 
our changes for part three. However, here is what we got done for 2:

We did model refactoring - Read-Only Interface
For this refacotrization, we split SanguineModel into two interfaces:
ReadOnlySanguineModel: Which contains only observation methods
SanguineModel: Which extends ReadOnlySanguineModel and adds mutation methods

We ensured:
Views should not have the ability to mutate the model
This ensures proper separation of concerns and prevents accidental model modification through the view
Enforces the MVC pattern by ensuring views can only observe, not modify

How:
Created ReadOnlySanguineModel with methods like: getCurrentPlayer(), isGameOver(), getWinner()
getScore(), getBoard(), getHand(), getDeckSize(), moveCard() (for checking legality), getCell(), getRowScore(),
getRows(), getCols()


SanguineModel extends ReadOnlySanguineModel and adds:
startGame(), placeCard(), passTurn(), addModelStatusListener()

We also added missing model functionality.

Already had: Deck configuration file reading via DeckConfiguration.readDeckFile()
Board copying: Implemented defensive copying in getBoard() and related methods

We added:
getRows() and getCols(): Returns board dimensions
getCell(row, col): Returns cell contents at coordinates
getHand(Player): Returns player's hand
Cell ownership: Accessible via Cell.getOwner()
moveCard(handIndex, row, col): Checks move legality
getRowScore(row, player): Calculates row score for a player
getScore(player): Calculates total score
isGameOver(): Checks if game is finished
getWinner(): Returns winner or null for tie

New Methods:
placeCard(handIndex, row, col): Current player plays a card
passTurn(): Current player passes

# Changes for Part 3
1. Controller Implemetation
We created a SanguineController class that implements both Features and ModelStatusListener
Each player has their own dedicated controller instance
We needed to coordinate between asynchronous GUI events and synchronous turn-based gameplay
- Registers as listener for both view events (mouse clicks, key presses) and model events (turn changes)
- Enforces turn-based play using isMyTurn flag
- Prevents players from selecting opponent's cards
- bValidates that both card and cell are selected before confirming moves
- Clears selections after moves or passes
- Displays error messages to users via JOptionPane
- Shows game over dilog with scores

2. Listener Interfaces & Features Interface:
Includfes:
- andleCardClick(handIndex, player): Called when card in hand is clicked
- handleCellClick(row, col): Called when board cell is clicked
- handleConfirm(): Called when user confirms move (ENTER key)
- handlePass(): Called when user passes turn (P key)

ModelStatusListener Interface:
Includes;
- onPlayerTurn(player): Called when active player changes
- onGameOver(winner): Called when game ends

3. Player Implementations & HumanPlayer:
- Implements PlayerType interface
- Waits for GUI input from the view
- Does not emit events directly (view emits them)

MachinePlayer:
- Implements PlayerType interface
- Uses a Strategy to compute moves
- Publishes player-action events when its turn starts
- Notifies controller of card click, cell click, and confirmation

4. Strategy Implementations (supposed to have done this for 6, sorry!)
   1. FillFirstStrategy
   Iterates through cards left-to-right in hand
   For each card, tries positions top-to-bottom, left-to-right
   Returns first legal move found
   Returns null if no legal moves exist

   2. MaximizeRowScoreStrategy
   Visits rows from top to bottom
   For each row where current player is losing or tied:
    Tries each card in hand order
    Tries positions left to right in that row
    Returns first move that wins the row (makes player's score > opponent's score)
    Returns null if no move wins any row

We also had a Strategy Interface which initialized choosing a move for any given player using an integer array.

Also, we included a MainGui with JFrame, an InfoPanel displays current player's turn, and a BoardPanel
rendering the game board grid.
We also had a HandPanel displaying current player's hand of cards.

# Two human players
java -jar sanguine.jar 5 7 docs/deck.config docs/deck.config human human

# Human vs AI
java -jar sanguine.jar 5 7 docs/deck.config docs/deck.config human strategy1

# Two AI players
java -jar sanguine.jar 5 7 docs/deck.config docs/deck.config strategy1 strategy2


Testing:
Strategy Tests (StrategyTests.java)
- Tests both strategies with various game states
- Verifies correct move selection
- Tests handling of no valid moves
- Uses mock models to verify behavior

Controller Tests (SanguineControllerTest.java)
- Tests controller coordination between model and view
- Verifies turn enforcement
- Tests selection and confirmation logic
- Validates error handling
