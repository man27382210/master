package ntu.im.bilab.jacky.master.tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import opennlp.tools.parser.Parse;

public class ParensParser {
	// something to keep track of parens nesting
	protected Deque<String> stack;
	// current level
	protected List<String> current;
	// input string to parse
	protected String string;
	// current character offset in string
	protected int position;
	// start of text-buffer
	protected int buffer_start;

	public String parse(String str) {
		Deque<Parse> stack = new ArrayDeque<Parse>();
		
		if(str.substring(0, 1).equals("(")){
			str.substring(1);
		}
		
		for(int i = 0; i<str.length(); i++) {
			String tmp = str.substring(i,i+1);
			if (tmp.equals("(")) {
				
				
			} else if (tmp.equals(")")) {
				
			} else {
				
			}
		}
		
		return str;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
