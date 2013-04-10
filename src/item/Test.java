package item;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.WuAndPalmer;

public class Test {

    private final WuAndPalmer wup;
    private final static double threshold = 0.8;
    public String dir;

    public Test() {


        JWS jws = new JWS("wordnet", "3.0");

        wup = jws.getWuAndPalmer();
    }

    public double getSimilarity(String word1, String word2 , String type) {

        double max = wup.max(word1, word2, type);
        // double spe = wup.wup(word1, 1, word2, 1, "n");

        // System.out.println(word1 + "\t\t" + word2
        // + "\t\t" + max + "\t" + spe);

        System.out.println(word1 + "\t\t\t" + word2 + "\t\t\t" + max);

        return max;
        // return wup.wup(word1, 1, word2, 1, "n");
    }

    public boolean isSimilar(String word1, String word2) {

        return (wup.max(word1, word2, "n") > threshold);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

         Test sim = new Test();
        
         sim.getSimilarity("car", "bus" ,"n");
         sim.getSimilarity("car", "wheel","n");
         sim.getSimilarity("car", "bike","n");
         sim.getSimilarity("tennis", "football","n");
         sim.getSimilarity("football", "soccer","n");
         sim.getSimilarity("tennis", "badminton","n");
         sim.getSimilarity("pizza", "pasta","n");
         sim.getSimilarity("taxi", "transport","n");
         sim.getSimilarity("bus", "transport","n");
         sim.getSimilarity("car", "transport","n");
         sim.getSimilarity("bike", "transport","n");
         sim.getSimilarity("fly", "travel","n");
         sim.getSimilarity("breakfast", "meal","n");
         sim.getSimilarity("man", "woman","n");
         sim.getSimilarity("quick", "slow","n");
         sim.getSimilarity("transmit","lock","v"); 
    }

}