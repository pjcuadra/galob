package alg.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import alg.LoadBalancingFitnessCalculator;
import alg.util.graph.Graph;
import alg.util.graph.GraphNode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

public class HeterogeneousComputingEnvTest {
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
  private static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  private static final int MAX_NUM_CORES = 16;
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  /**
   * Unit testing set-up.
   * 
   * @throws Exception failure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTasks =  1 + randomGen.nextInt(MAX_NUM_TASKS);
    numCores =  1 + randomGen.nextInt(MAX_NUM_CORES);

  }

  
  /**
   * Test constructor.
   */
  @Test
  public void testHeterogeneousComputingEnv() {
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
  public void testHeterogeneousComputingEnvZeroTasks() {
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Number of tasks shall be greater than 0");  
    
    new HeterogeneousComputingEnv(0, numCores);

  }
  
  /**
   * Test constructor zero core exception.
   */
  @Test
  public void testHeterogeneousComputingEnvZeroCores() {
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Number of cores shall be greater than 0");  
    
    new HeterogeneousComputingEnv(numTasks, 0);

  }

  @Test
  public void testAddDependency() {
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
  public void testAddDependencyInvalidSrc() {
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

  @Test
  public void testGetGraphNodeById() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    
    // Check that for every created task one can get the node by its id
    for (int i = 0; i < numTasks; i++) {
      GraphNode task =  env.getGraphNodeById(i);
      assertNotEquals(null, task);
    }
    
    // Try an id above max
    GraphNode task =  env.getGraphNodeById(numTasks);
    assertEquals(null, task);
    
  }

  @Test
  public void testAddTask() {
    double[] etcRow = {1, 2, 3};
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks + 1, etcRow.length);
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
  public void testAddTaskOverFlow() {
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

  @Test
  public void testAddUnitExecutionTimeTask() {
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

  @Test
  public void testGetNumberOfExecutors() {
    // Normal constructor
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, numCores);
    assertEquals(numCores, env.getNumberOfExecutors());
    
    // Random builder
    env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    assertEquals(numCores, env.getNumberOfExecutors());
    
    // Random unit builder
    env = HeterogeneousComputingEnv.ofRandomUnitary(numTasks, numCores, false);
    assertEquals(numCores, env.getNumberOfExecutors());
  }

  @Test
  public void testGetNumberOfTasks() {
    // Normal constructor
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, numCores);
    assertEquals(numTasks, env.getNumberOfTasks());
    
    // Random builder
    env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    assertEquals(numTasks, env.getNumberOfTasks());
    
    // Random unit builder
    env = HeterogeneousComputingEnv.ofRandomUnitary(numTasks, numCores, false);
    assertEquals(numTasks, env.getNumberOfTasks());
  }

  @Test
  public void testGetDependencyMatrix() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    double[][] matrix;
    int count = 0;
    matrix = env.getDependencyMatrix();
    
    // Verify size
    assertEquals(numTasks, matrix.length);
    assertEquals(numTasks, matrix[0].length);
    
    // Lower diagonal has to be zero
    for (int i = 0; i < numTasks; i++) {
      for (int j = i + 1; j < numTasks; j++) {
        if (matrix[i][j] != 0) {
          count++;
        }
      }
    }

    // Loops count
    assertEquals(count, 0);
  }

  @Test
  public void testGetCommunicationCostsMatrix() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandomUnitary(numTasks, 
        numCores, 
        false);
    double[][] delta = env.getDependencyMatrix();
    double[][] commCosts = env.getCommunicationCostsMatrix();
    
    // Verify size
    assertEquals(numTasks, commCosts.length);
    assertEquals(numTasks, commCosts[0].length);
    
    // Delta and communication costs matrices shall be equal for a unitary random HCE
    for (int i = 0; i < numTasks; i++) {
      for (int j = 0; j < numTasks; j++) {
        assertEquals(delta[i][j], commCosts[i][j], EPSILON);
      }
    }

  }

  @Test
  public void testGetExpectedTimeToComputeMatrix() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandomUnitary(numTasks, 
        numCores, 
        false);
    double[][] etcMatrix = env.getExpectedTimeToComputeMatrix();
    
    // Verify size
    assertEquals(numTasks, etcMatrix.length);
    assertEquals(numCores, etcMatrix[0].length);
    
    // Delta and communication costs matrices shall be equal for a unitary random HCE
    for (int i = 0; i < numTasks; i++) {
      for (int j = 0; j < numCores; j++) {
        assertEquals(1, etcMatrix[i][j], EPSILON);
      }
    }
  }

  @Test
  public void testGetSimulatedAnnealingEnabled() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    int initialTemp = 900;
    SimulatedAnnealing expectedSimAnn = new SimulatedAnnealing(gammaFactor, 
        initialTemp,
        lbFitnessCalc);
    
    assertEquals(false, env.getSimulatedAnnealingEnabled());
    
    // Add simulated annealing to the environment
    env.setSimulatedAnnealing(expectedSimAnn);
    
    assertEquals(true, env.getSimulatedAnnealingEnabled());
  }

  @Test
  public void testSetSimulatedAnnealing() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    int initialTemp = 900;
    SimulatedAnnealing expectedSimAnn = new SimulatedAnnealing(gammaFactor, 
        initialTemp,
        lbFitnessCalc);
    
    SimulatedAnnealing actualSimAnn;
    
    
    
    // Add simulated annealing to the environment
    env.setSimulatedAnnealing(expectedSimAnn);
    
    actualSimAnn = env.getSimulatedAnnealing();
    
    assertEquals(expectedSimAnn, actualSimAnn);
  }

  @Test
  public void testGetSimulatedAnnealing() {
    testSetSimulatedAnnealing();    
  }

  @Test
  public void testGetGraphCopy() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(numTasks, numCores, false);
    Graph graphCopy = env.getGraphCopy();
    Iterator<GraphNode> currNode = graphCopy.iterator();
    
    // Verify that it was correctly copied
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      GraphNode envNode = env.getGraphNodeById(node.getTaskId());
      
      assertArrayEquals(node.getEtcRow(), envNode.getEtcRow(), EPSILON);
      assertEquals(node.getCookie(), envNode.getCookie());
    }
    
    currNode = graphCopy.iterator();
    
    // Let's modify the copy
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      node.setCookie(10);
    }
    
    currNode = graphCopy.iterator();
    
    // Let's compare again
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
      GraphNode envNode = env.getGraphNodeById(node.getTaskId());
      
      assertArrayEquals(node.getEtcRow(), envNode.getEtcRow(), EPSILON);
      assertNotEquals(node.getCookie(), envNode.getCookie());
    }
  }

  @Test
  public void testOfRandom() {
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

  @Test
  public void testOfRandomUnitary() {
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

}
