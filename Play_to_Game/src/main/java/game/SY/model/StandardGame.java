package game.SY.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import game.GameKT.graph.ImmutableGraph;

import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;



public class StandardGame {

	private StandardGame() {}


	public static final Set<Integer> REVEAL_ROUND = Collections
			.unmodifiableSet(new HashSet<>(asList(3, 8, 13, 18, 24)));


	public static final List<Integer> DETECTIVE_LOCATIONS = unmodifiableList(
			asList(26, 29, 50, 53, 91, 94, 103, 112, 117, 123, 138, 141, 155, 174));


	public static final List<Integer> MRX_LOCATIONS = unmodifiableList(
			asList(35, 45, 51, 71, 78, 104, 106, 127, 132, 166, 170, 172));


	public static final EnumSet<Ticket> DETECTIVE_TICKETS = EnumSet.of(
			Ticket.TAXI,
			Ticket.BUS,
			Ticket.UNDERGROUND);


	public static final EnumSet<Ticket> MRX_TICKETS = EnumSet.allOf(Ticket.class);

	public static final List<Boolean> ROUNDS = IntStream.rangeClosed(1, 24)
			.mapToObj(REVEAL_ROUND::contains)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));


	public static List<Integer> generateDetectiveLocations(int seed, int n) {
		if (n > DETECTIVE_LOCATIONS.size())
			throw new IllegalArgumentException("n > max detective locations");
		List<Integer> locations = IntStream.range(0, DETECTIVE_LOCATIONS.size()).boxed()
				.collect(toList());
		shuffle(locations, new Random(seed));
		return locations.stream().limit(n).map(DETECTIVE_LOCATIONS::get).collect(toList());
	}


	public static int generateMrXLocation(int seed) {
		return MRX_LOCATIONS.get(new Random(seed).nextInt(MRX_LOCATIONS.size()));
	}


	public static Map<Ticket, Integer> generateMrXTickets() {
		Map<Ticket, Integer> map = new HashMap<>();
		map.put(Ticket.TAXI, 4);
		map.put(Ticket.BUS, 3);
		map.put(Ticket.UNDERGROUND, 3);
		map.put(Ticket.SECRET, 5);
		map.put(Ticket.DOUBLE, 2);
		return map;
	}


	public static Map<Ticket, Integer> generateDetectiveTickets() {
		Map<Ticket, Integer> map = new HashMap<>();
		map.put(Ticket.UNDERGROUND, 4);
		map.put(Ticket.BUS, 8);
		map.put(Ticket.TAXI, 11);
		map.put(Ticket.SECRET, 0);
		map.put(Ticket.DOUBLE, 0);
		return map;
	}

	public static InputStream pngMapAsStream() {
		return StandardGame.class.getResourceAsStream("map_large.png");
	}

	public static final int MAP_OFFSET = 60;

	public static Map<Integer, Entry<Integer, Integer>> pngMapPositionEntries() throws IOException {
		List<String> lines = readString("pos.txt");
		Map<Integer, Entry<Integer, Integer>> map = new HashMap<>();
		for (String line : lines) {
			Integer[] values = Stream.of(line.split("\\s+")).map(Integer::parseInt)
					.toArray(Integer[]::new);
			if (values.length != 3) continue;
			map.put(values[0],
					new SimpleImmutableEntry<>(
							values[1] + MAP_OFFSET,
							values[2] + MAP_OFFSET));
		}
		return Collections.unmodifiableMap(map);
	}

	public static ImmutableGraph<Integer, Transport> standardGraph() throws IOException {
		return ScotlandYardGraphReader.fromLines(readString("graph.txt"));
	}

	private static List<String> readString(String resource) throws IOException {
		try (InputStream stream = StandardGame.class
				.getClassLoader()
				.getResourceAsStream(resource)) {
			if (stream == null) throw new IOException("Resource " + resource + " not found");
			return new BufferedReader(
					new InputStreamReader(stream, StandardCharsets.UTF_8))
							.lines()
							.collect(toList());
		}
	}

}
