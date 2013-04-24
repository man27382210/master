package tools.sim;

import item.Patent;

import java.io.IOException;
import java.util.List;

import mdsj.MDSJ;

public class SimilarityMatrix {
	List<Patent> patents;
	List<Double> x_value;
	List<Double> y_value;

	public double[][] getMatrix(List<Patent> list) throws IOException, InterruptedException {
		int size = list.size();
		double[][] matrix = new double[size][size];
		PatentSimilarity sim = PatentSimilarity.getInstance();
		
		int x = 0, y = 0;
		for (Patent p1 : list) {
			for (Patent p2 : list) {
				if (x == y) {
					matrix[x][y] = 0;
					y++;
					continue;
				} else if (x > y) {
					matrix[x][y] = matrix[y][x];
					y++;
					continue;
				} else {
					matrix[x][y] = sim.getPatentSim(p1, p2);
					System.out.println("Fetching sim between " + p1.getId() + " and " + p2.getId() + " : " + matrix[x][y]);
					y++;
				}
			}
			x++;
			y = 0;
		}
		return matrix;
	}

	public double[][] getMap(double[][] matrix) {
		// apply MDS
		double[][] coordinates = MDSJ.classicalScaling(matrix);
		int num = coordinates[0].length;
		// output all coordinates double[dim][number]
		for (int i = 0; i < num; i++)
			System.out.println("x:" + coordinates[0][i] + ", y:" + coordinates[1][i]);
		return coordinates;
	}
}
