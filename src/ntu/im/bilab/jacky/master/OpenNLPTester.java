package ntu.im.bilab.jacky.master;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class OpenNLPTester {

	public static void main(String[] args) {

		String data = "In various embodiments, an online multiplayer "
		    + "game can provide a mechanism for player characters "
		    + "to acquire in-game assets.";
		OpenNLPTester opennlp = new OpenNLPTester();
		String[] sentences = opennlp.getSentence(data);
		for (String s : sentences) {
			// String[] tokens = opennlp.getToken(s);
			// String[] pos_tags = opennlp.getPOSTag(tokens);
			// String[] chunk_tags = opennlp.getChunkTag(tokens, pos_tags);
			// for (int i = 0; i < tokens.length; i++) {
			// System.out.println(tokens[i] + " : " + pos_tags[i] + " : "
			// + chunk_tags[i]);
			// }

			// for (String str : chunk_tags)
			// System.out.println(str);
			Parse p = opennlp.getParse(s);
			p.show();
			System.out.println(p.getText());
		}
	}

	protected String[] getSentence(String data) {
		String sentences[] = null;
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream("en-sent.bin");
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			sentences = sentenceDetector.sentDetect(data);
			// for (int i = 0; i < sentences.length; i++) {
			// System.out.println(sentences[i]);
			// }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return sentences;
	}

	protected String[] getToken(String sentence) {
		String[] tokens = null;
		InputStream modelIn = null;

		try {
			modelIn = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			tokens = tokenizer.tokenize(sentence);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return tokens;

	}

	protected String[] getPOSTag(String[] sent) {
		InputStream modelIn = null;
		String[] tags = null;

		try {
			modelIn = new FileInputStream("en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);
			tags = tagger.tag(sent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tags;
	}

	protected String[] getChunkTag(String[] sent, String[] pos) {
		InputStream modelIn = null;
		String[] tag = null;

		try {
			modelIn = new FileInputStream("en-chunker.bin");
			ChunkerModel model = new ChunkerModel(modelIn);
			ChunkerME chunker = new ChunkerME(model);
			tag = chunker.chunk(sent, pos);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return tag;
	}

	protected Parse getParse(String sentence) {
		InputStream modelIn = null;
		Parse parse = null;
		try {
			modelIn = new FileInputStream("en-parser-chunking.bin");
			ParserModel model = new ParserModel(modelIn);
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			parse = topParses[0];
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		return parse;

	}

}
