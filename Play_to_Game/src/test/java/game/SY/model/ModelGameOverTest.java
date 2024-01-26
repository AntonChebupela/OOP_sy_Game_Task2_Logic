package game.SY.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import game.SY.HelpWithTests.TestOut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static game.SY.simple.TestGames.bus;
import static game.SY.simple.TestGames.pass;
import static game.SY.simple.TestGames.taxi;
import static game.SY.HelpWithTests.PlayerInteractions.player;
import static game.SY.HelpWithTests.Requirement.gameNotOver;
import static game.SY.HelpWithTests.Requirement.gameOver;
import static game.SY.model.Colour.BLACK;
import static game.SY.model.Colour.BLUE;
import static game.SY.model.Colour.GREEN;
import static game.SY.model.Colour.RED;
import static game.SY.model.Colour.WHITE;


public class ModelGameOverTest extends ParameterisedModelTestBase {

	private TestOut testOut;

	@Before
	public void initialise() {
		testOut = new TestOut();
	}

	@After
	public void tearDown() {
		testOut.forceReleaseShutdownLock();
	}

	@Test
	public void testStartRoundShouldThrowIfGameAlreadyOver() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 105, 0, 0, 0, 0, 0);
		ScotlandYardGame game = createGame(mrX, blue);
		assertThat(game.isGameOver()).isTrue();
		assertThatThrownBy(game::startRotate).isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void testGameOverIfAllDetectivesStuck() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 105, 1, 0, 0, 0, 0);
		PlayerConfiguration red = testOut.newPlayer(RED, 70, 1, 0, 0, 0, 0);

		testOut.play(createGame(mrX, blue, red))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(104)),
						player(BLUE).makeMove().willPick(taxi(106)),
						player(RED).makeMove().willPick(taxi(71)))
				.thenRequire(gameOver())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testGameOverIfMrXStuck() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86, 1, 1, 1, 0, 0);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 108);
		testOut.play(createGame(mrX, blue))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(104)),
						player(BLUE).makeMove().willPick(bus(105)))
				.thenRequire(gameOver())
				.thenIgnoreAnyFurtherInteractions();
	}



	@Test
	public void testGameOverIfMrXCaptured() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 85);
		testOut.play(createGame(mrX, blue))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(103)),
						player(BLUE).makeMove().willPick(taxi(103)))
				.thenRequire(gameOver())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testDetectiveWinsIfMrXCornered() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 103);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 68, 0, 0, 0, 0, 0);
		PlayerConfiguration red = testOut.newPlayer(RED, 84, 0, 0, 0, 0, 0);
		PlayerConfiguration green = testOut.newPlayer(GREEN, 102);
		testOut.play(createGame(mrX, blue, red, green))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(85)),
						player(BLUE).makeMove().willPick(pass()),
						player(RED).makeMove().willPick(pass()),
						player(GREEN).makeMove().willPick(taxi(103)))
				.thenRequire(gameOver())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testGameNotOverIfMrXCorneredButCanStillEscape() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 40);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 39, 0, 0, 0, 0, 0);
		PlayerConfiguration red = testOut.newPlayer(RED, 51, 0, 0, 0, 0, 0);
		PlayerConfiguration white = testOut.newPlayer(WHITE, 69, 0, 0, 0, 0, 0);
		PlayerConfiguration green = testOut.newPlayer(GREEN, 41);
		testOut.play(createGame(mrX, blue, red, white, green))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(52)),
						player(BLUE).makeMove().willPick(pass()),
						player(RED).makeMove().willPick(pass()),
						player(WHITE).makeMove().willPick(pass()),
						player(GREEN).makeMove().willPick(taxi(40)))
				.thenRequire(gameNotOver())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testGameNotOverBeforeAnyRoundWithNonTerminatingConfiguration() {
		ScotlandYardGame game = createGame(
				testOut.newPlayer(BLACK, 86), testOut.newPlayer(BLUE, 108));
		assertThat(game.isGameOver()).isFalse();
	}

	@Test
	public void testGameOverBeforeAnyRoundWithTerminatingConfiguration() {
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 108, 0, 0, 0, 0, 0);
		ScotlandYardGame game = createGame(testOut.newPlayer(BLACK, 86), blue);
		assertThat(game.isGameOver()).isTrue();
	}

	@Test
	public void testWinningPlayerIsEmptyBeforeGameOver() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 108);
		testOut.play(createGame(mrX, blue))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(104)),
						player(BLUE).makeMove().willPick(bus(105)))
				.thenRequire(gameNotOver())
				.thenAssert("Winning players is empty",
						g -> assertThat(g.getWinningPlayers()).isEmpty())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testWinningPlayerOnlyContainsBlackIfMrXWins() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 86);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 108, 1, 0, 0, 0, 0);
		testOut.play(createGame(mrX, blue))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(104)),
						player(BLUE).makeMove().willPick(taxi(105)))
				.thenRequire(gameOver())
				.thenAssert("Black is the only winner",
						g -> assertThat(g.getWinningPlayers()).containsExactlyInAnyOrder(BLACK));
	}
}
