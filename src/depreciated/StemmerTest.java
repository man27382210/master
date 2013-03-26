package depreciated;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tools.nlp.Stemmer;

public class StemmerTest {
	private Stemmer stemmer;

	@Before
	public void setUp() {
		stemmer = new Stemmer();
	}

	@After
	public void tearDown() {
		stemmer = null;
	}
	
	@Test
	public void testGetStem(){
		String expected = "cat";
		String actural = stemmer.getStem("cats");
		assertEquals(expected,actural);
	}
}
