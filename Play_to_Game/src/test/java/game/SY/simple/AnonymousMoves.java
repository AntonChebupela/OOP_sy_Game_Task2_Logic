package game.SY.simple;

import game.SY.model.Colour;
import game.SY.model.DoubleMove;
import game.SY.model.Move;
import game.SY.model.PassMove;
import game.SY.model.Ticket;
import game.SY.model.TicketMove;

public class AnonymousMoves {

	private AnonymousMoves(){}


	public abstract static class AnonymousMove {
		public abstract Move colour(Colour colour);
	}
	public static class AnonymousPassMove extends AnonymousMove {
		@Override public PassMove colour(Colour colour) {
			return new PassMove(colour);
		}
	}
	public static class AnonymousTicketMove extends AnonymousMove {
		private final Ticket ticket;
		private final int destination;

		AnonymousTicketMove(Ticket ticket, int destination) {
			this.ticket = ticket;
			this.destination = destination;
		}

		@Override public TicketMove colour(Colour colour) {
			return new TicketMove(colour, ticket, destination);
		}
	}
	public static class AnonymousDoubleMove extends AnonymousMove {
		private final Ticket first;
		private final int firstDestination;
		private final Ticket second;
		private final int secondDestination;

		AnonymousDoubleMove(Ticket first, int firstDestination,
		                    Ticket second, int secondDestination) {
			this.first = first;
			this.firstDestination = firstDestination;
			this.second = second;
			this.secondDestination = secondDestination;
		}

		AnonymousDoubleMove(AnonymousTicketMove first, AnonymousTicketMove second) {
			this.first = first.ticket;
			this.firstDestination = first.destination;
			this.second = second.ticket;
			this.secondDestination = second.destination;
		}

		@Override public DoubleMove colour(Colour colour) {
			return new DoubleMove(colour, first, firstDestination, second, secondDestination);
		}
	}
}
