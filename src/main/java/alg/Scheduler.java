/**
 * 
 */
package alg;

import java.util.Iterator;
import java.util.Random;

import org.jenetics.AnyGene;
import org.jenetics.Chromosome;
import org.jenetics.util.ISeq;

/**
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class Scheduler {

	/**
	 * Execution Time matrix with ETC[i][j] where i is the
	 * node and j is the task index. 
	 */ 
	protected double[][] ETC;

	/**
	 * Dependency matrix, where i is independent task ID and j 
	 * dependent task ID and delta[i][j] is the communication cost
	 */
	protected double[][] delta;

	/**
	 * Constructor
	 * @param ETCMatrix Execution times matrix
	 */
	public Scheduler(double[][] ETCMatrix)
	{
		ETC = ETCMatrix;
	}

	/**
	 * @author Pedro Cuadra
	 * @author Sudheera Bandi
	 * 
	 * Genetics algorithm's gene
	 *
	 */
	public class ScheduleItem {

		private Integer taskID;
		private Integer executorID;

		/**
		 * Constructor
		 */
		public ScheduleItem()
		{
			Random randGen = new Random();

			taskID = randGen.nextInt(ETC[0].length - 1);
			executorID = randGen.nextInt(ETC.length - 1);
		}

		/**
		 * Get task ID
		 * @return task ID
		 */
		public Integer getTaskID() {
			return taskID;
		}

		/**
		 * Get executor ID
		 * @return executor ID
		 */
		public Integer getExecutorID() {
			return executorID;
		}
	}


	/**
	 * 
	 * @author Pedro Cuadra
	 * @author Sudheera Bandi
	 * 
	 * Genetic algorithm's chromosome 
	 *
	 */
	public class Schedule implements Chromosome<AnyGene<ScheduleItem>>
	{

		/**
		 * Scheduling sequence of ScheduleItem's genes
		 */
		private ISeq<AnyGene<ScheduleItem>> scheduleSeq;

		/* (non-Javadoc)
		 * @see org.jenetics.util.Verifiable#isValid()
		 */
		@Override
		public boolean isValid() {
			// TODO Implement this checker function taking dependencies into account
			return false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<AnyGene<ScheduleItem>> iterator() {
			return scheduleSeq.iterator();
		}

		/* (non-Javadoc)
		 * @see org.jenetics.util.Factory#newInstance()
		 */
		@Override
		public Chromosome<AnyGene<ScheduleItem>> newInstance() {
			// TODO Implement this factory function taking dependencies into account
			return null;
		}

		/* (non-Javadoc)
		 * @see org.jenetics.Chromosome#newInstance(org.jenetics.util.ISeq)
		 */
		@Override
		public Chromosome<AnyGene<ScheduleItem>> newInstance(ISeq<AnyGene<ScheduleItem>> genes) {
			// TODO Implement this factory function taking dependencies into account
			return null;
		}

		/* (non-Javadoc)
		 * @see org.jenetics.Chromosome#getGene(int)
		 */
		@Override
		public AnyGene<ScheduleItem> getGene(int index) {
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
		public ISeq<AnyGene<ScheduleItem>> toSeq() {
			return scheduleSeq;
		}

	}

}
