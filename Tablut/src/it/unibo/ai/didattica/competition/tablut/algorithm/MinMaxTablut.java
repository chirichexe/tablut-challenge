package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.List;



public class MinMaxTablut {
    private final Game game;
    private final int maxDepth;
    private final Evaluator evaluator;

    public MinMaxTablut(Game game, int maxDepth) {
        this.game = game;
        this.maxDepth = maxDepth;
        this.evaluator = new Evaluator();
    }

    /*
    * Controlla se la pedina appartiene al giocatore del turno corrente
    */
    private boolean isOwnPawn(State.Turn turn, State.Pawn pawn) {
        if (turn == State.Turn.WHITE) {
            return pawn == State.Pawn.WHITE || pawn == State.Pawn.KING;
        } else if (turn == State.Turn.BLACK) {
            return pawn == State.Pawn.BLACK;
        }
        return false;
    }

    /**
     * Genera tutte le mosse possibili per il giocatore di turno.
     */
    private List<Action> getPossibleMoves(State state) {
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

    /**
     * se è terminale
     */
    private boolean isTerminal(State state) {
        State.Turn turn = state.getTurn();
        return turn == State.Turn.WHITEWIN || turn == State.Turn.BLACKWIN || turn == State.Turn.DRAW;
    }

    public Action getBestMove(State state) {
        List<Action> possibleMoves = getPossibleMoves(state);

        if (possibleMoves.isEmpty()) {
            return null;
        }
        
        Action bestAction = null;
        State.Turn turn = state.getTurn();
        
        if (turn == State.Turn.WHITE) { /* siamo bianchi */
            // il bianco cerca di massimizzare il punteggio, partendo da un valore -inf
            float bestValue = Float.NEGATIVE_INFINITY;
            
            for (Action actionToEvaluate : possibleMoves) {
                // espandiamo l'albero fino a profondità 
                // oppure fino ad uno stato terminale
                // calcola minmax per la mossa
                // Dopo il bianco (max), tocca al nero (min)
                float value = minmax(applyMove(state, actionToEvaluate), maxDepth - 1);
                
                // se il valore è migliore del miglior valore trovato finora, 
                // aggiorna il miglior valore e la miglior mossa
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = actionToEvaluate;
                }
            }

        } else if (turn == State.Turn.BLACK) { /* siamo neri */
            // il nero cerca di minimizzare il punteggio, partendo da un valore +inf
            float bestValue = Float.POSITIVE_INFINITY;
            
            for (Action actionToEvaluate : possibleMoves) {
                // espandiamo l'albero fino a profondità 
                // oppure fino ad uno stato terminale
                // calcola minmax per la mossa
                // Dopo il nero (min), tocca al bianco (max)
                float value = minmax(applyMove(state, actionToEvaluate), maxDepth - 1);
                
                // se il valore è migliore (minore) del miglior valore trovato finora, 
                // aggiorna il miglior valore e la miglior mossa
                if (value < bestValue) {
                    bestValue = value;
                    bestAction = actionToEvaluate;
                }
            }
        }
        
        return bestAction;
    }

    /**
     * Algoritmo minmax
     */
    private float minmax(State state, int depth) {

        // STATO TERMINALE (oppure ho terminato la profondità minima da raggiungere): non faccio niente, uso il valore restituito
        if (depth == 0 || isTerminal(state)) {
            return evaluator.evaluate(state);
        }

        List<Action> possibleMoves = getPossibleMoves(state);
        // PROFONDITÀ MASSIMA RAGGIUNTA: uso l'euristica per capire la "bontà " della soluzione
        if (possibleMoves.isEmpty()) {
            return evaluator.evaluate(state);
        }

        State.Turn turn = state.getTurn();

        if (turn == State.Turn.WHITE) { /* se è MAX: sceglie il valore massimo tra i figli */
            
            float maxEval = Float.NEGATIVE_INFINITY;
            
            for (Action action : possibleMoves) {
                float eval = minmax(applyMove(state, action), depth - 1);
                maxEval = Math.max(maxEval, eval);
            }
            
            return maxEval;

        } else if (turn == State.Turn.BLACK) { /* se è MIN: sceglie il valore minimo tra i figli */
            
            float minEval = Float.POSITIVE_INFINITY;
            
            for (Action action : possibleMoves) {
                float eval = minmax(applyMove(state, action), depth - 1);
                minEval = Math.min(minEval, eval);
            }
            
            return minEval;
        }

        return evaluator.evaluate(state);
    }

    
    private List<Action> getMovesForPawn(State state, int row, int col) {
        List<Action> moves = new ArrayList<>();
        int boardSize = 9; 
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            for (int dist = 1; dist < boardSize; dist++) {
                int nextRow = row + dir[0] * dist;
                int nextCol = col + dir[1] * dist;

                if (nextRow < 0 || nextRow >= boardSize || nextCol < 0 || nextCol >= boardSize) break;

                try {
                    String from = state.getBox(row, col);
                    String to = state.getBox(nextRow, nextCol);
                    Action action = new Action(from, to, state.getTurn());
                    
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

    private State applyMove(State state, Action action) {
        try {
            // checkMove restituisce il nuovo stato risultante (con catture applicate)
            return game.checkMove(state.clone(), action);
        } catch (Exception e) {
            throw new RuntimeException("Mossa non valida generata: " + action, e);
        }
    }
}