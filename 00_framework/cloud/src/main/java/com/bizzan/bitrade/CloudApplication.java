package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.concurrent.RecursiveTask;

@EnableEurekaServer
@SpringBootApplication
public class CloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
    }
}

class ForkJoinDemo {
    // 1. 运行入口
    public static void main(String[] args) {
        int cap = 33;
        int n = cap - 1;
        n |= n >>> 1;
        System.out.println("result:" + n);
//        int n = 20;
//
//        // 为了追踪子线程名称，需要重写 ForkJoinWorkerThreadFactory 的方法
//        final ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
//            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
//            worker.setName("my-thread-" + worker.getPoolIndex());
//            return worker;
//        };
//
//        //创建分治任务线程池，可以追踪到线程名称
//        ForkJoinPool forkJoinPool = new ForkJoinPool(4, factory, null, false);
//
//        // 快速创建 ForkJoinPool 方法
//        // ForkJoinPool forkJoinPool = new ForkJoinPool(4);
//
//        //创建分治任务
//        Fibonacci fibonacci = new Fibonacci(n);
//
//        //调用 invoke 方法启动分治任务
//        Integer result = forkJoinPool.invoke(fibonacci);
//        System.out.println(String.format("Fibonacci %d 的结果是 %d", n, result));
    }
}

// 2. 定义拆分任务，写好拆分逻辑
class Fibonacci extends RecursiveTask<Integer> {
    final int n;

    Fibonacci(int n) {
        this.n = n;
    }

    @Override
    public Integer compute() {
        //和递归类似，定义可计算的最小单元
        if (n <= 1) {
            return n;
        }
        // 想查看子线程名称输出的可以打开下面注释
        System.out.println(Thread.currentThread().getName());

        Fibonacci f1 = new Fibonacci(n - 1);
        // 拆分成子任务
        f1.fork();
        Fibonacci f2 = new Fibonacci(n - 2);
        f2.fork();
        // f1.join 等待子任务执行结果
        return f2.join() + f1.join();
    }
}
