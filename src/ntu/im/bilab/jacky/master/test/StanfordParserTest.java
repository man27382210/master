package ntu.im.bilab.jacky.master.test;

import static org.junit.Assert.fail;
import ntu.im.bilab.jacky.master.patent.StanfordParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StanfordParserTest {
	StanfordParser parser;
		
	@Before
	public void setUp() throws Exception {
		parser = StanfordParser.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}

	@Test
	public void testParse() {
		String sentence = "My dog also likes eating sausage.";
		parser.parse(sentence).pennPrint();
		//fail("Not yet implemented");
	}

}
