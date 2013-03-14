package ntu.im.bilab.jacky.master.test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import ntu.im.bilab.jacky.master.patent.SAOExtractor;
import ntu.im.bilab.jacky.master.patent.SAOExtractorByOpenNLP;
import opennlp.tools.util.InvalidFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class SAOExtractorTest {
	private SAOExtractor extractor;
	private String sentence;
	private String subject;
	private String predicate;
	private String object;

	public SAOExtractorTest(String sentence, String subject, String predicate,
	    String object) {
		super();
		this.sentence = sentence;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Before
	public void setUp() {
		extractor = new SAOExtractor();
	}

	@After
	public void tearDown() {
		extractor = null;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays
		    .asList(new Object[][] {
		        { "My dog also likes sausage.", "dog", "likes", "sausage" },
		        {
		            "A rare black squirrel has become a regular visitor to a suburban garden.",
		            "squirrel", "become", "visitor" },

		        {
		            "The slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque.",
		            "control", "transferred", "sausage" }

		    });
	}

	@Ignore
	public void testGetSAOTuple() throws InvalidFormatException, IOException {
		String data = "A temporary indicated torque is obtained by taking a conventional dead zone area for a first slip control area, and the value proportional to the slip quantity for a maximum value, this temporary indicated torque is corrected by a correction value according to the tight cornering brake quantity to be the indicated torque of the transfer clutch, and occurrence of any tight cornering brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		System.out.println(extractor.getSAOTupleList(data));
	}

	@Test
	public void testGetSubject() throws InvalidFormatException, IOException {
		String expect = subject;
		String actural = extractor.getSubject(sentence);
		assertEquals(expect, actural);
	}

	@Ignore
	public void testGetPredicate() throws InvalidFormatException, IOException {
		String expect = predicate;
		String actural = extractor.getPredicate(sentence);
		assertEquals(expect, actural);
	}

	@Ignore
	public void testGetObject() throws InvalidFormatException, IOException {
		String expect = object;
		String actural = extractor.getObject(sentence);
		assertEquals(expect, actural);
	}
}
