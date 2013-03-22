package ntu.im.bilab.jacky.depreciated;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ntu.im.bilab.jacky.master.tools.nlp.SAOExtractor;
import opennlp.tools.util.InvalidFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SAOExtractorTest {
	private SAOExtractor extractor;

	@Before
	public void setUp() {
		extractor = new SAOExtractor();
	}

	@After
	public void tearDown() {
		extractor = null;
	}

	@Ignore
	public void testGetSAOTuple() throws InvalidFormatException, IOException {
	}

	@Test
	public void testGetSubjectList() throws InvalidFormatException, IOException {
		String sentence = "My dog and cat also likes eating sausage.";
		List<String> expect = Arrays.asList(new String [] {"dog","cat"});
		List<String> actural = extractor.getSubjectList(sentence);
		assertEquals(expect, actural);
	}

	@Test
	public void testGetPredicate() throws InvalidFormatException, IOException {
		//String expect = predicate;
		//String actural = extractor.getPredicate(sentence);
		//assertEquals(expect, actural);
	}

	@Test
	public void testGetObjectList() throws InvalidFormatException, IOException {
		String sentence = "My dog and cat also likes eating sausage and suger.";
		List<String> expect = Arrays.asList(new String [] {"sausage","suger"});
		
		sentence = "I arrived in Taipei.";
		expect = Arrays.asList(new String [] {"Taipei"});
		
		List<String> actural = extractor.getObjectList(sentence);
		assertEquals(expect, actural);
	}
}
