package in.buzzzz.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * @author jitendra on 26/9/15.
 */
@Service
@Scope("prototype")
public class ForkJoinTaskExecutorService {
    ForkJoinPool forkJoinPool;

    public void start() {
        forkJoinPool = new ForkJoinPool();
    }

    public void submit(Callable task) {
        Assert.notNull(forkJoinPool, "You must call start() method before submitting any task.");
        forkJoinPool.submit(task);
    }

    public void shutdown() {
        Assert.notNull(forkJoinPool, "You must call start() method before submitting any task.");
        forkJoinPool.shutdown();
        while(!forkJoinPool.isTerminated()){}

    }
}
