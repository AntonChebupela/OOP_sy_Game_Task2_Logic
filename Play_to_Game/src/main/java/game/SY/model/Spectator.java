package game.SY.model;

import java.util.Set;

public interface Spectator {


	default void onMoveMade(ScotlandYardView view, Move move) {}


	default void onRoundStarted(ScotlandYardView view, int round) {}


	default void onRotationComplete(ScotlandYardView view) {}


	default void onGameOver(ScotlandYardView view, Set<Colour> winningPlayers) {}

}
