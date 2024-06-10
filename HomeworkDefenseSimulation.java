import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class HomeworkDefenseSimulation {
    public static void main(String[] args) {
        int numberOfStudents = 10;
        ExecutorService professorThreadPool = Executors.newFixedThreadPool(2);
        ExecutorService assistantThreadPool = Executors.newSingleThreadExecutor();
        CyclicBarrier barrier = new CyclicBarrier(2);
        CountDownLatch latch = new CountDownLatch(numberOfStudents);

        List<Thread> studentThreads = new ArrayList<>();

        try {
            for (int i = 1; i <= numberOfStudents; i++) {
                String studentName = "Student " + i;
                double arrivalTime = Math.random();
                Student student = new Student(studentName, arrivalTime, barrier, latch, professorThreadPool, assistantThreadPool);
                Thread thread = new Thread(student);
                studentThreads.add(thread);
                thread.start();
            }

            latch.await(); // Wait for all students to be ready

            // Professor and assistant are available
            barrier.await();

            // Wait for students to finish defending
            for (Thread thread : studentThreads) {
                thread.join();
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            professorThreadPool.shutdown();
            assistantThreadPool.shutdown();
        }
    }
}

