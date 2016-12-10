package alg.util.genetics;

import org.jenetics.Gene;

public class ScheduleGene implements Gene<ScheduleAllel, ScheduleGene>{

	private ScheduleAllel alle;
	private int numTasks;
	private int numExecutors;

	public ScheduleGene(int numTasks, int numExecutors){
		this.numTasks = numTasks;
		this.numExecutors = numExecutors;

		alle = new ScheduleAllel(numTasks, numExecutors);
	}

	public ScheduleGene(int numTasks, int numExecutors, ScheduleAllel A){
		this.numTasks = numTasks;
		this.numExecutors = numExecutors;

		alle = A;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ScheduleAllel getAllele() {
		return alle;
	}


	@Override
	public ScheduleGene newInstance() {
		return new ScheduleGene(numTasks, numExecutors);
	}

	@Override
	public ScheduleGene newInstance(ScheduleAllel value) {
		return new ScheduleGene(numTasks, numExecutors, value);
	}

	public String toString()
	{
		return alle.toString();
	}

	public static ScheduleGene of(int numTasks, int numExecutors) {
		return new ScheduleGene(numTasks, numExecutors);
	}

}
