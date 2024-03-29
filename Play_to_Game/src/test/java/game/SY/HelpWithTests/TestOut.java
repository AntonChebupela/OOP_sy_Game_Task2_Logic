package game.SY.HelpWithTests;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import game.SY.HelpWithTests.Captures.Player.MakeMove;
import game.SY.HelpWithTests.Captures.Spectator.GameOver;
import game.SY.HelpWithTests.Captures.Spectator.MoveMade;
import game.SY.HelpWithTests.Captures.Spectator.RotationComplete;
import game.SY.HelpWithTests.Captures.Spectator.RoundStarted;
import game.SY.HelpWithTests.PlayerInteractions.MakeMoveInteraction;
import game.SY.HelpWithTests.SpectatorInteractions.GameOverInteraction;
import game.SY.HelpWithTests.SpectatorInteractions.MoveMadeInteraction;
import game.SY.HelpWithTests.SpectatorInteractions.RotationCompleteInteraction;
import game.SY.HelpWithTests.SpectatorInteractions.RoundStartedInteraction;
import game.SY.HelpWithTests.TestOut.Setups.Assert;
import game.SY.model.Colour;
import game.SY.model.Move;
import game.SY.model.PlayerConfiguration;
import game.SY.model.ScotlandYardGame;
import game.SY.model.ScotlandYardView;
import game.SY.model.Spectator;
import game.SY.model.Ticket;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.fail;
import static game.SY.simple.TestGames.DETECTIVE_LOCATIONS;
import static game.SY.simple.TestGames.MRX_LOCATIONS;
import static game.SY.simple.TestGames.detectiveTickets;
import static game.SY.simple.TestGames.mrXTickets;
import static game.SY.HelpWithTests.Assertions.assertFailure;
import static game.SY.HelpWithTests.Interactions.assertEach;
import static game.SY.HelpWithTests.PlayerInteractions.mkPlayerKey;
import static game.SY.HelpWithTests.Requirement.fromAssertion;
import static game.SY.HelpWithTests.SpectatorInteractions.mkSpectatorKey;
import static game.SY.HelpWithTests.TestOut.SpectatorEvent.ON_GAME_OVER;
import static game.SY.HelpWithTests.TestOut.SpectatorEvent.ON_MOVE_MADE;
import static game.SY.HelpWithTests.TestOut.SpectatorEvent.ON_ROTATION_COMPLETE;
import static game.SY.HelpWithTests.TestOut.SpectatorEvent.ON_ROUND_STARTED;


public class TestOut {


	enum EndMode {NO_MORE, IGNORE}

	private final int captureN;
	private final String callingClass;
	private final Queue<Interaction<?>> interactions = new ArrayDeque<>();
	private boolean printInteractions;
	private int totalInteractions = 0;
	private int interactionsProcessed = 0;
	private ScotlandYardGame game;
	private EndMode endMode = EndMode.IGNORE;
	private List<Requirement<ScotlandYardGame>> finalReqs;
	private Consumer<ScotlandYardGame> finalContinuation;

	private static boolean getBooleanFlag(String flagName) {
		return Optional.ofNullable(System.getProperty(flagName))
				.flatMap(flag -> {
					try {
						return Optional.of(Boolean.parseBoolean(flag));
					} catch (Exception e) {
						return Optional.empty();
					}
				}).orElse(false);
	}

	private static int getIntFlag(String flagName, int fallback) {
		return Optional.ofNullable(System.getProperty(flagName))
				.flatMap(flag -> {
					try {
						return Optional.of(Integer.parseInt(flag));
					} catch (Exception e) {
						return Optional.empty();
					}
				}).orElse(fallback);
	}

