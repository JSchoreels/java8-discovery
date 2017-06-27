package be.jschoreels.discovery.java8;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Created by jschoreels on 27.06.17.
 */
public class CompletableFutureTest {

    private final int delay = 150;
    private final int fasterDelay = 50;
    private final int slowerDelay = 250;
    private double epsilon = 10;

    @Test
    public void smokeTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> delay(delay));
        final Integer result = cf.join();
        Assert.assertEquals((Integer) delay, result);
    }

    private Integer delay(Integer ms) {
        try {
            Thread.sleep(ms);
            return ms;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void waitLongerTest() throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> fasterCF = CompletableFuture.supplyAsync(() -> delay(fasterDelay));
        final CompletableFuture<Integer> slowerCF = CompletableFuture.supplyAsync(() -> delay(slowerDelay));
        CompletableFuture<Object> cf = CompletableFuture.anyOf(
            fasterCF,
            slowerCF
        );
        final Integer result = (Integer) cf.join();
        slowerCF.cancel(true); // Only works if CF is not completed
        Assert.assertEquals((Integer) fasterDelay, result);
        Assert.assertTrue(fasterCF.isDone());
        Assert.assertTrue(slowerCF.isCancelled());
    }

    @Test
    public void waitSlowerTest() throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> fasterCF = CompletableFuture.supplyAsync(() -> delay(fasterDelay));
        final CompletableFuture<Integer> slowerCF = CompletableFuture.supplyAsync(() -> delay(slowerDelay));

        CompletableFuture<Void> cf = CompletableFuture.allOf(
            fasterCF,
            slowerCF
        );

        Instant startInstant = Instant.now();
        cf.join();
        Duration durationOfTest = Duration.between(startInstant, Instant.now());
        Assert.assertEquals(slowerDelay, durationOfTest.toMillis(), epsilon); // duration measure less precise
        Assert.assertTrue(fasterCF.isDone());
        Assert.assertTrue(slowerCF.isDone());
    }

    @Test
    public void composeSlower() throws ExecutionException, InterruptedException {
        final Integer compositionOfDelay = CompletableFuture.supplyAsync(() -> delay(fasterDelay))
            .thenCompose(result -> CompletableFuture.supplyAsync(() -> delay(result * 4)))
            .join();
        Assert.assertEquals(fasterDelay * 4, (int) compositionOfDelay);
    }

    @Test
    public void combineTwoSlowerOneThread() throws ExecutionException, InterruptedException {

        final Instant startInstant = Instant.now();
        final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        final Integer combinedDelay = CompletableFuture.supplyAsync(() -> delay(slowerDelay), singleThreadExecutor)
            .thenCombine(CompletableFuture.supplyAsync(() -> delay(slowerDelay), singleThreadExecutor),
                (x, y) -> x + y)
            .join();

        final Duration durationJoin = Duration.between(startInstant, Instant.now());
        final long durationJoinInMillis = durationJoin.toMillis();

        Assert.assertEquals(2 * slowerDelay, combinedDelay, epsilon);
        Assert.assertTrue(durationJoinInMillis > (2 * slowerDelay));
    }

    @Test
    public void combineTwoSlowerMultithreaded() {

        final Instant startInstant = Instant.now();
        final ThreadPoolExecutor fixedThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        final CompletableFuture<Integer> combinedCompletableFuture =
            CompletableFuture
                .supplyAsync(() -> delay(slowerDelay), fixedThreadPool)
                .thenCombine(CompletableFuture.supplyAsync(() -> delay(slowerDelay), fixedThreadPool),
                    (x, y) -> x + y);

        final Integer joinResult = combinedCompletableFuture.join();
        final Duration durationJoin = Duration.between(startInstant, Instant.now());
        final long durationJoinInMillis = durationJoin.toMillis();

        Assert.assertEquals(2 * slowerDelay, joinResult, epsilon);
        Assert.assertTrue(durationJoinInMillis < (2 * slowerDelay));
        Assert.assertTrue(durationJoinInMillis > slowerDelay);
        Assert.assertEquals(2, fixedThreadPool.getCompletedTaskCount());

    }

    @Test
    public void ignoreException() {
        CompletableFuture.supplyAsync(() -> delayedException(fasterDelay));

        final Integer delay = delay(300);
        Assert.assertEquals(300, (int) delay); // nothing happend, good !
    }

    private Integer delayedException(Integer ms) {
        try {
            Thread.sleep(ms);
            throw new RuntimeException(String.valueOf(ms));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void propagateException() {
        try {
            final CompletableFuture<Integer> completableFuture =
                CompletableFuture.supplyAsync(() -> delayedException(fasterDelay));

            delay(300);
            completableFuture.join();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("java.lang.RuntimeException: " + String.valueOf(fasterDelay), e.getMessage());
        }
    }

}
