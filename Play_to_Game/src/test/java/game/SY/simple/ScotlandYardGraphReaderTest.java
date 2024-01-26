package game.SY.simple;

import game.GameKT.graph.ImmutableGraph;
import game.SY.model.ScotlandYardGraphReader;
import game.SY.model.Transport;
import org.junit.Test;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScotlandYardGraphReaderTest {

	@Test
	public void testValidFile() {
		ImmutableGraph<Integer, Transport> graph =
				ScotlandYardGraphReader.fromLines(asList("3 1", "1", "2", "3", "1 2 Ferry"));
		assertThat(graph.getNodes()).hasSize(3);
		assertThat(graph.getEdges()).hasSize(2);
		assertThat(graph.getNode(1)).isNotNull();
		assertThat(graph.getEdges().iterator().next().data())
				.isEqualByComparingTo(Transport.FERRY);
	}

	@Test
	public void testEmptyInputShouldThrow() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(emptyList()));
	}

	@Test
	public void testBadFirstLine() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(singletonList("Foo Bar Baz")));
	}

	@Test
	public void testBadNodeCount() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(asList("4 1", "1", "2", "3", "1 2 Ferry")));
	}

	@Test
	public void testBadEdgeCount() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(asList("3 5", "1", "2", "3", "1 2 Ferry")));
	}

	@Test
	public void testBadNode() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(asList("1 0", "Foo")));
	}

	@Test
	public void testBadEdge() {
		assertThatThrownBy(() -> ScotlandYardGraphReader.fromLines(asList("2 1", "1", "2", "Foo Bar Baz")));
	}

}