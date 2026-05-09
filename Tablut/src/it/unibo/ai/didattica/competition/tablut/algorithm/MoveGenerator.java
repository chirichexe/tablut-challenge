package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveGenerator {

    /*****************************************************************************/
    // configurazione
    /*****************************************************************************/

    private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    private static final Set<String> ASHTON_CITADELS = new HashSet<>(Arrays.asList(
            "a4", "a5", "a6", "b5",
            "d1", "e1", "f1", "e2",
            "i4", "i5", "i6", "h5",
            "d9", "e9", "f9", "e8"
    ));
    private static final int MAX_CITADEL_TO_CITADEL_DISTANCE = 5;

    private final Game game;
    private final boolean useAshtonCitadelRules;

    public MoveGenerator(Game game) {
        this.game = game;
        this.useAshtonCitadelRules = game instanceof GameAshtonTablut;
    }

    /**
     * Genera tutte le mosse possibili riempiendo la lista fornita come argomento
     */
    public void generateMoves(State state, List<Action> moves) {

        // pulisce la lista 
        moves.clear();

        // ottiene la griglia attuale e il turno
        State.Pawn[][] board = state.getBoard();
        State.Turn turn = state.getTurn();

        int boardSize = board.length;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {

                // se la casella contiene una pedina del giocatore di turno
                if (isOwnPawn(turn, board[i][j])) {

                    // genero le mosse per quella pedina
                    addMovesForPawn(state, board, i, j, moves);
                }
            }
        }
    }

    /*
    * Restituisce tutte le mosse possibili per una pedina data la sua posizione
    */
    private void addMovesForPawn(State state, State.Pawn[][] board, int row, int col, List<Action> moves) {
        int boardSize = board.length;
        String fromBox = state.getBox(row, col);
        boolean fromCitadel = useAshtonCitadelRules && ASHTON_CITADELS.contains(fromBox);

        // esplora tutte le direzioni
        for (int[] direction : DIRECTIONS) {
            for (int dist = 1; dist < boardSize; dist++) {

                int nextRow = row + direction[0] * dist;
                int nextCol = col + direction[1] * dist;

                if (isOutOfBounds(nextRow, nextCol, boardSize)) break;
                if (!isPathCellTraversable(state, board, nextRow, nextCol, fromCitadel)) break;
                if (!isDestinationLegal(state, board, row, col, nextRow, nextCol, fromCitadel)) break;

                moves.add(createActionUnchecked(state, row, col, nextRow, nextCol));
            }
        }
    }

    /*
    * Applica una mossa allo stato corrente
    */
    public State applyMove(State state, Action action) {
        try {
            // checkMove restituisce il nuovo stato risultante (con catture applicate)
            return game.checkMove(state.clone(), action);
        } catch (Exception e) {
            throw new RuntimeException("Mossa non valida generata: " + action, e);
        }
    }

    /*****************************************************************************/
    // funzioni di utilità
    /*****************************************************************************/

    /*
    * Controlla se la pedina appartiene al giocatore del turno corrente
    */
    public boolean isOwnPawn(State.Turn turn, State.Pawn pawn) {
        if (turn == State.Turn.WHITE) {
            return pawn == State.Pawn.WHITE || pawn == State.Pawn.KING;
        } else if (turn == State.Turn.BLACK) {
            return pawn == State.Pawn.BLACK;
        }
        return false;
    }

    private boolean isOutOfBounds(int row, int col, int size) {
        return row < 0 || row >= size || col < 0 || col >= size;
    }

    private Action createActionUnchecked(State state, int fromRow, int fromCol, int toRow, int toCol) {
        try {
            String from = state.getBox(fromRow, fromCol);
            String to = state.getBox(toRow, toCol);
            return new Action(from, to, state.getTurn());
        } catch (IOException e) {
            throw new IllegalStateException("Errore nella creazione dell'azione", e);
        }
    }

    private boolean isDestinationLegal(State state, State.Pawn[][] board, int fromRow, int fromCol, int toRow, int toCol, boolean fromCitadel) {

        // la casella di arrivo deve essere vuota
        if (board[toRow][toCol] != State.Pawn.EMPTY) {
            return false;
        }

        // non si può arrivare sul trono
        if (isThrone(toRow, toCol)) {
            return false;
        }

        if (!useAshtonCitadelRules) {
            return true;
        }

        // gestione citadels (regola Ashton)
        String toBox = state.getBox(toRow, toCol);
        boolean toCitadel = ASHTON_CITADELS.contains(toBox);
        if (!toCitadel) {
            return true;
        }

        // non si può entrare in citadel dall'esterno
        if (!fromCitadel) {
            return false;
        }

        // da citadel a citadel non oltre 5 caselle
        int distance = Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol);
        return distance <= MAX_CITADEL_TO_CITADEL_DISTANCE;
    }

    private boolean isPathCellTraversable(State state, State.Pawn[][] board, int row, int col, boolean fromCitadel) {

        // la prima casella non è mai la sorgente: ogni casella sul cammino deve essere libera
        if (board[row][col] != State.Pawn.EMPTY) {
            return false;
        }

        // non si può attraversare il trono
        if (isThrone(row, col)) {
            return false;
        }

        // in Ashton non si può attraversare una citadel partendo da fuori citadel
        if (useAshtonCitadelRules && !fromCitadel && ASHTON_CITADELS.contains(state.getBox(row, col))) {
            return false;
        }

        return true;
    }

    private boolean isThrone(int row, int col) {
        return row == 4 && col == 4;
    }
}
