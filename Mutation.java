
import java.util.Random;

/**
 * A class that implements various mutation operators for use in a genetic
 * algorithm for the Traveling Salesperson Problem, where solutions are encoded
 * as permutations.
 *
 * @author Menachem Rosenberg
 * @author Robert Purnell Thurston
 */
public class Mutation {

    private static Random rng = new Random();

    /**
     * Chooses two random indexes in the given permutation and swaps the
     * elements at those indexes.
     *
     * @param permutation The permutation to mutate.
     */
    public static void swap(int[] permutation) {
        int i = rng.nextInt(0, permutation.length);
        int j = rng.nextInt(0, permutation.length);

        int temp = permutation[i];
        permutation[i] = permutation[j];
        permutation[j] = temp;
    }

    /**
     * Chooses a random index i and inserts the element at index i at a random
     * index j (which might be the same as i).
     *
     * @param permutation The permutation to mutate.
     */
    public static void insertion(int[] permutation) {
        int index = rng.nextInt(0, permutation.length);
        int insertionIndex = rng.nextInt(0, permutation.length);
        int temp = permutation[index];

        /*
         * In this case elements need to be shifted to the left.
         */
        if (index < insertionIndex) {
            for (int i = index + 1; i <= insertionIndex; i++) {
                permutation[i - 1] = permutation[i];
            }
        }
        /*
         * In this case elements need to be shifted to the right.
         */
        else if (index > insertionIndex) {
            for (int i = index - 1; i >= insertionIndex; i--) {
                permutation[i + 1] = permutation[i];
            }
        }
        permutation[insertionIndex] = temp;
    }

    /**
     * Reverses a continuous sub-array of elements in the permutation.
     *
     * @param permutation The permutation to mutate.
     */
    public static void reverse(int[] permutation) {
        int start = rng.nextInt(0, permutation.length);
        int stop = rng.nextInt(0, permutation.length);

        /*
         * Start needs to be smaller than stop for the following manipulations
         * to work.
         */
        if (start > stop) {
            int temp = stop;
            stop = start;
            start = temp;
        }

        /*
         * Swap the element at index start with the element at index stop,
         * increase start, decrease stop, and work your way towards the middle
         * of the continuous sub-array.
         */
        while (start < stop) {
            int temp = permutation[start];
            permutation[start] = permutation[stop];
            permutation[stop] = temp;
            start++;
            stop--;
        }
    }

    /**
     * Choose a random sub-array (a block) and insert that block into a randomly
     * chosen index j. If j is within the block, the permutation remains
     * unchanged.
     *
     * @param permutation The permutation to mutate.
     */
    public static void blockMove(int[] permutation) {
        int start = rng.nextInt(0, permutation.length);
        int stop = rng.nextInt(0, permutation.length);

        if (start > stop) {
            int temp = stop;
            stop = start;
            start = temp;
        }

        int insertionIndex = rng.nextInt(0, permutation.length);

        /*
         * Move each element within the block the appropriate number of places
         * to the right, starting with the rightmost element in the block.
         */
        if (insertionIndex > stop) {
            int positionsToMove = insertionIndex - stop;
            for (int i = stop; i >= start; i--) {
                for (int j = 0; j < positionsToMove; j++) {
                    int temp = permutation[i + j];
                    permutation[i + j] = permutation[i + j + 1];
                    permutation[i + j + 1] = temp;
                }
            }
        }
        /*
         * Move each element within the block the appropriate number of places
         * to the left, starting with the lefttmost element in the block.
         */
        else if (insertionIndex < start) {
            int positionToMove = start - insertionIndex;
            for (int i = start; i <= stop; i++) {
                for (int j = 0; j < positionToMove; j++) {
                    int temp = permutation[i - j];
                    permutation[i - j] = permutation[i - j - 1];
                    permutation[i - j - 1] = temp;
                }
            }
        }
    }

    /**
     * Randomly scrambles the elements within a randomly chosen sub-array.
     *
     * @param permutation The permutation to mutate.
     */
    public static void scramble(int[] permutation) {
        int start = rng.nextInt(permutation.length);
        int stop = rng.nextInt(permutation.length);

        if (start > stop) {
            int temp = start;
            start = stop;
            stop = temp;
        }

        for (int i = start; i < stop; i++) {
            int randomIndex = rng.nextInt(i, stop + 1);
            int temp = permutation[i];
            permutation[i] = permutation[randomIndex];
            permutation[randomIndex] = temp;

        }
    }

}
