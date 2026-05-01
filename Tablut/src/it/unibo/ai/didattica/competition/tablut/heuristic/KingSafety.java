package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class KingSafety extends HeuristicTablut {

    /* Definisce quanto il re è protetto o in pericolo immediato */
    @Override
    public float getValue(State state) {

        int[] kingPosition = state.getKingPosition();
        int freeAdjacentCells = getFreeAdjacentCells(state, kingPosition[0], kingPosition[1]);
        int adjacentEnemyCells = getAdjacentEnemyCells(state, kingPosition[0], kingPosition[1]);
        int adjacentFriendlyCells = getAdjacentFriendlyCells(state, kingPosition[0], kingPosition[1]);
        return normalize(freeAdjacentCells * HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT -
                         adjacentEnemyCells * HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT +
                         adjacentFriendlyCells * HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT);
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.KING_SAFETY_WEIGHT;
    }

    private int getFreeAdjacentCells(State state, int row, int col) {
        int freeCells = 0;
        int size = state.getBoard().length;

        // Controlla le celle adiacenti (su, giù, sinistra, destra)
        if (row > 0 && state.getBoard()[row - 1][col] == State.Pawn.EMPTY) freeCells++;
        if (row < size - 1 && state.getBoard()[row + 1][col] == State.Pawn.EMPTY) freeCells++;
        if (col > 0 && state.getBoard()[row][col - 1] == State.Pawn.EMPTY) freeCells++;
        if (col < size - 1 && state.getBoard()[row][col + 1] == State.Pawn.EMPTY) freeCells++;

        return freeCells;
    }

    private int getAdjacentFriendlyCells(State state, int row, int col) {
        int friendlyCells = 0;
        int size = state.getBoard().length;

        // Controlla le celle adiacenti (su, giù, sinistra, destra)
        if (row > 0 && state.getBoard()[row - 1][col] == State.Pawn.WHITE) friendlyCells++;
        if (row < size - 1 && state.getBoard()[row + 1][col] == State.Pawn.WHITE) friendlyCells++;
        if (col > 0 && state.getBoard()[row][col - 1] == State.Pawn.WHITE) friendlyCells++;
        if (col < size - 1 && state.getBoard()[row][col + 1] == State.Pawn.WHITE) friendlyCells++;

        return friendlyCells;
    }

    private int getAdjacentEnemyCells(State state, int row, int col) {
        int enemyCells = 0;
        int size = state.getBoard().length;

        // Controlla le celle adiacenti (su, giù, sinistra, destra)
        if (row > 0 && state.getBoard()[row - 1][col] == State.Pawn.BLACK) enemyCells++;
        if (row < size - 1 && state.getBoard()[row + 1][col] == State.Pawn.BLACK) enemyCells++;
        if (col > 0 && state.getBoard()[row][col - 1] == State.Pawn.BLACK) enemyCells++;
        if (col < size - 1 && state.getBoard()[row][col + 1] == State.Pawn.BLACK) enemyCells++;
        // if (col == 0 || col == size - 1 || row == 0 || row == size - 1) enemyCells++; // Considera i bordi come nemici  

        return enemyCells;
    }

    private float normalize(float  safety) {
        // TODO: Implementare la normalizzazione della sicurezza del re in un intervallo specifico, ad esempio [0, 1]
        
        float minValue = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 0 - HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 4 + HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT * 0; // Il re è completamente circondato da nemici
        float maxValueFree = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 4 - HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 0 + HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT * 0; // Il re è completamente libero
        float maxValueFriendly = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 0 - HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 0 + HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT * 4; // Il re è completamente circondato da amici
        float maxValue = Math.max(maxValueFree, maxValueFriendly);
        return ((2.0f * (safety - minValue)) / (maxValue - minValue)) - 1.0f; // Normalizza in [-1, 1]
        
        //return safety;
    }
}