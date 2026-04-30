package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;

public class MinMaxTablut {
    private Game game;
    private int maxDepth;
    
    public MinMaxTablut(Game game, int maxDepth) {
        this.game = game;
        this.maxDepth = maxDepth;
    }

    public Action getBestMove() {
        return minmax(game, maxDepth, true);
    }

    public Action minmax(Game game, int depth, boolean maximizingPlayer) {

        if (depth == 0 ) {
            return null; // Valutazione della posizione
        }
        
        if (maximizingPlayer) 

        return null;
    }

}