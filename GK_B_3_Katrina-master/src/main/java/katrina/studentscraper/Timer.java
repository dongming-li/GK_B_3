package katrina.studentscraper;

import java.util.function.Supplier;

public class Timer {
    static <T> void start(Supplier<T> fn) {
        long start = System.currentTimeMillis();
        T val = fn.get();
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
    }
}
