import java.util.List;
import java.util.Random;

import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.MutationOperator;

public class SudokuMutator extends MutationOperator {
	private static final long serialVersionUID = -8039552858576736059L;
	private GenomeProcessor gp;
	private Random rand = new Random();

	public SudokuMutator(GenomeProcessor gp, int a_desiredMutationRate) throws InvalidConfigurationException {
		super(gp.getConf(), a_desiredMutationRate);
		this.gp = gp;
	}

	@Override
	public void operate(Population a_population, List a_candidateChromosomes) {
		IChromosome[] chromosomes = a_population.toChromosomes();
		int mutationRate = getMutationRate();
		for (int i = 0; i < chromosomes.length; i++) {
			List<Integer>[] mappedCandidate = gp.mapCandidate(chromosomes[i]);
			for (int j = 0; j < mappedCandidate.length; j++) {
				for (int k = 0; k < mappedCandidate[j].size(); k++) {
					if (rand.nextInt(101) + 1 <= mutationRate) {
						int other = rand.nextInt(mappedCandidate[j].size());
						int swapWith = mappedCandidate[j].get(other);
						int temp = mappedCandidate[j].get(k);
						mappedCandidate[j].remove(k);
						mappedCandidate[j].add(k, swapWith);
						mappedCandidate[j].remove(other);
						mappedCandidate[j].add(other, temp);
					}
				}
			}
			a_candidateChromosomes.add(gp.reconstructChromosome(mappedCandidate));
		}
	}
}
