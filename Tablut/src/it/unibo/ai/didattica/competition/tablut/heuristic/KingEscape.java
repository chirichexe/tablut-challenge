package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class KingEscape implements HeuristicTablut {

    /* Valuta la distanza del re dalla fuga, quindi dai bordi della scacchiera
    * Distanza minorev = punteggio piu alto (bianco)
    * per il nero invertiamo
    */
    @Override
    public float getValue(State state) {
        
        int[] kingPosition = state.getKingPosition();
        // Calcola la distanza del re dai bordi della scacchiera
        int distanceToEdge = Math.min(kingPosition[0], state.getBoard().length - 1 - kingPosition[0]) + Math.min(kingPosition[1], state.getBoard().length - 1 - kingPosition[1]);

        return normalize(distanceToEdge);
    }

    private float normalize(float distance) {
        // TODO: Implementare la normalizzazione della distanza in un intervallo specifico, ad esempio [0, 1]
        return distance;
    }
}