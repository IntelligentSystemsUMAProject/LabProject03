package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.CrossoverOperator;

/**
 * The crossover operator selects, based on their fitness, two Chromosomes from
 * the population and "mates" them by randomly picking a breaking point in
 * genome and then swapping all subsequent genes between the two Chromosomes.
 * The two modified Chromosomes are then added to the list of candidate
 * Chromosomes. This CrossoverOperator supports both fixed and dynamic crossover
 * rates.A fixed rate is one specified at construction time by the user. This
 * operation is performed 1/m_crossoverRate as many times as there are
 * Chromosomes in the population. Another possibility is giving the crossover
 * rate as a percentage. A dynamic rate is determined by this class on the fly
 * if no fixed rate is provided.
 * 
 * @author panva
 */
public class SudokuCrossover extends CrossoverOperator {
	Random rand = new Random();

	private GenomeProcessor gp;

	private static final long serialVersionUID = -296842531410425494L;

	public SudokuCrossover(GenomeProcessor gp, int crossoverRate) throws InvalidConfigurationException {
		super(gp.getConf(), crossoverRate);
		this.gp = gp;
	}

	public SudokuCrossover(GenomeProcessor gp) throws InvalidConfigurationException {
		super(gp.getConf());
		this.gp = gp;
	}

	/**
	 * Auxiliary class just to save pair of chromosome position = absolute fitness
	 * 
	 * @param key   The position of the chromosome
	 * @param value Absolute fitness of the chromosome from 0 to 100;
	 * 
	 * @author panva
	 */
	private class MyEntry {

		private int key;
		private double value;

		public MyEntry(int key, double value) {
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * Does the crossing over. Firstly it calculates the total population fitness,
	 * then for each individuum we calculate its relative fitness and sum this value
	 * to the absolute fitness value. We store in a ordered by absolute fitness List
	 * the object of class MyEntry. If larger the value of relative one have, more
	 * chances he has to mate.
	 *
	 * @param a_population           the population of chromosomes from the current
	 *                               evolution prior to exposure to crossing over
	 * @param a_candidateChromosomes the pool of chromosomes that have been selected
	 *                               for the next evolved population
	 */
	@Override
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] crhomosomesToCross = a_population.toChromosomes();
		double populationFitness = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			populationFitness += crhomosomesToCross[i].getFitnessValue();
		}
		// How good is my fitness in percentage;
		List<MyEntry> fitnessArr = new ArrayList<>();

		double fitnessAbs = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			fitnessAbs += (((crhomosomesToCross[i].getFitnessValue()) / populationFitness) * 100);
			fitnessArr.add(new MyEntry(i, fitnessAbs));
		}
		// Mating loop
		for (int i = 0; i < crhomosomesToCross.length; i += 2) {
			List<IChromosome> crossed = doCrossOver(crhomosomesToCross, fitnessArr);
			a_candidateChromosomes.addAll(crossed);
		}
	}

	/**
	 * Pick two candidates from the array of individuums based on their fitness and
	 * randomly chooses the break points in the chromosome
	 * 
	 * @param crhomosomesToCross array of chromosomes
	 * @param fitnessArr         fitness array stores individuum position in the
	 *                           array and its absolute fitness
	 * @return
	 */
	private List<IChromosome> doCrossOver(IChromosome[] crhomosomesToCross, List<MyEntry> fitnessArr) {
		int index;
		// Choosing index based on fitness of the chromosome 4 lines;
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateOne = crhomosomesToCross[fitnessArr.get(index).key];
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateTwo = crhomosomesToCross[fitnessArr.get(index).key];

		int breakpoint = rand.nextInt(7) + 1;

		List<IChromosome> crossed = crossCandidates(candidateOne, candidateTwo, breakpoint);
		return crossed;
	}

	/**
	 * Does actual cross over
	 * 
	 * @param candidateOne chromosome to be crossed with the other one
	 * @param candidateTwo chromosome to be crossed with the other one
	 * @param breakpoint   that determines where exactly a chromosome will be split
	 * @return List that holds both chromosomes after the cross over operation
	 */
	private List<IChromosome> crossCandidates(IChromosome candidateOne, IChromosome candidateTwo, int breakpoint) {
		List<IChromosome> crossed = new ArrayList<>();
		int[][] mappedOne = gp.mapCandidate(candidateOne);
		int[][] mappedTwo = gp.mapCandidate(candidateTwo);
		int[] swap;
		for (int i = breakpoint; i < 9; i++) {
			swap = mappedOne[i];
			mappedOne[i] = mappedTwo[i];
			mappedTwo[i] = swap;
		}
		IChromosome dad = gp.reconstructChromosome(mappedOne);
		IChromosome mom = gp.reconstructChromosome(mappedTwo);
		crossed.add(dad);
		crossed.add(mom);
		return crossed;
	}

	/**
	 * Determines based on the fitness of each chromosome the bes suitable
	 * candidate;
	 * 
	 * @param fitnessArr fitness array stores individuum position in the array and
	 *                   its absolute fitness
	 * @return position of the candidate for cross over
	 */
	private int getIndexOfCandidate(List<MyEntry> fitnessArr) {
		double chance = rand.nextDouble() * 100;
		int j = 0;
		while (chance > fitnessArr.get(j).value && j < fitnessArr.size()) {
			j++;
		}
		return j;
	}

}
