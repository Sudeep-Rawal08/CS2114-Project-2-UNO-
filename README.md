# UNO Final Build

A console UNO game for one human against a Simple, Normal, or Master bot. The supplied generic LinkedChain stores the deck, each player's separate hand, and legal action candidates.

## Run and Test

Use JDK 11 or newer; the Eclipse compiler settings target Java 8. Import this project and the course CS2-Support project into the same Eclipse workspace. CS2-Support provides student.TestCase and test dependencies; JUnit 4.13.2 is included in lib.

In Eclipse, run src/UNO/UnoGame.java with **Run As > Java Application**. Run the test source folder with **Run As > JUnit Test**. Refresh with F5 and use **Project > Clean** if Eclipse has stale classes.

## Controls and Rules

Choose 1 Simple, 2 Normal, or 3 Master; the difficulty names also work. Your hand is numbered. Enter its displayed number to play a card, or D to draw. If the drawn card is playable, Y/Yes plays it and N/No keeps it and ends the turn. Invalid choices prompt again. Wild and +4 request a full color name: Red, Blue, Green, or Yellow, case-insensitively.

Both players start with seven cards. A shuffled numbered card starts the round. Match the active color, number, or special-card type. This version permits +4 even when a matching-color card is held. Skip, +2, and +4 skip the opponent; Reverse also skips the opponent in this two-player game. Drawing stops when the deck is exhausted; discards are not recycled. Input ending closes the round safely. After a round, Y/Yes starts another and N/No exits. There is no Q command.

Simple selects the first legal play. Normal favors special cards using fixed scores. Master adds the value of remaining cards matching the resulting color and a last-card bonus. These are local heuristics, not lookahead search or a guarantee of the best possible move. No external model API is used.

## System Architecture

UnoGame owns the console session, difficulty selection, round loop, and replay. PlayerClass reads human choices; BotClass inherits hand storage and overrides move selection. Both submit plays through GameClass.applyMove, which checks ownership, legality, and penalty-deck capacity before changing the played-card state. SpecialCards dispatches effects through GameClass helpers.

Each hand and the deck use LinkedChain<Card>, holding NumberedCards and SpecialCards. GameClass builds LinkedChain<Move> candidates for bot selection. Move describes a play and any chosen wild color, or a draw, without executing it. All collections use the same provided generic class through its public API.

