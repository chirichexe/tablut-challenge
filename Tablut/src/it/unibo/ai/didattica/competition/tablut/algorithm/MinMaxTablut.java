package it.unibo.ai.didattica.competition.tablut.algorithm;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

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

    public float evaluate(State state) {
        
        return 0.0f;
    }

}