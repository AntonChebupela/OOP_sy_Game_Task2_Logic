package game.SY.simple;



import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import game.SY.model.Colour;
import game.SY.model.PassMove;
import game.SY.model.Player;
import game.SY.model.PlayerConfiguration;
import game.SY.model.Ticket;
import game.SY.model.TicketMove;


import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;



public class TestGames {

	public static final Set<Integer> DEFAULT_REVEAL = unmodifiableSet(
			new HashSet<>(asList(3, 8, 13, 18, 23)));

	public static final List<Integer> DETECTIVE_LOCATIONS = unmodifiableList(
			asList(26, 29, 50, 53, 91, 94, 103, 112, 117, 123, 138, 141, 155, 174));

	public static final List<Integer> MRX_LOCATIONS = unmodifiableList(
			asList(35, 45, 51, 71, 78, 104, 106, 127, 132, 166, 170, 172));

	public static Map<Ticket, Integer> detectiveTickets() {
		return makeTickets(11, 8, 4, 0, 0);
	}

	public static Map<Ticket, Integer> mrXTickets() {
		return makeTickets(4, 3, 3, 2, 5);
	}



	public static Map<Ticket, Integer> makeTickets(int taxi, int bus, int underground, int x2,
												   int secret) {
		Map<Ticket, Integer> map = new HashMap<>();
		map.put(Ticket.TAXI, taxi);
		map.put(Ticket.BUS, bus);
		map.put(Ticket.UNDERGROUND, underground);
		map.put(Ticket.DOUBLE, x2);
		map.put(Ticket.SECRET, secret);
		return map;
	}



	public static List<Boolean> ofRounds(int rounds, Collection<Integer> reveal) {
		return IntStream.rangeClosed(1, rounds).mapToObj(reveal::contains).collect(toList());
	}



	public static TicketMove taxi(Colour colour, int destination) {
		return new TicketMove(requireNonNull(colour), Ticket.TAXI, destination);
	}

	public static AnonymousMoves.AnonymousTicketMove secret(int destination) {
		return new AnonymousMoves.AnonymousTicketMove(Ticket.SECRET, destination);
	}

	public static TicketMove secret(Colour colour, int destination) {
		return new TicketMove(requireNonNull(colour), Ticket.SECRET, destination);
	}

	public static AnonymousMoves.AnonymousTicketMove taxi(int destination) {
		return new AnonymousMoves.AnonymousTicketMove(Ticket.TAXI, destination);
	}

	public static TicketMove bus(Colour colour, int destination) {
		return new TicketMove(requireNonNull(colour), Ticket.BUS, destination);
	}

	public static AnonymousMoves.AnonymousTicketMove bus(int destination) {
		return new AnonymousMoves.AnonymousTicketMove(Ticket.BUS, destination);
	}

	public static TicketMove underground(Colour colour, int destination) {
		return new TicketMove(requireNonNull(colour), Ticket.UNDERGROUND, destination);
	}

	public static AnonymousMoves.AnonymousTicketMove underground(int destination) {
		return new AnonymousMoves.AnonymousTicketMove(Ticket.UNDERGROUND, destination);
	}

	public static PassMove pass(Colour colour) {
		return new PassMove(colour);
	}

	public static AnonymousMoves.AnonymousPassMove pass() {
		return new AnonymousMoves.AnonymousPassMove();
	}



	public static PlayerConfiguration doNothingPlayer(Colour colour, int location) {
		return new PlayerConfiguration.Builder(colour)
				.at(location)
				.with(colour.isDetective() ? detectiveTickets() : mrXTickets())
				.using(dummyPlayer()).build();
	}

	public static Player dummyPlayer() {
		return (view, location, moves, callback) -> {
			// do nothing
		};
	}


	public static PlayerConfiguration doNothingMrX() {
		return doNothingPlayer(Colour.BLACK, MRX_LOCATIONS.get(0));
	}

	public static PlayerConfiguration doNothingRed() {
		return doNothingPlayer(Colour.RED, DETECTIVE_LOCATIONS.get(0));
	}

	public static PlayerConfiguration doNothingGreen() {
		return doNothingPlayer(Colour.GREEN, DETECTIVE_LOCATIONS.get(1));
	}

	public static PlayerConfiguration doNothingBlue() {
		return doNothingPlayer(Colour.BLUE, DETECTIVE_LOCATIONS.get(2));
	}

	public static PlayerConfiguration doNothingYellow() {
		return doNothingPlayer(Colour.YELLOW, DETECTIVE_LOCATIONS.get(3));
	}

	public static PlayerConfiguration doNothingWhite() {
		return doNothingPlayer(Colour.WHITE, DETECTIVE_LOCATIONS.get(4));
	}


}
