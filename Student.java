import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class Student implements Runnable {
    private final String name;
    private final double arrivalTime;
    private final CyclicBarrier barrier;
    private final CountDownLatch latch;
    private final ExecutorService professorThreadPool;
    private final ExecutorService assistantThreadPool;
    private final List<Future<Integer>> scores;

    public Student(String name, double arrivalTime, CyclicBarrier barrier, CountDownLatch latch,
                   ExecutorService professorThreadPool, ExecutorService assistantThreadPool) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.barrier = barrier;
        this.latch = latch;
        this.professorThreadPool = professorThreadPool;
        this.assistantThreadPool = assistantThreadPool;
        this.scores = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) (arrivalTime * 1000)); // Simulate arrival time
            System.out.println("Thread: " + name + " Arrival: " + arrivalTime);

            // Decide whether to defend at professor or assistant
            if (new Random().nextBoolean()) {
                defendAtProfessor();
            } else {
                defendAtAssistant();
            }

        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void defendAtProfessor() throws InterruptedException, BrokenBarrierException {
        Future<Integer> score = professorThreadPool.submit(() -> {
            barrier.await(); // Wait for other student
            long start = System.currentTimeMillis();
            // Simulate defending time
            Thread.sleep((long) ((new Random().nextDouble() * 0.5 + 0.5) * 1000));
            System.out.println("Thread: " + name + " Prof: Professor TTC: " +
                    (System.currentTimeMillis() - start) / 1000.0 + " Score: " + getScore());
            return getScore();
        });
        scores.add(score);
    }

    private void defendAtAssistant() throws InterruptedException {
        Future<Integer> score = assistantThreadPool.submit(() -> {
            latch.countDown(); // Notify assistant that student is ready
            long start = System.currentTimeMillis();
            // Simulate defending time
            Thread.sleep((long) ((new Random().nextDouble() * 0.5 + 0.5) * 1000));
            System.out.println("Thread: " + name + " Prof: Assistant TTC: " +
                    (System.currentTimeMillis() - start) / 1000.0 + " Score: " + getScore());
            return getScore();
        });
        scores.add(score);
    }

    private int getScore() {
        return new Random().nextInt(6) + 5; // Random score between 5 and 10
    }
}