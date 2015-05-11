package com.inferneon.core.arffparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;

import com.inferneon.core.Attribute;

public class ParserUtils {

	public static ArffElements getArffElements(String filePath){

		ArffElements arffElements = null;		

		try {
			ArffGrammarParser parser = parseArffFile(filePath);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + filePath);
			List<Attribute> attributes = parser.getAttributes();				
			String relationName = parser.getRelationshipName();			
			int dataStartLine = parser.getDataStartLine();

			String data = getData(filePath);

			arffElements = new ArffElements(relationName, attributes, data, dataStartLine );
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return arffElements;
	}

	private static String getData(String filePath) {
		String result = null;
		try(InputStream is = ParserUtils.class.getResourceAsStream(filePath);) {
			result = IOUtils.toString(is);
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return result;
	}

	public static List<Attribute> getAttributes(String filePath){

		List<Attribute> attributes = null;
		try {
			ArffGrammarParser parser = parseArffFile(filePath);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + filePath);
			attributes = parser.getAttributes();				
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return attributes;
	}

	public static String getRelationshipName(String filePath){

		String relationshipName = null;
		try(InputStream is = ParserUtils.class.getResourceAsStream(filePath);) {
			ArffGrammarParser parser = parseArffFile(filePath);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + filePath);
			relationshipName = parser.getRelationshipName();	
		} 
		catch (RecognitionException | IOException re) {
			re.printStackTrace();
		}

		return relationshipName;
	}

	private static ArffGrammarParser parseArffFile(String filePath){
		FileReader fr = null;
		BufferedReader br = null;

		ArffGrammarParser parser = null;
		try(InputStream is = ParserUtils.class.getResourceAsStream(filePath);) {
			String str = IOUtils.toString(is);
			if (str != null) {
				ANTLRStringStream strStream = new ANTLRStringStream(str);
				ArffGrammarLexer lexer = new ArffGrammarLexer(strStream);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				parser = new ArffGrammarParser(tokens);						
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fr != null) {
				try {
					fr.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		return parser;

	}
}
