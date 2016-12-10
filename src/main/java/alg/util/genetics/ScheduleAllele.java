package alg.util.genetics;

import java.util.Random;

/**
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 * Genetics algorithm's gene
 *
 */
public class ScheduleAllele{

	/**
	 * ID of the task
	 */
	private int taskID;
	/**
	 * ID of the executor
	 */
	private int executorID;

	/**
	 * Constructor
	 */
	public ScheduleAllele(int numTasks, int numExecutors)
	{
		Random randGen = new Random();
		
		taskID = randGen.nextInt(numTasks);
		executorID = randGen.nextInt(numExecutors);
	}
			
	/**
	 * Constructor
	 * @param numTask number of tasks
	 * @param numExecutors number of executing units
	 * @param taskID ID of the task
	 */
	public ScheduleAllele(int numTask, int numExecutors, int taskID)
	{
		Random randGen = new Random();
		this.taskID = taskID;
		this.executorID = randGen.nextInt(numExecutors);
	}

	/**
	 * Get task ID
	 * @return task ID
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * Get executor ID
	 * @return executor ID
	 */
	public int getExecutorID() {
		return executorID;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "{ " + taskID +" : " + executorID + " }" ;
	}
}