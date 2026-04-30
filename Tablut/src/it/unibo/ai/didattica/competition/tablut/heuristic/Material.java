package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class Material implements HeuristicTablut {

    /* Definisce un'euristica basata sul materiale, quindi il numero di pedine 
    * rimaste per ogni giocatore e il valore associato a ciascuna pedina 
    * (ad esempio, il re potrebbe valere di più rispetto ai pedoni)
    */
    @Override
    public float getValue(StateTablut state) {
        // Implementazione dell'euristica per l'escape del re
        return 0.0f;
    }
}