# UNO Prototype

A console UNO game for one human against a Simple, Normal, or Master bot. Hands, the draw deck, and legal moves use the supplied generic LinkedChain.

## Run and Test

Use JDK 11 or newer. Eclipse compiler settings target Java 8, which also runs on Java 17. JUnit 4.13.2 and Hamcrest 3.0 are included in `lib`; their license texts are in `lib/licenses`.

In Eclipse, import this folder as an existing project. Run `src/UNO/UnoGame.java` with **Run As > Java Application**. Run the `test` source folder with **Run As > JUnit Test**. If Eclipse has stale compiled files, refresh the project with F5 and use **Project > Clean**.

## Controls

Choose `1` Simple, `2` Normal, or `3` Master. Enter a card code shown beside a card in your hand: `R5` means Red 5, `B+` means Blue Draw Two, `GS` means Green Skip, and `YR` means Yellow Reverse. `WW` is Wild, `W4` is Draw Four, `D` draws, and `Q` quits. For a Wild, enter Red, Blue, Green, or Yellow (or R/B/G/Y). Input is case-insensitive.

A legal drawn card is played immediately; an illegal drawn card stays in the hand and ends the turn. Draw Four is rejected if the player has a regular-colored card matching the active color. Skip and draw penalties skip the opponent. Reverse changes direction; in this two-player prototype it still passes play to the other player. An empty draw deck stops the round, as specified.

## Reflection

- **Changes from the spec:** Added `Move` for legal action candidates and three bot levels. Simple plays the first legal card; Normal favors its remaining colors and conserves Wilds; Master also prioritizes winning and pressures opponents with few cards. `GameClass` validates actions for both players.
- **LinkedChain changes:** None. The actual `add(T)` and `remove(T)` return boolean, and the class has no iterator. Application code follows that API and uses `toArray` for traversal. `NumberedCards`, `SpecialCards`, and `Move` are stored and retrieved through the same generic class.
- **Unknowns:** We had not defined a clear list of unknowns before building this prototype. During implementation, we clarified the supplied LinkedChain API, legal-move generation, and console-input testing. Manual playtesting is still needed to evaluate usability and the differences between bot difficulty levels.
- **Remaining limitations:** Manual gameplay review is pending. The prototype uses local bots; an external model API is not implemented. A depleted deck ends the round instead of recycling discards.

For complexity, n is the size of the collection being processed; when both the hand and deck are involved, use the larger size. For console-input methods, n also includes the input-line length. Card/Move accessors and turn updates are O(1). Game setup, drawing, hand display, fixed draw penalties, and applying a move are O(n). `isValid` is O(n) in the worst-case +4 check; ordinary checks are O(1). Legal-move generation, bot selection, and bot turns are O(n²), including the chain's linear `addToEnd` cost. Human input is O(n) per attempt, excluding waiting for input. The round's cost is the sum of its turns and input attempts. These retain the spec's linear draw and quadratic bot bounds.

## Next Steps

1. Manually play all three levels and check special effects and input recovery. Owner: ___Greek Yuan___.
2. For defects found during play, add regression cases to `PlayerClassTest`, `BotClassTest`, or `GameClassTest` before fixing them. Owner: ___Sudeep Rawal___.
3. Confirm scope coverage, contributions, and submission details. Owner: ___Edward Wei___.

We refined the smarter-bot stretch goal into three local difficulty levels. Master uses heuristics for immediate wins, color selection, and opponent pressure; it does not guarantee a globally optimal move. API integration is dropped from this prototype. The tests include empty collections, removal of a missing element, rejected-state preservation, and complete human/bot turns.

## Attribution

| Class or test file | Contributor | Work performed |
|---|---|---|
| `src/UNO/LinkedChain.java` | CS 2114 Staff | Provided; unchanged |
| `src/UNO/Card.java` | Sudeep Rawal | Made the base card and color checks |
| `src/UNO/NumberedCards.java` | Greek Yuan | Made numbered cards and number rules |
| `src/UNO/SpecialCards.java` | Edward Wei | Made special cards like skip and wild |
| `src/UNO/Move.java` | Sudeep Rawal | Made the move actions and helper tools |
| `src/UNO/BotDifficulty.java` | Greek Yuan | Set up the difficulty levels |
| `src/UNO/GameClass.java` | Edward Wei | Built game rules, turns, and flow |
| `src/UNO/PlayerClass.java` | Sudeep Rawal | Made human player hand and typing reader |
| `src/UNO/BotClass.java` | Greek Yuan | Coded the simple, normal, and smart bots |
| `src/UNO/UnoGame.java` | Edward Wei | Made the game menu and start screen |
| `test/UNO/CardTest.java` | Sudeep Rawal | Added tests for cards and colors |
| `test/UNO/MoveTest.java` | Sudeep Rawal | Added tests for move actions |
| `test/UNO/LinkedChainReuseTest.java` | Greek Yuan | Tested the list tools and arrays |
| `test/UNO/GameClassTest.java` | Edward Wei | Added tests for game turns and setup |
| `test/UNO/SpecialCardsTest.java` | Edward Wei | Added tests for special cards |
| `test/UNO/PlayerClassTest.java` | Sudeep Rawal | Added tests for human actions |
| `test/UNO/BotClassTest.java` | Greek Yuan | Added tests for bot decisions |
| `test/UNO/UnoGameTest.java` | Edward Wei | Added tests for the game start |
| `test/UNO/GameFixture.java` | Greek Yuan | Built test helpers for game states |

## GenAI Use

GenAI assisted with code development and review, testing, architectural discussions, and debugging.
