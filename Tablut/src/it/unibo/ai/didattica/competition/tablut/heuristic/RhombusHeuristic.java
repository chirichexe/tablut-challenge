package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class RhombusHeuristic extends HeuristicTablut {
    
    private final int[][] rhombus = {
        {1,2}, {1,6},
        {2,1}, {2,7},
        {6,1}, {6,7},
        {7,2}, {7,6}
    };

    @Override
    public float getValue(State state) {
        if (state.getTurn() != State.Turn.BLACK && state.getTurn() != State.Turn.BLACKWIN) {
            // Se non è il turno dei neri, valutiamo lo stato precedente
            // In TablutChallenge l'evaluator viene chiamato dopo l'applicazione della mossa
        }

        int count = 0;
        for (int[] pos : rhombus) {
            if (state.getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return (float) count / rhombus.length;
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.RHOMBUS_WEIGHT;
    }
}
