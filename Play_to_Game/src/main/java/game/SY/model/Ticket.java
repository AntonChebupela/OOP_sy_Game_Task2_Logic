package game.SY.model;

import java.util.Objects;


public enum Ticket {
	TAXI, BUS, UNDERGROUND, DOUBLE, SECRET;


	public static Ticket fromTransport(Transport transport) {
		switch (Objects.requireNonNull(transport)) {
			case TAXI:
				return TAXI;
			case BUS:
				return BUS;
			case UNDERGROUND:
				return UNDERGROUND;
			case FERRY:
				return SECRET;
			default:
				return TAXI;
		}
	}

}
