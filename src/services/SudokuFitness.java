package services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/**
 * Fitness function are used to determine how optimal a particular solution is
 * relative to other solutions. The higher the value, the more fit the
 * Chromosome. The actual range of fitness values is ranged from 18 to 162
 * (counting unique numbers in each constrain domain)
 * 
 * @author panva
 *
 */
public class SudokuFitness extends FitnessFunction {
	private static final long serialVersionUID = -7132625076669418940L;
	private GenomeProcessor gp;

	public SudokuFitness(GenomeProcessor gp) {
		this.gp = gp;
	}

	/**
	 * Determine the fitness of the given Chromosome instance. The higher the return
	 * value, the more fit the instance. This method always returns the same fitness
	 * value for two equivalent Chromosome instances.
	 * 
	 * First it reconstructs the puzzle and then evaluate the score for the column
	 * constrain and region constrain
	 */
	@Override
	protected double evaluate(IChromosome a_subject) {
		double result = 0;
		int[] reconstructedPuzzle = gp.reconstructPuzzle(a_subject);
		result = columnFitness(reconstructedPuzzle) + RegionFitness(reconstructedPuzzle);
		return result;
	}

	/**
	 * Calculates the fitness of chromosome looking at the column constrain. Each
	 * unique number in the column gives a 1 point to the final fitness
	 * 
	 * @param reconstructedPuzzle sudoku representation in a form of unidimensional
	 *                            array
	 * @return fitness applied to the column constrain
	 */
	private int columnFitness(int[] reconstructedPuzzle) {
		Map<Integer, Set<Integer>> columnElem = new HashMap<>();
		int fitness = 0;
		for (int i = 0; i < 9; i++) {
			columnElem.put(i, new HashSet<Integer>());
		}
		for (int i = 0; i < reconstructedPuzzle.length; i++) {
			columnElem.get(i % 9).add(reconstructedPuzzle[i]);
		}
		fitness = calculateUnique(columnElem);
		return fitness;
	}

	/**
	 * Calculates the fitness of chromosome looking at the region constrain. Each
	 * unique number in the region gives a 1 point to the final fitness
	 * 
	 * @param reconstructedPuzzle sudoku representation in a form of unidimensional
	 *                            array
	 * @return fitness applied to the region constrain
	 */
	private int RegionFitness(int[] reconstructedPuzzle) {
		Map<Integer, Set<Integer>> regionElem = new HashMap<>();
		int fitness = 0;
		for (int i = 0; i < 9; i++) {
			regionElem.put(i, new HashSet<Integer>());
		}
		for (int i = 0; i < reconstructedPuzzle.length; i++) {
			int row = i / 9;
			int col = i % 9;
			if (row >= 0 && row < 3 && col >= 0 && col < 3) {
				regionElem.get(0).add(reconstructedPuzzle[i]);
			} else if (row >= 3 && row < 6 && col >= 0 && col < 3) {
				regionElem.get(3).add(reconstructedPuzzle[i]);
			} else if (row >= 6 && row < 9 && col >= 0 && col < 3) {
				regionElem.get(6).add(reconstructedPuzzle[i]);
			} else if (row >= 0 && row < 3 && col >= 3 && col < 6) {
				regionElem.get(1).add(reconstructedPuzzle[i]);
			} else if (row >= 0 && row < 3 && col >= 6 && col < 9) {
				regionElem.get(2).add(reconstructedPuzzle[i]);
			} else if (row >= 3 && row < 6 && col >= 3 && col < 6) {
				regionElem.get(4).add(reconstructedPuzzle[i]);
			} else if (row >= 3 && row < 6 && col >= 6 && col < 9) {
				regionElem.get(5).add(reconstructedPuzzle[i]);
			} else if (row >= 6 && row < 9 && col >= 3 && col < 6) {
				regionElem.get(7).add(reconstructedPuzzle[i]);
			} else if (row >= 6 && row < 9 && col >= 6 && col < 9) {
				regionElem.get(8).add(reconstructedPuzzle[i]);
			}
		}
		fitness = calculateUnique(regionElem);
		return fitness;
	}

	/**
	 * Calculates all unique numbers in a given sudoku representation
	 * 
	 * @param mappedPuzzle Sudoku representation in a form of map. Depending on the
	 *                     constrain applied, key element of the map is the zone
	 *                     (column, row, or region) index, values are fields in the
	 *                     actual sudoku that located in a given zone.
	 * @return fitness value;
	 */
	private int calculateUnique(Map<Integer, Set<Integer>> mappedPuzzle) {
		int fitness = 0;
		for (Set<Integer> zone : mappedPuzzle.values()) {
			fitness += zone.size();
		}
		return fitness;
	}

}
