package services;

import java.util.Map;
import java.util.Map.Entry;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class SudokuFitnessBroken extends FitnessFunction {

	private static final long serialVersionUID = -4467471883087959103L;
	private Map<Tuple, Integer> geneRowMap;
	private int[] puzzle;

	/**
	 * Since current implementation uses rows as chromosome we need to calculate the
	 * fitness function as the number of unique digits in each column and region of
	 * the sudoku table. Then fitness of the individual can vary from 18 to 162
	 * 
	 * @return fitness of the individual
	 */

	public SudokuFitnessBroken(Map<Tuple, Integer> geneRowMap, int[] puzzle) {
		this.geneRowMap = geneRowMap;
		this.puzzle = puzzle;
	}

	@Override
	protected double evaluate(IChromosome a_subject) {
		Gene[] genes = a_subject.getGenes();
		int[][] columnsArray = columnsArray(completePuzzle(genes));
		int[][] regionsArray = regionsArray(completePuzzle(genes));
		double result = fitnessColumn(columnsArray) + fitnessRegion(regionsArray);
		// System.out.println(result);
		return result;
	}

	private int[] completePuzzle(Gene[] genes) {
		int geneNumber = 0;
		int gene = 0;
		for (int i = 0; i < puzzle.length; i++) {
			if (puzzle[i] == 0) {
				for (Entry<Tuple, Integer> e : geneRowMap.entrySet()) {
					if (i == e.getKey().getCellNumber()) {
						geneNumber = e.getKey().getGeneNumber();
					}
				}
				gene = (int) genes[geneNumber].getAllele();
				puzzle[i] = gene;
			}
		}

		return puzzle;
	}

	private int[][] columnsArray(int[] puzzle) {
		int[][] doubleArray = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				doubleArray[i][j] = puzzle[i + 9 * j];
			}
		}

		return doubleArray;
	}

	private int[][] regionsArray(int[] puzzle) {
		int[][] regionsArray = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					regionsArray[i][k + j * 3] = puzzle[(3 * (i % 3) + k + (j + i / 3 * 3) * 9)];
				}
			}
		}

		return regionsArray;
	}

	private int fitnessColumn(int[][] columnsArray) {
		int result = 1;

		for (int j = 0; j < columnsArray.length; j++) {
			for (int i = 1; i < columnsArray.length; i++) {
				int k;
				for (k = 0; k < i; k++) {
					if (columnsArray[i][j] == columnsArray[k][j]) {
						break;
					}
				}

				if (i == k) {
					result++;
				}
			}
		}

		return result;
	}

	private int fitnessRegion(int[][] regionsArray) {
		int result = 1;

		for (int j = 0; j < regionsArray.length; j++) {
			for (int i = 1; i < regionsArray.length; i++) {
				int k;
				for (k = 0; k < i; k++) {
					if (regionsArray[i][j] == regionsArray[k][j]) {
						break;
					}
				}

				if (i == k) {
					result++;
				}
			}
		}

		return result;
	}

}
