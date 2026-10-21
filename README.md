# UNO Prototype

A console UNO game for one human against a Simple, Normal, or Master bot. Hands, the draw deck, and legal moves use the supplied generic LinkedChain.

## Run and Test
Import this project and the course CS2-Support project into the same Eclipse workspace. Run UnoGame.java as a Java Application and the test folder as JUnit Tests; CS2-Support provides student.TestCase and its test dependencies.

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

For complexity, n is the collection size; when multiple collections are involved, use the largest size. For console input, also include input-line length. Card/Move accessors, card validation, and turn updates are O(1). Setup and shuffling, hand display, legal-move generation, and Simple/Normal bot selection are O(n²). Drawing and fixed draw penalties are O(n), since adding to the end of a hand traverses it. Applying an ordinary play is O(n); applying a draw is O(n²) in the bot-wild case because chooseColor() uses indexed hand traversal. Master selection is O(n³): each of O(n) candidates scores the hand using repeated linear get(i) calls. Bot turns have the same selection bound; human turns are O(n²) per input attempt including hand display, excluding waiting for input. A full round sums all turns and attempts. Compared with the spec, drawing remains linear, but indexed linked-chain traversal raises setup/display from linear to quadratic and Master selection from quadratic to cubic; the +4 validity check is now constant because this prototype permits +4 regardless of matching-color cards.

## Next Steps

1. Manually play all three levels and check special effects and input recovery. Owner: ___Greek Yuan___.
2. For defects found during play, add regression cases to `PlayerClassTest`, `BotClassTest`, or `GameClassTest` before fixing them. Owner: ___Sudeep Rawal___.
3. Confirm scope coverage, contributions, and submission details. Owner: ___Edward Wei___.

We refined the smarter-bot stretch goal into three local difficulty levels. Master uses heuristics for immediate wins, color selection, and opponent pressure; it does not guarantee a globally optimal move. API integration is dropped from this prototype. The tests include empty collections, removal of a missing element, rejected-state preservation, and complete human/bot turns.

## Attribution

Fill in each team member's actual contribution before submission.

| Class or test file | Contributor |
|---|---|
| `src/UNO/LinkedChain.java` | CS 2114 Staff (provided) |
| `src/UNO/Card.java` | Edward Wei, Greek Yuan |
| `src/UNO/NumberedCards.java` | Sudeep Rawal, Greek Yuan |
| `src/UNO/SpecialCards.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `src/UNO/Move.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `src/UNO/BotDifficulty.java` | Edward Wei, Greek Yuan |
| `src/UNO/GameClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `src/UNO/PlayerClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `src/UNO/BotClass.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `src/UNO/UnoGame.java` | Sudeep Rawal, Edward Wei |
| `test/UNO/CardTest.java` | Sudeep Rawal, Greek Yuan |
| `test/UNO/MoveTest.java` | Sudeep Rawal, Edward Wei, Greek Yuan |
| `test/UNO/LinkedChainReuseTest.java` | Greek Yuan, Edward Wei |
| `test/UNO/GameClassTest.java` | Sudeep Rawal |
| `test/UNO/SpecialCardsTest.java` | Sudeep Rawal, Edward Wei |
| `test/UNO/PlayerClassTest.java` | Edward Wei, Greek Yuan |
| `test/UNO/BotClassTest.java` | Sudeep Rawal, Greek Yuan |
| `test/UNO/UnoGameTest.java` | Sudeep Rawal, Edward Wei |

## GenAI Use

GenAI assisted with code development and review, testing, architectural discussions, and debugging.
