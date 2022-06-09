import java.util.ArrayList;
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
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import com.qqwing.QQWing;

public class SudokuSolverGenetic {

	public static final int maxFitness = 162;
	public static int maxPopulation = 20;

	public static Map<Integer, List<Integer>> MyFirstChromo = new HashMap<>();

	public static void main(String[] args) throws InvalidConfigurationException {

		// Generating new sudoku;
		QQWing mySudoku = new QQWing();
		mySudoku.generatePuzzle();
		mySudoku.printPuzzle();

		// in total we'll have 9 rows
		// each row could have different size due to unknown number of empty cells;
		int[] rowSize = new int[9];
		int chromosomeSize = 0;

		Map<Tuple, Integer> geneRowMap = new HashMap<>();
		Map<Integer, Set<Integer>> sudokuRowNumbersMap = new HashMap<>();

		// to get puzzle;
		int[] puzzle = mySudoku.getPuzzle();

		// Determining each gene size;
		for (int i = 0; i < puzzle.length; i++) {
			System.out.print(puzzle[i] + " ");
			sudokuRowNumbersMap.putIfAbsent(i / 9, new HashSet<Integer>());
			MyFirstChromo.putIfAbsent(i / 9, new ArrayList<Integer>());
			if (puzzle[i] == 0) {
				rowSize[i / 9]++;
				geneRowMap.put(new Tuple(i, chromosomeSize), i / 9);
				chromosomeSize++;
			} else {
				Set<Integer> values = sudokuRowNumbersMap.get(i / 9);
				values.add(puzzle[i]);
				sudokuRowNumbersMap.put(i / 9, values);
			}
		}
		System.out.println();

		// Printing row sizes for testing purposes
		for (int i = 0; i < rowSize.length; i++) {
			System.out.print(rowSize[i] + " ");
		}
		System.out.println();
		System.out.println(geneRowMap.toString());
		System.out.println(sudokuRowNumbersMap.toString());

		Configuration conf = new DefaultConfiguration();

		conf.setKeepPopulationSizeConstant(true);

		Gene[] sampleGene = new Gene[chromosomeSize];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new IntegerGene(conf, 1, 9);
		}
		IChromosome sampleChromosome = new Chromosome(conf, sampleGene);
//		IGeneConstraintChecker constrChecker = new SudokuConstrainChecker(sudokuRowNumbersMap, geneRowMap);
//		sampleChromosome.setConstraintChecker(constrChecker);

		conf.setSampleChromosome(sampleChromosome);
		conf.setPopulationSize(maxPopulation);
		conf.setFitnessFunction(new SudokuFitness());
		conf.setPreservFittestIndividual(true);
		SudokuCrossover sudokuCrossover = new SudokuCrossover(conf, sudokuRowNumbersMap);
		conf.addGeneticOperator(sudokuCrossover);
		Genotype population = generatePopulation(sampleChromosome, conf, geneRowMap, sudokuRowNumbersMap);

// TODO Enable evolution when everything is ready;
		boolean enableEvolution = false;
		if (enableEvolution) {
			while (population.getFittestChromosome().getFitnessValue() != maxFitness) {
				population.evolve();
				System.out.println(
						"Fittest individual has: " + population.getFittestChromosome().getFitnessValue() + " points");
			}
		}
		// Solving sudoku
		// mySudoku.solve();
		// mySudoku.printSolution();
	}

	private static Genotype generatePopulation(IChromosome sampleChromosome, Configuration conf,
			Map<Tuple, Integer> geneRowMap, Map<Integer, Set<Integer>> sudokuRowNumbersMap)
			throws InvalidConfigurationException {
		Genotype genotype;
		Population population = new Population(conf, maxPopulation);
		int chromosomeSize = sampleChromosome.size();

		// Generating one chromosome;
		IChromosome chromo = (IChromosome) sampleChromosome.clone();
		int[] values = generateChromosome(chromosomeSize, geneRowMap, sudokuRowNumbersMap);
		for (int j = 0; j < chromosomeSize; j++) {
			chromo.getGene(j).setAllele(values[j]);
		}
		population.addChromosome(chromo);
		// Cloning this choromosome maxPopulation times;
		for (int i = 0; i < maxPopulation - 1; i++) {
			IChromosome chromoClone = reproduceChromosome(sampleChromosome);
			population.addChromosome(chromoClone);
		}
		genotype = new Genotype(conf, population);
		return genotype;
	}

	private static IChromosome reproduceChromosome(IChromosome sampleChromosome) {
		IChromosome chromosome = (IChromosome) sampleChromosome.clone();
		int gene = 0;
		for (List<Integer> row : MyFirstChromo.values()) {
			Collections.shuffle(row);
			for (Integer allele : row) {
				chromosome.getGene(gene).setAllele(allele);
				gene++;
			}
		}
		return chromosome;
	}

	private static int[] generateChromosome(int cromosomeSize, Map<Tuple, Integer> geneRowMap,
			Map<Integer, Set<Integer>> sudokuRowNumbersMap) {
		int[] chromosomeArr = new int[cromosomeSize];
		for (Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			int i = 1;
			while (sudokuRowNumbersMap.get(entry.getValue()).contains(i)
					|| MyFirstChromo.get(entry.getValue()).contains(i)) {
				i++;
			}
			chromosomeArr[entry.getKey().getGeneNumber()] = i;
			MyFirstChromo.get(entry.getValue()).add(i);
		}
		return chromosomeArr;
	}
}
