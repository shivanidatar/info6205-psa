package edu.neu.coe.info6205.sort.linearithmic;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.elementary.HeapSort;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.Benchmark;
import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.StatPack;
import edu.neu.coe.info6205.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = new InsertionSort<>(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public MergeSort(int N, Config config) {
        super(DESCRIPTION + ":" + getConfigString(config), N, config);
        insertionSort = new InsertionSort<>(getHelper());
    }

    @Override
    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        return result;
    }

    @Override
    public void sort(X[] a, int from, int to) {
        // CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
        X[] aux = Arrays.copyOf(a, a.length);
        sort(a, aux, from, to);
    }


    private void sort(X[] a, X[] aux, int from, int to) {
        final Helper<X> helper = getHelper();
        Config config = helper.getConfig();
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        if (to <= from + helper.cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }
        // FIXME : implement merge sort with insurance and no-copy optimizations
        int middle = from + (to - from) / 2;
        if (noCopy) {
            sort(aux, a, from, middle);
            sort(aux, a, middle, to);
            if (insurance && helper.less(aux, middle - 1, middle)) {
                System.arraycopy(aux, from, a, from, to - from);
                helper.incrementCopies(to - from);
            } else
                merge(aux, a, from, middle, to);
        } else {
            sort(a, aux, from, middle);
            sort(a, aux, middle, to);
            System.arraycopy(a, from, aux, from, to - from);
            if (insurance && helper.less(a[middle - 1], a[middle])) return;
            merge(aux, a, from, middle, to);
        }


        // END
    }

    // CONSIDER combine with MergeSortBasic perhaps.
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        final Helper<X> helper = getHelper();
        int i = from;
        int j = mid;
        for (int k = from; k < to; k++)
            if (i >= mid) helper.copy(sorted, j++, result, k);
            else if (j >= to) helper.copy(sorted, i++, result, k);
            else if (helper.less(sorted[j], sorted[i])) {
                helper.incrementFixes(mid - i);
                helper.copy(sorted, j++, result, k);
            } else helper.copy(sorted, i++, result, k);
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;

    public static void main(String[] args){
        //Random rand = new Random();
        Config c = Config.setupConfig("true", "", "0", "1", "");
        final int num = 10000;
        // Merge Sort
        for (int n = 10000; n <= 256000; n = n * 2) {
            System.out.println("With N = "+n);
            final Helper<Integer> helper = HelperFactory.create("Mergesort", n, c);
            final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(num));
            System.out.println(helper);
            Sort<Integer> mergeSort = new MergeSort<>(helper);
            mergeSort.init(n);
            helper.preProcess(xs);
            Benchmark<Boolean> benchmarkRandom = new Benchmark_Timer<>(
                    "MergeSort", b -> {
                mergeSort.sort(xs.clone(), 0, xs.length);
            });
            double resultRandom = benchmarkRandom.run(true, 20);
            Integer[] ys = mergeSort.sort(xs);
            helper.postProcess(ys);
            PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
           //InstrumentedHelper stat = new InstrumentedHelper("MergeSort",n,c);
           int compares = (int) statPack.getStatistics(InstrumentedHelper.COMPARES).mean();
           int swaps = (int) statPack.getStatistics(InstrumentedHelper.SWAPS).mean();
           int hits = (int) statPack.getStatistics(InstrumentedHelper.HITS).mean();
           int copies = (int) statPack.getStatistics(InstrumentedHelper.COPIES).mean();
           System.out.println(" Time taken for Merge sort with random array : " + resultRandom);
           System.out.println("Number of compares = "+compares);
           System.out.println("Number of swaps = "+swaps);
           System.out.println("Number of array accesses = "+hits);
           System.out.println("Number of copies = "+copies);

            //Quick sort dual pivot

            final BaseHelper<Integer> helper1 = (BaseHelper<Integer>) HelperFactory.create("quick sort dual pivot", n, c);
            //System.out.println(helper1);
            Sort<Integer> quicksort = new QuickSort_DualPivot<>(helper1);
            quicksort.init(n);
            //final int f = 500000;
            final Integer[] xs1 = helper1.random(Integer.class, r -> r.nextInt(num));
            System.out.println(helper1);
            helper1.preProcess(xs1);
            Benchmark<Boolean> benchmarkRandom1 = new Benchmark_Timer<>(
                    "QuickDualPivotSort", b -> {
                quicksort.sort(xs1);
            });
            double resultRandom1 = benchmarkRandom1.run(true, 20);
            Integer[] ys1 = quicksort.sort(xs1);
            helper1.postProcess(ys1);
            final PrivateMethodTester privateMethodTester1 = new PrivateMethodTester(helper1);
            final StatPack statPack1 = (StatPack) privateMethodTester1.invokePrivate("getStatPack");
            int compares1 = (int) statPack1.getStatistics(InstrumentedHelper.COMPARES).mean();
            int swaps1 = (int) statPack1.getStatistics(InstrumentedHelper.SWAPS).mean();
            int hits1 = (int) statPack1.getStatistics(InstrumentedHelper.HITS).mean();
            System.out.println(" Time taken for Quick sort with random array : " + resultRandom1);
            System.out.println("Number of compares = "+compares1);
            System.out.println("Number of swaps = "+swaps1);
            System.out.println("Number of array accesses = "+hits1);

             //Heap Sort

            Helper<Integer> helper2 = HelperFactory.create("HeapSort", n, c);
            final Integer[] randomArray = helper2.random(Integer.class, r -> r.nextInt(num));
            helper2.init(n);
            SortWithHelper<Integer> heapsort = new HeapSort<Integer>(helper2);
            heapsort.preProcess(randomArray);
            Benchmark<Boolean> benchmarkRandom2 = new Benchmark_Timer<>(
                    "HeapSort", b -> {
                heapsort.sort(randomArray);
            });
            double resultRandom2 = benchmarkRandom2.run(true, 20);
            PrivateMethodTester privateMethodTester2 = new PrivateMethodTester(helper2);
            StatPack statPack2 = (StatPack) privateMethodTester2.invokePrivate("getStatPack");
            Integer[] ys2 = heapsort.sort(randomArray);
            helper2.postProcess(ys2);
            int compares2 = (int) statPack2.getStatistics(InstrumentedHelper.COMPARES).mean();
            int swaps2 = (int) statPack2.getStatistics(InstrumentedHelper.SWAPS).mean();
            int hits2 = (int) statPack2.getStatistics(InstrumentedHelper.HITS).mean();
            System.out.println(" Time taken for Heap sort with random array : " + resultRandom2);
            System.out.println("Number of compares = "+compares2);
            System.out.println("Number of swaps = "+swaps2);
            System.out.println("Number of array accesses = "+hits2);

        }
    }
}

