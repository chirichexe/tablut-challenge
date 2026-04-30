package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Material implements HeuristicTablut {

    /* Definisce un'euristica basata sul materiale, quindi il numero di pedine 
    * rimaste per ogni giocatore e il valore associato a ciascuna pedina 
    * (ad esempio, il re potrebbe valere di più rispetto ai pedoni)
    */
    @Override
    public float getValue(State state, Game game) {

        int whiteMaterial = state.getNumberOf(State.Pawn.WHITE);
        int blackMaterial = state.getNumberOf(State.Pawn.BLACK);

        return normalize(whiteMaterial - blackMaterial);
    }

    private float normalize(float material) {
        return material;
    }
}