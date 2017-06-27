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

    private double epsilon = 10;
    private final int delay = 150;
    private final int fasterDelay = 50;
    private final int slowerDelay = 250;

    @Test
    public void smokeTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> delay(delay));
        final Integer result = cf.join();
        Assert.assertEquals((Integer) delay, result);
    }

    @Test
    public void waitLongerTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> cf = CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> delay(fasterDelay)),
            CompletableFuture.supplyAsync(() -> delay(slowerDelay))
        );
        final Integer result = (Integer) cf.join();
        Assert.assertEquals((Integer) fasterDelay, result);
    }

    @Test
    public void waitSlowerTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cf = CompletableFuture.allOf(
            CompletableFuture.supplyAsync(() -> delay(fasterDelay)),
            CompletableFuture.supplyAsync(() -> delay(slowerDelay))
        );
        Instant startInstant = Instant.now();
        cf.join();
        Duration durationOfTest = Duration.between(startInstant, Instant.now());
        Assert.assertEquals(slowerDelay, durationOfTest.toMillis()); // duration measure less precise
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
            .get();

        final Duration durationJoin = Duration.between(startInstant, Instant.now());
        final long durationJoinInMillis = durationJoin.toMillis();

        Assert.assertEquals(2 * slowerDelay, combinedDelay, epsilon);
        Assert.assertTrue(durationJoinInMillis > (2 * slowerDelay));
    }

    @Test
    public void combineTwoSlowerMultithreaded() {

        final Instant startInstant = Instant.now();
        final ThreadPoolExecutor fixedThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        final CompletableFuture<Integer> combinedCompletableFuture = CompletableFuture.supplyAsync(() -> delay(slowerDelay), fixedThreadPool)
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
    public void ignoreException(){
        CompletableFuture.supplyAsync(() -> delayedException(fasterDelay));
        
        final Integer delay = delay(300);
        Assert.assertEquals(300, (int) delay); // nothing happend, good !
    }

    @Test
    public void propagateException(){
        try {
            final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> delayedException(fasterDelay));

            delay(300);
            completableFuture.join();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("java.lang.RuntimeException: " + String.valueOf(fasterDelay), e.getMessage());
        }
    }


    private Integer delay(Integer ms){
        try {
            Thread.sleep(ms);
            return ms;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer delayedException(Integer ms){
        try {
            Thread.sleep(ms);
            throw new RuntimeException(String.valueOf(ms));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
