package game.SY.simple;

import game.SY.model.Colour;
import game.SY.model.Player;
import game.SY.model.PlayerConfiguration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static game.SY.simple.TestGames.mrXTickets;


public class PlayerConfigurationTest {


	@Test
	public void testProducesCorrectOutput() {
		Player player = (v, l, moves, callback) -> callback.accept(moves.iterator().next());
		PlayerConfiguration configuration = new PlayerConfiguration.Builder(Colour.BLACK).using(player)
				.with(mrXTickets()).at(10).build();
		assertThat(configuration.colour).isEqualTo(Colour.BLACK);
		assertThat(configuration.player).isSameAs(player);
		assertThat(configuration.tickets).isEqualTo(mrXTickets());
		assertThat(configuration.location).isEqualTo(10);
	}

	@Test
	public void testNullColourThrows() {
		assertThatThrownBy(() -> new PlayerConfiguration.Builder(null))
				.isInstanceOf(NullPointerException.class);
	}


	@Test
	public void testMissingTicketThrows() {
		Player player = (v, l, moves, callback) -> callback.accept(moves.iterator().next());
		assertThatThrownBy(() -> new PlayerConfiguration.Builder(Colour.BLACK).using(player).build())
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testNullPlayerThrows() {
		assertThatThrownBy(() -> new PlayerConfiguration.Builder(Colour.BLACK).using(null).with(mrXTickets()).build())
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testNullTicketThrows() {
		Player player = (v, l, moves, callback) -> callback.accept(moves.iterator().next());
		assertThatThrownBy(() -> new PlayerConfiguration.Builder(Colour.BLACK).using(player).with(null).build())
				.isInstanceOf(NullPointerException.class);
	}

}
