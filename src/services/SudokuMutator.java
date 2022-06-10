package services;

import java.util.List;
import java.util.Random;

import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.MutationOperator;

/**
 * The mutation operator runs through the genes in each of the Chromosomes in
 * the population and mutates them in statistical accordance to the given
 * mutation rate. Mutated Chromosomes are then added to the list of candidate
 * Chromosomes destined for the natural selection process.
 *
 * This MutationOperator supports both fixed and dynamic mutation rates. A fixed
 * rate is one specified at construction time by the user. A dynamic rate is
 * determined by this class if no fixed rate is provided, and is calculated
 * based on the size of the Chromosomes in the population. Details are specified
 * in the DefaultMutationRateCalculator class.
 * 
 * @author panva
 *
 */
public class SudokuMutator extends MutationOperator {
	private static final long serialVersionUID = -8039552858576736059L;
	private GenomeProcessor gp;
	private Random rand = new Random();

	public SudokuMutator(GenomeProcessor gp, int a_desiredMutationRate) throws InvalidConfigurationException {
		super(gp.getConf(), a_desiredMutationRate);
		this.gp = gp;
	}

	public SudokuMutator(GenomeProcessor gp) throws InvalidConfigurationException {
		super(gp.getConf());
		this.gp = gp;
	}

	/**
	 * Perform mutation of each chromosome going through each its gene and decides
	 * if it mutates or note In positive case given gene is swapped with randomly
	 * chosen from the same sudoku field
	 * 
	 * @param a_population           the population of chromosomes from the current
	 *                               evolution prior to exposure to any genetic
	 *                               operators. Chromosomes in this array is not be
	 *                               modified.
	 * @param a_candidateChromosomes the pool of chromosomes that have been mutated
	 */
	@Override
	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] chromosomes = a_population.toChromosomes();
		int mutationRate = getMutationRate();
		for (int i = 0; i < chromosomes.length; i++) {
			int[][] mappedCandidate = gp.mapCandidate(chromosomes[i]);
			for (int j = 0; j < mappedCandidate.length; j++) {
				for (int victim = 0; victim < mappedCandidate[j].length; victim++) {
					if (rand.nextInt(101) + 1 <= mutationRate) {
						int candidate = rand.nextInt(mappedCandidate[j].length);
						if (victim != candidate) {
							int tmp = mappedCandidate[j][victim];
							mappedCandidate[j][victim] = mappedCandidate[j][candidate];
							mappedCandidate[j][candidate] = tmp;
						}
					}
				}
			}
			a_candidateChromosomes.add(gp.reconstructChromosome(mappedCandidate));
		}
	}
}
