import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    private static final int TEXT_SIZE = 100_000;
    private static final int LENGTH = 100_00;
    private static final int QUEUE_SIZE = 100;


    private static final BlockingQueue<String> searchA = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static final BlockingQueue<String> searchB = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static final BlockingQueue<String> searchC = new ArrayBlockingQueue<>(QUEUE_SIZE);


    public static void main(String[] args) {
        Thread textCreatore = new Thread(() -> {
            for (int i = 0; i < LENGTH; i++) {
                String text = generateText("abc", TEXT_SIZE);
                searchA.add(text);
                searchB.add(text);
                searchC.add(text);
            }
        });
        Thread counterForA = new Thread(new SearcCharThread(searchA, 'a'));
        Thread counterForB = new Thread(new SearcCharThread(searchB, 'b'));
        Thread counterForC= new Thread(new SearcCharThread(searchC, 'c'));
        textCreatore.start();
        counterForA.start();
        counterForB.start();
        counterForC.start();

        try {
            counterForA.join();
            counterForB.join();
            counterForC.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SearcCharThread implements Runnable {
        private BlockingQueue<String> queue;
        private char symbol;

        public SearcCharThread(BlockingQueue<String> queue, char symbol) {
            this.queue = queue;
            this.symbol = symbol;
        }

        @Override
        public void run() {
            int maxCount = 0;
            String maxStr = "";
            for (int i = 0; i < LENGTH; i++) {
                try {
                    String text = this.queue.take();
                    int count = (int) IntStream.range(0, text.length()).filter(j -> text.charAt(j) == this.symbol).count();
                    if (count > maxCount) {
                        maxStr = text;
                        maxCount = count;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество " + this.symbol + " в  " + maxStr + " длиной в " + maxCount);
        }

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}