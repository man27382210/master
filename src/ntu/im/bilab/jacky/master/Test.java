package ntu.im.bilab.jacky.master;

import java.io.*;
import java.util.List;

import opennlp.tools.*;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.*;
import opennlp.tools.util.*;

public class Test {

	public static void main(String[] args) throws InvalidFormatException, IOException {
		String s = "A temporary indicated torque is obtained by taking a conventional dead zone area for a first slip control area, and the value proportional to the slip quantity for a maximum value, this temporary indicated torque is corrected by a correction value according to the tight cornering brake quantity to be the indicated torque of the transfer clutch, and occurrence of any tight cornering brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		s = "A temporary indicated torque is obtained by taking a conventional dead zone area";
		List<SAOTuple> saol = SAOExtractor.getSAOTuple(s);
		System.out.println(saol);
	}

	public static void Parse() throws InvalidFormatException, IOException {
		// http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Parser#Training_Tool
		InputStream is = new FileInputStream("en-parser-chunking.bin");
 
		ParserModel model = new ParserModel(is);
 
		Parser parser = ParserFactory.create(model);
 
		String sentence = "Programcreek is a very huge and useful website.";
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
 
		for (Parse p : topParses)
			p.show();
 
		is.close();
 
		/*
		 * (TOP (S (NP (NN Programcreek) ) (VP (VBZ is) (NP (DT a) (ADJP (RB
		 * very) (JJ huge) (CC and) (JJ useful) ) ) ) (. website.) ) )
		 */
	}
}
