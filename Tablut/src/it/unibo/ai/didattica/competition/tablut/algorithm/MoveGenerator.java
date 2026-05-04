package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    private final Game game;

    public MoveGenerator(Game game) {
        this.game = game;
    }

    /**
     * Genera tutte le mosse possibili per il giocatore di turno.
     */
    public List<Action> getPossibleMoves(State state) {
        List<Action> moves = new ArrayList<>();
        State.Pawn[][] board = state.getBoard();
        State.Turn turn = state.getTurn();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (isOwnPawn(turn, board[i][j])) {
                    moves.addAll(getMovesForPawn(state, i, j));
                }
            }
        }
        return moves;
    }

    /*
    * Restituisce tutte le mosse possibili per una pedina data la sua posizione
    */
    private List<Action> getMovesForPawn(State state, int row, int col) {
        List<Action> moves = new ArrayList<>();
        int boardSize = state.getBoard().length;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            for (int dist = 1; dist < boardSize; dist++) {
                int nextRow = row + dir[0] * dist;
                int nextCol = col + dir[1] * dist;

                if (isOutOfBounds(nextRow, nextCol, boardSize)) break;

                try {
                    Action action = createAction(state, row, col, nextRow, nextCol);
                    
                    // Verifica la mossa tramite le regole del gioco
                    game.checkMove(state.clone(), action);

                    moves.add(action);
                } catch (Exception e) {
                    // Mossa non valida (es. ostacolo), interrompi la direzione
                    break;
                }
            }
        }
        return moves;
    }

    /*
    * Crea una nuova azione per una mossa
    */
    private Action createAction(State state, int fromRow, int fromCol, int toRow, int toCol) throws Exception {
        String from = state.getBox(fromRow, fromCol);
        String to = state.getBox(toRow, toCol);
        return new Action(from, to, state.getTurn());
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
}
