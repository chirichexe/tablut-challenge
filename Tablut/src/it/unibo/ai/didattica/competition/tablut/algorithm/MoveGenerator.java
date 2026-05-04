package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class MoveGenerator {
    private final Game game;
    
    // direzioni di movimento
    private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public MoveGenerator(Game game) {
        this.game = game;
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
                    addMovesForPawn(state, i, j, moves);
                }
            }
        }
    }

    /*
    * Restituisce tutte le mosse possibili per una pedina data la sua posizione
    */
    private void addMovesForPawn(State state, int row, int col, List<Action> moves) {
        int boardSize = state.getBoard().length;

        // esplora tutte le direzioni
        for (int[] dir : DIRECTIONS) {
            for (int dist = 1; dist < boardSize; dist++) {

                int nextRow = row + dir[0] * dist;
                int nextCol = col + dir[1] * dist;

                if (isOutOfBounds(nextRow, nextCol, boardSize)) break;

                try {
                    Action action = createAction(state, row, col, nextRow, nextCol);
                    
                    // verifica la mossa tramite le regole del gioco
                    game.checkMove(state.clone(), action);
                    
                    // se la mossa è valida, aggiungila alla lista
                    moves.add(action);

                } catch (Exception e) {
                    // Mossa non valida, interrompe l'esplorazione in questa direzione
                    break;
                }
            }
        }
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
}
