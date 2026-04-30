package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface HeuristicTablut {

    /* Valuta un'euristica in base allo stato attuale */
    public float getValue(State state, Game game);
}