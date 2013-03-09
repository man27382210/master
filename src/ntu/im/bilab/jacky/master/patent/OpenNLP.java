package ntu.im.bilab.jacky.master.patent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class OpenNLP {

	protected static String[] getSentence(String data) throws InvalidFormatException,
	    IOException {
		String sentences[] = null;
		InputStream modelIn = null;
		modelIn = new FileInputStream("opennlp/en-sent.bin");
		SentenceModel model = new SentenceModel(modelIn);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		sentences = sentenceDetector.sentDetect(data);
		if (modelIn != null)
			modelIn.close();
		return sentences;
	}

	protected static String[] getToken(String sentence) throws InvalidFormatException,
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

	protected static String[] getPOSTag(String[] sent) throws InvalidFormatException,
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

	protected static String[] getChunkTag(String[] sent, String[] pos)
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

	protected static Parse getParse(String sentence) throws InvalidFormatException,
	    IOException {
		InputStream modelIn = null;
		Parse parse = null;
		modelIn = new FileInputStream("opennlp/en-parser-chunking.bin");
		ParserModel model = new ParserModel(modelIn);
		Parser parser = ParserFactory.create(model);
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
		parse = topParses[0];
		if (modelIn != null)
			modelIn.close();
		return parse;
	}

}