	public TestOut() {
		this(getBooleanFlag("printInteractions"));
	}
	public TestOut(boolean printInteractions) {
		this(getIntFlag("captureN", 10), printInteractions);
	}
	public TestOut(int captureN) {
		this(captureN, getBooleanFlag("printInteractions"));
	}
	public TestOut(int captureN, boolean printInteractions) {
		Assertions.disableSystemExit();
		this.captureN = captureN;
		this.printInteractions = printInteractions;
		this.callingClass = Arrays.stream(Thread.currentThread().getStackTrace())
				.skip(1)
				.map(StackTraceElement::getClassName)
				.filter(s -> !s.equals(getClass().getName()))
				.findFirst().orElse(getClass().getName());
	}

	public void forcePrintInteraction() {
		printInteractions = true;
	}

	public interface Interaction<C> {
		Comparable<?> key();
		void applyAssertion(C capture, AssertionContext ctx);
		default void executeAction(C capture, String callingClass) {
			// no
		}
		String describe();
		List<StackTraceElement> origin();

		static String describeMethod(String methodName, ImmutableMap<String, Collection<?>> kvs) {
			return format("%s(\n%s\n)", methodName, kvs.entrySet().stream()
					.map(e -> e.getValue().isEmpty() ?
							new SimpleImmutableEntry<>(e.getKey(), ImmutableList.of("can be " +
									"anything")) : e)
					.map(e -> format("\t%s %s", e.getKey(), e.getValue().stream()
							.map(Object::toString)
							.collect(joining(","))))
					.collect(joining(",\n")));

		}
	}


	public Assert play(ScotlandYardGame game) {
		this.game = game;
		return new TestAssert();
	}

	private class TestAssert implements Assert {
		private TestAssert previousRound;
		private Interaction<?>[] interactions;
		private List<Requirement<ScotlandYardGame>> reqs = new ArrayList<>();
		Consumer<ScotlandYardGame> continuation = game -> Assertions.enableSystemExit();

		private TestAssert(Interaction<?>... interactions) {
			this(null, interactions);
		}
		private TestAssert(TestAssert previousRound, Interaction<?>... interactions) {
			this.previousRound = previousRound;
			this.interactions = interactions;
			totalInteractions += interactions.length;
		}
		@Override
		public Assert startRotationAndAssertTheseInteractionsOccurInOrder(Interaction<?>... interactions) {
			return new TestAssert(this, interactions);
		}
		@Override public final Assert thenRequire(
				Requirement<ScotlandYardGame> requirement) {
			reqs.add(requirement);
			return this;
		}
		@Override public void thenIgnoreAnyFurtherInteractions() {
			endMode = EndMode.IGNORE;
			runInteractions();
		}
		@Override public void thenAssertNoFurtherInteractions() {
			endMode = EndMode.NO_MORE;
			runInteractions();
		}
		@Override public void shouldThrow(Class<? extends Throwable> clazz) {
			try {
				this.runInteractions();
			} catch (Throwable e) {
				if (!clazz.isInstance(e)) {
					StringWriter logger = new StringWriter();
					e.printStackTrace(new PrintWriter(logger));
					assertFailure(
							String.format("Wrong exception thrown: %s expected but found:\n%s\n%s",
									clazz.getSimpleName(), e.toString(), logger.toString()),
							Assertions.capture(),
							callingClass);
				}
				return;
			}
			assertFailure(
					String.format("%s was expected but not thrown", clazz.getSimpleName()),
					Assertions.capture(),
					callingClass);
		}

		private void runInteractions() {
			if (previousRound != null) {
				previousRound.continuation = game -> {
					finalContinuation = continuation;
					interactionsProcessed += interactions.length;
					TestOut.this.interactions.clear();
					TestOut.this.interactions.addAll(asList(interactions));
					finalReqs = reqs;
					game.startRotate();
				};
				previousRound.runInteractions();
			} else {
				assertEach((message, stack) ->
						assertFailure(String.format("Error at game creation: " +
										"\nThe following assertion failed: %s",
								message), stack, callingClass), reqs, game);
			}

			assertMoreInteractions();

			if (previousRound == null) {
				continuation.accept(game);
			}
		}
	}

	public void forceReleaseShutdownLock() {
		Assertions.enableSystemExit();
	}

