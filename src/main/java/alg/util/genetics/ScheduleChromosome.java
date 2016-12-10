package alg.util.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.jenetics.Chromosome;
import org.jenetics.util.ISeq;

import alg.util.genetics.ScheduleChromosome;

/**
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 * Genetic algorithm's chromosome 
 *
 */
public class ScheduleChromosome implements Chromosome<ScheduleGene> 
{


	/**
	 * Scheduling sequence of ScheduleItem's genes
	 */
	public ISeq<ScheduleGene> scheduleSeq;
	private int numTasks;
	private int numExecutors;
	private int[][] delta;


	public ScheduleChromosome(int[][] delta, int numExecutors) {
		ArrayList<ScheduleAllel> toSchedule = new ArrayList<ScheduleAllel>();
		ArrayList<ScheduleGene> myList = new ArrayList<ScheduleGene>();
		ArrayList<ScheduleAllel> allocable = null;
		ScheduleGene gene =  null;;
		boolean clean = false;
		
		this.delta = new int[delta.length][delta.length];

		for (int i = 0; i < delta.length; i++)
		{
			for (int j = 0; j < delta.length; j++)
			{
				this.delta[i][j] = delta[i][j];
			}
		}
		
		this.numTasks = delta.length;
		this.numExecutors = numExecutors;
		
		gene =  ScheduleGene.of(numTasks, numExecutors);

		scheduleSeq = ISeq.of();

		for (int i = 0; i < numTasks; i++)
		{
			toSchedule.add(new ScheduleAllel(numTasks, numExecutors, i));
		}

		while (!toSchedule.isEmpty())
		{
			allocable =  new ArrayList<ScheduleAllel>();

			for (ScheduleAllel currItem: toSchedule)
			{
				clean = true;
				for (int j = 0; j < delta.length; j++)
				{
					if (this.delta[j][currItem.getTaskID()] != 0)
					{
						clean = false;
						break;
					}

				}

				if (!clean) continue;

				allocable.add(currItem);

			}
			
			if (allocable.isEmpty())
			{
				break;
			}

			Collections.shuffle(allocable);

			myList.add(gene.newInstance(allocable.get(0)));

			toSchedule.remove(allocable.get(0));
			
			for (int j = 0; j < delta.length; j++)
			{
				this.delta[allocable.get(0).taskID][j] = 0;
			}
		}

		scheduleSeq = ISeq.of(myList);

	}

	public ScheduleChromosome(int[][] delta, int numExecutors, ISeq<ScheduleGene> genes) {
		
		this.delta = delta;
		this.numTasks = delta.length;
		this.numExecutors = numExecutors;
		
		scheduleSeq = genes;
	}

	/* (non-Javadoc)
	 * @see org.jenetics.util.Verifiable#isValid()
	 */
	@Override
	public boolean isValid() {
		
		boolean[] allocated = new boolean[numTasks];
		
		for (ScheduleGene currGene: scheduleSeq)
		{
			if (allocated[currGene.getAllele().getTaskID()])
			{
				return false;
			}
			
			for (int j = 0; j < delta.length; j++)
			{
				if (this.delta[j][currGene.getAllele().getTaskID()] != 0)
				{
					return false;
				}
			}
			
			allocated[currGene.getAllele().getTaskID()] = true;
			
			for (int j = 0; j < delta.length; j++)
			{
				this.delta[currGene.getAllele().getTaskID()][j] = 0;
			}
		}
		
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ScheduleGene> iterator() {			
		return scheduleSeq.iterator();
	}



	/* (non-Javadoc)
	 * @see org.jenetics.util.Factory#newInstance()
	 */
	@Override
	public Chromosome<ScheduleGene> newInstance() {
		return new ScheduleChromosome(delta, numExecutors);

	}

	/* (non-Javadoc)
	 * @see org.jenetics.Chromosome#newInstance(org.jenetics.util.ISeq)
	 */
	@Override
	public Chromosome<ScheduleGene> newInstance(ISeq<ScheduleGene> genes) {
		return new ScheduleChromosome(delta, numExecutors, genes);
	}

	/* (non-Javadoc)
	 * @see org.jenetics.Chromosome#getGene(int)
	 */
	@Override
	public ScheduleGene getGene(int index) {
		return scheduleSeq.get(index);
	}

	/* (non-Javadoc)
	 * @see org.jenetics.Chromosome#length()
	 */
	@Override
	public int length() {
		return scheduleSeq.length();
	}

	/* (non-Javadoc)
	 * @see org.jenetics.Chromosome#toSeq()
	 */
	@Override
	public ISeq<ScheduleGene> toSeq() {
		return scheduleSeq;
	}

	/**
	 * TODO: Fill this
	 * @param delta
	 * @param numExecutors
	 * @return
	 */
	public static Chromosome<ScheduleGene> of(int[][] delta, int numExecutors) {
		return new ScheduleChromosome(delta, numExecutors);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return scheduleSeq.toString();
	}

}