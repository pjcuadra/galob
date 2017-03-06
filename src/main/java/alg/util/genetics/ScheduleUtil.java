package alg.util.genetics;

import org.jenetics.util.ISeq;

/**
 * Schedule Utile class.
 * 
 * @author Pedro Cuadra
 *
 */
public class ScheduleUtil {
  /**
   * Calculate the allocation matrix from a gene sequence.
   * 
   * @param scheduleSeq genes sequence of a valid chromosome
   * @param numCores number of cores
   * 
   * @return CONV matrix
   */
  public static int[][] createOmegaMatrix(ISeq<ScheduleGene> scheduleSeq, final int numCores) { 
    int[][] omega = new int[numCores][scheduleSeq.size()];
    ScheduleAllele currAllel = null;

    // Just set to one where it is allocated
    for (ScheduleGene gene: scheduleSeq) {
      currAllel = gene.getAllele();
      omega[currAllel.getExecutorId()][currAllel.getTaskId()] = 1;
    }

    return omega;
  }
}
