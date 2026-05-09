package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.List;

public class MinMaxTablut {

    /*****************************************************************************/
    // configurazione
    /*****************************************************************************/

    private static final int WIN_SCORE = 1_000_000;
    private static final int DRAW_SCORE = -100;
    private static final int CAPTURE_SCORE = 300;
    private static final int OWN_LOSS_SCORE = 150;
    private static final int KING_EDGE_PROGRESS_SCORE = 120;
    private static final int KING_OPEN_LINE_SCORE = 200;
    private static final int KING_MOVE_BONUS = 25;
    private static final int KING_PRESSURE_SCORE = 100;
    private static final int ADJACENT_TO_KING_BONUS = 80;
    private static final int ACTIVE_MOVE_SCORE = 5;
    private static final int MAX_ACTIVE_MOVE_LENGTH = 4;

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

    /*****************************************************************************/
    // ricerca
    /*****************************************************************************/

    /**
     * Cerca la mossa migliore in un dato stato
     */
    public Action search(State state, int timeoutSeconds) {

        // interrompe la ricerca se si avvicina al timeout
        this.startTime = System.currentTimeMillis();
        this.maxTime = Math.max(500L, (timeoutSeconds - 2) * 1000L); // margine di sicurezza
        Action bestActionSoFar = null;
        
        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {

            // ottiene la migliore mossa a questa profondità
            Action currentBest = getBestMove(state, currentDepth);
            if (currentBest != null) {
                bestActionSoFar = currentBest;
            }
            
            if (System.currentTimeMillis() - startTime > maxTime) {
                break;
            }
        }
        
        // restituisce la migliore mossa trovata finora
        return bestActionSoFar;
    }

