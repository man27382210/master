package ntu.im.bilab.jacky.master.test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ntu.im.bilab.jacky.master.patent.SAOExtractor;
import opennlp.tools.util.InvalidFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
		String data = "A temporary indicated torque is obtained by taking a conventional dead zone area for a first slip control area, and the value proportional to the slip quantity for a maximum value, this temporary indicated torque is corrected by a correction value according to the tight cornering brake quantity to be the indicated torque of the transfer clutch, and occurrence of any tight cornering brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		//System.out.println(extractor.getSAOTupleList(data));
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
