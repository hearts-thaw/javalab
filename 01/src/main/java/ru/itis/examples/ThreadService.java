package main.java.ru.itis.examples;

/**
 * 10.09.2020
 * Threads
 *
 * @author Sidikov Marsel (First Software Engineering Platform)
 * @version v1.0
 */
public class ThreadService {
    public void submit(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
    }
}
