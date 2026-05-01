package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Material extends HeuristicTablut {

    /* Definisce un'euristica basata sul materiale, quindi il numero di pedine 
    * rimaste per ogni giocatore e il valore associato a ciascuna pedina 
    * (ad esempio, il re potrebbe valere di più rispetto ai pedoni)
    */
    @Override
    public float getValue(State state) {

        int whiteMaterial = state.getNumberOf(State.Pawn.WHITE);
        int blackMaterial = state.getNumberOf(State.Pawn.BLACK);

        return normalize(HeuristicWeights.MATERIAL_WHITE_VALUE * whiteMaterial - HeuristicWeights.MATERIAL_BLACK_VALUE * blackMaterial);
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.MATERIAL_WEIGHT;
    }

    private float normalize(float material) {
        // TODO: Implementare la normalizzazione del materiale in un intervallo specifico, ad esempio [0, 1]
        
        float minValue = HeuristicWeights.MATERIAL_WHITE_VALUE * 0 - HeuristicWeights.MATERIAL_BLACK_VALUE * 16; // Tutti i pedoni neri e nessun pedone bianco
        float maxValue = HeuristicWeights.MATERIAL_WHITE_VALUE * 8 - HeuristicWeights.MATERIAL_BLACK_VALUE * 0; // Tutti i pedoni bianchi e nessun pedone nero
        return ((2.0f * (material - minValue)) / (maxValue - minValue)) - 1.0f; // Normalizza in [-1, 1]
        
        //return material;
    }
}