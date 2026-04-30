package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Mobility implements HeuristicTablut {

    /* Quante mosse può fare il re
    */
    @Override
    public float getValue(State state, Game game) {
        
        int[] kingPosition = state.getKingPosition();

        int mobility = 0;
        mobility += countDirection(state, kingPosition[0], kingPosition[1], -1, 0); // up
        mobility += countDirection(state, kingPosition[0], kingPosition[1], 1, 0);  // down
        mobility += countDirection(state, kingPosition[0], kingPosition[1], 0, -1); // left
        mobility += countDirection(state, kingPosition[0], kingPosition[1], 0, 1);  // right

        return normalize(mobility);
    }

    private int countDirection(State state, int row, int col, int dRow, int dCol) {

        int size = state.getBoard().length;
        int count = 0;

        int r = row + dRow;
        int c = col + dCol;

        while (r >= 0 && r < size && c >= 0 && c < size) {

            if (state.getPawn(r, c) != State.Pawn.EMPTY) {
                break;
            }

            count++;

            r += dRow;
            c += dCol;
        }

        return count;
    }

    private float normalize(float mobility) {
        // TODO: Implementare la normalizzazione della mobilità in un intervallo specifico, ad esempio [0, 1]
        return mobility;
    }
}