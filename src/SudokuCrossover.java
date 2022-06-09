import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.CrossoverOperator;

public class SudokuCrossover extends CrossoverOperator {
	Random rand = new Random();

	private Map<Tuple, Integer> geneRowMap;
	private IChromosome sampleChromosome;

	private static final long serialVersionUID = -296842531410425494L;

	public SudokuCrossover(Configuration a_configuration, Map<Tuple, Integer> geneRowMap, IChromosome sampleChromosome)
			throws InvalidConfigurationException {
		super(a_configuration);
		this.geneRowMap = geneRowMap;
		this.sampleChromosome = sampleChromosome;
	}

	/**
	 * 
	 * @author panva
	 *
	 * @param <K> The position of the chromosome
	 * @param <V> Absolute fitness of the chromosome from 0 to 100;
	 */
	private class MyEntry<K, V> implements Entry<K, V> {

		private K key;
		private V value;

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public V setValue(V value) {
			return this.value = value;
		}
	}

	@Override
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] crhomosomesToCross = a_population.toChromosomes();
		a_candidateChromosomes.clear();
		// Classification if higher my fitness more probability i have to mate;
		double populationFitness = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			populationFitness += crhomosomesToCross[i].getFitnessValue();
		}
		// How good is my fitness in percentage;
		List<MyEntry<Integer, Double>> fitnessArr = new ArrayList<>();

		// Map<Double, IChromosome> fitChromoMap = new TreeMap<>();
		double fitnessAbs = 0;
		for (int i = 0; i < crhomosomesToCross.length; i++) {
			fitnessAbs += (((crhomosomesToCross[i].getFitnessValue()) / populationFitness) * 100);
			// fitChromoMap.put(fitnessAbs, crhomosomesToCross[i]);
			fitnessArr.add(new MyEntry<Integer, Double>(i, fitnessAbs));
		}
		// Mating loop
		for (int i = 0; i < crhomosomesToCross.length; i += 2) {
			List<IChromosome> crossed = doCrossOver(crhomosomesToCross, fitnessArr);			
			a_candidateChromosomes.addAll(crossed);
		}

	}

	private List<IChromosome> doCrossOver(IChromosome[] crhomosomesToCross, List<SudokuCrossover.MyEntry<Integer, Double>> fitnessArr) {
		int index;
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateOne = (IChromosome) crhomosomesToCross[fitnessArr.get(index).key].clone();		
		index = getIndexOfCandidate(fitnessArr);
		IChromosome candidateTwo = (IChromosome) crhomosomesToCross[fitnessArr.get(index).key].clone();		
		int breakpoint = rand.nextInt(7) + 1;
		
		List<IChromosome> crossed = crossCandidates(candidateOne, candidateTwo, breakpoint);
		return crossed;
	}

	private List<IChromosome> crossCandidates(IChromosome candidateOne, IChromosome candidateTwo, int breakpoint) {
		List<IChromosome> crossed = new ArrayList<>();
		List<Integer>[] mapone = mapCandidate(candidateOne);
		List<Integer>[] maptwo = mapCandidate(candidateTwo);
		System.out.println(Arrays.toString(mapone));
		System.out.println(Arrays.toString(maptwo));
		System.out.println(breakpoint);
		for(int i = breakpoint; i < 9; i++) {
			List<Integer> swapped = mapone[i];
			mapone[i] = maptwo[i];
			maptwo[i] = swapped;
		}
		System.out.println(Arrays.toString(mapone));
		System.out.println(Arrays.toString(maptwo));
		crossed.add(reconstructChromosome(mapone));
		crossed.add(reconstructChromosome(maptwo));
		return crossed;
	}

	private IChromosome reconstructChromosome(List<Integer>[] mappedChromo) {
		IChromosome result = (IChromosome) sampleChromosome.clone();
		int cnt = 0;
		for(int i = 0; i< mappedChromo.length; i++) {
			for(Integer value : mappedChromo[i]) {
				result.getGene(cnt).setAllele(value);
				cnt++;
			}
		}
		return result;
	}

	private List<Integer>[] mapCandidate(IChromosome candidate) {
		List<Integer>[] mappedGene = (List<Integer>[]) new ArrayList[9];
		for(int i = 0; i< 9; i++) {
			mappedGene[i] = new ArrayList<Integer>();
		}
		Gene[] genes = candidate.getGenes();
		
		for(Entry<Tuple, Integer> entry : geneRowMap.entrySet()) {
			mappedGene[entry.getValue()].add((Integer) genes[entry.getKey().getGeneNumber()].getAllele());
		}
		return mappedGene;
	}

	private int getIndexOfCandidate(List<MyEntry<Integer, Double>> fitnessArr) {
		double chance = rand.nextDouble() * 100;
		int j = 0;
		while (chance > fitnessArr.get(j).value && j < fitnessArr.size()) {
			j++;
		}
		return j;
	}

}
