package game.SY.model;

import java.util.HashMap;
import java.util.Map;


public class ScotlandYardPlayer {

	private final Player player;
	private final Colour colour;
	private int location;
	private final Map<Ticket, Integer> tickets;


	public ScotlandYardPlayer(Player player, Colour colour, int location,
			Map<Ticket, Integer> tickets) {
		this.player = player;
		this.colour = colour;
		this.location = location;
		this.tickets = new HashMap<>(tickets);
	}


	public Player player() {
		return player;
	}

	public Colour colour() {
		return colour;
	}


	public boolean isMrX() {
		return colour.isMrX();
	}


	public boolean isDetective() {
		return colour.isDetective();
	}

	public void location(int location) {
		this.location = location;
	}


	public int location() {
		return location;
	}


	public Map<Ticket, Integer> tickets() {
		return tickets;
	}


	public void addTicket(Ticket ticket) {
		adjustTicketCount(ticket, 1);
	}


	public void removeTicket(Ticket ticket) {
		adjustTicketCount(ticket, -1);
	}

	private void adjustTicketCount(Ticket ticket, int by) {
		Integer ticketCount = tickets.get(ticket);
		ticketCount += by;
		tickets.remove(ticket);
		tickets.put(ticket, ticketCount);
	}

	public boolean hasTickets(Ticket ticket) {
		return tickets.get(ticket) != 0;
	}

	
	public boolean hasTickets(Ticket ticket, int quantityInclusive) {
		return tickets.get(ticket) >= quantityInclusive;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ScotlandYardPlayer{");
		sb.append("player=").append(player);
		sb.append(", colour=").append(colour);
		sb.append(", location=").append(location);
		sb.append(", tickets=").append(tickets);
		sb.append('}');
		return sb.toString();
	}
}
