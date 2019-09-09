/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Arrays.asList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmExecutorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static ThreadFactory namedThreadFactory(Class classe) {
        return namedThreadFactory(classe.getName());
    }

    public static ThreadFactory namedThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    public static void shutdownQuietly(ExecutorService... executors) {
        asList(executors).forEach((executor) -> executor.shutdown());
        asList(executors).forEach((executor) -> {
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
            }
        });
        asList(executors).forEach((executor) -> {
            if (!executor.isTerminated()) {
                LOGGER.warn("executor service = {} failed to stop in time", executor);
            }
        });
    }

    public static <T> T awaitCompletionIgnoreInterrupt(Future<T> future) {
        while (true) {
            try {
                return future.get();
            } catch (InterruptedException ex) {
                LOGGER.trace("interrupted", ex);
            } catch (ExecutionException ex) {
                throw runtime(ex.getCause());//TODO check this
            }
        }
    }

    public static void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            LOGGER.warn("sleep interrupted", ex);
        }
    }

    public static void waitUntil(Supplier<Boolean> condition) {
        final int timeout = 120;
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (!condition.get() == true && stopwatch.elapsed(TimeUnit.SECONDS) < timeout) {
            sleepSafe(100);
        }
        checkArgument(condition.get() == true, "error: condition not true after %s seconds", timeout);
    }

    public static <T extends ExecutorService> RestartableExecutorHelper<T> restartable(Supplier<T> supplier) {
        return new RestartableExecutorHelperImpl<>(supplier);
    }

    private static class RestartableExecutorHelperImpl<T extends ExecutorService> implements RestartableExecutorHelper<T> {

        private final Supplier<T> supplier;
        private T instance;

        public RestartableExecutorHelperImpl(Supplier<T> supplier) {
            this.supplier = checkNotNull(supplier);
        }

        @Override
        public synchronized T get() {
            return checkNotNull(instance, "executor is not running");
        }

        @Override
        public synchronized T start() {
            checkArgument(!isRunning());
            return instance = checkNotNull(supplier.get());
        }

        @Override
        public synchronized void stop() {
            checkArgument(isRunning());
            T inst = instance;
            instance = null;
            shutdownQuietly(inst);
        }

        @Override
        public boolean isRunning() {
            return instance != null;
        }

    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger counter = new AtomicInteger(0);
        private final String name;

        public NamedThreadFactory(String name) {
            this.name = checkNotBlank(name);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, format("%s-%s", name, counter.getAndIncrement()));
        }
    }

}
