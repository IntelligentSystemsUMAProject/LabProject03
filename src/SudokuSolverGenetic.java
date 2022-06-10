import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import temp.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.GaussianRandomGenerator;
import org.jgap.impl.IntegerGene;

import com.qqwing.QQWing;

public class SudokuSolverGenetic {

	public static final int maxFitness = 162;
	public static int maxPopulation = 2000;

	

	public static void main(String[] args) throws InvalidConfigurationException {

		// Generating new sudoku;
		QQWing mySudoku = new QQWing();
		mySudoku.generatePuzzle();
		mySudoku.printPuzzle();
		
		System.out.println();

		// Printing row sizes for testing purposes
		/*
		 * for (int i = 0; i < rowSize.length; i++) { System.out.print(rowSize[i] +
		 * " "); } System.out.println(); System.out.println(geneRowMap.toString());
		 * System.out.println(sudokuRowNumbersMap.toString());
		 */
		Configuration conf = new DefaultConfiguration();
		conf.getGeneticOperators().clear();
		conf.removeNaturalSelectors(false);
		GenomeProcessor gp = new GenomeProcessor(conf, mySudoku.getPuzzle(), maxPopulation);

		conf.setKeepPopulationSizeConstant(false);
		conf.setMinimumPopSizePercent(0);
		conf.setSelectFromPrevGen(1);
		Gene[] sampleGene = new Gene[gp.getCrhomosomeSize()];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new IntegerGene(conf, 1, 9);
		}
//		conf.setRandomGenerator(new GaussianRandomGenerator());
//		conf.setEventManager(new EventManager());
		conf.addNaturalSelector(new BestChromosomesSelector(conf, 0.95), false);
		conf.setSampleChromosome(new Chromosome(conf, sampleGene));
		conf.setPopulationSize(maxPopulation);
		SudokuFitness sudokuFitness = new SudokuFitness(gp.getGeneRowMap(), gp.getPuzzle());
		SudokuFitnessAlt sudokuFitnessAlt = new SudokuFitnessAlt(gp);
		conf.setFitnessFunction(sudokuFitnessAlt);
		conf.setPreservFittestIndividual(true);
		SudokuCrossover sudokuCrossover = new SudokuCrossover(gp);
		conf.addGeneticOperator(sudokuCrossover);
		SudokuMutator sudokuMutator = new SudokuMutator(gp, 10);
		conf.addGeneticOperator(sudokuMutator);
		conf.setChromosomePool(new ChromosomePool());
		Genotype population = gp.generatePopulation();

// TODO Enable evolution when everything is ready;
		boolean enableEvolution = true;
		int iterNum = 2000;
		double fitness = 0;
		if (enableEvolution) {
			while (population.getFittestChromosome().getFitnessValue() != maxFitness && iterNum > 0) {
				population.evolve();
				/*
				 * IChromosome[] testChromo = population.getPopulation().toChromosomes();
				 * for(int i = 0; i < testChromo.length; i++) { Gene[] genes =
				 * testChromo[i].getGenes(); for(int j = 0; j < genes.length; j++) {
				 * System.out.printf("%d ", genes[j].getAllele()); } System.out.println(); }
				 * System.out.println("---");
				 */
				double currMaxFitness = population.getFittestChromosome().getFitnessValue();
				if (currMaxFitness > fitness) {
					fitness = currMaxFitness;
					System.out.println("Fittest individual has: " + fitness + " points");
				}
				iterNum--;
			}
		}
		System.out.println("Best individual found has " + population.getFittestChromosome().getFitnessValue() + " points.");
		System.out.println("Real Solution");
		mySudoku.solve();
		mySudoku.printSolution();
		mySudoku.setPuzzle(gp.reconstructPuzzle(population.getFittestChromosome()));
		System.out.println("calculated Solution");
		mySudoku.printPuzzle();
		// Solving sudoku
		// mySudoku.solve();
		// mySudoku.printSolution();
	}
}
