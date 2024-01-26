package game.SY.model;

import java.util.Objects;


public class DoubleMove extends Move {

	private static final long serialVersionUID = 8857602351332595005L;
	private final TicketMove firstMove;
	private final TicketMove secondMove;


	public DoubleMove(Colour player, TicketMove firstMove, TicketMove secondMove) {
		super(player);
		this.firstMove = firstMove;
		this.secondMove = secondMove;
	}


	public DoubleMove(Colour player, Ticket first, int firstDestination, Ticket second,
			int secondDestination) {
		super(player);
		this.firstMove = new TicketMove(player, first, firstDestination);
		this.secondMove = new TicketMove(player, second, secondDestination);
	}

	public TicketMove firstMove() {
		return firstMove;
	}

	public TicketMove secondMove() {
		return secondMove;
	}


	public int finalDestination() {
		return secondMove.destination();
	}


	public boolean hasSameTicket() {
		return firstMove.ticket() == secondMove.ticket();
	}

	@Override
	public void visit(MoveVisitor visitor) {
		Objects.requireNonNull(visitor).visit(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		DoubleMove that = (DoubleMove) o;
		return Objects.equals(firstMove, that.firstMove)
				&& Objects.equals(secondMove, that.secondMove);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), firstMove, secondMove);
	}

	@Override
	public String toString() {
		return "Double[" + colour() + "-(" + firstMove.ticket() + ")->" + firstMove.destination()
				+ "-(" + secondMove.ticket() + ")->" + secondMove.destination() + "]";
	}

}
