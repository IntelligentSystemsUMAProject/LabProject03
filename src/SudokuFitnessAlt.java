import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class SudokuFitnessAlt extends FitnessFunction {
	private static final long serialVersionUID = -7132625076669418940L;
	private GenomeProcessor gp;

	public SudokuFitnessAlt(GenomeProcessor gp) {
		this.gp = gp;
	}

	@Override
	protected double evaluate(IChromosome a_subject) {
		double result = 0;
		int[] reconstructedPuzzle = gp.reconstructPuzzle(a_subject);
		/*
		 * for(int i = 0; i < reconstructedPuzzle.length; i++) {
		 * System.out.printf("%d ", reconstructedPuzzle[i]); if(i % 9 == 8) {
		 * System.out.println(); } }
		 */
		result = columnFitness(reconstructedPuzzle) + RegionFitness(reconstructedPuzzle);
		return result;
	}

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

	private int calculateUnique(Map<Integer, Set<Integer>> mappedPuzzle) {
		int fitness = 0;
		for (Set<Integer> zone : mappedPuzzle.values()) {
			fitness += zone.size();
		}
		return fitness;
	}

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

}
