package alg.util.graph;

import java.util.Arrays;

/**
 * Graph node.
 * 
 * @author Pedro Cuadra
 *
 */
public class GraphNode {
  /**
   * Task ID.
   */
  private int taskId;
  /**
   * Expected time to compute for the different cores. 
   */
  private double[] etcRow;
  /**
   * Custom stored value.
   */
  private double value;
  
  /**
   * Constructor.
   * 
   * @param taskId task ID
   * @param etcRow expected time to compute for the different cores
   */
  public GraphNode(int taskId, double[] etcRow) {
    this.setTaskId(taskId);
    this.setEtcRow(Arrays.stream(etcRow).toArray());
  }

  /**
   * Get task ID.
   * 
   * @return task ID
   */
  public int getTaskId() {
    return taskId;
  }

  /**
   * Set task ID of the graph node.
   * 
   * @param taskId task ID
   */
  private void setTaskId(int taskId) {
    this.taskId = taskId;
  }

  /**
   * Get the expected time to compute for all cores.
   * 
   * @return expected time to compute for all cores
   */
  public double[] getEtcRow() {
    return etcRow;
  }

  /**
   * Set the expected time to compute for all cores.
   * 
   * @param etcRow expected time to compute for all cores
   */
  private void setEtcRow(double[] etcRow) {
    this.etcRow = etcRow;
  }
  
  /**
   * Set custom value.
   * 
   * @param value custom value
   */
  public void setValue(double value) {
    this.value = value;
  }
  
  /**
   * Get custom value.
   * 
   * @return custom value
   */
  public double getValue() {
    return this.value;
  }
  
  /**
   * Get expected time to compute of a given core.
   * 
   * @param exeUnit core ID
   * @return expected time to computes of the given core
   */
  public double getExecutionTimeOnUnit(int exeUnit) {
    return this.etcRow[exeUnit];
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#clone()
   */
  @Override
  public GraphNode clone() {
    return new GraphNode(this.taskId, this.etcRow);
  }
}
