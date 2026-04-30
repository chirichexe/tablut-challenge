package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class OpenPaths implements HeuristicTablut {

    /* Definisce le vie di fuga per il re, quindi le celle libere tra il re e 
    * i bordi della scacchiera
    */
    @Override
    public float getValue(State state, Game game) {
        
        int[] kingPosition = state.getKingPosition();
        int openPaths = getOpenPaths(state, kingPosition[0], kingPosition[1]);
        return normalize(openPaths);
    }

    private int getOpenPaths(State state, int row, int col) {
        
        int openPaths = 0;
        int size = state.getBoard().length;

        // Controlla le direzioni di fuga (su, giù, sinistra, destra)
        while (row > 0 && state.getBoard()[row - 1][col] == State.Pawn.EMPTY) row--; // up
        while (row < size - 1 && state.getBoard()[row + 1][col] == State.Pawn.EMPTY) row++; // down
        while (col > 0 && state.getBoard()[row][col - 1] == State.Pawn.EMPTY) col--; // left
        while (col < size - 1 && state.getBoard()[row][col + 1] == State.Pawn.EMPTY) col++; // right

        return openPaths;
    }

    private float normalize(float paths) {
        return paths;
    }
}