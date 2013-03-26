package depreciated;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tools.nlp.StanfordParser;

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
