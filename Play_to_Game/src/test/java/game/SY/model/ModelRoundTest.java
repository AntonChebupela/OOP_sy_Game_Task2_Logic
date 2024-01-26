package game.SY.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import game.SY.HelpWithTests.TestOut;

import static game.SY.simple.TestGames.bus;

import static game.SY.simple.TestGames.taxi;

import static game.SY.HelpWithTests.PlayerInteractions.player;
import static game.SY.HelpWithTests.Requirement.eq;

import static game.SY.HelpWithTests.Requirement.isOnRound;
import static game.SY.HelpWithTests.Requirement.neq;

import static game.SY.model.Colour.BLACK;
import static game.SY.model.Colour.BLUE;
import static game.SY.model.Colour.RED;


/**
 * {@link Player}
 */
public class ModelRoundTest extends ParameterisedModelTestBase {

	private TestOut testOut;
	@Before public void initialise() { testOut = new TestOut(); }
	@After public void tearDown() { testOut.forceReleaseShutdownLock(); }

	@Test
	public void testPlayerNotified() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);
		testOut.play(createGame(mrX, blue)).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testCallbackIsNotNull() {
		testOut.play(createGame(testOut.newPlayer(BLACK), testOut.newPlayer(BLUE)))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().givenCallback(neq(null)))
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testInitialPositionMatchFirstRound() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);


		testOut.play(createGame(mrX, blue)).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove().givenLocation(eq(mrX.location)).willPickFirst(),
				player(BLUE).makeMove().givenLocation(eq(blue.location)))
				.thenIgnoreAnyFurtherInteractions();
	}


	@Test
	public void testRoundIncrementsAfterAllPlayersHavePlayed() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 35);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 50);

		testOut.play(createGame(mrX, blue))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(taxi(65)),
						player(BLUE).makeMove().willPick(taxi(49)))
				.startRotationAndAssertTheseInteractionsOccurInOrder(
						player(BLACK).makeMove().willPick(bus(82)),
						player(BLUE).makeMove().givenGameState(isOnRound(2)))
				.thenIgnoreAnyFurtherInteractions();
	}



	@Test
	public void testMrXIsTheFirstToPlay() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);

		testOut.play(createGame(mrX, blue)).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove().willPickFirst(),
				player(BLUE).makeMove())
				.thenIgnoreAnyFurtherInteractions();
	}

	@Test
	public void testRoundWaitsForPlayerWhoDoesNotRespond() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);

		testOut.play(createGame(mrX, blue)).startRotationAndAssertTheseInteractionsOccurInOrder(
				// black player does nothing
				player(BLACK).makeMove().wontRespond())
				// blue should not receive any move request
				.thenAssertNoFurtherInteractions();
	}

	@Test
	public void testRoundRotationNotifiesAllPlayer() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK, 35);
		PlayerConfiguration blue = testOut.newPlayer(BLUE, 50);
		PlayerConfiguration red = testOut.newPlayer(RED, 26);

		ScotlandYardGame game = createGame(mrX, blue, red);
		testOut.play(game).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove().willPick(taxi(22)),
				player(BLUE).makeMove().willPick(taxi(37)),
				player(RED).makeMove().willPick(taxi(15)))

				// only one notifies
				.thenAssertNoFurtherInteractions();
	}

	@Test
	public void testCallbackWithNullWillThrow() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);
		ScotlandYardGame game = createGame(mrX, blue);

		testOut.play(game).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove().willPick((Move) null))
				.shouldThrow(NullPointerException.class);
	}

	@Test
	public void testCallbackWithIllegalMoveNotInGivenMovesWillThrow() {
		PlayerConfiguration mrX = testOut.newPlayer(BLACK);
		PlayerConfiguration blue = testOut.newPlayer(BLUE);
		ScotlandYardGame game = createGame(mrX, blue);


		testOut.play(game).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove().willPick(bus(20)))
				.shouldThrow(IllegalArgumentException.class);
	}

}
