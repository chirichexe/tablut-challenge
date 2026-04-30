package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class Mobility implements HeuristicTablut {

    /* Quante mosse può fare il re
    */
    @Override
    public float getValue(StateTablut state) {
        // Implementazione dell'euristica per l'escape del re
        return 0.0f;
    }
}