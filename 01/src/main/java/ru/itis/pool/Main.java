package main.java.ru.itis.pool;

/**
 * @author Sidikov Marsel (First Software Engineering Platform)
 * @version v1.0
 */
public class Main {
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);

        ThreadPool executorService = ThreadPool.newPool(4);

        Runnable task1 = () -> {
            int res = 0;
            for (int i = 0; i < 100; i++) {
                res++;
            }
            System.out.println(res);
        };
        Runnable task2 = () -> {
            int res = 0;
            for (int i = 2; i < 200; i += 2) {
                res++;
            }
            System.out.println(res);
        };
        Runnable task3 = () -> {
            int res = 0;
            for (int i = 4; i < 400; i += 4) {
                res++;
            }
            System.out.println(res);
        };

        executorService.submit(task1);
        executorService.submit(task2);
        executorService.submit(task3);

//        executorService.stopAll();
    }
}
