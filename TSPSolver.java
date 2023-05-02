
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A class that attempts to minimize the cost of a given TSP instance, using
 * either a genetic algorithm or VBSS.
 *
 * @author Menachem Rosenberg
 * @author Robert Purnell Thurston
 */
public class TSPSolver {

    private static Random rng;

    /*
     * Holds the permutation that at any point is one with lowest-cost of all
     * permutations examined.
     */
    private int[] bestMember;

    /*
     * The number of generations to simulate if using a genetic algorithm.
     */
    private int numberOfGenerations;

    /*
     * The number of cities in the given TSP instance.
     */
    private int numberOfCities;

    private int[][] distancesBetweenCities;

    /*
     * The permutations that comprise the population.
     */
    private int[][] population;

    public TSPSolver(int numberOfCities) {
        this.numberOfCities = numberOfCities;
        this.distancesBetweenCities = new int[numberOfCities][numberOfCities];
        this.bestMember = new int[this.numberOfCities];
        rng = new Random();

    }

    public TSPSolver(int populationSize, int numberOfCities, int numberOfGenerations) {
        this(numberOfCities);
        this.numberOfGenerations = numberOfGenerations;
        this.population = new int[populationSize][numberOfCities];

    }

    /**
     * Constructs a random initial population.
     */
    public void generateInitialPopulation() {
        for (int i = 0; i < this.population.length; i++) {
            this.population[i] = this.randomPermutation();
        }
        this.bestMember = Arrays.copyOf(this.mostFit(), this.numberOfCities);
    }

    /**
     * The genetic algorithm. It runs selection, mutation, and crossover
     * this.numberOfGenerations times. There are different selection, mutation,
     * and crossover operators that can be used, but none of them gives
     * satisfactory results.
     */
    public void evolve() {
        for (int i = 0; i < this.numberOfGenerations; i++) {
            this.fitnessProportionateSelection();
            this.mutation();
            this.crossover();

            /*
             * Keeps track of the member with the lowest cost seen.
             */
            int[] generationBest = this.mostFit();
            if (this.memberFitness(generationBest, true) < this.memberFitness(
                    this.bestMember, true)) {
                this.bestMember = Arrays.copyOf(generationBest,
                        this.numberOfCities);
            }
        }
    }

    /**
     * Conducts a crossover operation between two permutations, according to
     * some crossover rate. The crossover rate and the particular crossover
     * operation can both be adjusted.
     */
    public void crossover() {
        double crossoverRate = .5;
        for (int i = 0; i < this.population.length - 1; i += 2) {

            double randomNumber = rng.nextDouble();
            if (randomNumber < crossoverRate) {
                Crossover.cycle(this.population[i], this.population[i + 1]);
            }

        }
    }

    /**
     * Conducts mutation according to some mutation rate. The mutation rate and
     * the mutation operator can both be adjusted.
     */
    public void mutation() {
        double mutationRate = .5;
        for (int[] permutation : this.population) {

            double randomNumber = rng.nextDouble();
            if (randomNumber < mutationRate) {
                Mutation.reverse(permutation);
            }
        }
    }

    /**
     * Conducts fitness proportionate selection in combination with diversity.
     * If the population size is n, then the n members with the highest fitness
     * + diversity are selected, where the diversity of member k is in relation
     * to the k - 1 members that have already been chosen. The first member is
     * chosen based solely on fitness, so in essence elitism is being
     * implemented with the most elite member guaranteed to survive to the next
     * generation.
     */
    public void fitnessProportionateSelection() {

        double[] fitnesses = this.memberFitnesses();
        int[][] newPopulation = new int[this.population.length][this.numberOfCities];

        for (int i = 0; i < this.population.length; i++) {
            if (i == 0) {
                newPopulation[i] = this.mostFit();
            }
            else {
                int index = this.bestFitnessPlusDiversity(fitnesses,
                        newPopulation,
                        i);
                newPopulation[i] = this.population[index];
            }
        }
        this.population = newPopulation;

    }

