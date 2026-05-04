package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.List;

public class MinMaxTablut {
    private final int maxDepth;
    private final Evaluator evaluator;
    private final KillerHeuristic killerHeuristic;
    private final MoveGenerator moveGenerator;

    public MinMaxTablut(Game game, int maxDepth) {
        this.maxDepth = maxDepth;
        this.evaluator = new Evaluator();
        this.killerHeuristic = new KillerHeuristic(maxDepth);
        this.moveGenerator = new MoveGenerator(game);
    }

    /**
     * Metodo principale per l'Iterative Deepening.
     * Sostituisce la chiamata diretta a getBestMove nel Client.
     */
    public Action search(State state, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long maxTime = (timeoutSeconds - 2) * 1000L; // Margine di sicurezza di 2 secondi
        Action bestActionSoFar = null;

        // Partiamo da profondità 1 e aumentiamo progressivamente
        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed > maxTime) break;

            System.out.println("Searching at depth: " + currentDepth + " (Elapsed: " + elapsed + "ms)");
            
            Action currentBest = getBestMove(state, currentDepth);
            
            if (currentBest != null) {
                bestActionSoFar = currentBest;
            }
        }
        
        return bestActionSoFar;
    }

    public Action getBestMove(State state, int depth) {
        List<Action> possibleMoves = moveGenerator.getPossibleMoves(state);

        if (possibleMoves.isEmpty()) {
            return null;
        }
        
        Action bestAction = null;
        State.Turn turn = state.getTurn();
        boolean maximizing = isMaximizing(turn);

        if (maximizing) { /* siamo bianchi */
            // il bianco cerca di massimizzare il punteggio, partendo da un valore -inf
            float bestValue = Float.NEGATIVE_INFINITY;
            
            for (Action actionToEvaluate : possibleMoves) {
                // espandiamo l'albero fino a profondità maxDepth
                // oppure fino ad uno stato terminale
                // calcola minmax per la mossa
                // Dopo il bianco (max), tocca al nero (min)
                
                /* AlfaBeta */
                float value = alphaBeta(
                    moveGenerator.applyMove(state, actionToEvaluate),
                    depth - 1,
                    Float.NEGATIVE_INFINITY,  // alpha iniziale
                    Float.POSITIVE_INFINITY   // beta iniziale
                );      

                // se il valore è migliore del miglior valore trovato finora, 
                // aggiorna il miglior valore e la miglior mossa
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = actionToEvaluate;
                }
            }

        } else { /* siamo neri */
            // il nero cerca di minimizzare il punteggio, partendo da un valore +inf
            float bestValue = Float.POSITIVE_INFINITY;
            
            for (Action actionToEvaluate : possibleMoves) {
                // espandiamo l'albero fino a profondità 
                // oppure fino ad uno stato terminale
                // calcola minmax per la mossa
                // Dopo il nero (min), tocca al bianco (max)
                
                /* AlfaBeta */
                float value = alphaBeta(
                    moveGenerator.applyMove(state, actionToEvaluate),
                    depth - 1,
                    Float.NEGATIVE_INFINITY,  // alpha iniziale
                    Float.POSITIVE_INFINITY   // beta iniziale
                );
                
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

    /*
    Algoritmo AlfaBeta
    */
    private float alphaBeta(State state, int depth, float alpha, float beta) {
        
        // STATO TERMINALE (oppure ho terminato la profondità minima da raggiungere): non faccio niente, uso il valore restituito
        if (depth == 0 || isTerminal(state)) {
            return evaluator.evaluate(state);
        }

        // ottengo tutte le mosse possibili 
        List<Action> possibleMoves = moveGenerator.getPossibleMoves(state);
        
        // ordino le mosse in modo da valutare prima quelle ricordate come killer moves 
        // per questo livello di profondità
        orderMovesByKillerHeuristic(possibleMoves, depth);

        // STATO TERMINALE restituisco il valore di valutazione
        if (possibleMoves.isEmpty()) {
            return evaluator.evaluate(state);
        }

        // NON TERMINALE, continuo a espandere l'albero
        State.Turn turn = state.getTurn();

        if (isMaximizing(turn)) { /* se è MAX: sceglie il valore massimo tra i figli */
            return maxValue(state, possibleMoves, depth, alpha, beta);
        } else { /* se è MIN: sceglie il valore minimo tra i figli */
            return minValue(state, possibleMoves, depth, alpha, beta);
        }
    }

    private float maxValue(State state, List<Action> moves, int depth, float alpha, float beta) {
        float maxEval = Float.NEGATIVE_INFINITY;
        for (Action action : moves) {
            float childEval = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, alpha, beta);
            maxEval = Math.max(maxEval, childEval);
            alpha = Math.max(alpha, childEval);
            
            if (beta <= alpha) { // beta cutoff (potatura)
                
                // questa mossa è ottima per il bianco, ma pessima per il nero, 
                // non serve più esplorare le altre mosse a questo livello
                // la memorizzo come killer move per questo livello di profondità
                killerHeuristic.addKillerMove(action, depth);

                break;
            }      
        }
        return maxEval;
    }

    private float minValue(State state, List<Action> moves, int depth, float alpha, float beta) {
        float minEval = Float.POSITIVE_INFINITY;
        for (Action action : moves) {
            float childEval = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, alpha, beta);
            minEval = Math.min(minEval, childEval);
            beta = Math.min(beta, childEval);

            if (beta <= alpha) { // alpha cutoff (potatura) 
                
                // questa mossa è ottima per il nero, ma pessima per il bianco, 
                // non serve più esplorare le altre mosse a questo livello
                // la memorizzo come killer move per questo livello di profondità
                killerHeuristic.addKillerMove(action, depth);

                break; 
            }      
        }
        return minEval;
    }

    private void orderMovesByKillerHeuristic(List<Action> moves, int depth) {
        moves.sort((a, b) -> Integer.compare(
            killerHeuristic.getKillerScore(b, depth), 
            killerHeuristic.getKillerScore(a, depth)
        ));
    }

    /************************************************************************************ */
    // Funzioni di utilità 
    /************************************************************************************ */
    
    private boolean isMaximizing(State.Turn turn) {
        return turn == State.Turn.WHITE;
    }

    /**
     * se è terminale
     */
    private boolean isTerminal(State state) {
        State.Turn turn = state.getTurn();
        return turn == State.Turn.WHITEWIN || turn == State.Turn.BLACKWIN || turn == State.Turn.DRAW;
    }
}
