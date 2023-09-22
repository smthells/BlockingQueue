import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {
    private static int MAX_QUEUE = 100;
    private static int NUMBER_OF_SENTENCES = 10_000;
    private static int TEXT_LENGTH = 100_000;
    private static BlockingQueue<String> queueForA = new ArrayBlockingQueue<>(MAX_QUEUE);
    private static BlockingQueue<String> queueForB = new ArrayBlockingQueue<>(MAX_QUEUE);
    private static BlockingQueue<String> queueForC = new ArrayBlockingQueue<>(MAX_QUEUE);

    public static void main(String[] args) {
        startGenerator();
        startAnalyzer(queueForA, 'a');
        startAnalyzer(queueForB, 'b');
        startAnalyzer(queueForC, 'c');
    }

    public static void startGenerator() {
        new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_SENTENCES; i++) {
                String text = generateText("abc", TEXT_LENGTH);
                try {
                    queueForA.put(text);
                    queueForB.put(text);
                    queueForC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
    }

    public static void startAnalyzer(BlockingQueue<String> queue, char ch) {
        new Thread(() -> {
            int maxCount = 0;
            String textWithMaxCount = null;
            for (int i = 0; i < NUMBER_OF_SENTENCES; i++) {
                try {
                    String currentText = queue.take();
                    int currentCount = countCharInText(currentText, ch);
                    if (currentCount > maxCount) {
                        maxCount = currentCount;
                        textWithMaxCount = currentText;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (textWithMaxCount != null) {
                System.out.println("Текст с максимальным количеством символа \"" + ch + "\" = " + maxCount + ": " +
                                   textWithMaxCount.substring(0, 50) + "...");
            }
        }).start();
    }

    public static int countCharInText(String text, char ch) {
        int count = 0;
        for (char character : text.toCharArray()) {
            if (character == ch) {
                count++;
            }
        }
        return count;
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