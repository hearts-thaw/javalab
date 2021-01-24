package main.java.ru.itis.examples;

/**
 * 10.09.2020
 * Threads
 *
 * @author Sidikov Marsel (First Software Engineering Platform)
 * @version v1.0
 */
public class HenThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("Hen");
        }
    }
}
