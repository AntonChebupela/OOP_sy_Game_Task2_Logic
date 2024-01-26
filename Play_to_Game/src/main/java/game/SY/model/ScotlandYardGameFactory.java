package game.SY.model;

import java.util.List;

import game.GameKT.graph.Graph;

import static java.util.stream.Collectors.*;
// создание игры с заданными параметрами
public interface ScotlandYardGameFactory {


	ScotlandYardGame createGame(
			List<Boolean> rounds, // скрытый или открытый
			Graph<Integer, Transport> graph,
			PlayerConfiguration mrX,
			PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives);

	static List<? extends ScotlandYardGameFactory> instantiate(
			List<Class<? extends ScotlandYardGameFactory>> factories) {
		return factories.stream().map(f -> {
			try {
				return f.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}).collect(toList());
	}

}