[System diagram (editable .drawio)](UNO_System_Diagram.drawio). Open this file in [diagrams.net](https://app.diagrams.net/) using File > Open From > Device.

## Reflection

- **Specification to prototype:** Card gives NumberedCards and SpecialCards a common storage type. Move represents legal action candidates, so the same generic chain can hold a third concrete element type. Application code uses the supplied chain's boolean add/remove results and indexed traversal rather than the iterator proposed in the spec.
- **Prototype to final:** UnoGame runs the round and replay loop. PlayerClass.playCard now accepts hand indices and asks whether to play a legal draw. Card's type and effect methods replace repeated casts. Normal/Master use the local scoring described above. These changes simplify console selection and separate card behavior from the session loop.
- **Validation fixes:** Human plays now use GameClass.applyMove as bot plays do. +2/+4 capacity is checked before removing the selected card, and direct penalty draws are checked before drawing anything. Wild input finishes before submitting a play. Regression tests verify rejection preserves hands, deck, active color, and top card.
- **LinkedChain changes:** The provided implementation is unchanged apart from its UNO package declaration. Game rules and input handling remain in the application classes.
- **Unknowns:** We had not defined a clear list before the prototype. Implementation clarified the actual chain contract, legal-action generation, and console-input testing. Real input is tested with Scanner instances; EOF and full-round entry-point tests run in separate Java processes to isolate the shared console input.

For complexity, n is the largest collection involved; for console input, also include input-line length. Card/Move accessors, isValid, and turn updates are O(1). Setup/shuffling, hand display, legal-move generation, and Simple/Normal bot selection are O(n²), because indexed get(i) and addToEnd traverse the chain. Drawing and fixed draw penalties are O(n). An ordinary applyMove is O(n); a bot drawing a Wild can take O(n²) because chooseColor uses indexed traversal. Master selection is O(n³): O(n) candidates each score the hand with O(n²) indexed traversal. Human turns are O(n²) per attempt including display, excluding time waiting for input. A round sums its turns and attempts. Compared with the spec, drawing remains linear, while setup/display become quadratic and Master selection becomes cubic; +4 validity is now constant because this version does not impose the matching-color restriction.

## Test Coverage

The JUnit suite covers each declared public application method with normal behavior and invalid-input or boundary cases. Parameterless methods and accessors use empty collections, default values, alternate card types, and value boundaries instead of invented invalid arguments. The course LinkedChain is checked through reuse tests, including empty collections and removal of missing entries.

- CardTest: base-card defaults, numbered-card values, constructors, setters, formatting, and rejected mutations.
- MoveTest: play/draw accessors and invalid card/color combinations.
- GameClassTest: setup, rule checks, draw/apply overloads, candidates, display, winner checks, and rejected-state preservation.
- PlayerClassTest: actual numbered input, empty/invalid/oversized input, Y/N draw choices, real color prompts, EOF, and shared play validation.
- BotClassTest: difficulty behavior, scoring, color selection, drawn cards, special effects, null arguments, and deck exhaustion.
- SpecialCardsTest: each effect's dispatch, invalid arguments, wild color boundaries, and insufficient penalties.
- HouseRulesTest: disabled defaults, independent instances, and toggles in both directions; rules remain unimplemented.
- UnoGameTest: all difficulty constructors, real entry-point input recovery, EOF termination, and a complete round.
- LinkedChainReuseTest: store/retrieve card subclasses and moves using one generic class, empty collections, and missing removal.

## Next Steps

1. Manually play all three levels and check special effects and input recovery. Owner: Greek Yuan.
2. Add regression tests for any further playtesting defects before fixing them. Owner: Sudeep Rawal.
3. Finish attribution details and rehearse the final demonstration. Owner: Edward Wei.

The smarter-bot goal is implemented as the revised Normal/Master heuristics; the original globally optimal wording is not claimed. External model integration and HouseRules gameplay are outside this final build.

## Attribution

## 👥 Contributions & Work Performed

| Class or test file | Contributor | Work performed |
|---|---|---|
| `src/UNO/LinkedChain.java` | CS 2114 Staff (provided) | Provided baseline custom linked list data structure for card management. |
| `src/UNO/Card.java` | Edward Wei, Greek Yuan | Implemented the base abstract Card class, common attributes, and interface methods. |
| `src/UNO/NumberedCards.java` | Sudeep Rawal, Greek Yuan | Implemented standard colored numbered cards and value comparisons. |
| `src/UNO/SpecialCards.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Implemented special action cards (`+2`, Skip, Reverse, Wild, `+4`) and their unique effects. |
| `src/UNO/Move.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Implemented move representation for card plays, color choices, and draw actions. |
| `src/UNO/BotDifficulty.java` | Edward Wei, Greek Yuan | Defined computer opponent difficulty tiers (Simple, Normal, Master). |
| `src/UNO/GameClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Coordinated game rules, turns, deck shuffling, and integrated house rules (stacking penalties, 7-swap hand exchanges, and draw-till-match). |
| `src/UNO/PlayerClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Managed human player hand state, card displays, and interactive input prompts. |
| `src/UNO/BotClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Implemented AI decision-making logic and difficulty-based strategic move evaluation. |
| `src/UNO/UnoGame.java` | Sudeep Rawal, Edward Wei | Managed game loop execution, round flow, bot difficulty selection, and house rule preference prompting. |
| `test/UNO/CardTest.java` | Sudeep Rawal, Greek Yuan | Unit tested card properties, matching logic, and validation rules. |
| `test/UNO/MoveTest.java` | Sudeep Rawal, Edward Wei, Greek Yuan | Unit tested move creation and state getters. |
| `test/UNO/LinkedChainReuseTest.java` | Greek Yuan, Edward Wei | Tested LinkedChain structural operations and data reusability. |
| `test/UNO/GameClassTest.java` | Sudeep Rawal | Unit tested game orchestration, turn management, and win conditions. |
| `test/UNO/SpecialCardsTest.java` | Sudeep Rawal, Edward Wei | Unit tested special card effects, scoring, and application. |
| `test/UNO/PlayerClassTest.java` | Edward Wei, Greek Yuan | Unit tested player hand handling and input choices. |
| `test/UNO/BotClassTest.java` | Sudeep Rawal, Greek Yuan | Unit tested bot turn decisions, move selection, and difficulty levels. |
| `test/UNO/UnoGameTest.java` | Sudeep Rawal, Edward Wei | Unit tested game startup, round transitions, and termination handling. |
| `src/UNO/HouseRules.java` | Sudeep Rawal | Implemented optional house rule configurations (`stackingPlus`, `sevenSwap`, `drawTillMatch`) and toggle methods. |
| `test/UNO/HouseRulesTest.java` | Sudeep Rawal | Unit tested house rule defaults, toggles, getter/setter safety, and rule-driven game mechanics (stacking restrictions, 7-swaps, and draw-till-match behavior). |

## GenAI Use

GenAI assisted with code development and review, testing, architectural discussions, and debugging. The final presentation will distinguish generated suggestions from checks actually performed by team members.
