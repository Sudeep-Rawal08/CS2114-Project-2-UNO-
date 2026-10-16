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
- **Unknowns:** Checked the provided chain API and implemented bot selection and scripted input testing. The supplied spec does not include the original scope's full unknowns list; the team still needs to compare it.
- **Remaining limitations:** Manual gameplay review is pending. The prototype uses local bots; an external model API is not implemented. A depleted deck ends the round instead of recycling discards.

For complexity, let h be hand size and d be deck size. Card/Move accessors and turn updates are O(1); drawing and fixed draw penalties are O(d); `isValid` is O(h) in the +4 case; applying a move is O(h + d). Legal-move generation and bot selection are O(h²), including the chain's linear `addToEnd` cost. Human input is O(h + d + L) per attempt, where L is input length. The round's cost is the sum of its turns and input attempts. These retain the spec's linear draw and quadratic bot bounds.

## Next Steps

1. Manually play all three levels and check special effects and input recovery. Owner: ______.
2. For defects found during play, add regression cases to `PlayerClassTest`, `BotClassTest`, or `GameClassTest` before fixing them. Owner: ______.
3. Confirm scope coverage, contributions, and submission details. Owner: ______.

The smarter-bot stretch goal is committed and implemented as Normal/Master. API integration is dropped from this prototype. The tests include empty collections, removal of a missing element, rejected-state preservation, and complete human/bot turns.

## Attribution

Fill in each team member's actual contribution before submission.

| Class or test file | Contributor | Work performed |
|---|---|---|
| `src/UNO/LinkedChain.java` | CS 2114 Staff | Provided; unchanged |
| `src/UNO/Card.java` | | |
| `src/UNO/NumberedCards.java` | | |
| `src/UNO/SpecialCards.java` | | |
| `src/UNO/Move.java` | | |
| `src/UNO/BotDifficulty.java` | | |
| `src/UNO/GameClass.java` | | |
| `src/UNO/PlayerClass.java` | | |
| `src/UNO/BotClass.java` | | |
| `src/UNO/UnoGame.java` | | |
| `test/UNO/CardTest.java` | | |
| `test/UNO/MoveTest.java` | | |
| `test/UNO/LinkedChainReuseTest.java` | | |
| `test/UNO/GameClassTest.java` | | |
| `test/UNO/SpecialCardsTest.java` | | |
| `test/UNO/PlayerClassTest.java` | | |
| `test/UNO/BotClassTest.java` | | |
| `test/UNO/UnoGameTest.java` | | |
| `test/UNO/GameFixture.java` | | |

## GenAI Use
