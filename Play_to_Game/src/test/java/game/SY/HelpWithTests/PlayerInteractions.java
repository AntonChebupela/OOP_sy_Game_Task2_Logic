package game.SY.HelpWithTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import game.SY.simple.AnonymousMoves;
import game.SY.model.Colour;
import game.SY.model.Move;
import game.SY.model.ScotlandYardView;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.format;
import static game.SY.HelpWithTests.Assertions.capture;

public final class PlayerInteractions {

	private PlayerInteractions() {}

	static Comparable<?> mkPlayerKey(Colour colour) {
		return format("Player(%s)", colour);
	}



	public static PlayerMethods player(Colour colour) {
		List<StackTraceElement> stack = capture();
		return () -> new Action() {
			private List<Requirement<ScotlandYardView>> viewReqs = new ArrayList<>();
			private List<Requirement<Integer>> locationReqs = new ArrayList<>();
			private List<Requirement<Set<Move>>> movesReqs = new ArrayList<>();
			private List<Requirement<Consumer<Move>>> callbackReqs = new ArrayList<>();
			private Move move = null;
			private boolean pickFirstMove = true;
			private boolean respond = true;
			@Override public Action givenGameState(Requirement<ScotlandYardView> req) {
				viewReqs.add(req);
				return this;
			}
			@Override public Action givenLocation(Requirement<Integer> req) {
				locationReqs.add(req);
				return this;
			}
			@Override public Action givenMoves(Requirement<Set<Move>> req) {
				movesReqs.add(req);
				return this;
			}
			@Override public Action givenCallback(Requirement<Consumer<Move>> req) {
				callbackReqs.add(req);
				return this;
			}
			@Override public MakeMoveInteraction willPick(Move move) {

				this.respond = true;
				this.pickFirstMove = false;
				this.move = move;
				return this;
			}
			@Override public MakeMoveInteraction willPick(AnonymousMoves.AnonymousMove move) {
				return willPick(move.colour(colour));
			}
			@Override public MakeMoveInteraction willPickFirst() {
				this.respond = true;
				this.pickFirstMove = true;
				return this;
			}
			@Override public MakeMoveInteraction wontRespond() {
				this.respond = false;
				return this;
			}
			@Override public Comparable<?> key() {
				return mkPlayerKey(colour);
			}
			@Override
			public void applyAssertion(Captures.Player.MakeMove capture, TestOut.AssertionContext ctx) {
				Interactions.assertEach(ctx, viewReqs, capture.view);
				Interactions.assertEach(ctx, locationReqs, capture.location);
				Interactions.assertEach(ctx, movesReqs, capture.moves);
				Interactions.assertEach(ctx, callbackReqs, capture.callback);
			}
			@Override public void executeAction(Captures.Player.MakeMove capture, String callingClass) {
				if (!respond) return;
				if (pickFirstMove) capture.callback.accept(capture.moves.iterator().next());
				else capture.callback.accept(move);

			}
			@Override public String describe() {
				return TestOut.Interaction.describeMethod("makeMove", of(
						"view", viewReqs,
						"location", locationReqs,
						"moves", movesReqs,
						"callback", callbackReqs));
			}
			@Override public List<StackTraceElement> origin() { return stack; }
		};
	}

	interface MakeMoveInteraction extends TestOut.Interaction<Captures.Player.MakeMove> {}

	public interface PlayerMethods {
		Action makeMove();
	}

	public interface Action extends MakeMoveInteraction {
		Action givenGameState(Requirement<ScotlandYardView> req);
		Action givenLocation(Requirement<Integer> req);
		Action givenMoves(Requirement<Set<Move>> req);
		Action givenCallback(Requirement<Consumer<Move>> req);
		MakeMoveInteraction willPick(Move move);
		MakeMoveInteraction willPickFirst();
		MakeMoveInteraction willPick(AnonymousMoves.AnonymousMove move);
		MakeMoveInteraction wontRespond();
	}

}
