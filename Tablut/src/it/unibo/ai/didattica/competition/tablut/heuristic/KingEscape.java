package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class KingEscape implements HeuristicTablut {

    /* Valuta la distanza del re dalla fuga, quindi dai bordi della scacchiera
    * Distanza minorev = punteggio piu alto (bianco)
    * per il nero invertiamo
    */
    @Override
    public float getValue(StateTablut state) {
        // Implementazione dell'euristica per l'escape del re
        return 0.0f;
    }
}