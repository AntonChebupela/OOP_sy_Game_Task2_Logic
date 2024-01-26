package game.GameKT.graph;

import java.io.Serializable;
import java.util.Objects;
// ребро по узлам и данным
public class Edge<N, D> implements Serializable {

	private static final long serialVersionUID = -6248988489116039832L;
	private final Node<N> source;
	private final Node<N> destination;
	private final D data;

	public Edge(Node<N> source, Node<N> destination, D data) {
		this.source = source;
		this.destination = destination;
		this.data = data;
	}


	public Node<N> source() {
		return source;
	}


	public Node<N> destination() {
		return destination;
	}

	public D data() {
		return data;
	}


	public Edge<N, D> swap() {
		return new Edge<>(destination, source, data);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Edge<?, ?> edge = (Edge<?, ?>) o;
		return Objects.equals(source, edge.source) && Objects.equals(destination, edge.destination)
				&& Objects.equals(data, edge.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, destination, data);
	}

	@Override
	public String toString() {
		return "Edge{" + source + "--(" + data + ")-->" + destination + "}";
	}
}