    /**
     * Tournament selection.
     *
     * @param t The tournament size.
     */
    public void tournamentSelection(int t) {
        int[][] newPopulation = new int[this.population.length][this.numberOfCities];

        for (int i = 0; i < this.population.length; i++) {
            int[] temp = new int[t];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = rng.nextInt(this.population.length);
            }
            int index = 0;
            double bestFitness = this.memberFitness(this.population[temp[0]],
                    false);
            for (int j = 1; j < temp.length; j++) {
                double fitness = this.memberFitness(this.population[temp[j]],
                        false);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    index = j;
                }
            }
            newPopulation[i] = this.population[temp[index]];

        }
        this.population = newPopulation;
    }

    /**
     * Computes the fitness of each member of the population.
     *
     * @return An array in which the ith element contains the fitness of
     * population member i.
     */
    public double[] memberFitnesses() {
        double[] fitnesses = new double[this.population.length];
        for (int i = 0; i < fitnesses.length; i++) {
            fitnesses[i] = this.memberFitness(this.population[i], false);
        }
        return fitnesses;
    }

    /**
     * Determines the population member that has the best fitness + diversity,
     * where the diversity is measured in relation to the given permutations.
     *
     * @param fitnesses The fitnesses of the permutations comprising the population.
     * @param permutations The permutations against which the diversities are to be measured.
     * @param limit The number of permutations against which to measure the diversities.
     * @return The index of the population member that has the best fitness + diversity.
     */
    public int bestFitnessPlusDiversity(double[] fitnesses, int[][] permutations, int limit) {
        int index = -1;
        double bestCombination = -1;
        for (int i = 0; i < fitnesses.length; i++) {
            double temp = this.diversity(this.population[i], permutations, limit) + fitnesses[i];
            if (temp > bestCombination) {
                bestCombination = temp;
                index = i;
            }
        }
        return index;
    }

    /**
     * Finds the member of the population that has the highest fitness.
     *
     * @return A permutation that represents the population member with the
     * highest fitness.
     */
    public int[] mostFit() {
        int[] currentBestMember = this.population[0];
        double best = Double.MIN_VALUE;
        for (int i = 1; i < this.population.length; i++) {
            double temp = this.memberFitness(this.population[i], false);
            if (temp > best) {
                best = temp;
                currentBestMember = this.population[i];
            }
        }
        return currentBestMember;
    }

    /**
     * Measures the relative diversity of two permutations. The diversity score
     * is given by how many indexes contain different entries. A diversity score
     * of zero would mean that the two permutations are identical, and a
     * diversity score equal to the length of the permutations would mean that
     * there is no index i such that permutation1[i] == permutation2[i]. It is
     * debatable whether this definition of diversity is a good one, because any
     * two permutations that are the same cyclic permutation would be considered
     * to be maximally diverse, even though they are effectively the same
     * permutation.
     *
     * @param permutation1 One of the permutations in the comparison.
     * @param permutation2 The other permutation in the comparison.
     * @return
     */
    public int diversity(int[] permutation1, int[] permutation2) {
        int diversity = 0;
        for (int i = 0; i < permutation1.length; i++) {
            if (permutation1[i] != permutation2[i]) {
                diversity++;
            }
        }
        return diversity;
    }

    /**
     * Measures how different a given permutation is compared to one or more
     * other permutations.
     *
     * @param permutation The permutation whose diversity is to be measured.
     * @param permutations One or more permutations against which the diversity
     * of a permutation is to be measured.
     * @param limit The number of permutations against which to measure the
     * diversity of permutation (i.e., permutations[0], ..., permutations[limit
     * - 1].
     * @return The sum of the diversity scores for permutation vs.
     * permutation[i], for i = 0, 1, ..., (limit - 1).
     */
    public int diversity(int[] permutation, int[][] permutations, int limit) {
        int diversity = 0;
        for (int i = 0; i < limit; i++) {
            diversity += this.diversity(permutation, permutations[i]);
        }
        return diversity;
    }

    public void printAll() {
        for (int[] x : this.population) {
            System.out.println(Arrays.toString(x));
        }
    }

    /**
     * Calculates the total fitness of the population, which is the sum of the
     * fitness of the individual members of the population.
     *
     * @return The total fitness of the population.
     */
    public double totalFitness() {
        double totalFitness = 0;
        for (int i = 0; i < this.population.length; i++) {
            totalFitness += this.memberFitness(this.population[i], false);
        }
        return totalFitness;
    }

    /**
     * Constructs a random permutation of the integers in the interval [0,
     * this.numberOfCities - 1], using an algorithm suggested by Dr. Vincent A.
     * Cicirello.
     *
     * @return An array with the integers 0, 1, ..., (this.numberOfCities - 1)
     * in a random order.
     */
    public int[] randomPermutation() {
        int[] temp = new int[this.numberOfCities];

        for (int i = 0; i < this.numberOfCities; i++) {
            temp[i] = i;
        }
        for (int i = 0; i < temp.length - 1; i++) {
            int randomIndex = rng.nextInt(i, temp.length);
            int saved = temp[i];
            temp[i] = temp[randomIndex];
            temp[randomIndex] = saved;
        }
        return temp;
    }

    /**
     * Computes either a permutation's fitness, or the cost of the tour
     * represented by the permutation
     *
     * @param member The permutation whose fitness or tour cost is to be
     * computed.
     * @param tour A flag to indicate whether fitness or tour cost is desired;
     * false indicates fitness and true indicates tour cost.
     *
     * @return Either the fitness or tour cost of the population member,
     * depending on the value of the tour parameter.
     */
    public double memberFitness(int[] member, boolean tour) {
        int fitness = 0;
        for (int i = 0; i < member.length; i++) {
            fitness += this.distancesBetweenCities[member[i]][member[(i + 1) % member.length]];
        }
        return tour ? fitness : 42.0 / fitness;
    }

    /**
     * Computes and stores the distance between every pair of cities.
     *
     * @param cityData A pre-processed array that results from parsing a TSPLIB
     * file.
     */
    public void precomputeDistances(String[] cityData) {
        for (int i = 0; i < cityData.length - 1; i++) {
            for (int j = i + 1; j < cityData.length; j++) {
                String[] first = cityData[i].split("[\\s+]");
                String[] second = cityData[j].split("[\\s+]");
                int distance = this.euclideanIntegerDistance(Double.valueOf(
                        first[1]), Double.valueOf(first[2]), Double.valueOf(
                        second[1]), Double.valueOf(second[2]));
                this.distancesBetweenCities[i][j] = this.distancesBetweenCities[j][i] = distance;
            }
        }

    }

    public int euclideanIntegerDistance(double x1, double y1, double x2, double y2) {
        return (int) Math.round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(
                y1 - y2, 2)));
    }

    public double averageFitness() {
        return this.totalFitness() / this.population.length;
    }

    public void iterativeSampling() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < this.numberOfGenerations; i++) {
            int[] temp = this.randomPermutation();
            int cost = (int) this.memberFitness(temp, true);
            if (cost < min) {
                this.bestMember = temp;
                min = cost;
            }
        }
    }

    public int[] VBSS(double b) {
        int startingCity = rng.nextInt(this.numberOfCities);
        int[] tour = new int[this.numberOfCities];
        tour[0] = startingCity;
        Set<Integer> citiesUsed = new HashSet<>();
        citiesUsed.add(startingCity);

        for (int i = 1; i < tour.length; i++) {
            double denominator = 0;
            for (int j = 0; j < this.numberOfCities; j++) {
                if (!citiesUsed.contains(j)) {
                    denominator += 1.0 / Math.pow(
                            this.distancesBetweenCities[startingCity][j], b);
                }
            }

            double[] cityProbabilities = new double[this.numberOfCities - i];
            Map<Integer, Integer> indexesToCities = new HashMap<>();
            int k = 0;
            for (int j = 0; j < cityProbabilities.length; j++) {
                while (citiesUsed.contains(k)) {
                    k++;
                }
                cityProbabilities[j] = (1.0 / Math.pow(
                        this.distancesBetweenCities[startingCity][k], b)) / denominator;
                indexesToCities.put(j, k);
                k++;

            }
            for (int j = 1; j < cityProbabilities.length; j++) {
                cityProbabilities[j] += cityProbabilities[j - 1];
            }
            double randomNumber = rng.nextDouble();
            for (int j = 0; j < cityProbabilities.length; j++) {
                if (randomNumber < cityProbabilities[j]) {
                    int nextCity = indexesToCities.get(j);
                    tour[i] = nextCity;
                    citiesUsed.add(nextCity);
                    startingCity = nextCity;
                    break;
                }
            }
        }
        return tour;
    }

    /**
     * Uses VBSS to attempt to minimize the cost of a tour in the given TSP
     * instance.
     *
     * @param samples The number of samples of VBSS to run.
     * @param b The parameter "B" in the VBSS formula.
     */
    public void VBSS(int samples, double b) {
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < samples; i++) {
            int[] tour = this.VBSS(b);
            int fitness = (int) this.memberFitness(tour, true);
            if (fitness < best) {
                best = fitness;
                this.bestMember = tour;
            }
        }
    }

    /**
     * Prints the lowest cost tour found, followed by the tour itself (given as
     * integers that each represent a city) with one city per line.
     */
    public void printResults() {
        System.out.println((int) this.memberFitness(this.bestMember, true));
        for (int x : this.bestMember) {
            System.out.println(x + 1);
        }
    }
}
