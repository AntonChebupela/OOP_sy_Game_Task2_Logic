package game.SY.model;

import java.util.Set;
import java.util.function.Consumer;


public interface Player {


	void makeMove(ScotlandYardView view, int location, Set<Move> moves, Consumer<Move> callback);

}
