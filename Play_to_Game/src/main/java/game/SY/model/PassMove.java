package game.SY.model;

import java.util.Objects;


public class PassMove extends Move {

	private static final long serialVersionUID = 3547117852693198139L;

	public PassMove(Colour colour) {
		super(colour);
	}

	@Override
	public void visit(MoveVisitor visitor) {
		Objects.requireNonNull(visitor).visit(this);
	}

	@Override
	public String toString() {
		return "Pass[" + colour() + "]";
	}



}
