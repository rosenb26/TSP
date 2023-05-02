The class with the main() method is Homework12.java.

When running the program from the command line, there are various options:
	If any command line arguments are given, then the first argument must be a 
	filename with a .tsp extension and that file needs to exist in the current  
	working directory. If more than one command line is given, and if the first 
	argument is a valid .tsp filename and the second argument is "GA" (case insensitive),
	then the program will run a genetic algorithm with a population of 50 and 25,000 generations
	on the TSP instance that is supplied. On the smaller TSP instances (e.g. Djibouti and Western Sahara)
	it should take about a minute. 
	(The particulars of the algorithm can be adjusted manually, if desired.)
	
	In all other cases, including ones where there are no command line arguments, the
	program will run 10,000 iterations of VBSS with parameter B = 7. If there are
	no command line arguments, the TSP instance used will be dj38.tsp (Djibouti). Otherwise
	VBSS will be run on the given TSP instance. On the smaller TSP instances the program returns
	almost instantly, with results very close to optimal, or perhaps the optimal cost in some cases.
	
	The output in all cases has the tour cost of the lowest-cost tour found on the first line,
	and the integers corresponding to the cities in the tour are then listed one per line.