package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Material implements HeuristicTablut {

    /* Definisce un'euristica basata sul materiale, quindi il numero di pedine 
    * rimaste per ogni giocatore e il valore associato a ciascuna pedina 
    * (ad esempio, il re potrebbe valere di più rispetto ai pedoni)
    */
    @Override
    public float getValue(State state) {

        int whiteMaterial = state.getNumberOf(State.Pawn.WHITE);
        int blackMaterial = state.getNumberOf(State.Pawn.BLACK);

        return normalize(whiteMaterial - blackMaterial);
    }

    private float normalize(float material) {
        // TODO: Implementare la normalizzazione del materiale in un intervallo specifico, ad esempio [0, 1]
        return material;
    }
}