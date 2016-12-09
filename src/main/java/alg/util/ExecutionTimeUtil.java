package alg.util;

public class ExecutionTimeUtil {
	
	/**
	 * Get ones matrix of size rows*cols
	 * @param rows rows of the matrix
	 * @param cols columns of the matrix
	 * @return ones matrix
	 */
	static public double[][] getOnesMatrix(int rows, int cols)
	{
		double[][] ones = new double[rows][cols];
		// Initialize ones matrix with ones}
		for (int currRow = 0; currRow < rows; currRow++)
		{
			for (int currCol = 0; currCol < cols; currCol++)
			{
				ones[currRow][currCol] = 1;
			}
		}
		
		return ones;
	}

}
