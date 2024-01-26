package game.SY.model;

import java.util.Map;
import java.util.Objects;


public class PlayerConfiguration {


	public final Colour colour;


	public final Player player;


	public final Map<Ticket, Integer> tickets;


	public final int location;

	private PlayerConfiguration(Colour colour, Player player, Map<Ticket, Integer> tickets,
			int location) {
		this.colour = colour;
		this.player = player;
		this.tickets = tickets;
		this.location = location;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PlayerConfiguration{");
		sb.append("colour=").append(colour);
		sb.append(", player=").append(player);
		sb.append(", tickets=").append(tickets);
		sb.append(", location=").append(location);
		sb.append('}');
		return sb.toString();
	}


	public static class Builder {
		private final Colour colour;
		private Player player;
		private Map<Ticket, Integer> tickets;
		private int location;


		public Builder(Colour colour) {
			this.colour = Objects.requireNonNull(colour);
		}


		public Builder using(Player player) {
			this.player = Objects.requireNonNull(player);
			return this;
		}


		public Builder with(Map<Ticket, Integer> tickets) {
			this.tickets = Objects.requireNonNull(tickets);
			return this;
		}


		public Builder at(int location) {
			this.location = location;
			return this;
		}


		public PlayerConfiguration build() {
			return new PlayerConfiguration(colour, Objects.requireNonNull(player),
					Objects.requireNonNull(tickets), location);
		}

	}

}
