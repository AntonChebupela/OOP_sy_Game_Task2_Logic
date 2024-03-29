package game.SY.HelpWithTests;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import game.SY.model.Colour;
import game.SY.model.Move;
import game.SY.model.PlayerConfiguration;
import game.SY.model.PlayerConfiguration.Builder;
import game.SY.model.ScotlandYardView;
import game.SY.model.Spectator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public final class CodeGenRecorder {


	public interface CodeGen {
		String name();
		void movePicked(ImmutableScotlandYardView seed,
		                ScotlandYardView view, int location, Set<Move> moves, Move picked);
		Spectator mkSpectator(ImmutableScotlandYardView seed);
		String readOut(ImmutableScotlandYardView seed,
		               ImmutableMap<Colour, PlayerConfiguration> configs,
		               String graphMethod);
	}

	private ImmutableScotlandYardView seed;
	private final Map<Colour, PlayerConfiguration> configs = new HashMap<>();
	private final List<CodeGen> codeGens;

	public CodeGenRecorder(List<CodeGen> codeGens) {this.codeGens = codeGens;}

	public final void snap(ScotlandYardView view) {
		seed = ImmutableScotlandYardView.snapshot(view);
		if (!configs.keySet().equals(seed.players.stream().map(v -> v.colour).collect(toSet()))) {
			throw new IllegalArgumentException("Not all created player(s) were added, created:" +
					configs + " but game has " + configs);
		}
	}

	public final Map<CodeGen, String> readOut(String graphMethod) {
		return codeGens.stream().collect(toMap(
				Function.identity(),
				cg -> cg.readOut(seed, ImmutableMap.copyOf(configs), graphMethod)));
	}

	public final PlayerConfiguration observePlayer(PlayerConfiguration that) {
		PlayerConfiguration configuration = new Builder(that.colour)
				.at(that.location)
				.with(that.tickets)
				.using((view, location, moves, callback) ->
						that.player.makeMove(view, location, moves, (Move picked) -> {
							codeGens.forEach(v -> v.movePicked(seed, view, location, moves,
									picked));
							callback.accept(picked);
						})).build();
		configs.put(that.colour, configuration);
		return configuration;
	}

	public final Spectator createSpectator() {
		List<Spectator> spectators = codeGens.stream()
				.map(v -> v.mkSpectator(seed))
				.collect(toList());
		return new Spectator() {
			@Override public void onMoveMade(ScotlandYardView view, Move move) {
				spectators.forEach(s -> s.onMoveMade(view, move));
			}
			@Override public void onRoundStarted(ScotlandYardView view, int round) {
				spectators.forEach(s -> s.onRoundStarted(view, round));
			}
			@Override public void onRotationComplete(ScotlandYardView view) {
				spectators.forEach(s -> s.onRotationComplete(view));
			}
			@Override public void onGameOver(ScotlandYardView view, Set<Colour> winningPlayers) {
				spectators.forEach(s -> s.onGameOver(view, winningPlayers));
			}
		};
	}


}
