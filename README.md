# UNO Prototype

A console UNO game for one human against a Simple, Normal, or Master bot. Hands, the draw deck, and legal moves use the supplied generic LinkedChain.

## Run and Test
Download the files in Eclipse and run 'UNO' package or 'UnoGame.java'. Tests are also included.

## Controls

Choose `1` Simple, `2` Normal, or `3` Master for bot difficulty. Simple plays the first card it can find, while Master thinks ahead and tries to find the best move. 

To play a card, type the number next to it. For example, if your hand is:
1. Green 5
2. Yellow 4
3. +4

You would type `3` in order to play the `+4`. Playing a Wild card or +4 card allows you to choose the color. Input is case-insensitive.

A legal drawn card gives the user the choice to either play it or not; an illegal drawn card stays in the hand and ends the turn. Skip and draw penalties skip the opponent. Reverse changes direction; in this two-player prototype it skips the other player. An empty draw deck stops the round, as specified.

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

GenAI assisted with code development and review, testing, architectural discussions, and debugging.
