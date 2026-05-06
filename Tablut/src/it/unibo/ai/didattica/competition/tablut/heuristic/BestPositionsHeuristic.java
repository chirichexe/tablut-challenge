package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BestPositionsHeuristic extends HeuristicTablut {
    
    private final int[][] bestPositions = {
        {2,3}, {3,5},
        {5,3}, {6,5}
    };

    @Override
    public float getValue(State state) {
        int count = 0;
        for (int[] pos : bestPositions) {
            if (state.getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.WHITE.toString())) {
                count++;
            }
        }
        return (float) count / bestPositions.length;
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.BEST_POSITIONS_WEIGHT;
    }
}
