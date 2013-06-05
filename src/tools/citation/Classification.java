package tools.citation;

import java.util.Random;

import tools.data.DBManager;
import util.MakeInstrumentationUtil;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.LibSVMSaver;
import weka.filters.unsupervised.attribute.Remove;


public class Classification {
  private Instances data;
  
  public enum ClassifierType {
    J48, NaiveBayes, Logistic, SVM
  }
  
  public Classification(Instances data){
    this.data = data;
  } 
  
  public void run() throws Exception {
    
    data.setClassIndex(9);
    System.out.println(data);

    Remove rm = new Remove();
    //rm.setAttributeIndices("1");
    rm.setAttributeIndices("1,2-5");
    //rm.setAttributeIndices("1,6-9");
    
    J48 j48 = new J48(); 
    j48.setUnpruned(true);
    Logistic log = new Logistic();
    NaiveBayes nb = new NaiveBayes();
        
    FilteredClassifier fc = new FilteredClassifier();
    fc.setFilter(rm);
    fc.setClassifier(nb);
    fc.buildClassifier(data);
    System.out.println(fc);
    
    Evaluation eval = new Evaluation(data);
    eval.crossValidateModel(fc, data, 10, new Random(1));
    
    System.out.println(eval.areaUnderROC(0));
    System.out.println(eval.toClassDetailsString());
  }
  
  public void loadFeature() {
    
  }
  
  public static void main(String[] args) throws Exception {
    //MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();
    DatabaseLoader loader = new DatabaseLoader();
    loader.setSource(mgr.getUrl(), mgr.getUser(), mgr.getPassword());
    loader.setQuery("SELECT `patent_id`, `backward_citation`, `science_linkage`, `uspc_originality`, `sim_bc_structure`, `novelty_sao`, `novelty_vsm`, `novelty_lsa`, `novelty_plsa`, `class` FROM `feature`");
    Instances data = loader.getDataSet();
    Classification c = new Classification(data);
    c.run();
    mgr.close();
  }
}
