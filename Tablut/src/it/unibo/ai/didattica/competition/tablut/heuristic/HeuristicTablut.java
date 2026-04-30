package it.unibo.ai.didattica.competition.tablut.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public abstract class HeuristicTablut {

    protected final float weight;

    protected HeuristicTablut() {
        this.weight = setWeight();
    }

    protected abstract float setWeight();

    public abstract float getValue(State state);

    public float getWeight() {
        return weight;
    }
}