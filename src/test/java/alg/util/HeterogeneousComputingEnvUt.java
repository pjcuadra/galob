/**
 * Utils unit testing.
 */

package alg.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import alg.util.graph.Graph;
import alg.util.graph.GraphNode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 * Utils unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class HeterogeneousComputingEnvUt {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Number of cores.
   */
  private int numCores;
  /**
   * Max. number of tasks.
   */
  static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  static final int MAX_NUM_CORES = 16;
  /**
   * Delta for double comparison.
   */
  static final double EPSILON = 0.00001;

  /**
   * Test environment set-up.
   * 
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {
    
    randomGen = new Random();
    
    // Get random values
    numTasks = 1 + randomGen.nextInt(MAX_NUM_TASKS);
    numCores = 1 + randomGen.nextInt(MAX_NUM_CORES);

  }
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test constructor.
   */
  @Test
  public void constructor() {
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, numCores);
    
    assertEquals(numTasks, env.getNumberOfTasks());
    assertEquals(numCores, env.getNumberOfExecutors());
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("No tasks added to the HCE");  
    
    assertEquals(numTasks, env.getCommunicationCostsMatrix()[0].length);
    

  }
  
  /**
   * Test constructor zero tasks exception.
   */
  @Test
  public void constructorZeroTasks() {
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Number of tasks shall be greater than 0");  
    
    new HeterogeneousComputingEnv(0, numCores);

  }
  
  /**
   * Test constructor zero core exception.
   */
  @Test
  public void constructorZeroCores() {
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Number of cores shall be greater than 0");  
    
    new HeterogeneousComputingEnv(numTasks, 0);

  }
  
  /**
   * Test task adding.
   */
  @Test
  public void addTask() {
    double[] etcRow = {1, 2, 3};
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, etcRow.length);
    GraphNode task;
    
    // Add the task
    task = env.addTask(etcRow);
    
    // Check that was added to the graph
    assertTrue(env.containsVertex(task));
    
    // Test the elements of the ETC row
    assertArrayEquals(etcRow, task.getEtcRow(), EPSILON);
    assertArrayEquals(etcRow, env.getExpectedTimeToComputeMatrix()[0], EPSILON);
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("ETC per core array with wrong size");

    double[] etcRowWrongSize = {1, 2, 3, 4};
    
    // Add the task with wrong etc
    task = env.addTask(etcRowWrongSize);

  }
  
  /**
   * Test adding a task after the last possible task.
   */
  @Test
  public void addTaskOverFlow() {
    double[] etcRow = {1, 2, 3};
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, etcRow.length);
    GraphNode task;
    
    // Add all possible tasks
    for (int i = 0; i < numTasks; i++) {
      // Add the task
      task = env.addTask(etcRow);
      
      // Check that was added to the graph
      assertTrue(env.containsVertex(task));
    }
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Tasks limit has been reached");
    
    // Add one more!
    task = env.addTask(etcRow);
    
  }
  
  /**
   * Test adding unit execution time task.
   */
  @Test
  public void addUnitExecutionTimeTask() {
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode task;
    
    // Add the task
    task = env.addUnitExecutionTimeTask();
    
    // Check that was added to the graph
    assertTrue(env.containsVertex(task));
    
    double[] expectedRow = DoubleStream.generate(() -> 1).limit(numCores).toArray();
    
    // Test the elements of the ETC row
    assertArrayEquals(expectedRow, task.getEtcRow(), EPSILON);
    assertArrayEquals(expectedRow, env.getExpectedTimeToComputeMatrix()[0], EPSILON);
    
  }
  
  /**
   * Test adding a dependency.
   */
  @Test
  public void addDependency() {
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks + 2, numCores);
    GraphNode src;
    GraphNode dst;
    
    // Add the tasks
    src = env.addUnitExecutionTimeTask();
    dst = env.addUnitExecutionTimeTask();
    
    // Check that was added to the graph
    assertTrue(env.containsVertex(src));
    assertTrue(env.containsVertex(dst));
    
    double randomCost = randomGen.nextDouble();
    
    env.addDependency(src, dst, randomCost);
    
    // Check that the edge was added with the correct cost
    assertEquals(randomCost, env.getEdgeWeight(env.getEdge(src, dst)), EPSILON);
    
    // Check the dependency matrix
    double[][] delta = env.getDependencyMatrix();
    assertEquals(1, delta[src.getTaskId()][dst.getTaskId()], EPSILON);
    
    // Check the communication costs matrix
    double[][] commCosts = env.getCommunicationCostsMatrix();
    assertEquals(randomCost, commCosts[src.getTaskId()][dst.getTaskId()], EPSILON);
    
  }
  
  /**
   * Test adding a dependency with invalid source node.
   */
  @Test
  public void addDependencyInvalidSrc() {
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks + 2, numCores);
    GraphNode dst;
    
    // Add the tasks
    dst = env.addUnitExecutionTimeTask();
    
    // Check that was added to the graph
    assertTrue(env.containsVertex(dst));
    
    GraphNode wrongTask = new GraphNode(randomGen.nextInt(numTasks), 
        new double[]{1, 2, 3});
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Invalid task node");
    
    env.addDependency(wrongTask, dst, 1);
    
  }
  
  /**
   * Test adding a dependency with invalid destination node.
   */
  @Test
  public void addDependencyInvalidDst() {
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks + 2, numCores);
    GraphNode src;
    
    // Add the tasks
    src = env.addUnitExecutionTimeTask();
    
    // Check that was added to the graph
    assertTrue(env.containsVertex(src));
    
    GraphNode wrongTask = new GraphNode(randomGen.nextInt(numTasks), 
        new double[]{1, 2, 3});
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Invalid task node");
    
    env.addDependency(src, wrongTask, 1);
    
  }
  
  /**
   * Test graph copy.
   */
  @Test
  public void getGraphCopy() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    Graph graphCopy = env.getGraphCopy();
    Iterator<GraphNode> currNode = graphCopy.iterator();
    
    // Verify that it was correctly copied
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      GraphNode envNode = env.getGraphNodeById(node.getTaskId());
      
      assertArrayEquals(node.getEtcRow(), envNode.getEtcRow(), EPSILON);
      assertEquals(node.getValue(), envNode.getValue(), EPSILON);
    }
    
    currNode = graphCopy.iterator();
    
    // Let's modify the copy
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      node.setValue(10);
    }
    
    currNode = graphCopy.iterator();
    
    // Let's compare again
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      GraphNode envNode = env.getGraphNodeById(node.getTaskId());
      
      assertArrayEquals(node.getEtcRow(), envNode.getEtcRow(), EPSILON);
      assertNotEquals(node.getValue(), envNode.getValue(), EPSILON);
    }
    
  }
  
  /**
   * Test Unitary HCE random generation.
   */
  @Test
  public void ofRandomUnitary() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandomUnitary(numTasks, 
        numCores, 
        false);
    Iterator<GraphNode> currNode = env.iterator();
    double[] expectedRow = DoubleStream.generate(() -> 1).limit(numCores).toArray();
    
    // Verify that it was correctly copied
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      
      assertArrayEquals(expectedRow, node.getEtcRow(), EPSILON);
    }
    
    // Check that all the dependencies have cost 1
    double[][] delta = env.getDependencyMatrix();
    double[][] commCost = env.getCommunicationCostsMatrix();
    
    for (int i = 0; i < env.getNumberOfTasks(); i++) {
      for (int j = 0; j < env.getNumberOfTasks(); j++) {
        if (delta[i][j] == 0) {
          continue;
        }
        
        // Check the value in the dependency matrix
        assertEquals(1, commCost[i][j], EPSILON);
        
        GraphNode src = env.getGraphNodeById(i);
        GraphNode dst = env.getGraphNodeById(j);
        
        // Check the value from the graph
        assertEquals(1, env.getEdgeWeight(env.getEdge(src, dst)), EPSILON);
        
      }
    }
    
  }
  
  /**
   * Test random HCE generation.
   */
  @Test
  public void ofRandom() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, 
        numCores, 
        false);
    
    // Check that the dependency matrix and the graph have the same value
    double[][] delta = env.getDependencyMatrix();
    double[][] commCost = env.getCommunicationCostsMatrix();
    
    for (int i = 0; i < env.getNumberOfTasks(); i++) {
      for (int j = 0; j < env.getNumberOfTasks(); j++) {
        if (delta[i][j] == 0) {
          continue;
        }
        
        GraphNode src = env.getGraphNodeById(i);
        GraphNode dst = env.getGraphNodeById(j);
        
        // Check the value from the graph
        assertEquals(commCost[i][j], env.getEdgeWeight(env.getEdge(src, dst)), EPSILON);
        
      }
    }
    
  }
  
  /**
   * Test getTologicalLevelNodes.
   */  
  @Test
  public void getTologicalLevelNodes() {
    assertTrue(false);
  }
  
  /**
   * Test getMaxTopologicalLevel.
   */  
  @Test
  public void getMaxTopologicalLevel() {
    assertTrue(false);
  }
   
}
