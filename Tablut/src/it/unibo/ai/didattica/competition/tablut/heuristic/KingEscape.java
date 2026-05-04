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

    private float normalize(int distance) {
        // Normalizza la distanza del re dai bordi in [-1.0, 1.0]
        // Distanza minore = re più vicino al bordo = migliore per il Bianco
        // Formula: quanto più il re è vicino al bordo (distanza bassa), tanto più positivo è il valore
        return 1.0f - (distance / 8.0f) * 2.0f; // Normalizza in [-1, 1]
    }


}