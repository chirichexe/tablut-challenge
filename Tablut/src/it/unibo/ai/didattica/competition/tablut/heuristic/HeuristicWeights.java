package it.unibo.ai.didattica.competition.tablut.heuristic;

public final class HeuristicWeights {
    public static final float KING_ESCAPE_WEIGHT = 1.5f; // Peso per l'euristica dell'escape del re
    public static final float KING_SAFETY_WEIGHT = 3.0f; // Peso per l'euristica della sicurezza del re
    public static final float MATERIAL_WEIGHT = 1.5f; // Peso per l'euristica del materiale
    public static final float MOBILITY_WEIGHT = 2.0f; // Peso per l'euristica della mobilità
    public static final float OPEN_PATH_WEIGHT = 2.0f; // Peso per l'euristica delle vie aperte fino al bordo

    /* Pesi per KingSafety */
    public static final float KING_SAFETY_FREE_CELLS_WEIGHT = 2.0f;
    public static final float KING_SAFETY_ENEMY_CELLS_WEIGHT = 1.0f;
    public static final float KING_SAFETY_FRIENDLY_CELLS_WEIGHT = 2.5f;

    /* Pesi per Material */
    public static final float MATERIAL_BLACK_VALUE = 1.0f;
    public static final float MATERIAL_WHITE_VALUE = 1.0f;

    /* Nuove Euristiche */
    public static final float RHOMBUS_WEIGHT = 2.0f;
    public static final float BEST_POSITIONS_WEIGHT = 1.5f;
}