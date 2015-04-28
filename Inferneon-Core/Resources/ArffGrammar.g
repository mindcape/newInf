grammar ArffGrammar;

options { language=Java;
	output=AST;
    backtrack=true;
    memoize=true;
}
                         
@header{
	package com.inferneon.core.arffparser;
				import com.inferneon.core.Attribute;
}

@lexer::header{ package com.inferneon.core.arffparser;
				import com.inferneon.core.Attribute;
               }
 
@members {

	private int dataTypeLine = 0;
	private int dataTypePos = 0;
	
	private int dataStartLine = 0;
	
	private enum AttributeDataType{
		NUMERIC_INTEGER,
		NUMERIC_REAL,
		NOMINAL_VALUE_LIST,
		STRING,
		DATE
	};

	 private String relationshipName;
	 private List <Attribute> attributes = new ArrayList<Attribute>();	 
	 private List<String> nominalAttributeValueNames = new ArrayList<String>();
	
	private void createNewAttribute(String name, AttributeDataType attributeDataType){
		Attribute newAttribute = null; 
		name = trimName(name);
		if(attributeDataType == AttributeDataType.NUMERIC_INTEGER){
			newAttribute = new Attribute(name, Attribute.NumericType.INTEGER);
		}
		if(attributeDataType == AttributeDataType.NUMERIC_REAL){			
			newAttribute = new Attribute(name, Attribute.NumericType.REAL);
		}
		else if(attributeDataType == AttributeDataType.NOMINAL_VALUE_LIST){
			newAttribute = new Attribute(name, nominalAttributeValueNames );
			nominalAttributeValueNames = new ArrayList<String>();
		}
		else if(attributeDataType == AttributeDataType.STRING){
			// TODO Implement later
		}
		else if(attributeDataType == AttributeDataType.DATE){
			// TODO Implement later
		}		
		attributes.add(newAttribute);		
	}
	
	private String trimName(String name){
		if(!name.startsWith("'")){
			return name;
		}
		
		return name.substring(1, name.length() -1);		
	}
    	   
	private void addAttibuteValue(String attrValueName) {
		attrValueName = trimName(attrValueName);
		nominalAttributeValueNames.add(attrValueName);
	}	
  
  	public List <Attribute> getAttributes() {
    	return attributes;
  	}	
  	
  	public String getRelationshipName() {
    	return relationshipName;
  	}
  	
  	public int getDataStartLine(){
  		return dataStartLine;
  	}
}			 	      

// TERMINALS
NUMERIC : 'numeric' | 'NUMERIC' ;
REAL : 'real' | 'REAL';
RELATION : 'relation' | 'RELATION';
ATTRIBUTE : 'attribute' | 'ATTRIBUTE';
STRING : 'string' | 'STRING';
COMMA	:	',';
ats : '@';
DATA : 'data' | 'DATA';
START_CURLY : '{';
END_CURLY : '}';
NEWLINE	:	'\r'?'\n';
LINE_COMMENT : '%' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;};
NAME_OR_NOMINAL_VALUE :  (LETTER (LETTER |'0'..'9' | '!' | '@'  | '#' | '$'  | '^' | '&'  | '*' | '('  | ')' | '-' | '_' | '<' | '>' | '?')*)
				   | ('\'' LETTER (LETTER |'0'..'9' | '!' | '@'  | '#' | '$'  | '^' | '&'  | '*' | '('  | ')' | '-' | '_' | '<' | '>' | '?')* '\'');
fragment
LETTER
	:   'A'..'Z'
	|	'a'..'z'
	;
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { $channel = HIDDEN; };

// NON-TERMINALS
arff: NEWLINE* relation_element attribute_elements data_element;
relation_element: ats RELATION e1=NAME_OR_NOMINAL_VALUE {relationshipName = trimName($e1.text);} NEWLINE*;
attribute_elements : (attribute_element)+;
attribute_element: ats ATTRIBUTE e1=NAME_OR_NOMINAL_VALUE e2=data_type {createNewAttribute($e1.text, $e2.attrDataType);} NEWLINE*;
data_element : ats e1=DATA (NEWLINE)* (.*) { dataStartLine = $e1.line;};
data_type returns [AttributeDataType attrDataType] : NUMERIC               {$attrDataType = AttributeDataType.NUMERIC_INTEGER;}
													| REAL                 {$attrDataType = AttributeDataType.NUMERIC_REAL;}
                                                    | nominal_value_list   {$attrDataType = AttributeDataType.NOMINAL_VALUE_LIST;}
                                                    | STRING               {$attrDataType = AttributeDataType.STRING;}
                                                    | date_type            {$attrDataType = AttributeDataType.DATE;}
                                                    ;
nominal_value_list : (START_CURLY e1=NAME_OR_NOMINAL_VALUE)
	{
		addAttibuteValue($e1.text);
    } nominal_value* END_CURLY;               
nominal_value : COMMA e1=NAME_OR_NOMINAL_VALUE
           {
                addAttibuteValue($e1.text);
           };
 date_type : 'mm-dd-yy'
             | 'mm-dd-yyyy'
             ;