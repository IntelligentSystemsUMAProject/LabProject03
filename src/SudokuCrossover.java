import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.CrossoverOperator;

public class SudokuCrossover extends CrossoverOperator {
	Random rand = new Random();

	private GenomeProcessor gp;

	private static final long serialVersionUID = -296842531410425494L;

	public SudokuCrossover(GenomeProcessor gp) throws InvalidConfigurationException {
		super(gp.getConf());
		this.gp = gp;
	}

	/**
	 * @author panva
	 *
	 * @param key   The position of the chromosome
	 * @param value Absolute fitness of the chromosome from 0 to 100;
	 */
	private class MyEntry {

		private int key;
		private double value;
		public MyEntry(int key, double value) {
			this.key = key;
			this.value = value;
		}
	}

	@Override
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] crhomosomesToCross = a_population.toChromosomes();
//		a_candidateChromosomes.clear();
		// Classification if higher my fitness more probability i have to mate;
		double populationFitness = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			populationFitness += crhomosomesToCross[i].getFitnessValue();
		}
		// How good is my fitness in percentage;
		List<MyEntry> fitnessArr = new ArrayList<>();

		// Map<Double, IChromosome> fitChromoMap = new TreeMap<>();
		double fitnessAbs = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			fitnessAbs += (((crhomosomesToCross[i].getFitnessValue()) / populationFitness) * 100);
			// fitChromoMap.put(fitnessAbs, crhomosomesToCross[i]);
			fitnessArr.add(new MyEntry(i, fitnessAbs));
		}
		// Mating loop
		for (int i = 0; i < crhomosomesToCross.length; i += 2) {
			List<IChromosome> crossed = doCrossOver(crhomosomesToCross, fitnessArr);
			a_candidateChromosomes.addAll(crossed);
		}
//		System.out.println("End Crossover");
	}

	private List<IChromosome> doCrossOver(IChromosome[] crhomosomesToCross, List<MyEntry> fitnessArr) {
		int index;
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateOne = crhomosomesToCross[fitnessArr.get(index).key];
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateTwo = crhomosomesToCross[fitnessArr.get(index).key];
//		System.out.println(candidateOne.toString());
//		System.out.println(candidateTwo.toString());
		int breakpoint = rand.nextInt(7) + 1;

		List<IChromosome> crossed = crossCandidates(candidateOne, candidateTwo, breakpoint);
		return crossed;
	}

	private List<IChromosome> crossCandidates(IChromosome candidateOne, IChromosome candidateTwo, int breakpoint) {
		List<IChromosome> crossed = new ArrayList<>();
		List<Integer>[] mapone = gp.mapCandidate(candidateOne);
		List<Integer>[] maptwo = gp.mapCandidate(candidateTwo);
		for (int i = breakpoint; i < 9; i++) {
			List<Integer> swapped = mapone[i];
			mapone[i] = maptwo[i];
			maptwo[i] = swapped;
		}
		IChromosome dad = gp.reconstructChromosome(mapone);
		IChromosome mum = gp.reconstructChromosome(maptwo);
//		System.out.println("After crossing:");
//		System.out.println(dad.toString());
//		System.out.println(mum.toString());
		crossed.add(dad);
		crossed.add(mum);
		return crossed;
	}

	private int getIndexOfCandidate(List<MyEntry> fitnessArr) {
		double chance = rand.nextDouble() * 100;
		int j = 0;
		while (chance > fitnessArr.get(j).value && j < fitnessArr.size()) {
			j++;
		}
		return j;
	}

}
