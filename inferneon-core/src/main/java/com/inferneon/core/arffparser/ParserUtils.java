package com.inferneon.core.arffparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.inferneon.core.Attribute;

public class ParserUtils {

	public static ArffElements getArffElements(String rootPath, String arffFileName){

		ArffElements arffElements = null;		

		try {
			
	        String fullPath = rootPath + "/" + arffFileName;
	        URL url = ParserUtils.class.getResource(fullPath);
	        
	        String  f = url.getFile();
	        File file = new File(f);
	        
			ArffGrammarParser parser = parseArffFile(file);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + file.getAbsolutePath());
			List<Attribute> attributes = parser.getAttributes();				
			String relationName = parser.getRelationshipName();			
			int dataStartLine = parser.getDataStartLine();

			String data = getData(file, dataStartLine);

			arffElements = new ArffElements(relationName, attributes, data, dataStartLine );
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return arffElements;
	}

	private static String getData(File file, int dataStartLine) {
		
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader br = null;
		try 
		{
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

	public static String getRelationshipName(File file, String arffFileName){

		String relationshipName = null;
		try {
			ArffGrammarParser parser = parseArffFile(file);
			parser.arff();
			System.out.println("Arff file parsed sucessfully: " + file.getAbsolutePath());
			relationshipName = parser.getRelationshipName();	
		} 
		catch (RecognitionException re) {
			re.printStackTrace();
		}

		return relationshipName;
	}

	private static ArffGrammarParser parseArffFile(File file){
		FileReader fr = null;
		BufferedReader br = null;

		ArffGrammarParser parser = null;
		try {			
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