	public interface Setups {
		interface Interactions {
			Assert startRotationAndAssertTheseInteractionsOccurInOrder(Interaction<?>... interactions);
		}

		interface Assert extends Rest {
			Assert thenRequire(Requirement<ScotlandYardGame> requirement);
			default Assert thenAssert(Consumer<ScotlandYardGame> assertion) {
				return thenRequire(fromAssertion(assertion));
			}
			default Assert thenAssert(String name, Consumer<ScotlandYardGame> assertion) {
				return thenRequire(fromAssertion(name, assertion));
			}
		}

		interface Rest extends Interactions {
			void thenIgnoreAnyFurtherInteractions();
			void thenAssertNoFurtherInteractions();
			void shouldThrow(Class<? extends Throwable> clazz);
		}
	}

	private <C> void captureAndAssertInteraction(Comparable<?> key,
	                                             Class<? extends Interaction<C>> interactionClazz,
	                                             Supplier<C> unsafeCapture) {

		C initial = unsafeCapture.get();
		List<C> captures = Stream.generate(unsafeCapture)
				.limit(captureN - 1)
				.collect(toList());

		List<Entry<Boolean, C>> statuses = captures.stream()
				.map(c -> new SimpleImmutableEntry<>(c.equals(initial), c))
				.collect(toList());
		if (!statuses.stream().allMatch(Entry::getKey)) {
			String restOfTheCaptures = Streams.mapWithIndex(statuses.stream(),
					(e, index) -> format("[%s] Capture::%d: %s",
							e.getKey() ? "OK" : "FAIL", index + 1, e.getValue()))
					.collect(joining("\n"));

			List<Long> failedOnes = Streams.mapWithIndex(statuses.stream(),
					(e, index) -> Optional.of(index + 1).filter(i -> !e.getKey()))
					.flatMap(Streams::stream)
					.collect(toList());
			throw new AssertionError(format("After capturing for %d time(s), " +
							"capture [%s] was different from the first capture." +
							"\nFirst capture: \n%s" +
							"\nThe rest was : \n%s",
					captureN,
					failedOnes.stream().map(v -> "::" + v).collect(joining(",")),
					initial, restOfTheCaptures));
		}


		Interaction<?> polled = interactions.poll();
		if (polled == null) {
			switch (endMode) {
				case IGNORE:
					return;
				default:
				case NO_MORE:
					fail("No more interactions expected but got:\n%s::%s", key, initial);
			}
		} else {


			String item = String.format("%d/%d",
					interactionsProcessed - interactions.size(), totalInteractions);

			if (!interactionClazz.isAssignableFrom(polled.getClass())) {
				assertFailure(String.format("Was expecting the following interaction " +
								"(%s)" +
								":\n%s::%s" +
								"\n\nBut instead got:\n%s::%s",
						item, polled.key(), polled.describe(), key, initial),
						polled.origin(), callingClass);
			} else {

				Interaction<C> matched = interactionClazz.cast(polled);

				if (!Objects.equals(matched.key(), key)) {
					assertFailure(format("Error at interaction %s: " +
									"\nWas expecting subject of interaction to be %s but got %s" +
									"\nExpected:\n%s::%s" +
									"\nBut found:\n%s::%s",
							item, polled.key(), key,
							polled.key(), polled.describe(),
							key, initial), polled.origin(), callingClass);
				}
				if (printInteractions) System.out.printf("[%s] %s::%s%n", item, key, initial);

				matched.applyAssertion(initial, (message, stack) ->
					assertFailure(String.format("Error at interaction %s: " +
									"\nExpected result:" +
									"\n%s::%s" +
									"\n\nBut some parameters did not match, actual invocation " +
									"was:" +
									"\n%s::%s" +
									"\n\nSpecifically, the following assertion failed: %s",
							item, polled.key(), polled.describe(),
							key, initial, message), stack, callingClass));
				matched.executeAction(initial, callingClass);

				AssertionContext sra = (message, stack) ->
					assertFailure(String.format("Error after interaction %s: " +
									"\nThe following assertion failed: %s",
							item, message), stack, callingClass);

				if (interactions.isEmpty()) {
					assertEach(sra, finalReqs, game);
					finalContinuation.accept(game);
				}
			}
		}
	}

