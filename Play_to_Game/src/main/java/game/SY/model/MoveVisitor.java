package game.SY.model;


public interface MoveVisitor {


	default void visit(PassMove move) {}

	default void visit(TicketMove move) {}


	default void visit(DoubleMove move) {}

}
