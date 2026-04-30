package it.unibo.ai.didattica.competition.tablut.heuristic;

public class HeuristicWeights {
    public static final float ESCAPE_WEIGHT = 1.0f; // Peso per l'euristica dell'escape del re
    public static final float KING_SAFETY_WEIGHT = 0.5f; // Peso per l'euristica della sicurezza del re
    public static final float MATERIAL_WEIGHT = 0.5f; // Peso per l'euristica del materiale
    public static final float MOBILITY_WEIGHT = 0.5f; // Peso per l'euristica della mobilità
    public static final float CONTROL_WEIGHT = 0.5f; // Peso per l'euristica del controllo del centro

    /* Pesi per KingSafety */
    public static final float KING_SAFETY_FREE_CELLS_WEIGHT = 1.0f;
    public static final float KING_SAFETY_ENEMY_CELLS_WEIGHT = 0.5f;
    public static final float KING_SAFETY_FRIENDLY_CELLS_WEIGHT = 1.0f;
}