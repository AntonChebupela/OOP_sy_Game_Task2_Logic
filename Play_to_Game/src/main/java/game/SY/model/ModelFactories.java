package game.SY.model;

import java.util.Collections;
import java.util.List;

import game.GameKT.graph.Graph;


public class ModelFactories {

	static List<Class<? extends ScotlandYardGameFactory>> factories() {
		return Collections.singletonList(ImperativeModelFactory.class);
	}


	static class ImperativeModelFactory implements ScotlandYardGameFactory {

		@Override
		public ScotlandYardGame createGame(List<Boolean> rounds, Graph<Integer, Transport> graph,
				PlayerConfiguration mrX, PlayerConfiguration firstDetective,
				PlayerConfiguration... restOfTheDetectives) {
			return new ScotlandYardModel(rounds, graph, mrX, firstDetective, restOfTheDetectives);
		}

		@Override
		public String toString() {
			return "ScotlandYardModel";
		}

	}

}
