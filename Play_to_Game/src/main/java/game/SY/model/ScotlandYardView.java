package game.SY.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import game.GameKT.graph.Graph;

// тукущие состояние игры
public interface ScotlandYardView {


	int NOT_STARTED = 0;

	// в порядке хода
	List<Colour> getPlayers();

	// может быть пустым
	Set<Colour> getWinningPlayers();

	// последнее месторосположение (для икса последнее известное)
	Optional<Integer> getPlayerLocation(Colour colour);


	Optional<Integer> getPlayerTickets(Colour colour, Ticket ticket);


	boolean isGameOver();


	Colour getCurrentPlayer();


	int getCurrentRound();


	List<Boolean> getRounds();


	Graph<Integer, Transport> getGraph();

}
