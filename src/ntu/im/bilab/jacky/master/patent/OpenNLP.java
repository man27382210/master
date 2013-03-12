package ntu.im.bilab.jacky.master.patent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;

import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.chunking.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class OpenNLP {
	private static OpenNLP instance = null;
	private static SentenceModel sentenceModel;
	private static SentenceDetectorME sentenceDetector;
	private static ParserModel parserModel;
	private static Parser parser;
	
	// singleton pattern
	public static OpenNLP getInstance() throws InvalidFormatException, IOException {
		if(OpenNLP.instance == null) {
			InputStream modelIn = null;

			modelIn = new FileInputStream("opennlp/en-sent.bin");
			sentenceModel = new SentenceModel(modelIn);
			sentenceDetector = new SentenceDetectorME(sentenceModel);

			modelIn = new FileInputStream("opennlp/en-parser-chunking.bin");
			parserModel = new ParserModel(modelIn);
			//parser = (Parser) ParserFactory.create(parserModel, 10, Parser.defaultAdvancePercentage);
			parser = (Parser) ParserFactory.create(parserModel);
			
			if (modelIn != null) modelIn.close();
			
			OpenNLP.instance = new OpenNLP();
		}
		return OpenNLP.instance;
	}
	
	protected List<String> getSentence(String data)
	    throws InvalidFormatException, IOException {
		return Arrays.asList(sentenceDetector.sentDetect(data));
	}

	protected Parse getParse(String sentence) throws InvalidFormatException,
	    IOException {
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
		return topParses[0];
	}

	protected String[] getToken(String sentence) throws InvalidFormatException,
	    IOException {
		String[] tokens = null;
		InputStream modelIn = null;
		modelIn = new FileInputStream("opennlp/en-token.bin");
		TokenizerModel model = new TokenizerModel(modelIn);
		Tokenizer tokenizer = new TokenizerME(model);
		tokens = tokenizer.tokenize(sentence);
		if (modelIn != null)
			modelIn.close();
		return tokens;
	}

	protected String[] getPOSTag(String[] sent) throws InvalidFormatException,
	    IOException {
		InputStream modelIn = null;
		String[] tags = null;
		modelIn = new FileInputStream("opennlp/en-pos-maxent.bin");
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		tags = tagger.tag(sent);
		if (modelIn != null)
			modelIn.close();
		return tags;
	}

	protected String[] getChunkTag(String[] sent, String[] pos)
	    throws InvalidFormatException, IOException {
		InputStream modelIn = null;
		String[] tag = null;
		modelIn = new FileInputStream("opennlp/en-chunker.bin");
		ChunkerModel model = new ChunkerModel(modelIn);
		ChunkerME chunker = new ChunkerME(model);
		tag = chunker.chunk(sent, pos);
		if (modelIn != null)
			modelIn.close();
		return tag;
	}

}
