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
        int threatsAtDistance2 = getThreatsAtDistance2(state, kingPosition[0], kingPosition[1]);
        int specialCellThreats = getSpecialCellThreats(state, kingPosition[0], kingPosition[1]);
        
        float safety = freeAdjacentCells * HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT -
                       adjacentEnemyCells * HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT +
                       adjacentFriendlyCells * HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT -
                       threatsAtDistance2 * 0.5f - // Minacce a distanza 2 hanno peso ridotto
                       specialCellThreats * 1.5f;   // Caselle speciali (Trono, Citadels) aumentano il pericolo
        
        return normalize(safety);
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

    private int getThreatsAtDistance2(State state, int row, int col) {
        int threats = 0;
        int size = state.getBoard().length;

        // Controlla le celle a distanza 2 in linea retta (minacce di sandwich nel turno successivo)
        // Su
        if (row > 1 && state.getBoard()[row - 2][col] == State.Pawn.BLACK && isLineFree(state, row, col, row - 2, col)) {
            threats++;
        }
        // Giù
        if (row < size - 2 && state.getBoard()[row + 2][col] == State.Pawn.BLACK && isLineFree(state, row, col, row + 2, col)) {
            threats++;
        }
        // Sinistra
        if (col > 1 && state.getBoard()[row][col - 2] == State.Pawn.BLACK && isLineFree(state, row, col, row, col - 2)) {
            threats++;
        }
        // Destra
        if (col < size - 2 && state.getBoard()[row][col + 2] == State.Pawn.BLACK && isLineFree(state, row, col, row, col + 2)) {
            threats++;
        }

        return threats;
    }

    private boolean isLineFree(State state, int fromRow, int fromCol, int toRow, int toCol) {
        // Controlla se il percorso tra due celle è libero
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        return state.getBoard()[midRow][midCol] == State.Pawn.EMPTY;
    }

    private int getSpecialCellThreats(State state, int row, int col) {
        int threats = 0;

        // Controlla se il re è adiacente al Trono
        if (!isKingOnThrone(row, col)) {
            if (isAdjacentToThrone(row, col)) {
                threats += 2; // Trono è una minaccia significativa
            }
        }

        // Controlla se il re è adiacente a una Citadel
        threats += countAdjacentCitadels(state, row, col);

        return threats;
    }

    private boolean isKingOnThrone(int row, int col) {
        // Il trono è al centro (posizione [4, 4] in una board 9x9)
        return row == 4 && col == 4;
    }

    private boolean isAdjacentToThrone(int row, int col) {
        int throneRow = 4, throneCol = 4;
        return (Math.abs(row - throneRow) + Math.abs(col - throneCol)) == 1;
    }

    private boolean isCitadel(int row, int col) {
        // Citadels nel regolamento Ashton
        // Nord: a4, a5, a6, b5 (col=0-1, row=3-5)
        // Sud: d9, e9, f9, e8 (col=3-5, row=7-8)
        // Est: i4, i5, i6, h5 (col=7-8, row=3-5)
        // Ovest: d1, e1, f1, e2 (col=3-5, row=0-1)
        
        return (row == 0 && (col == 3 || col == 4 || col == 5)) ||  // d1, e1, f1
               (row == 1 && (col == 3 || col == 4 || col == 5)) ||  // d2, e2, f2 (se e2 è citadel)
               (row == 3 && (col == 0 || col == 1 || col == 8)) ||  // a4, b4, i4
               (row == 4 && (col == 0 || col == 1 || col == 8)) ||  // a5, b5, i5
               (row == 5 && (col == 0 || col == 1 || col == 8)) ||  // a6, b6, i6
               (row == 7 && (col == 3 || col == 4 || col == 5)) ||  // d8, e8, f8
               (row == 8 && (col == 3 || col == 4 || col == 5));    // d9, e9, f9
    }

    private int countAdjacentCitadels(State state, int row, int col) {
        int count = 0;
        int size = state.getBoard().length;

        // Controlla le celle adiacenti (su, giù, sinistra, destra)
        if (row > 0 && isCitadel(row - 1, col)) count++;
        if (row < size - 1 && isCitadel(row + 1, col)) count++;
        if (col > 0 && isCitadel(row, col - 1)) count++;
        if (col < size - 1 && isCitadel(row, col + 1)) count++;

        return count;
    }

    private float normalize(float  safety) {
        // Normalizza la sicurezza del re in un intervallo [-1.0, 1.0]
        // Scenario peggiore: re circondato da nemici + minacce a distanza 2 + citadels
        float minValue = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 0 - 
                        HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 4 - 
                        4 * 0.5f - // 4 minacce a distanza 2
                        4 * 1.5f;  // 4 citadels adiacenti
        
        // Scenario migliore: re completamente libero
        float maxValueFree = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 4 - 
                            HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 0 - 
                            0 * 0.5f - 
                            0 * 1.5f;
        
        // Scenario migliore alternativo: re circondato da amici
        float maxValueFriendly = HeuristicWeights.KING_SAFETY_FREE_CELLS_WEIGHT * 0 - 
                                HeuristicWeights.KING_SAFETY_ENEMY_CELLS_WEIGHT * 0 + 
                                HeuristicWeights.KING_SAFETY_FRIENDLY_CELLS_WEIGHT * 4 - 
                                0 * 0.5f - 
                                0 * 1.5f;
        
        float maxValue = Math.max(maxValueFree, maxValueFriendly);
        return ((2.0f * (safety - minValue)) / (maxValue - minValue)) - 1.0f; // Normalizza in [-1, 1]
    }
}