    public Action getBestMove(State state, int depth) {
        List<Action> moves = new ArrayList<>();
        moveGenerator.generateMoves(state, moves);

        if (moves.isEmpty()) {
            return null;
        }
        
        orderMoves(moves, state);

        Action bestAction = null;
        boolean isMaxNode = isMaximizing(state.getTurn());

        if (isMaxNode) {
            float bestValue = Float.NEGATIVE_INFINITY;
            for (Action action : moves) {
                if (System.currentTimeMillis() - startTime > maxTime) return null;
                float value = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                if (System.currentTimeMillis() - startTime > maxTime) return null;
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            }
        } else {
            float bestValue = Float.POSITIVE_INFINITY;
            for (Action action : moves) {
                if (System.currentTimeMillis() - startTime > maxTime) return null;
                float value = alphaBeta(moveGenerator.applyMove(state, action), depth - 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
                if (System.currentTimeMillis() - startTime > maxTime) return null;
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

        List<Action> moves = new ArrayList<>();
        moveGenerator.generateMoves(state, moves);
        
        if (moves.isEmpty()) {
            return evaluator.evaluate(state);
        }

        orderMovesByKillerHeuristic(moves, depth);

        if (isMaximizing(state.getTurn())) {
            float maxEval = Float.NEGATIVE_INFINITY;
            for (Action action : moves) {
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
            for (Action action : moves) {
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

    /*****************************************************************************/
    // ordinamento mosse
    /*****************************************************************************/

    private void orderMoves(List<Action> moves, State state) {
        // Ordinamento statico iniziale per migliorare il cut-off ai primi livelli
        moves.sort((firstMove, secondMove) -> {
            int scoreFirst = calculateSimpleScore(firstMove, state);
            int scoreSecond = calculateSimpleScore(secondMove, state);
            return Integer.compare(scoreSecond, scoreFirst);
        });
    }

    private void orderMovesByKillerHeuristic(List<Action> moves, int depth) {
        // Ordinamento dinamico in profondità basato su killer move già viste
        moves.sort((firstMove, secondMove) -> {
            int firstScore = killerHeuristic.getKillerScore(firstMove, depth);
            int secondScore = killerHeuristic.getKillerScore(secondMove, depth);
            return Integer.compare(secondScore, firstScore);
        });
    }

    /**
     * Euristica per l'ordinamento iniziale
     */
    private int calculateSimpleScore(Action action, State state) {
        State.Turn currentTurn = state.getTurn();
        State stateAfterMove = moveGenerator.applyMove(state, action);

        if (stateAfterMove.getTurn() == State.Turn.WHITEWIN) {
            return currentTurn == State.Turn.WHITE ? WIN_SCORE : -WIN_SCORE;
        }
        if (stateAfterMove.getTurn() == State.Turn.BLACKWIN) {
            return currentTurn == State.Turn.BLACK ? WIN_SCORE : -WIN_SCORE;
        }
        if (stateAfterMove.getTurn() == State.Turn.DRAW) {
            return DRAW_SCORE;
        }

        int score = 0;

        int blackBeforeMove = state.getNumberOf(State.Pawn.BLACK);
        int blackAfterMove = stateAfterMove.getNumberOf(State.Pawn.BLACK);
        int whiteBeforeMove = state.getNumberOf(State.Pawn.WHITE);
        int whiteAfterMove = stateAfterMove.getNumberOf(State.Pawn.WHITE);

        int enemyCaptured;
        int ownCaptured;

        /* CATTURE */

        // calcolo i pezzi catturati da entrambi i lati
        // e i punti guadagnati o persi in base a chi è il giocatore corrente
        if (currentTurn == State.Turn.WHITE) {
            enemyCaptured = blackBeforeMove - blackAfterMove;
            ownCaptured = whiteBeforeMove - whiteAfterMove;
        } else {
            enemyCaptured = whiteBeforeMove - whiteAfterMove;
            ownCaptured = blackBeforeMove - blackAfterMove;
        }

        // assegno punteggio per le catture
        score += enemyCaptured * CAPTURE_SCORE;
        score -= ownCaptured * OWN_LOSS_SCORE;

        /* POSIZIONE DEL RE */
        int[] kingPositionBefore = state.getKingPosition();
        int[] kingPositionAfter = stateAfterMove.getKingPosition();
        int boardSize = state.getBoard().length;

        int kingDistanceBefore = distanceToClosestEdge(kingPositionBefore[0], kingPositionBefore[1], boardSize);
        int kingDistanceAfter = distanceToClosestEdge(kingPositionAfter[0], kingPositionAfter[1], boardSize);

        if (currentTurn == State.Turn.WHITE) {
            score += (kingDistanceBefore - kingDistanceAfter) * KING_EDGE_PROGRESS_SCORE;
            score += countOpenKingEscapeLines(stateAfterMove, kingPositionAfter[0], kingPositionAfter[1]) * KING_OPEN_LINE_SCORE;
            if (isKingMove(action, state)) {
                score += KING_MOVE_BONUS;
            }
        } else {
            score += (kingDistanceAfter - kingDistanceBefore) * KING_EDGE_PROGRESS_SCORE;
            int adjacentBlackBefore = countAdjacentBlackToKing(state, kingPositionBefore[0], kingPositionBefore[1]);
            int adjacentBlackAfter = countAdjacentBlackToKing(stateAfterMove, kingPositionAfter[0], kingPositionAfter[1]);
            score += (adjacentBlackAfter - adjacentBlackBefore) * KING_PRESSURE_SCORE;

            int distanceFromKingAfterMove = Math.abs(action.getRowTo() - kingPositionAfter[0])
                    + Math.abs(action.getColumnTo() - kingPositionAfter[1]);
            if (distanceFromKingAfterMove == 1) {
                score += ADJACENT_TO_KING_BONUS;
            }
        }

        int moveLength = Math.abs(action.getRowTo() - action.getRowFrom())
                + Math.abs(action.getColumnTo() - action.getColumnFrom());
        score += Math.min(moveLength, MAX_ACTIVE_MOVE_LENGTH) * ACTIVE_MOVE_SCORE;
        
        return score;
    }

    /*****************************************************************************/
    // funzioni di utilità
    /*****************************************************************************/

    private boolean isMaximizing(State.Turn turn) {
        return turn == State.Turn.WHITE;
    }

    private boolean isTerminal(State state) {
        State.Turn turn = state.getTurn();
        return turn == State.Turn.WHITEWIN || turn == State.Turn.BLACKWIN || turn == State.Turn.DRAW;
    }

    private boolean isKingMove(Action action, State state) {
        return state.getPawn(action.getRowFrom(), action.getColumnFrom()) == State.Pawn.KING;
    }

    private int distanceToClosestEdge(int row, int col, int boardSize) {
        return Math.min(Math.min(row, boardSize - 1 - row), Math.min(col, boardSize - 1 - col));
    }

    private int countAdjacentBlackToKing(State state, int kingRow, int kingCol) {
        int adjacentBlackCount = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int boardSize = state.getBoard().length;

        for (int[] direction : directions) {
            int nextRow = kingRow + direction[0];
            int nextCol = kingCol + direction[1];
            if (nextRow < 0 || nextRow >= boardSize || nextCol < 0 || nextCol >= boardSize) {
                continue;
            }
            if (state.getPawn(nextRow, nextCol) == State.Pawn.BLACK) {
                adjacentBlackCount++;
            }
        }
        return adjacentBlackCount;
    }

    private int countOpenKingEscapeLines(State state, int kingRow, int kingCol) {
        int openLines = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int boardSize = state.getBoard().length;

        for (int[] direction : directions) {
            int currentRow = kingRow + direction[0];
            int currentCol = kingCol + direction[1];
            boolean isBlocked = false;

            while (currentRow >= 0 && currentRow < boardSize && currentCol >= 0 && currentCol < boardSize) {
                if (state.getPawn(currentRow, currentCol) != State.Pawn.EMPTY) {
                    isBlocked = true;
                    break;
                }
                currentRow += direction[0];
                currentCol += direction[1];
            }

            if (!isBlocked) {
                openLines++;
            }
        }
        return openLines;
    }
}
