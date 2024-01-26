package game.GameKT.graph;

import java.util.Collection;
import java.util.List;


public interface Graph<V, D> {

	// пустое но не null
	void addNode(Node<V> node);


	void addEdge(Edge<V, D> edge);



	Node<V> getNode(V value);


	boolean containsNode(V value);


	List<Node<V>> getNodes();


	Collection<Edge<V, D>> getEdges();

	// для рисования
	Collection<Edge<V, D>> getEdgesFrom(Node<V> source);


	Collection<Edge<V, D>> getEdgesTo(Node<V> destination);


	boolean isEmpty();


	int size();

}
