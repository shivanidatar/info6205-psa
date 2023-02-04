package edu.neu.coe.info6205.threesum;

import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.TimeLogger;
import edu.neu.coe.info6205.util.Utilities;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ThreeSumBenchmark {
    public ThreeSumBenchmark(int runs, int n, int m) {
        this.runs = runs;
        this.supplier = new Source(n, m).intsSupplier(10);
        this.n = n;
    }

    public void runBenchmarks() {
        System.out.println("ThreeSumBenchmark: N=" + n);
        benchmarkThreeSum("ThreeSumQuadratic", (xs) -> new ThreeSumQuadratic(xs).getTriples(), n, timeLoggersQuadratic);
        benchmarkThreeSum("ThreeSumQuadrithmic", (xs) -> new ThreeSumQuadrithmic(xs).getTriples(), n, timeLoggersQuadrithmic);
        benchmarkThreeSum("ThreeSumCubic", (xs) -> new ThreeSumCubic(xs).getTriples(), n, timeLoggersCubic);
    }

    public static void main(String[] args) {
        new ThreeSumBenchmark(100, 250, 250).runBenchmarks();
        new ThreeSumBenchmark(50, 500, 500).runBenchmarks();
        new ThreeSumBenchmark(20, 1000, 1000).runBenchmarks();
        new ThreeSumBenchmark(10, 2000, 2000).runBenchmarks();
        new ThreeSumBenchmark(5, 4000, 4000).runBenchmarks();
        new ThreeSumBenchmark(3, 8000, 8000).runBenchmarks();
        new ThreeSumBenchmark(2, 16000, 16000).runBenchmarks();
    }

    private void benchmarkThreeSum(final String description, final Consumer<int[]> function, int n, final TimeLogger[] timeLoggers) {
        // FIXME
        if (description.equals("ThreeSumQuadratic") && n >=250){
            Benchmark_Timer time1 = new Benchmark_Timer(description,function);
            double timeQ = time1.runFromSupplier(supplier,runs);
            timeLoggers[0].log(timeQ,n);
            timeLoggers[1].log(timeQ,n);
            System.out.println("total time taken "+ timeQ+ " for n "+n);
        }
        else if(description.equals("ThreeSumQuadrithmic") && n >=250){
            Benchmark_Timer time2 = new Benchmark_Timer(description,function);
            double timeL = time2.runFromSupplier(supplier,runs);
            timeLoggers[0].log(timeL,n);
            timeLoggers[1].log(timeL,n);
            System.out.println("total time taken "+ timeL+ " for n "+n);
        }
        else if(description.equals("ThreeSumQuadraticWithCalipers") && n >=250){
            Benchmark_Timer time4 = new Benchmark_Timer(description,function);
            double timeQC = time4.runFromSupplier(supplier,runs);
            timeLoggers[0].log(timeQC,n);
            timeLoggers[1].log(timeQC,n);
            System.out.println("total time taken "+ timeQC+ " for n "+n);
        }
        else if(description.equals("ThreeSumCubic") && n >=250){
            Benchmark_Timer time3 = new Benchmark_Timer(description,function);
            double timeC = time3.runFromSupplier(supplier,runs);
            timeLoggers[0].log(timeC,n);
            timeLoggers[1].log(timeC,n);
            System.out.println("total time taken "+ timeC + " for n "+n);
        }

        // END 
    }

    private final static TimeLogger[] timeLoggersCubic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^3): ", (time, n) -> time / n / n / n * 1e6)
    };
    private final static TimeLogger[] timeLoggersQuadrithmic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^2 log n): ", (time, n) -> time / n / n / Utilities.lg(n) * 1e6)
    };
    private final static TimeLogger[] timeLoggersQuadratic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^2): ", (time, n) -> time / n / n * 1e6)
    };

    private final int runs;
    private final Supplier<int[]> supplier;
    private final int n;
}
