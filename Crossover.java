
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A class that implements various crossover operators between permutations for
 * use in a genetic algorithm.
 *
 * @author Menachem Rosenberg
 * @author Robert Purnell Thurston
 */
public class Crossover {

    private static Random rng = new Random();

    /**
     * Cycle crossover.
     *
     * @param parent1 One of the permutations to be used in the crossover.
     * @param parent2 The other permutation to be used in the crossover.
     */
    public static void cycle(int[] parent1, int[] parent2) {
        /*
         * Rather than conducting a linear search to find the index of the next
         * element in parent1, a linear time operation is conducted once in
         * constructing a map that maps each element of parent1 to the index at
         * which it sits. This is a well-defined map because the keys of the map
         * are distinct integers in the interval [0, n - 1], where n is the
         * length of the parent permutations.
         */
        Map<Integer, Integer> valueToIndex = new HashMap<>();
        for (int i = 0; i < parent1.length; i++) {
            valueToIndex.put(parent1[i], i);
        }

        /*
         * A set that keeps track of which indexes are in the cycle.
         */
        Set<Integer> indexesInCycle = new HashSet<>();
        int start = rng.nextInt(parent1.length);

        while (true) {
            if (indexesInCycle.contains(start)) {
                break;
            }
            indexesInCycle.add(start);
            start = valueToIndex.get(parent2[start]);
        }

        /*
         * Iterate over the indexes in the cycle and swap the elements between
         * the parents at each index.
         */
        for (int index : indexesInCycle) {
            int temp = parent1[index];
            parent1[index] = parent2[index];
            parent2[index] = temp;
        }

    }

    /**
     * Order crossover.
     *
     * @param parent1 One of the permutations to be used in the crossover.
     * @param parent2 The other permutation to be used in the crossover.
     */
    public static void order(int[] parent1, int[] parent2) {
        int start = rng.nextInt(parent1.length);
        int end = rng.nextInt(parent1.length);

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        Set<Integer> numbersBetweenCrossPoints1 = new HashSet<>();
        Set<Integer> numbersBetweenCrossPoints2 = new HashSet<>();

        for (int i = start; i <= end; i++) {
            numbersBetweenCrossPoints1.add(parent1[i]);
            numbersBetweenCrossPoints2.add(parent2[i]);
        }

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        /*
         * For each parent, add to the appropriate list the elements that do not
         * appear between the cross points of the other parent, in the order
         * that they appear in the given parent.
         */
        for (int i = 0; i < parent1.length; i++) {
            if (!numbersBetweenCrossPoints2.contains(parent1[i])) {
                list1.add(parent1[i]);
            }
            if (!numbersBetweenCrossPoints1.contains(parent2[i])) {
                list2.add(parent2[i]);
            }

        }

        /*
         * Swap corresponding elements that appear between the cross points.
         */
        for (int i = start; i <= end; i++) {
            int temp = parent1[i];
            parent1[i] = parent2[i];
            parent2[i] = temp;
        }

        /*
         * Fill out the remainder of the children by starting to the right of
         * the rightmost cross point and wrapping around to the beginning of the
         * permutation, if necessary.
         */
        int indexStart = (end + 1) % parent1.length;
        for (int i = 0; i < list1.size(); i++) {
            parent1[indexStart] = list1.get(i);
            parent2[indexStart] = list2.get(i);
            indexStart = (indexStart + 1) % parent1.length;
        }
    }
}
