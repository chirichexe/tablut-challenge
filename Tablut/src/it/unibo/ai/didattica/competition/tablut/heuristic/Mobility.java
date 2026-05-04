package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Mobility extends HeuristicTablut {

    /* Quante mosse può fare il re */
    @Override
    public float getValue(State state) {
        
        int[] kingPosition = state.getKingPosition();

        int mobility = getMobility(state, kingPosition[0], kingPosition[1]);
        return normalize(mobility);
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.MOBILITY_WEIGHT;
    }

    private int getMobility(State state, int row, int col) {
        
        int mobility = 0;
        mobility += countDirection(state, row, col, -1, 0); // up
        mobility += countDirection(state, row, col, 1, 0);  // down
        mobility += countDirection(state, row, col, 0, -1); // left
        mobility += countDirection(state, row, col, 0, 1);  // right
        return mobility;
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
        // Normalizza la mobilità del re in [-1.0, 1.0]
        // Più mosse disponibili = migliore per il Bianco
        return (mobility / 8.0f) - 1.0f; // Normalizza in [-1, 1]
    }
}