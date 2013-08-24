package core.learning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.dbmodel.MakeInstrumentationUtil;

import libsvm.svm;

import tools.data.DBManager;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.LibSVMLoader;
import weka.core.converters.LibSVMSaver;
import weka.core.converters.SVMLightLoader;
import weka.filters.unsupervised.attribute.Remove;
import wlsvm.WLSVM;

public class Classification {
  List<Instances> list;

  public enum ClassifierType {
    J48, NaiveBayes, Logistic
  }

  public Classification(List<Instances> list) {
    this.list = list;
  }

  public void runCrossValidation(DatabaseLoader loader) throws Exception {
    loader.setQuery("SELECT `patent_id`, `backward_citation`, `science_linkage`, " + "`uspc_originality`, `sim_bc_structure`, `novelty_sao`, `novelty_vsm`, " + "`novelty_lsa`, `novelty_plsa`, `class` FROM `feature`");
    Instances data = loader.getDataSet();
    data.setClassIndex(9);

    Remove rm = new Remove();
    rm.setAttributeIndices("1");
    // rm.setAttributeIndices("1,2-5");
    // rm.setAttributeIndices("1,6-9");

    // 三種classifier
    J48 j48 = new J48();
    j48.setUnpruned(true);
    Logistic log = new Logistic();
    NaiveBayes nb = new NaiveBayes();

    // classifier設定remove和data
    FilteredClassifier fc = new FilteredClassifier();
    fc.setFilter(rm);
    fc.setClassifier(j48);
    fc.buildClassifier(data);

    //
    Evaluation eval = new Evaluation(data);
    eval.crossValidateModel(fc, data, 10, new Random(System.currentTimeMillis()));
    System.out.println(eval.toSummaryString("\nResults\n\n", false));
    System.out.println(eval.toClassDetailsString());
  }

  public void run() throws Exception {

    // train.setClassIndex(9);
    // test.setClassIndex(9);
    // System.out.println(train);

    Remove rm = new Remove();
    rm.setAttributeIndices("1");
    // rm.setAttributeIndices("1,2-5");
    // rm.setAttributeIndices("1,6-9");

    J48 j48 = new J48();
    j48.setUnpruned(true);
    Logistic log = new Logistic();
    NaiveBayes nb = new NaiveBayes();
    SMO svm = new SMO();

    FilteredClassifier fc = new FilteredClassifier();
    fc.setFilter(rm);
    fc.setClassifier(j48);
    fc.buildClassifier(train);
    System.out.println(fc);

    Evaluation eval = new Evaluation(train);
    eval.evaluateModel(fc, test);
    // eval.crossValidateModel(fc, data, 10, new Random(1));
    System.out.println(eval.toSummaryString("\nResults\n\n", false));
    System.out.println(eval.toClassDetailsString());
  }

  public static void main(String[] args) throws Exception {
    // 針對ORM所做的.class injection
    MakeInstrumentationUtil.make();

    // 打開DB管理員
    DBManager mgr = DBManager.getInstance();

    // 打開DB
    mgr.open();

    // 利用weka的loader把db schema mapping到weka的 instance
    DatabaseLoader loader = new DatabaseLoader();
    loader.setSource(mgr.getUrl(), mgr.getUser(), mgr.getPassword());

    // 將db.feature(已經預存的feature) mapping到dataset
    List<Instances> list = new ArrayList<Instances>();
    for (int i = 1; i <= 5; i++) {
      loader.setQuery("SELECT `patent_id`, `backward_citation`, `science_linkage`, " + "`uspc_originality`, `sim_bc_structure`, `novelty_sao`, `novelty_vsm`, " + "`novelty_lsa`, `novelty_plsa`, `class` FROM `feature` where dataset = 'dataset-" + i + "'");
      list.add(loader.getDataSet());
    }

    // 決定要不要用 cross validation
    Boolean cv = true;
    if (cv) {
      // cross validation 大雜燴訓練(通常是隨機分成10等分)
      Classification c = new Classification(list);
      c.runCrossValidation(loader);

    } else {
      // 非 cv 需要給定training和testing
      Classification c = new Classification(train, test);
      c.run();
    }

    // 關閉DB
    mgr.close();
  }
}