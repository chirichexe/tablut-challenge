package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class KingSafety implements HeuristicTablut {

    /* Definisce le vie di fuga per il re, quindi le celle libere tra il re e 
    * i bordi della scacchiera
    */
    @Override
    public float getValue(StateTablut state) {
        // Implementazione dell'euristica per l'escape del re
        return 0.0f;
    }
}