package main.java.ru.itis.examples;

public class Main {

    public static void main(String[] args) throws Exception {

        Thread henThread = new HenThread();
        Thread eggThread = new EggThread();

        henThread.start();
        eggThread.start();

        henThread.join();
        eggThread.join();

        for (int i = 0; i < 100; i++) {
            System.out.println("Human");
        }

//        HumanRunnable humanRunnable = new HumanRunnable();
//        Thread thread = new Thread(humanRunnable);
//        thread.start();
        ThreadService threadService = new ThreadService();
        threadService.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("Tirex");
            }
        });
    }
}