	private void assertMoreInteractions() {
		if (!interactions.isEmpty()) {
			assertFailure(format("The following interaction was expected but never " +
							"occurred:\n%s",
					interactions.stream().map(v -> format("%s::%s", v.key(), v.describe()))
							.collect(joining("\n"))),
					interactions.peek().origin(), callingClass);
		}
	}

	public PlayerConfiguration newPlayer(ImmutableScotlandYardView.ImmutablePlayer player) {
		return newPlayer(player.colour, player.location, player.tickets);
	}

	public PlayerConfiguration newPlayer(Colour colour, int start,
                                         int taxi, int bus, int underground, int x2, int secret) {
		return newPlayer(colour, start, ImmutableMap.of(
				Ticket.TAXI, taxi,
				Ticket.BUS, bus,
				Ticket.UNDERGROUND, underground,
				Ticket.DOUBLE, x2,
				Ticket.SECRET, secret));
	}

	public PlayerConfiguration newPlayer(Colour colour) {
		return newPlayer(colour, colour.isMrX() ? MRX_LOCATIONS.get(0) : DETECTIVE_LOCATIONS.get(0));
	}

	public PlayerConfiguration newPlayer(Colour colour, int start) {
		return newPlayer(colour, start, colour.isMrX() ? mrXTickets() : detectiveTickets());
	}

	public PlayerConfiguration newPlayer(Colour colour, int start, Map<Ticket, Integer> tickets) {
		return new PlayerConfiguration.Builder(colour)
				.at(start)
				.with(tickets)
				.using((view, location, moves, callback) -> captureAndAssertInteraction(
						mkPlayerKey(colour),
						MakeMoveInteraction.class,
						() -> new MakeMove(view, location, moves, callback)))
				.build();
	}

	public enum SpectatorEvent {
		ON_MOVE_MADE,
		ON_ROUND_STARTED,
		ON_ROTATION_COMPLETE,
		ON_GAME_OVER
	}

	static final String DEFAULT_SPECTATOR = "the only one";

	public Spectator createSpectator(SpectatorEvent... listens) {
		return createSpectator(DEFAULT_SPECTATOR, listens);
	}
	public Spectator createSpectator(String key, SpectatorEvent ... listens) {
		Set<SpectatorEvent> listenEvents =
				new HashSet<>(asList(listens.length == 0 ? SpectatorEvent.values() : listens));
		Comparable<?> k = mkSpectatorKey(key);
		return new Spectator() {
			@Override public void onMoveMade(ScotlandYardView view, Move move) {
				if (!listenEvents.contains(ON_MOVE_MADE)) return;
				captureAndAssertInteraction(k, MoveMadeInteraction.class,
						() -> new MoveMade(view, move));
			}
			@Override public void onRoundStarted(ScotlandYardView view, int round) {
				if (!listenEvents.contains(ON_ROUND_STARTED)) return;
				captureAndAssertInteraction(k, RoundStartedInteraction.class,
						() -> new RoundStarted(view, round));
			}
			@Override public void onRotationComplete(ScotlandYardView view) {
				if (!listenEvents.contains(ON_ROTATION_COMPLETE)) return;
				captureAndAssertInteraction(k, RotationCompleteInteraction.class,
						() -> new RotationComplete(view));
			}
			@Override public void onGameOver(ScotlandYardView view, Set<Colour> winningPlayers) {
				if (!listenEvents.contains(ON_GAME_OVER)) return;
				captureAndAssertInteraction(k, GameOverInteraction.class,
						() -> new GameOver(view, winningPlayers));
			}
		};
	}

	interface AssertionContext {
		void fail(String message, List<StackTraceElement> stack);
	}

}
