package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public interface HeuristicTablut {

    /* Valuta un'euristica in base allo stato attuale */
    public float getValue(StateTablut state);
}