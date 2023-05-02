
import java.io.File;
import java.util.Scanner;

/**
 * Artificial Intelligence, Spring 2023, Stockton University.
 *
 * @author Menachem Rosenberg
 * @author Robert Purnell Thurston
 */
public class Homework12 {

    public static void main(String[] args) throws Exception {

        String[] cityData;

        if (args.length == 0) {
            cityData = parseTSPLIBFile("dj38.tsp");
        }
        else {
            cityData = parseTSPLIBFile(args[0]);

        }
        TSPSolver solver = new TSPSolver(50, cityData.length, 25000);
        solver.precomputeDistances(cityData);

        if (args.length > 1 && args[1].equalsIgnoreCase("ga")) {

            solver.generateInitialPopulation();
            solver.evolve();

        }
        else {
            solver.VBSS(10000, 7);
        }
        solver.printResults();
    }

    public static String[] parseTSPLIBFile(String fileName) throws Exception {
        Scanner reader = new Scanner(new File(fileName));
        int size = 0;

        /*
         * Extract the dimension of the TSP instance, i.e. the number of cities
         * in a tour of this TSP instance.
         */
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            while (!line.startsWith("DIMENSION")) {
                line = reader.nextLine();

            }
            size = Integer.valueOf(line.split(":")[1].strip());
            break;
        }

        String[] cityCoordinates = new String[size];

        reader = new Scanner(new File(fileName));
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            while (!isInteger(line.split("[\\s+]")[0])) {
                line = reader.nextLine();
            }
            cityCoordinates[0] = line;
            for (int i = 1; i < size; i++) {
                cityCoordinates[i] = reader.nextLine();
            }
            break;
        }
        return cityCoordinates;
    }

    public static boolean isInteger(String x) {
        try {
            Integer.valueOf(x);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

}
