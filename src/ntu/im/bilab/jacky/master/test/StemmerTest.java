package ntu.im.bilab.jacky.master.test;

import static org.junit.Assert.assertEquals;
import ntu.im.bilab.jacky.master.tools.Stemmer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
