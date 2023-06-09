package com.lab.snippet.定时任务;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.BiFunction;

@Component
public class BaseScheduled {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static CompletionService<String> completionService;

    static {
        completionService = new ExecutorCompletionService<>(executorService);
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void job1() {
        System.out.println("定时任务开始=" + Thread.currentThread().getName() + "-" + LocalDateTime.now());
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void job2() throws InterruptedException, ExecutionException {

        BiFunction<Integer, Integer, Callable<String>> function = (id, second) -> {
            return new Callable<String>() {
                @Override
                public String call() throws Exception {
                    TimeUnit.SECONDS.sleep(second);
                    return String.format("任务{ %s }已完成，当前时间={ %s }", id, LocalDateTime.now());
                }
            };
        };

        completionService.submit(function.apply(1, 2));
        completionService.submit(function.apply(2, 8));
        completionService.submit(function.apply(3, 10));

        for (int index = 0; index < 3; index++) {
            System.out.println(completionService.take().get());
        }
    }
}
