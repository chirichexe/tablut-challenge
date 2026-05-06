package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.heuristic.*;
import java.util.ArrayList;

public class Evaluator {

    private final ArrayList<HeuristicTablut> heuristics;

    public Evaluator() {

        this.heuristics = new ArrayList<>();
        this.heuristics.add(new KingEscape());
        this.heuristics.add(new KingSafety());  
        this.heuristics.add(new Material());
        this.heuristics.add(new Mobility());
        this.heuristics.add(new OpenPaths());
        this.heuristics.add(new RhombusHeuristic());
        this.heuristics.add(new BestPositionsHeuristic());
    }

    public float evaluate(State state){

        State.Turn turn = state.getTurn();
        if (turn == State.Turn.WHITEWIN) return 10000.0f;
        if (turn == State.Turn.BLACKWIN) return -10000.0f;
        if (turn == State.Turn.DRAW) return 0.0f;

        float score = 0;
        for (HeuristicTablut heuristic : heuristics) {
            score += heuristic.getValue(state) * heuristic.getWeight();
        }
        return score;
    }

}
