package game.SY.model;


import com.google.common.collect.ImmutableSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import game.SY.HelpWithTests.ImmutableScotlandYardView;
import game.SY.HelpWithTests.TestOut;

import static game.SY.simple.TestGames.bus;
import static game.SY.simple.TestGames.secret;
import static game.SY.simple.TestGames.taxi;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.TicketAbbr.BS;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.TicketAbbr.SC;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.TicketAbbr.TX;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.TicketAbbr.UG;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.TicketAbbr.X2;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.black;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.blue;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.green;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.red;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.white;
import static game.SY.HelpWithTests.ImmutableScotlandYardView.yellow;
import static game.SY.HelpWithTests.PlayerInteractions.player;
import static game.SY.HelpWithTests.Requirement.eq;
import static game.SY.HelpWithTests.Requirement.hasSize;
import static game.SY.HelpWithTests.SpectatorInteractions.spectator;
import static game.SY.HelpWithTests.SpectatorInteractions.startRotate;
import static game.SY.model.Colour.BLACK;
import static game.SY.model.Colour.BLUE;
import static game.SY.model.Colour.GREEN;
import static game.SY.model.Colour.RED;
import static game.SY.model.Colour.WHITE;
import static game.SY.model.Colour.YELLOW;


public class ModelSixPlayerPlayOutTestSimple extends ParameterisedModelTestBase {

	private TestOut testOut;
	@Before public void initialise() { testOut = new TestOut(); }
	@After public void tearDown() { testOut.forceReleaseShutdownLock(); }




	@Test
	public void testGameWhereMrXNeverPickedADoubleMove() {
		PlayerConfiguration black = testOut.newPlayer(
				black(127).with(TX, 4, BS, 3, UG, 3, X2, 2, SC, 5));
		PlayerConfiguration blue = testOut.newPlayer(
				blue(26).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0));
		PlayerConfiguration green = testOut.newPlayer(
				green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0));
		PlayerConfiguration red = testOut.newPlayer(
				red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0));
		PlayerConfiguration white = testOut.newPlayer(
				white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0));
		PlayerConfiguration yellow = testOut.newPlayer(
				yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0));
		List<Boolean> rounds = Arrays.asList(
				false, false, true
				, false, false, false, false, true
				, false, false, false, false, true
				, false, false, false, false, true
				, false, false, false, false, false, true);
		ScotlandYardGame game = createGame(rounds, defaultGraph(),
				black, blue, green, red, white, yellow);
		Spectator spectator = testOut.createSpectator();
		game.registerSpectator(spectator);
		ImmutableScotlandYardView seed = ImmutableScotlandYardView.snapshot(game);
		testOut.play(game).startRotationAndAssertTheseInteractionsOccurInOrder(
				player(BLACK).makeMove()
						.givenGameState(eq(seed.players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 4, BS, 3, UG, 3, X2, 2, SC, 5),
								blue(26).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(127))
						.givenMoves(hasSize(165))
						.willPick(secret(BLACK, 133)),
				spectator().onRoundStarted()
						.givenGameState(eq(seed.current(BLUE).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 4, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(26).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenRound(eq(1)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(BLUE).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 4, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(26).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(secret(BLACK, 0))),
				player(BLUE).makeMove()
						.givenGameState(eq(seed.current(BLUE).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 4, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(26).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(26))
						.givenMoves(hasSize(3))
						.willPick(taxi(BLUE, 39)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(GREEN).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 5, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(BLUE, 39))),
				player(GREEN).makeMove()
						.givenGameState(eq(seed.current(GREEN).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 5, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(155).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(155))
						.givenMoves(hasSize(4))
						.willPick(taxi(GREEN, 156)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(RED).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 6, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(GREEN, 156))),
				player(RED).makeMove()
						.givenGameState(eq(seed.current(RED).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 6, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(174).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(174))
						.givenMoves(hasSize(3))
						.willPick(taxi(RED, 161)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(WHITE).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 7, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(RED, 161))),
				player(WHITE).makeMove()
						.givenGameState(eq(seed.current(WHITE).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 7, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(123).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(123))
						.givenMoves(hasSize(9))
						.willPick(taxi(WHITE, 124)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(YELLOW).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 8, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(WHITE, 124))),
				player(YELLOW).makeMove()
						.givenGameState(eq(seed.current(YELLOW).round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 8, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(50).with(TX, 11, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(50))
						.givenMoves(hasSize(3))
						.willPick(taxi(YELLOW, 49)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(YELLOW, 49))),
				spectator().onRotationComplete()
						.givenGameState(eq(seed.round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.respondWith(startRotate(game)),
				player(BLACK).makeMove()
						.givenGameState(eq(seed.round(1).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 4),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(133))
						.givenMoves(hasSize(152))
						.willPick(secret(BLACK, 157)),
				spectator().onRoundStarted()
						.givenGameState(eq(seed.current(BLUE).round(2).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 3),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenRound(eq(2)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(BLUE).round(2).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 3),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(secret(BLACK, 0))),
				player(BLUE).makeMove()
						.givenGameState(eq(seed.current(BLUE).round(2).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 9, BS, 3, UG, 3, X2, 2, SC, 3),
								blue(39).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(39))
						.givenMoves(hasSize(4))
						.willPick(taxi(BLUE, 51)),
				spectator().onMoveMade()
						.givenGameState(eq(seed.current(GREEN).round(2).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 10, BS, 3, UG, 3, X2, 2, SC, 3),
								blue(51).with(TX, 9, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenMove(eq(taxi(BLUE, 51))),
				player(GREEN).makeMove()
						.givenGameState(eq(seed.current(GREEN).round(2).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 10, BS, 3, UG, 3, X2, 2, SC, 3),
								blue(51).with(TX, 9, BS, 8, UG, 4, X2, 0, SC, 0),
								green(156).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
								//</editor-fold>
						                  ))
						.givenLocation(eq(156))
						.givenMoves(hasSize(8))
						.willPick(bus(GREEN, 157)),
				spectator().onMoveMade().givenGameState(eq(seed.current(RED).round(2).over(true)
								.winning(GREEN, BLUE, RED, YELLOW, WHITE).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 10, BS, 4, UG, 3, X2, 2, SC, 3),
								blue(51).with(TX, 9, BS, 8, UG, 4, X2, 0, SC, 0),
								green(157).with(TX, 10, BS, 7, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
						//</editor-fold>
				                                          ))
						.givenMove(eq(bus(GREEN, 157))),
				spectator().onGameOver().givenGameState(eq(seed.current(RED).round(2).over(true)
								.winning(GREEN, BLUE, RED, YELLOW, WHITE).players(
								//<editor-fold defaultstate="collapsed">
								black(0).with(TX, 10, BS, 4, UG, 3, X2, 2, SC, 3),
								blue(51).with(TX, 9, BS, 8, UG, 4, X2, 0, SC, 0),
								green(157).with(TX, 10, BS, 7, UG, 4, X2, 0, SC, 0),
								red(161).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								white(124).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0),
								yellow(49).with(TX, 10, BS, 8, UG, 4, X2, 0, SC, 0))
						//</editor-fold>
				                                          ))
						.givenWinners(eq(ImmutableSet.of(GREEN, BLUE, RED, YELLOW, WHITE))))
				.thenAssertNoFurtherInteractions();


	}
}