import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;

public class GenomeProcessor {

	private final Map<Integer, List<Integer>> MyFirstChromo;
	private final int maxPopulation;
	private final int[] puzzle;
	private final int crhomosomeSize;
	private Map<Tuple, Integer> geneRowMap;
	private Map<Integer, Set<Integer>> sudokuRowNumbersMap;

	private Configuration conf;

	public GenomeProcessor(Configuration conf, int[] puzzle, int maxPopulation) {
		this.conf = conf;
		this.puzzle = puzzle;
		this.maxPopulation = maxPopulation;
		this.MyFirstChromo = new HashMap<>();
		this.geneRowMap = new HashMap<>();
		this.sudokuRowNumbersMap = new HashMap<>();
		
		// in total we'll have 9 rows
		// each row could have different size due to unknown number of empty cells;
		int[] rowSize = new int[9];
		int chromosomeSize = 0;
		// Determining each gene size;
		for (int i = 0; i < puzzle.length; i++) {
			/*
			 * System.out.print(puzzle[i] + " "); if (i % 9 == 8) { System.out.println(); }
			 */
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
		this.crhomosomeSize = chromosomeSize;
	}

	public List<Integer>[] mapCandidate(IChromosome candidate) {
		List<Integer>[] mappedGene = (List<Integer>[]) new ArrayList[9];
		for (int i = 0; i < 9; i++) {
			mappedGene[i] = new ArrayList<Integer>();
		}
		Gene[] genes = candidate.getGenes();

		for (Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			mappedGene[entry.getValue()].add((Integer) genes[entry.getKey().getGeneNumber()].getAllele());
		}
		return mappedGene;
	}

	public IChromosome reconstructChromosome(List<Integer>[] mappedChromo) {
		IChromosome result = (IChromosome) conf.getSampleChromosome().clone();
		int cnt = 0;
		for (int i = 0; i < mappedChromo.length; i++) {
			for (Integer value : mappedChromo[i]) {
				result.getGene(cnt).setAllele(value);
				cnt++;
			}
		}
		return result;
	}

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

	public Genotype generatePopulation() throws InvalidConfigurationException {
		Genotype genotype;
		IChromosome sampleChromosome = conf.getSampleChromosome();
		Population population = new Population(conf, maxPopulation);
		int chromosomeSize = sampleChromosome.size();

		// Generating one chromosome;
		IChromosome chromo = (IChromosome) sampleChromosome.clone();
		int[] values = generateChromosome(chromosomeSize);
		for (int j = 0; j < chromosomeSize; j++) {
			chromo.getGene(j).setAllele(values[j]);
		}
		population.addChromosome(chromo);
		// Cloning this choromosome maxPopulation times;
		for (int i = 0; i < maxPopulation - 1; i++) {
			IChromosome chromoClone = reproduceChromosome();
			population.addChromosome(chromoClone);
		}
		genotype = new Genotype(conf, population);
		return genotype;
	}

	private int[] generateChromosome(int cromosomeSize) {
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
	
	public void printChromosome(IChromosome chromo) {
		
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

	public int getCrhomosomeSize() {
		return crhomosomeSize;
	}
}
