package tools.nlp;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;

public class StanfordParser {
	private static StanfordParser instance = null;
	private LexicalizedParser lp;

	// singleton pattern
	public static StanfordParser getInstance() {
		if (instance == null) {
			instance = new StanfordParser(); 
			instance.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		}
		return StanfordParser.instance;
	}

	// load lp model
	public void loadModel(String model) {
		this.lp = LexicalizedParser.loadModel(model);
	}

	// parse sentence
	public Tree parse(String sentence) {
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(
				new StringReader(sentence)).tokenize();
		Tree parse = lp.apply(rawWords2);
		return parse;
	}

}
