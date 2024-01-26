package game.SY.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import game.SY.HelpWithTests.TestOut;
import game.SY.model.PlayerConfiguration.Builder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static game.SY.simple.TestGames.doNothingBlue;

import static game.SY.simple.TestGames.doNothingMrX;
import static game.SY.simple.TestGames.doNothingPlayer;

import static game.SY.simple.TestGames.dummyPlayer;
import static game.SY.simple.TestGames.makeTickets;
import static game.SY.model.Colour.BLACK;
import static game.SY.model.Colour.BLUE;

import static game.SY.model.Colour.RED;

public class ModelCreationTest extends ParameterisedModelTestBase {

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
	public void testNullMrXShouldThrow() {
		assertThatThrownBy(() -> createGame(null, doNothingPlayer(RED, 1)))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testNoMrXShouldThrow() {
		assertThatThrownBy(() -> createGame(
				doNothingPlayer(BLUE, 1),
				doNothingPlayer(RED, 2))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testMoreThanOneMrXShouldThrow() {
		assertThatThrownBy(() -> createGame(
				doNothingPlayer(BLACK, 1),
				doNothingPlayer(BLACK, 2))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testDuplicateDetectivesShouldThrow() {
		assertThatThrownBy(() -> createGame(
				doNothingPlayer(BLACK, 1),
				doNothingPlayer(BLUE, 2),
				doNothingPlayer(BLUE, 2))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testLocationOverlapBetweenMrXAndDetectiveShouldThrow() {
		assertThatThrownBy(() -> createGame(
				doNothingPlayer(BLACK, 1),
				doNothingPlayer(BLUE, 1))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testDetectiveHaveSecretTicketShouldThrow() {
		PlayerConfiguration blue = new Builder(BLUE).using(dummyPlayer())
				.with(makeTickets(1, 1, 1, 0, 1))
				.at(2).build();
		assertThatThrownBy(() -> createGame(doNothingMrX(), blue))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testEmptyRoundsShouldThrow() {
		assertThatThrownBy(() -> createGame(
				emptyList(),
				defaultGraph(),
				doNothingPlayer(BLACK, 1),
				doNothingPlayer(BLUE, 1))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetRoundsIsImmutable() {
		ScotlandYardGame game = createGame(
				new ArrayList<>(asList(true, false)),
				defaultGraph(),
				doNothingMrX(),
				doNothingBlue());
		assertThatThrownBy(() -> game.getRounds().add(true))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void testGetGraphMatchesSupplied() {
		ScotlandYardGame game = createGame(
				asList(true, false),
				defaultGraph(),
				doNothingMrX(),
				doNothingBlue());
		assertThat(game.getGraph()).isEqualTo(defaultGraph());
	}

	@Test
	public void testGameIsNotOverInitially() {
		assertThat(createValidSixPlayerGame().isGameOver()).isFalse();
	}


}

