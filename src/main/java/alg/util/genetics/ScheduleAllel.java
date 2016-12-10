package alg.util.genetics;

import java.util.Random;

/**
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 * Genetics algorithm's gene
 *
 */
public class ScheduleAllel{

	public int taskID;
	public int executorID;

	/**
	 * Constructor
	 */
	public ScheduleAllel(int numTasks, int numExecutors)
	{
		Random randGen = new Random();
		
		taskID = randGen.nextInt(numTasks);
		executorID = randGen.nextInt(numExecutors);
	}
			
	/**
	 * Constructor
	 * @param taskID task ID
	 */
	public ScheduleAllel(int numTask, int numExecutors, int taskID)
	{
		Random randGen = new Random();
		this.taskID = taskID;
		this.executorID = randGen.nextInt(numExecutors);
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
	
	public String toString(){
		return "{ " + taskID +" : " + executorID + " }" ;
	}
}