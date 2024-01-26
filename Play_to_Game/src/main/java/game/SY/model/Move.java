package game.SY.model;

import java.io.Serializable;
import java.util.Objects;


public abstract class Move implements Serializable {

	private static final long serialVersionUID = 5298684730389684751L;
	private final Colour colour;

	protected Move(Colour colour) {
		this.colour = colour;
	}


	public Colour colour() {
		return colour;
	}


	public abstract void visit(MoveVisitor visitor);

	@Override
	public String toString() {
		return this.colour.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return colour == move.colour;
	}

	@Override
	public int hashCode() {
		return Objects.hash(colour);
	}
}
