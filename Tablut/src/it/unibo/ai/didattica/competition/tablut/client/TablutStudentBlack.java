package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

/**
 * TablutStudentBlack
 */
public class TablutStudentBlack extends TablutClient {

    private final int timeout;

    public TablutStudentBlack(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.timeout = timeout;
    }

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        // Parametri obbligatori come da Slide 20: <Role> <Timeout> <ServerIP>
        if (args.length < 3) {
            System.out.println("Usage: java TablutStudentBlack <WHITE|BLACK> <timeout> <serverIP>");
            System.exit(-1);
        }

        String role = args[0].toUpperCase();
        int timeout = Integer.parseInt(args[1]);
        String ipAddress = args[2];
        String teamName = "Lucani"; 

        System.out.println("Lancio del player " + teamName + " come " + role + " con timeout " + timeout + "s su " + ipAddress);

        TablutStudentBlack client = new TablutStudentBlack(role, teamName, timeout, ipAddress);
        client.run();
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Game rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");

        while (true) {
            try {
                this.read();
                State state = this.getCurrentState();
                
                /* gioco finito */
                if (state.getTurn().equals(Turn.WHITEWIN) || state.getTurn().equals(Turn.BLACKWIN) || state.getTurn().equals(Turn.DRAW)) {
                    System.out.println("Fine partita: " + state.getTurn().toString());
                    break;
                }

                /* è il mio turno */
                if (state.getTurn().equals(this.getPlayer())) {
                    
                    System.out.println("È il mio turno.");
                    
                    /* scelgo la mossa */
                    Action bestMove = getPlaceholderMove(state, rules);

                    if (bestMove != null) {
                        System.out.println("Invio mossa: " + bestMove.toString());
                        
                        /* sottometto la nuova mossa */
                        this.write(bestMove);
                        
                        // 4. Read the new state modified by my move (Slide 15)
                        // Molte implementazioni di TablutClient.write() lo fanno già, 
                        // ma il protocollo lo richiede esplicitamente.
                    }
                } 
                else {
                    System.out.println("In attesa dell'avversario...");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Placeholder che sceglie una mossa a caso tra quelle legali.
     */
    private Action getPlaceholderMove(State state, Game rules) {
        List<Action> legalMoves = new ArrayList<>();
        int boardSize = state.getBoard().length;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (state.getPawn(i, j).equalsPawn(this.getPlayer().toString()) || 
                   (this.getPlayer().equals(Turn.WHITE) && state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString()))) {
                    
                    for (int k = 0; k < boardSize; k++) {
                        for (int l = 0; l < boardSize; l++) {
                            try {
                                Action action = new Action(state.getBox(i, j), state.getBox(k, l), this.getPlayer());
                                rules.checkMove(state.clone(), action);
                                legalMoves.add(action);
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        }

        if (legalMoves.isEmpty()) return null;
        return legalMoves.get(new Random().nextInt(legalMoves.size()));
    }
}
