import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import com.qqwing.QQWing;

public class SudokuSolverGenetic {
	
	public static int maxFitness = 162;
	
	public static void main(String[] args) throws InvalidConfigurationException {

		// generatig new sudoku;
		QQWing mySudoku = new QQWing();
		mySudoku.generatePuzzle();
		mySudoku.printPuzzle();

		// in total we'll have 9 genes, 1 for each row of the sudoku board;
		// each gene could have different size duen to unknown number of empty cells in a given row;
		int[] geneSize = new int[9];
		int chromosomeSize = 0;

		// to get puzzle;
		int[] puzzle = mySudoku.getPuzzle();
		
		// Determining each gene size;
		for (int i = 0; i < puzzle.length; i++) {
			System.out.print(puzzle[i] + " ");
			if (puzzle[i] == 0) {
				geneSize[i/9]++;
				chromosomeSize++;
			}
		}
		System.out.println();
		
		// Printing gene size for testing purposes 
		for(int i = 0; i< geneSize.length; i++) {
			System.out.print(geneSize[i] + " ");
		}
		System.out.println();
		
		
		Configuration conf = new DefaultConfiguration();
		conf.setPreservFittestIndividual(true);
		conf.setKeepPopulationSizeConstant(true);
		

		Gene[] sampleGene = new Gene[chromosomeSize];
		for(int i = 0; i< sampleGene.length; i++) {
			sampleGene[i] = new IntegerGene(conf,1,9);
		}		
		IChromosome sampleChromosome = new Chromosome(conf, sampleGene);
		conf.setSampleChromosome(sampleChromosome);
		conf.setPopulationSize(20);
		conf.setFitnessFunction(new SudokuFitness());		
		Genotype population = Genotype.randomInitialGenotype(conf);
		
		while(population.getFittestChromosome().getFitnessValue() != maxFitness) {
			population.evolve();
			System.out.println("Fittest individual has: " + population.getFittestChromosome().getFitnessValue() + " points");
		}
		

		
		
		// Solving sudoku
		// mySudoku.solve();
		// mySudoku.printSolution();
	}
}
