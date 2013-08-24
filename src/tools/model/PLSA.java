package tools.model;

//The code is taken from:
//http://code.google.com/p/mltool4j/source/browse/trunk/src/edu/thu/mltool4j/topicmodel/plsa
//I noticed some difference with original Hofmann concept in computation of P(z). It is 
//always even and actually not involved, that makes this algorithm non-negative matrix 
//factoring and not PLSA.
//Found and tested by Andrew Polar. 
//My version can be found on semanticsearchart.com or ezcodesample.com


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import core.dbmodel.Patent;
import core.dbmodel.Patents;
import core.similarity.PatentMatrixGenerator;
import core.similarity.Similarity;

import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.measure.MoehrleNovelty;
import tools.model.Lucene.WeightType;

public class PLSA implements Similarity {
  private int I = -1; // number of data
  private int J = -1; // number of words
  private int K = -1; // number of topics
  private RealMatrix matrix;
  private Map<String, Integer> docMap;

  public PLSA(Lucene tool) throws IOException {
    matrix = tool.getDocTermMatrix();
    docMap = tool.getDocMap();
    I = matrix.getRowDimension();
    J = matrix.getColumnDimension();
  }

  public boolean doPLSA(int ntopics, int iters, int times) {
    this.K = ntopics;
    RealMatrix sum = null;
    for (int i = 0; i < times; i++) {
      // run EM algorithm
      RealMatrix matrix = this.EM(iters);
      if (i == 0)
        sum = matrix;
      else
        sum = sum.add(matrix);
    }
    matrix = sum;
    return true;
  }

  // Build the inverted index for M-step fast calculation. Format:
  // invertedIndex[w][]: a unsorted list of document and position which word w
  // occurs.
  // @param ds the dataset which to be analysis

  private RealMatrix EM(int iters) {
    // p(z), size: K
    double[] Pz = new double[this.K];

    // p(d|z), size: K x I
    double[][] Pd_z = new double[this.K][this.I];

    // p(w|z), size: K x J
    double[][] Pw_z = new double[this.K][this.J];

    // p(z|d,w), size: K x I x J
    double[][][] Pz_dw = new double[this.K][this.I][this.J];

    // L: log-likelihood value
    double L = -1;

    // run EM algorithm
    this.init(Pz, Pd_z, Pw_z, Pz_dw);
    for (int it = 0; it < iters; it++) {
      // E-step
      if (!this.Estep(Pz, Pd_z, Pw_z, Pz_dw)) {
        System.out.println("EM,  in E-step");
      }

      // M-step
      if (!this.Mstep(Pz, Pd_z, Pw_z, Pz_dw)) {
        System.out.println("EM, in M-step");
      }

      L = calcLoglikelihood(Pz, Pd_z, Pw_z);
      //System.out.println("[" + it + "]" + "\tlikelihood: " + L);
    }

    // print result
    for (int i = 0; i < this.I; i++) {
      double norm = 0.0;
      for (int k = 0; k < this.K; k++) {
        norm += Pd_z[k][i];
      }
      if (norm <= 0.0)
        norm = 1.0;
      for (int k = 0; k < this.K; k++) {
        //System.out.format("%10.4f", Pd_z[k][i] / norm);
      }
      //System.out.println();
    }

    RealMatrix matrix = new Array2DRowRealMatrix(Pd_z);
    matrix = matrix.transpose();
    return matrix;
  }

  private boolean init(double[] Pz, double[][] Pd_z, double[][] Pw_z, double[][][] Pz_dw) {
    // p(z), size: K
    double zvalue = (double) 1 / (double) this.K;
    for (int k = 0; k < this.K; k++) {
      Pz[k] = zvalue;
    }

    // p(d|z), size: K x I
    for (int k = 0; k < this.K; k++) {
      double norm = 0.0;
      for (int i = 0; i < this.I; i++) {
        Pd_z[k][i] = Math.random();
        norm += Pd_z[k][i];
      }

      for (int i = 0; i < this.I; i++) {
        Pd_z[k][i] /= norm;
      }
    }

    // p(w|z), size: K x J
    for (int k = 0; k < this.K; k++) {
      double norm = 0.0;
      for (int j = 0; j < this.J; j++) {
        Pw_z[k][j] = Math.random();
        norm += Pw_z[k][j];
      }

      for (int j = 0; j < this.J; j++) {
        Pw_z[k][j] /= norm;
      }
    }

    return false;
  }

