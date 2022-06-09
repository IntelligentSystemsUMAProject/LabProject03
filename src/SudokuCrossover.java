import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.CrossoverOperator;

public class SudokuCrossover extends CrossoverOperator {
	Random rand = new Random();
	
	private class MyEntry<K, V> implements Entry<K, V>{
		
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

	private Map<Integer, Set<Integer>> sudokuRowNumbersMap;

	private static final long serialVersionUID = -296842531410425494L;

	public SudokuCrossover(Configuration a_configuration, Map<Integer, Set<Integer>> sudokuRowNumbersMap)
			throws InvalidConfigurationException {
		super(a_configuration);
		this.sudokuRowNumbersMap = sudokuRowNumbersMap;
	}

	@Override
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] crhomosomesToCross = a_population.toChromosomes();
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
		for (int i = 0; i < a_population.size(); i++) {
			double chance = rand.nextDouble() * 100;
			// TODO Iterathe through fitness array and search for candidate;
		}

	}

}
