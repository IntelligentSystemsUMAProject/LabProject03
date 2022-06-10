package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DefaultFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StockRandomGenerator;

import com.qqwing.QQWing;

import services.*;

public class SudokuSolverGenetic {

	public static final int maxFitness = 162;
	public static int maxPopulation = 1000;
	public static int iterNum = 1000;

	public static void main(String[] args) throws InvalidConfigurationException, FileNotFoundException {
		PrintStream output = new PrintStream(new File("output.txt"));
		PrintStream console = System.out;

		// Generating new sudoku;
		QQWing mySudoku = new QQWing();
		mySudoku.generatePuzzle();
		System.out.println("Initial Puzzle:");
		mySudoku.printPuzzle();

		switchToFile(output);
		System.out.println("Initial Puzzle:");
		mySudoku.printPuzzle();
		switchToConsole(console);

		Configuration conf = new Configuration();
		GenomeProcessor gp = new GenomeProcessor(conf, mySudoku.getPuzzle(), maxPopulation);
		Gene[] sampleGene = new Gene[gp.getChromosomeSize()];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new IntegerGene(conf, 1, 9);
		}

		conf.setMinimumPopSizePercent(0); // we allow to drop population to 0
		conf.setKeepPopulationSizeConstant(false);
		conf.setChromosomePool(new ChromosomePool()); // we save computation resources allowing to store some discarded
														// chromosome in a pool, that will be used later by the system
		conf.setSelectFromPrevGen(1); // We don't allow to generate new chromosomes. All chromosomes must be
										// reused/taken from the previous generation
		conf.setPopulationSize(maxPopulation); // Setting up maximum population
		conf.addGeneticOperator(new SudokuCrossover(gp, 95)); // Crossover function. Percentage of individuums to cross
																// can be specified or left empty
		conf.addGeneticOperator(new SudokuMutator(gp, 5)); // Mutation function. Mutation rate can be specified or left
															// empty (random)
		conf.setRandomGenerator(new StockRandomGenerator()); // default java random generator is used for internal need
																// of the algorithm
		conf.setEventManager(new EventManager()); // default event manager is used
		conf.setFitnessEvaluator(new DefaultFitnessEvaluator());
		conf.addNaturalSelector(new BestChromosomesSelector(conf, 0.25), false); // Only 25 percent of the best
																					// individuums goes to the next
																					// generation. Other 75 percent is
																					// taken from the pool
		conf.setPreservFittestIndividual(true); // Elitism is enabled. Best individuums survive
		conf.setFitnessFunction(new SudokuFitness(gp)); // Fitness function we use;
		conf.setSampleChromosome(new Chromosome(conf, sampleGene)); // Chromosome for references in a sense of size and
																	// gene type

		Genotype population = gp.generatePopulation();

		// Enable evolution when everything is ready;
		boolean enableEvolution = true;
		double fitness = 0;
		if (enableEvolution) {
			while (population.getFittestChromosome().getFitnessValue() != maxFitness && iterNum > 0) {
				population.evolve();
				double currMaxFitness = population.getFittestChromosome().getFitnessValue();
				if (currMaxFitness > fitness) {
					fitness = currMaxFitness;
					System.out.println("Fittest individual has: " + fitness + " points");
					switchToFile(output);
					System.out.println("Fittest individual has: " + fitness + " points");
					switchToConsole(console);
				}
				iterNum--;
			}
		}
		System.out.println(
				"Best individual found has " + population.getFittestChromosome().getFitnessValue() + " points.");
		System.out.println();
		System.out.println("One of the solutions:");
		mySudoku.solve();
		mySudoku.printSolution();
		mySudoku.setPuzzle(gp.reconstructPuzzle(population.getFittestChromosome()));
		System.out.println("Best approximation:");
		mySudoku.printPuzzle();

		switchToFile(output);
		System.out.println(
				"Best individual found has " + population.getFittestChromosome().getFitnessValue() + " points.");
		System.out.println();
		System.out.println("One of the solutions:");
		mySudoku.solve();
		mySudoku.printSolution();
		mySudoku.setPuzzle(gp.reconstructPuzzle(population.getFittestChromosome()));
		System.out.println("Best approximation:");
		mySudoku.printPuzzle();
		switchToConsole(console);

		output.close();
	}

	private static void switchToConsole(PrintStream console) {
		System.setOut(console);
	}

	private static void switchToFile(PrintStream output) {
		System.setOut(output);
	}

}