  private boolean Estep(double[] Pz, double[][] Pd_z, double[][] Pw_z, double[][][] Pz_dw) {
    for (int i = 0; i < this.I; i++) {
      for (int j = 0; j < this.J; j++) {
        double norm = 0.0;
        for (int k = 0; k < this.K; k++) {
          double val = Pz[k] * Pd_z[k][i] * Pw_z[k][j];
          Pz_dw[k][i][j] = val;
          norm += val;
        }

        // normalization
        for (int k = 0; k < this.K; k++) {
          Pz_dw[k][i][j] /= norm;
        }
      }
    }

    return true;
  }

  private boolean Mstep(double[] Pz, double[][] Pd_z, double[][] Pw_z, double[][][] Pz_dw) {
    // p(w|z)
    for (int k = 0; k < this.K; k++) {
      double norm = 0.0;
      for (int j = 0; j < this.J; j++) {
        double sum = 0.0;
        for (int i = 0; i < this.I; i++) {
          double n = matrix.getEntry(i, j);
          sum += n * Pz_dw[k][i][j];
        }
        Pw_z[k][j] = sum;
        norm += sum;
      }

      // normalization
      for (int j = 0; j < this.J; j++) {
        Pw_z[k][j] /= norm;
      }
    }

    // p(d|z)
    for (int k = 0; k < this.K; k++) {
      double norm = 0.0;
      for (int i = 0; i < this.I; i++) {
        double sum = 0.0;
        for (int j = 0; j < this.J; j++) {
          double n = matrix.getEntry(i, j);
          sum += n * Pz_dw[k][i][j];
        }
        Pd_z[k][i] = sum;
        norm += sum;
      }

      // normalization
      for (int i = 0; i < this.I; i++) {
        Pd_z[k][i] /= norm;
      }
    }

    // This is definitely a bug
    // p(z) values are even, but they should not be even
    double norm = 0.0;
    for (int k = 0; k < this.K; k++) {
      double sum = 0.0;
      for (int i = 0; i < this.I; i++) {
        for (int j = 0; j < this.J; j++) {
          double n = matrix.getEntry(i, j);
          sum += n * Pz_dw[k][i][j];
        }
      }
      Pz[k] = sum;
      norm += sum;
    }

    // normalization
    for (int k = 0; k < this.K; k++) {
      Pz[k] /= norm;
      // System.out.format("%10.4f", Pz[k]); //here you can print to see
    }
    // System.out.println();

    return true;
  }

  private double calcLoglikelihood(double[] Pz, double[][] Pd_z, double[][] Pw_z) {
    double L = 0.0;
    for (int i = 0; i < this.I; i++) {

      for (int j = 0; j < this.J; j++) {
        double n = matrix.getEntry(i, j);
        double sum = 0.0;
        for (int k = 0; k < this.K; k++) {
          sum += Pz[k] * Pd_z[k][i] * Pw_z[k][j];
        }
        L += n * Math.log10(sum);

      }
    }
    return L;
  }

  public static void main(String[] args) throws Exception {
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    int[] set = { 1, 2, 3, 4, 5 };
    //int[] set = { 1 };
    int[] topicSet = { 30, 25, 20, 15, 10, 5 };
    //int[] topicSet = { 30};
    for (int i = 0; i < set.length; i++) {
      int num = set[i];
      for (int j = 0; j < topicSet.length; j++) {
        int topic = topicSet[j];
        Patents dataset = new Patents("dataset" + num, "data/dataset-" + num + ".txt", "data/dataset-" + num + "-answer.txt");
        PLSA plsa = new PLSA(new Lucene(dataset, WeightType.TF));
        System.out.println("==========");
        System.out.println("model = plsa, dataset = " + dataset.getName() + ", topic = " + topic);
        plsa.doPLSA(topic, 60, 20);
        PatentMatrixGenerator.setSimilarity(plsa);
        PatentMatrixGenerator.generate(dataset);
        MoehrleNovelty.getRanking(dataset);
        //PRCurve.evaluate(dataset);
        AUC.evaluate(dataset);
      }
    }
    

    mgr.close();
  }

  public double getCosineSimiliarty(Patent p1, Patent p2) {
    String id1 = p1.getString("patent_id");
    String id2 = p2.getString("patent_id");
    RealVector v1 = matrix.getRowVector(docMap.get(id1));
    RealVector v2 = matrix.getRowVector(docMap.get(id2));
    double value = v1.cosine(v2);
    // System.out.println("cosine-sim between " + id1 + " and " + id2 + " : " +
    // value);
    return value;
  }

  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return getCosineSimiliarty(p1, p2);
  }
}