/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Stopwatch;
import java.io.File;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static java.util.function.Function.identity;
import java.util.function.Supplier;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.lang.ArrayUtils;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class BenchmarkCommandRunner extends AbstractCommandRunner {

	private boolean fail = false;

	public BenchmarkCommandRunner() {
		super("benchmark", "run a benchmark test to validate host performance");
	}

	@Override
	protected Options buildOptions() {
		Options options = super.buildOptions();
		options.addOption("m", true, "benchmark iterations (default to 1)");
		options.addOption("f", "fail if one test is in error");
		return options;
	}

	@Override
	protected void exec(CommandLine cmd) throws Exception {
		System.out.printf("\nPerforming benchmark test of this host (%s):\n\n", getHostname());

		int iterations;
		if (cmd.hasOption("m")) {
			iterations = Integer.valueOf(cmd.getOptionValue("m"));
			checkArgument(iterations > 0 && iterations < 100);
		} else {
			iterations = 1;
		}
		if (cmd.hasOption("f")) {
			fail = true;
		}

		List<Double> scores = list();

		System.out.printf("            test                 result       score\n");
		map(
				"memory test", (Supplier) this::runMemoryTest,
				"cpu test", (Supplier) this::runCpuTest,
				"disk test", (Supplier) this::runDiskReadWriteTest
		).forEach((desc, test) -> {
			try {
				System.out.printf(" %-24s   ", desc + " ...");
				long value = 0;
				for (int i = 0; i < iterations; i++) {
					value += ((Supplier<Long>) test).get();
					System.gc();
				}
				long reference = 5000 * iterations;
				double score;
				if (value < reference) {
					score = sqrt(reference) / sqrt(value);
				} else {
					score = reference / (double) value;
				};
				scores.add(score);
				System.out.printf("     %6s        %3s\n", value, round(score * 100));
			} catch (Error ex) {
				if (fail) {
					throw runtime(ex);
				} else {
					logger.debug("test error for test = {}", desc, ex);
					System.out.printf("         error : %s\n", ex.toString());
					scores.add(0d);
				}
			}
		});
		double average = scores.stream().collect(averagingDouble(n -> n));
		System.out.printf("\n  your average system score is %s, which is %s\n", round(average * 100), average < 1 ? "not good (a score of 100 or more is recommended to ensure good system performance)" : "good");
		if (scores.stream().anyMatch(i -> i < 1)) {
			System.out.printf("  some of your scores are below the recommended minimum score of 100\n");
		}
		System.out.printf("  to improve test results, you should stop all applications while you're running this test utility.\n");
	}

	private long runMemoryTest() {
		int n = 300;

		byte[] data = new byte[1024 * 1024];
		new Random().nextBytes(data);
		List<byte[]> list = IntStream.range(0, 4000).mapToObj((i) -> new byte[1024 * 1024]).collect(toList());

		Stopwatch stopwatch = Stopwatch.createStarted();

		for (int i = 0; i < n; i++) {
			int count = list.size() / 50;
			Collections.shuffle(list);
			list.stream().limit(count).forEach((target) -> {
				System.arraycopy(data, 0, target, 0, data.length);
			});
			Collections.shuffle(list);
			list.stream().limit(count).forEach((source) -> {
				System.arraycopy(source, 0, data, 0, data.length);
			});
		}

		return stopwatch.elapsed(TimeUnit.MILLISECONDS);
	}

	private long runCpuTest() {
		int n = 4000;

		int threadCount = 4;
		List<byte[]> data = IntStream.range(0, threadCount).mapToObj(i -> {
			byte[] d = new byte[1024];
			new Random().nextBytes(d);
			return d;
		}).collect(toList());

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		Stopwatch stopwatch = Stopwatch.createStarted();

		List<Future> futures = list();

		for (int t = 0; t < threadCount; t++) {

			byte[] d = data.get(t);

			Future future = executorService.submit(() -> {

				for (int i = 0; i < n; i++) {

					for (int j = 0; j < d.length; j++) {
						double x = d[0] + ((double) Integer.MAX_VALUE);
						double y = d[1] + ((double) Integer.MAX_VALUE);
						double w = 1;
						for (int k = 0; k < 100000; k++) {
							double z = (y + k) / x;
							w = 1 + (z / (k + 1));
						}
						checkArgument(w > 0);
					}

					for (int j = 0; j < 10; j++) {
						List<Byte> list = Arrays.asList(ArrayUtils.toObject(d));
						Collections.sort(list);
					}

				}
			});
			futures.add(future);
		}

		futures.forEach((f) -> {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException ex) {
				throw runtime(ex);
			}
		});
		executorService.shutdown();
		try {
			checkArgument(executorService.awaitTermination(1, TimeUnit.MINUTES));
		} catch (InterruptedException ex) {
			throw runtime(ex);
		}

		return stopwatch.elapsed(TimeUnit.MILLISECONDS);

	}

	private long runDiskReadWriteTest() {
		int n = 10;

		dropSystemCache();

		Stopwatch stopwatch = Stopwatch.createStarted();

		File dir = tempDir();
		List<File> files = list();
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			File file = new File(dir, randomId());
			byte[] data = new byte[1024 * 1024 * toIntExact(round((1 + min(1, abs(random.nextGaussian())) / 2)))];
			random.nextBytes(data);
			writeToFile(data, file);
			files.add(file);
		}

		dropSystemCache();

		for (int i = 0; i < n; i++) {
			byte[] data = null;
			for (int j = 0; j < 6; j++) {
				data = toByteArray(files.get(random.nextInt(files.size())));
			}
			writeToFile(data, files.get(random.nextInt(files.size())));
			dropSystemCache();
		}

		deleteQuietly(dir);

		return stopwatch.elapsed(TimeUnit.MILLISECONDS);
	}

	private void dropSystemCache() {
		executeProcess("/bin/bash", "-c", "sync; echo 3 | sudo dd of=/proc/sys/vm/drop_caches");
	}

}
