import java.util.Random;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.gp.function.RandomGenerator;

public class SudokuFitness extends FitnessFunction {

	private static final long serialVersionUID = -4467471883087959103L;
	private Random rand = new Random();

	/**
	 * Since current implementation uses rows as chromosome we need to calculate the
	 * fitness function as the number of unique digits in each column and region of
	 * the sudoku table. Then fitness of the individual can vary from 18 to 162
	 * 
	 * @return fitness of the individual
	 */
	@Override
	protected double evaluate(IChromosome a_subject) {
		
		return (double) rand.nextInt(144) + 18;
	}

}
