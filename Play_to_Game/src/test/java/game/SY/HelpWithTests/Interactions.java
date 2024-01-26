package game.SY.HelpWithTests;

import java.util.List;

class Interactions {
	private Interactions() {}

	static <T> void assertEach(TestOut.AssertionContext sra, List<Requirement<T>> rs, T t) {
		rs.forEach(v -> v.check(sra, t));
	}

}
