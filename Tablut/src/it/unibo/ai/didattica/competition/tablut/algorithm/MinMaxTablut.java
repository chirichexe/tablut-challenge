package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.List;

public class MinMaxTablut {
    private final int maxDepth;
    private final Evaluator evaluator;
    private final KillerHeuristic killerHeuristic;
    private final MoveGenerator moveGenerator;

    private long startTime;
    private long maxTime;

    public MinMaxTablut(Game game, int maxDepth) {
        this.maxDepth = maxDepth;
        this.evaluator = new Evaluator();
        this.killerHeuristic = new KillerHeuristic(maxDepth + 10); // Buffer per ID
        this.moveGenerator = new MoveGenerator(game);
    }

    public Action search(State state, int timeoutSeconds) {
        this.startTime = System.currentTimeMillis();
        this.maxTime = (timeoutSeconds - 2) * 1000L; // Margine di 2 secondi
        Action bestActionSoFar = null;

        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
            Action currentBest = getBestMove(state, currentDepth);
            if (currentBest != null) {
                bestActionSoFar = currentBest;
            }
            
            if (System.currentTimeMillis() - startTime > maxTime) {
                break;
            }
        }
        
        return bestActionSoFar;
    }

    public Action getBestMove(State state, int depth) {
        List<Action> possibleMoves = new ArrayList<>();
        moveGenerator.generateMoves(state, possibleMoves);

        if (possibleMoves.isEmpty()) {
            return null;
        }
        
        orderMoves(possibleMoves, state);

        Action bestAction = null;
        State.Turn turn = state.getTurn();
        boolean maximizing = isMaximizing(turn);

        if (maximizing) {
            float bestValue = Float.NEGATIVE_INFINITY;
            for (Action action : possibleMoves) {
                if (System.currentTimeMillis() - startTime > maxTime) break;
                float value = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            }
        } else {
            float bestValue = Float.POSITIVE_INFINITY;
            for (Action action : possibleMoves) {
                if (System.currentTimeMillis() - startTime > maxTime) break;
                float value = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                if (value < bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            }
        }
        
        return bestAction;
    }

    private float alphaBeta(State state, int depth, float alpha, float beta) {
        if (depth <= 0 || isTerminal(state)) {
            return evaluator.evaluate(state);
        }

        if (System.currentTimeMillis() - startTime > maxTime) {
            return evaluator.evaluate(state);
        }

        List<Action> possibleMoves = new ArrayList<>();
        moveGenerator.generateMoves(state, possibleMoves);
        
        if (possibleMoves.isEmpty()) {
            return evaluator.evaluate(state);
        }

        orderMovesByKillerHeuristic(possibleMoves, depth);

        if (isMaximizing(state.getTurn())) {
            float maxEval = Float.NEGATIVE_INFINITY;
            for (Action action : possibleMoves) {
                float eval = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    killerHeuristic.addKillerMove(action, depth);
                    break;
                }
            }
            return maxEval;
        } else {
            float minEval = Float.POSITIVE_INFINITY;
            for (Action action : possibleMoves) {
                float eval = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    killerHeuristic.addKillerMove(action, depth);
                    break;
                }
            }
            return minEval;
        }
    }

    private void orderMoves(List<Action> moves, State state) {
        // Ordinamento preliminare: potremmo mettere prima le mosse che portano a catture
        // Per semplicità usiamo il killer heuristic già integrato o un ordinamento statico
        moves.sort((a, b) -> {
            int scoreA = calculateSimpleScore(a, state);
            int scoreB = calculateSimpleScore(b, state);
            return Integer.compare(scoreB, scoreA);
        });
    }

    private int calculateSimpleScore(Action a, State state) {
        // Score base: 10 per catture, 20 per re che si muove verso il bordo
        int score = 0;
        // Mockup di logica di cattura o importanza mossa
        return score;
    }

    private void orderMovesByKillerHeuristic(List<Action> moves, int depth) {
        moves.sort((a, b) -> Integer.compare(
            killerHeuristic.getKillerScore(b, depth), 
            killerHeuristic.getKillerScore(a, depth)
        ));
    }

    private boolean isMaximizing(State.Turn turn) {
        return turn == State.Turn.WHITE;
    }

    private boolean isTerminal(State state) {
        State.Turn turn = state.getTurn();
        return turn == State.Turn.WHITEWIN || turn == State.Turn.BLACKWIN || turn == State.Turn.DRAW;
    }
}
