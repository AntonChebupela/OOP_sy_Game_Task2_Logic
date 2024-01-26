package game.SY.model;

import game.GameKT.graph.Graph;
import game.SY.simple.TestGames;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(Parameterized.class)
public abstract class ParameterisedModelTestBase implements ScotlandYardGameFactory {

	static final List<Class<? extends ScotlandYardGameFactory>> FACTORIES = ModelFactories
			.factories();

	@SuppressWarnings("WeakerAccess") @Parameter public ScotlandYardGameFactory factory;

	@Parameters(name = "{0}")
	public static ScotlandYardGameFactory[] data() {
		return ScotlandYardGameFactory.instantiate(FACTORIES)
				.toArray(new ScotlandYardGameFactory[0]);
	}

	@BeforeClass
	public static void setUp() {
		try {
			defaultGraph = ScotlandYardGraphReader.fromLines(Files.readAllLines(
					Paths.get(ParameterisedModelTestBase.class.getResource("/game_graph.txt")
							.toURI())));

		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static Graph<Integer, Transport> defaultGraph;

	static Graph<Integer, Transport> defaultGraph() {
		return defaultGraph;
	}


	@Override
	public ScotlandYardGame createGame(List<Boolean> rounds, Graph<Integer, Transport> graph,
	                                   PlayerConfiguration mrX, PlayerConfiguration firstDetective,
	                                   PlayerConfiguration... restOfTheDetectives) {
		return factory.createGame(rounds, graph, mrX, firstDetective, restOfTheDetectives);
	}

	/**
	 *  with 1..23 rounds and the default graph
	 */
	ScotlandYardGame createGame(PlayerConfiguration mrX, PlayerConfiguration firstDetective,
	                            PlayerConfiguration... restOfTheDetectives) {
		return createGame(TestGames.ofRounds(23, TestGames.DEFAULT_REVEAL), defaultGraph, mrX,
				firstDetective,
				restOfTheDetectives);
	}




	ScotlandYardGame createValidSixPlayerGame() {
		return createGame(
				TestGames.doNothingMrX(),
				TestGames.doNothingRed(),
				TestGames.doNothingGreen(),
				TestGames.doNothingBlue(),
				TestGames.doNothingWhite(),
				TestGames.doNothingYellow());
	}

}
