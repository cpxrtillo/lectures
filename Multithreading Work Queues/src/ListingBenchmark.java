import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Attempts to benchmark the different multithreading approaches. Run multiple
 * times to see whether the execution order impacts runtimes.
 */
public class ListingBenchmark {

	/** Number of warmup rounds. */
	public static final int WARMUP_ROUNDS = 10;

	/** Number of timed rounds. */
	public static final int TIMED_ROUNDS = 20;

	/*
	 * This is really meant to demonstrate just how bad we can make the runtime
	 * with a poor multithreading implementation, and the difference using a
	 * work queue can make on the runtime. However, even with the work queue, it
	 * might be slower than single threading for this particular problem since it
	 * has so many write operations versus read operations.
	 */

	/**
	 * Runs the benchmarks in random order.
	 *
	 * @param args unused
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Change this to a large directory on your system!
		Path test = Path.of("..", "..");
		Set<Path> expected = SerialDirectoryListing.list(test);

		// collect benchmarks in a list and shuffle to avoid order effects
		List<Benchmarker> benchmarks = new ArrayList<>();

		benchmarks.add(new Benchmarker("Serial") {
			@Override
			public Set<Path> run(Path path) throws IOException {
				return SerialDirectoryListing.list(test);
			}
		});

		benchmarks.add(new Benchmarker("Slow") {
			@Override
			public Set<Path> run(Path path) throws InterruptedException {
				return SlowMultithreadedDirectoryListing.list(test);
			}
		});

		benchmarks.add(new Benchmarker("Multi") {
			@Override
			public Set<Path> run(Path path) throws InterruptedException {
				return MultithreadedDirectoryListing.list(test);
			}
		});

		benchmarks.add(new Benchmarker("Queue") {
			@Override
			public Set<Path> run(Path path) throws InterruptedException {
				return WorkQueueDirectoryListing.list(test);
			}
		});

		benchmarks.add(new Benchmarker("Executor") {
			@Override
			public Set<Path> run(Path path) throws InterruptedException {
				return ExecutorDirectoryListing.list(test);
			}
		});

		Collections.shuffle(benchmarks);

		for (Benchmarker current : benchmarks) {
			current.benchmark(test, expected);
		}

		Collections.sort(benchmarks);

		String order = benchmarks.stream()
				.map(benchmark -> benchmark.name)
				.collect(Collectors.joining(" > "));

		System.out.println();
		System.out.println(order);
	}

	/**
	 * A class to benchmark directory listings.
	 */
	private static abstract class Benchmarker implements Comparable<Benchmarker> {
		/** Name of the benchmark. */
		public String name;

		/** Calculated average runtime. */
		public double average;

		/**
		 * Initializes this benchmark.
		 *
		 * @param name the name of the benchmark
		 */
		public Benchmarker(String name) {
			this.name = name;
			this.average = 0;
		}

		/**
		 * Generates a directory listing.
		 *
		 * @param directory the directory to start with
		 * @return set of paths found within the directory
		 * @throws IOException
		 * @throws InterruptedException
		 */
		public abstract Set<Path> run(Path directory) throws IOException, InterruptedException;

		/**
		 * Conducts the benchmark.
		 *
		 * @param directory the directory to start with
		 * @param expected the expected results
		 * @throws InterruptedException
		 * @throws IOException
		 */
		public void benchmark(Path directory, Set<Path> expected) throws InterruptedException, IOException {
			System.out.print(String.format("%9s: ", name));

			Set<Path> actual = run(directory);

			// verify results first
			if (!actual.equals(expected)) {
				System.err.printf("Unexpected results! Expected %d elements, found %d elements.",
						expected.size(), actual.size());
			}

			// warmup
			for (int i = 0; i < WARMUP_ROUNDS; i++) {
				actual.addAll(run(directory));
			}

			// timed
			Instant start = Instant.now();
			for (int i = 0; i < TIMED_ROUNDS; i++) {
				actual.addAll(run(directory));
			}
			Instant end = Instant.now();

			// averaged result
			Duration elapsed = Duration.between(start, end);
			average = (double) elapsed.toMillis() / TIMED_ROUNDS;
			System.out.printf("%8.2fms%n", average);
		}

		@Override
		public int compareTo(Benchmarker other) {
			return Double.compare(this.average, other.average);
		}
	}
}
