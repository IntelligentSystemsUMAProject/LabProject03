package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;

/**
 * Auxiliary class that hold all information related to the sudoku field, such
 * as different map structures that describe the sudoku field, size of each
 * empty row, position of gene in a chromosome
 * 
 * @author panva
 */
public class GenomeProcessor {
	private final Map<Integer, List<Integer>> MyFirstChromo;
	private final int maxPopulation;
	private final int[] puzzle;
	private final int chromosomeSize;
	private Map<Tuple, Integer> geneRowMap;
	private Map<Integer, Set<Integer>> sudokuRowNumbersMap;
	private int[] rowSize;

	private Configuration conf;

	/**
	 * 
	 * @param conf          configuration of the genetic algorithm
	 * @param puzzle        puzzle description as an unidimensional array
	 * @param maxPopulation Maximum population of the genetic algorithm
	 */
	public GenomeProcessor(Configuration conf, int[] puzzle, int maxPopulation) {
		this.conf = conf;
		this.puzzle = puzzle;
		this.maxPopulation = maxPopulation;
		this.MyFirstChromo = new HashMap<>();
		this.geneRowMap = new TreeMap<>();

		this.sudokuRowNumbersMap = new HashMap<>();

		// in total we'll have 9 rows
		// each row could have different size due to unknown number of empty cells;
		int[] rowSize = new int[9];
		int chromosomeSize = 0;
		// Determining each gene size;
		for (int i = 0; i < puzzle.length; i++) {
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
		this.chromosomeSize = chromosomeSize;
		this.rowSize = rowSize;
	}

	/**
	 * Converts a chromosome into a two-dimensional array such that each array of
	 * the lower level holds genes that corresponds to those in a same field of the
	 * sudoku table
	 * 
	 * @param candidate a Chromosome we want to convert in two-dimensional array
	 * @return chromosome representation as two-dimensional array
	 */
	public int[][] mapCandidate(IChromosome candidate) {
		int[][] mappedChromo = new int[9][];
		for (int i = 0; i < 9; i++) {
			mappedChromo[i] = new int[rowSize[i]];
		}
		Gene[] genes = candidate.getGenes();
		int pos = 0;
		for (Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			mappedChromo[entry.getValue()][pos] = (int) genes[entry.getKey().getGeneNumber()].getAllele();
			pos++;
			if (pos == mappedChromo[entry.getValue()].length) {
				pos = 0;
			}
		}
		return mappedChromo;
	}

	/**
	 * Converts back a two-dimensional array of int into a chromosome
	 * 
	 * @param mappedChromo A two-dimensional array that represents chromosome
	 * @return reconstructed chromosome of type IChromosome
	 */
	public IChromosome reconstructChromosome(int[][] mappedChromo) {
		IChromosome result = (IChromosome) conf.getSampleChromosome().clone();
		int cnt = 0;
		for (int i = 0; i < mappedChromo.length; i++) {
			for (int j = 0; j < mappedChromo[i].length; j++) {
				result.getGene(cnt).setAllele(mappedChromo[i][j]);
				cnt++;
			}
		}
		return result;
	}

	/**
	 * Having a possible solution stored in the chromosome and the initially given
	 * sudoku puzzle combine two on this datums and produce complete sudoku as a
	 * unidimensional array;
	 * 
	 * @param chromo chromosome that holds possible solution for the sudoku puzzle
	 * @return array of size of 81 that represents entire sudoku table
	 */
	public int[] reconstructPuzzle(IChromosome chromo) {
		int[] result = Arrays.copyOf(puzzle, puzzle.length);
		Gene[] genes = chromo.getGenes();
		for (int i = 0; i < puzzle.length; i++) {
			if (puzzle[i] != 0) {
				result[i] = puzzle[i];
			}
		}
		for (Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			result[entry.getKey().getCellNumber()] = (int) genes[entry.getKey().getGeneNumber()].getAllele();
		}
		return result;
	}

	/**
	 * Creates initial population to begin to work with
	 * 
	 * @return initial population
	 * @throws InvalidConfigurationException
	 */
	public Genotype generatePopulation() throws InvalidConfigurationException {
		Genotype genotype;
		Population population = new Population(conf, maxPopulation);

		IChromosome chromo = generateChromosome();
		population.addChromosome(chromo);
		// Cloning this chromosome maxPopulation times;
		for (int i = 0; i < maxPopulation - 1; i++) {
			IChromosome chromoClone = reproduceChromosome();
			population.addChromosome(chromoClone);
		}
		genotype = new Genotype(conf, population);
		return genotype;
	}

	/**
	 * Generates a one single chromosome that satisfies the row constrain Stores
	 * this first chromosome as a template for future use
	 * 
	 * @return chromosome
	 */
	private IChromosome generateChromosome() {
		int[] chromosomeArr = new int[chromosomeSize];
		for (Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			int i = 1;
			while (sudokuRowNumbersMap.get(entry.getValue()).contains(i)
					|| MyFirstChromo.get(entry.getValue()).contains(i)) {
				i++;
			}
			chromosomeArr[entry.getKey().getGeneNumber()] = i;
			MyFirstChromo.get(entry.getValue()).add(i);
		}
		IChromosome chromo = (IChromosome) conf.getSampleChromosome().clone();
		for (int j = 0; j < chromosomeSize; j++) {
			chromo.getGene(j).setAllele(chromosomeArr[j]);
		}
		return chromo;
	}

	/**
	 * generates copies of the first chromosome mixing the content of each row
	 * 
	 * @return chromosome
	 */
	private IChromosome reproduceChromosome() {
		IChromosome chromosome = (IChromosome) conf.getSampleChromosome().clone();
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

	/**
	 * Prints on the screen genes of given chromosome in form of a line of alleles
	 * Auxiliary methods for debug purposes.
	 * 
	 * @param chromo chromosome to be printed
	 */
	public void printChromosome(IChromosome chromo) {
		Gene[] genes = chromo.getGenes();
		for (int i = 0; i < genes.length; i++) {
			System.out.print(genes[i].getAllele() + " ");
		}
		System.out.println();
	}

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public Map<Tuple, Integer> getGeneRowMap() {
		return geneRowMap;
	}

	public Map<Integer, Set<Integer>> getSudokuRowNumbersMap() {
		return sudokuRowNumbersMap;
	}

	public int[] getPuzzle() {
		return puzzle;
	}

	public int getChromosomeSize() {
		return chromosomeSize;
	}
}
