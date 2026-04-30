package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class OpenPaths implements HeuristicTablut {

    /* Definisce il numero di vie possibili fino al bordo */
    @Override
    public float getValue(State state, Game game) {
        
        int[] kingPosition = state.getKingPosition();
        int openPaths = getOpenPaths(state, kingPosition[0], kingPosition[1]);
        return normalize(openPaths);
    }

    private int getOpenPaths(State state, int row, int col) {
        
        int openPaths = 0;

        openPaths += countDirection(state, row, col, -1, 0); // up
        openPaths += countDirection(state, row, col, 1, 0);  // down
        openPaths += countDirection(state, row, col, 0, -1); // left
        openPaths += countDirection(state, row, col, 0, 1);  // right
        return openPaths;
    }

    private int countDirection(State state, int row, int col, int dRow, int dCol) {

        int size = state.getBoard().length;

        int r = row + dRow;
        int c = col + dCol;

        while (r >= 0 && r < size && c >= 0 && c < size) {

            if (state.getPawn(r, c) != State.Pawn.EMPTY) {
                return 0;
            }

            r += dRow;
            c += dCol;
        }

        return 1;
    }

    private float normalize(float paths) {
        return paths;
    }
}