package com.inferneon.core.arffparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.inferneon.core.Attribute;

public class ParserUtils {

	public static ArffElements getArffElements(String rootPath, String LLVMFileName){

		ArffElements arffElements = null;		

		try {
			File file = new File(rootPath + File.separator + LLVMFileName);
			ArffGrammarParser parser = parseArffFile(rootPath, LLVMFileName);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + file.getAbsolutePath());
			List<Attribute> attributes = parser.getAttributes();				
			String relationName = parser.getRelationshipName();			
			int dataStartLine = parser.getDataStartLine();

			String data = getData(rootPath, LLVMFileName, dataStartLine);

			arffElements = new ArffElements(relationName, attributes, data, dataStartLine );
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return arffElements;
	}

	private static String getData(String rootPath, String LLVMFileName,
			int dataStartLine) {
		
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader br = null;
		try 
		{
			File file = new File(rootPath + File.separator + LLVMFileName);

			br = new BufferedReader(new FileReader(file));
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if(count < dataStartLine){
					count++;
					continue;
				}
				
				if(line.trim().isEmpty()){
					count++;
					continue;
				}
				
				stringBuffer.append(line + System.getProperty("line.separator"));				
				count++;
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return stringBuffer.toString();
	}

	public static List<Attribute> getAttributes(String rootPath, String LLVMFileName){

		List<Attribute> attributes = null;
		try {
			File file = new File(rootPath + File.separator + LLVMFileName);
			ArffGrammarParser parser = parseArffFile(rootPath, LLVMFileName);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + file.getAbsolutePath());
			attributes = parser.getAttributes();				
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return attributes;
	}

	public static String getRelationshipName(String rootPath, String LLVMFileName){

		String relationshipName = null;
		try {
			File file = new File(rootPath + File.separator + LLVMFileName);
			ArffGrammarParser parser = parseArffFile(rootPath, LLVMFileName);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + file.getAbsolutePath());
			relationshipName = parser.getRelationshipName();	
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return relationshipName;
	}

	private static ArffGrammarParser parseArffFile(String rootPath, String LLVMFileName){
		FileReader fr = null;
		BufferedReader br = null;

		ArffGrammarParser parser = null;
		try {
			File file = new File(rootPath + File.separator + LLVMFileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			char[] chArr = new char[(int) file.length()];
			br.read(chArr);
			String str = new String(chArr);

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
