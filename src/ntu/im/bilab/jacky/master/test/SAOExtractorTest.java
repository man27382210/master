package ntu.im.bilab.jacky.master.test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import ntu.im.bilab.jacky.master.patent.SAOExtractor;
import opennlp.tools.util.InvalidFormatException;
import org.junit.After;
import org.junit.Before;
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

	@Test
	public void testGetSAOTuple() throws InvalidFormatException, IOException {
		String data = "A temporary indicated torque is obtained by taking a conventional dead zone area for a first slip control area, and the value proportional to the slip quantity for a maximum value, this temporary indicated torque is corrected by a correction value according to the tight cornering brake quantity to be the indicated torque of the transfer clutch, and occurrence of any tight cornering brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		SAOExtractor.getSAOTuple(data);
	}

//	@Test
//	public void testGetSubject() throws InvalidFormatException, IOException {
//		String data = "A temporary indicated torque is obtained by taking a conventional dead zone area";
//		String expect = "torque";
//		String subject = extractor.getSubject(data);
//		assertEquals(expect, subject);
//	}

}
