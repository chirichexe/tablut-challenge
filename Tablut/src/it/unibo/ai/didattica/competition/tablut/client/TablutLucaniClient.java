package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.algorithm.MinMaxTablut;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * 
 * @author Davide Chirichella, Gabriele Doti
 *
 */

public class TablutLucaniClient extends TablutClient {

	private int game;
	private int maxDepth;

	public TablutLucaniClient(String player, String name, int gameChosen, int timeout, String ipAddress, int maxDepth) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
		game = gameChosen;
		this.maxDepth = Math.max(1, maxDepth);
	}
	
	public TablutLucaniClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, name, 4, timeout, ipAddress, computeAdaptiveMaxDepth(timeout));
	}
	
	public TablutLucaniClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, "Lucani", 4, timeout, ipAddress, computeAdaptiveMaxDepth(timeout));
	}

	public TablutLucaniClient(String player) throws UnknownHostException, IOException {
		this(player, "Lucani", 4, 60, "localhost", computeAdaptiveMaxDepth(60));
	}


	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		String role = "";
		String name = "Lucani";
		String ipAddress = "localhost";
		int timeout = 60;
		int maxDepth = computeAdaptiveMaxDepth(timeout);
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			role = (args[0]);
		}
		if (args.length >= 2) {
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			ipAddress = args[2];
		}
		maxDepth = computeAdaptiveMaxDepth(timeout);
		if (args.length >= 4) {
			maxDepth = Math.max(1, Integer.parseInt(args[3]));
		}

		System.out.println("Selected client: " + role + " (Team Lucani)");
		System.out.println("Search max depth: " + maxDepth);

		TablutLucaniClient client = new TablutLucaniClient(role, name, gametype, timeout, ipAddress, maxDepth);
		client.run();
	}

	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		State state;

		Game rules = null;
		switch (this.game) {
		case 1:
			state = new StateTablut();
			rules = new GameTablut();
			break;
		case 2:
			state = new StateTablut();
			rules = new GameModernTablut();
			break;
		case 3:
			state = new StateBrandub();
			rules = new GameTablut();
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		System.out.println("You are player " + this.getPlayer().toString() + "!");

		MinMaxTablut minMax = new MinMaxTablut(rules, this.maxDepth);

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// e1.printStackTrace();
                System.out.println("Server disconnected or communication error. Exiting...");
				System.exit(1);
			}
			
			state = this.getCurrentState();
			
			if (this.getPlayer().equals(Turn.WHITE)) {
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Computing best move...");
					Action a = minMax.search(state, this.getTimeout());
					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// e.printStackTrace();
                        System.out.println("Error writing to server. Exiting...");
                        System.exit(1);
					}
				}
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for opponent move... ");
				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			} else {
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Computing best move...");
					Action a = minMax.search(state, this.getTimeout());
					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// e.printStackTrace();
                        System.out.println("Error writing to server. Exiting...");
                        System.exit(1);
					}
				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for opponent move... ");
				} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			}
		}
	}

	private static int computeAdaptiveMaxDepth(int timeoutSeconds) {
		if (timeoutSeconds <= 10) {
			return 1;
		}
		if (timeoutSeconds <= 20) {
			return 2;
		}
		if (timeoutSeconds <= 40) {
			return 5;
		}
		return 10;
	}
}
