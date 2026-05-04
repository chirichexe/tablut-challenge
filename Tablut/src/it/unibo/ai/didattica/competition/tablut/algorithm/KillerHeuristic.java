package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;

public class KillerHeuristic {
    private final Action[][] killerMoves;
    private final int maxDepth;

    public KillerHeuristic(int maxDepth) {
        this.maxDepth = maxDepth;
        this.killerMoves = new Action[maxDepth + 1][2]; // 2 slot per livello di profondità
    }

    /**
     * Memorizza una mossa che ha causato un cutoff (sostituendo)
     */
    public void addKillerMove(Action move, int depth) {

        if (depth < 0 || depth > maxDepth) return;
        
        // Se la mossa è già la prima killer, non fare nulla
        if (move.equals(killerMoves[depth][0])) return;

        // la vecchia primaria diventa secondaria, la nuova diventa primaria
        killerMoves[depth][1] = killerMoves[depth][0];
        killerMoves[depth][0] = move;
    }

    /**
     * Restituisce un punteggio se la mossa è tra le killer di quel livello.
     */
    public int getKillerScore(Action move, int depth) {
        if (depth < 0 || depth > maxDepth) return 0;
        
        if (move.equals(killerMoves[depth][0])) return 10000; // Bonus primario
        if (move.equals(killerMoves[depth][1])) return 5000;  // Bonus secondario
        
        return 0;
    }
    


}