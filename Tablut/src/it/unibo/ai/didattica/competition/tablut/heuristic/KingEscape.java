package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class KingEscape extends HeuristicTablut {

    /* Valuta la distanza del re dai vertici della scacchiera
    * Distanza minorev = punteggio piu alto (bianco)
    * per il nero invertiamo
    */
    @Override
    public float getValue(State state) {
        
        int[] kingPosition = state.getKingPosition();

        // int distanceToEdge = Math.min(Math.min(kingPosition[0], state.getBoard().length - 1 - kingPosition[0]), Math.min(kingPosition[1], state.getBoard().length - 1 - kingPosition[1]));
        int distanceToEdge = Math.min(kingPosition[0], state.getBoard().length - 1 - kingPosition[0]) + Math.min(kingPosition[1], state.getBoard().length - 1 - kingPosition[1]);

        return normalize(distanceToEdge);
    }

    @Override
    public float setWeight() {
        return HeuristicWeights.KING_ESCAPE_WEIGHT;
    }

    private float normalize(float distance) {
        // TODO: Implementare la normalizzazione della distanza in un intervallo specifico, ad esempio [0, 1]
        /* return (1 - distance/4.0); */
        /* return (1 - distance/8.0); */
        return distance;
    }


}