// $ANTLR 3.3 Nov 30, 2010 12:50:56 Resources/ArffGrammar.g 2015-04-29 14:37:50

	package com.inferneon.core.arffparser;
				import com.inferneon.core.Attribute;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class ArffGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NUMERIC", "REAL", "RELATION", "ATTRIBUTE", "STRING", "COMMA", "DATA", "START_CURLY", "END_CURLY", "NEWLINE", "LINE_COMMENT", "ALPHA", "OTHER_CHARS", "NAME", "WHITESPACE", "'@'", "'mm-dd-yy'", "'mm-dd-yyyy'"
    };
    public static final int EOF=-1;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int NUMERIC=4;
    public static final int REAL=5;
    public static final int RELATION=6;
    public static final int ATTRIBUTE=7;
    public static final int STRING=8;
    public static final int COMMA=9;
    public static final int DATA=10;
    public static final int START_CURLY=11;
    public static final int END_CURLY=12;
    public static final int NEWLINE=13;
    public static final int LINE_COMMENT=14;
    public static final int ALPHA=15;
    public static final int OTHER_CHARS=16;
    public static final int NAME=17;
    public static final int WHITESPACE=18;

    // delegates
    // delegators


        public ArffGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ArffGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[23+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return ArffGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "Resources/ArffGrammar.g"; }



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
    	
    	public void setRelationshipName(String name)  throws RecognitionException {
    		name = trimName(name);
    		char firstChar = name.charAt(0);
            if(!Character.isLetter(firstChar)){
         			throw new RecognitionException();
            }
            		
            relationshipName = name;
            
    	}
    	
    	private void createNewAttribute(String name, AttributeDataType attributeDataType)  throws RecognitionException{		
    		name = trimName(name);
    		char firstChar = name.charAt(0);
            if(!Character.isLetter(firstChar)){
         			throw new RecognitionException();
            }
            
    		Attribute newAttribute = null; 
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


    public static class ats_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ats"
    // Resources/ArffGrammar.g:109:1: ats : '@' ;
    public final ArffGrammarParser.ats_return ats() throws RecognitionException {
        ArffGrammarParser.ats_return retval = new ArffGrammarParser.ats_return();
        retval.start = input.LT(1);
        int ats_StartIndex = input.index();
        Object root_0 = null;

        Token char_literal1=null;

        Object char_literal1_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // Resources/ArffGrammar.g:109:5: ( '@' )
            // Resources/ArffGrammar.g:109:7: '@'
            {
            root_0 = (Object)adaptor.nil();

            char_literal1=(Token)match(input,19,FOLLOW_19_in_ats173); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal1_tree = (Object)adaptor.create(char_literal1);
            adaptor.addChild(root_0, char_literal1_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, ats_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "ats"

    public static class arff_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arff"
    // Resources/ArffGrammar.g:138:1: arff : ( NEWLINE )* relation_element attribute_elements data_element ;
    public final ArffGrammarParser.arff_return arff() throws RecognitionException {
        ArffGrammarParser.arff_return retval = new ArffGrammarParser.arff_return();
        retval.start = input.LT(1);
        int arff_StartIndex = input.index();
        Object root_0 = null;

        Token NEWLINE2=null;
        ArffGrammarParser.relation_element_return relation_element3 = null;

        ArffGrammarParser.attribute_elements_return attribute_elements4 = null;

        ArffGrammarParser.data_element_return data_element5 = null;


        Object NEWLINE2_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // Resources/ArffGrammar.g:138:5: ( ( NEWLINE )* relation_element attribute_elements data_element )
            // Resources/ArffGrammar.g:138:7: ( NEWLINE )* relation_element attribute_elements data_element
            {
            root_0 = (Object)adaptor.nil();

            // Resources/ArffGrammar.g:138:7: ( NEWLINE )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==NEWLINE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Resources/ArffGrammar.g:0:0: NEWLINE
            	    {
            	    NEWLINE2=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_arff392); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    NEWLINE2_tree = (Object)adaptor.create(NEWLINE2);
            	    adaptor.addChild(root_0, NEWLINE2_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            pushFollow(FOLLOW_relation_element_in_arff395);
            relation_element3=relation_element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relation_element3.getTree());
            pushFollow(FOLLOW_attribute_elements_in_arff397);
            attribute_elements4=attribute_elements();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, attribute_elements4.getTree());
            pushFollow(FOLLOW_data_element_in_arff399);
            data_element5=data_element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, data_element5.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, arff_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arff"

    public static class relation_element_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relation_element"
    // Resources/ArffGrammar.g:139:1: relation_element : ats RELATION e1= NAME ( NEWLINE )* ;
    public final ArffGrammarParser.relation_element_return relation_element() throws RecognitionException {
        ArffGrammarParser.relation_element_return retval = new ArffGrammarParser.relation_element_return();
        retval.start = input.LT(1);
        int relation_element_StartIndex = input.index();
        Object root_0 = null;

        Token e1=null;
        Token RELATION7=null;
        Token NEWLINE8=null;
        ArffGrammarParser.ats_return ats6 = null;


        Object e1_tree=null;
        Object RELATION7_tree=null;
        Object NEWLINE8_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // Resources/ArffGrammar.g:139:17: ( ats RELATION e1= NAME ( NEWLINE )* )
            // Resources/ArffGrammar.g:139:19: ats RELATION e1= NAME ( NEWLINE )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ats_in_relation_element405);
            ats6=ats();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ats6.getTree());
            RELATION7=(Token)match(input,RELATION,FOLLOW_RELATION_in_relation_element407); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RELATION7_tree = (Object)adaptor.create(RELATION7);
            adaptor.addChild(root_0, RELATION7_tree);
            }
            e1=(Token)match(input,NAME,FOLLOW_NAME_in_relation_element411); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            e1_tree = (Object)adaptor.create(e1);
            adaptor.addChild(root_0, e1_tree);
            }
            if ( state.backtracking==0 ) {
              setRelationshipName((e1!=null?e1.getText():null));
            }
            // Resources/ArffGrammar.g:139:73: ( NEWLINE )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==NEWLINE) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Resources/ArffGrammar.g:0:0: NEWLINE
            	    {
            	    NEWLINE8=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_relation_element415); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    NEWLINE8_tree = (Object)adaptor.create(NEWLINE8);
            	    adaptor.addChild(root_0, NEWLINE8_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, relation_element_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relation_element"

    public static class attribute_elements_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute_elements"
    // Resources/ArffGrammar.g:140:1: attribute_elements : ( attribute_element )+ ;
    public final ArffGrammarParser.attribute_elements_return attribute_elements() throws RecognitionException {
        ArffGrammarParser.attribute_elements_return retval = new ArffGrammarParser.attribute_elements_return();
        retval.start = input.LT(1);
        int attribute_elements_StartIndex = input.index();
        Object root_0 = null;

        ArffGrammarParser.attribute_element_return attribute_element9 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // Resources/ArffGrammar.g:140:20: ( ( attribute_element )+ )
            // Resources/ArffGrammar.g:140:22: ( attribute_element )+
            {
            root_0 = (Object)adaptor.nil();

            // Resources/ArffGrammar.g:140:22: ( attribute_element )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==19) ) {
                    int LA3_1 = input.LA(2);

                    if ( (synpred3_ArffGrammar()) ) {
                        alt3=1;
                    }


                }


                switch (alt3) {
            	case 1 :
            	    // Resources/ArffGrammar.g:140:23: attribute_element
            	    {
            	    pushFollow(FOLLOW_attribute_element_in_attribute_elements424);
            	    attribute_element9=attribute_element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, attribute_element9.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, attribute_elements_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "attribute_elements"

    public static class attribute_element_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute_element"
    // Resources/ArffGrammar.g:141:1: attribute_element : ats ATTRIBUTE e1= NAME e2= data_type ( NEWLINE )* ;
    public final ArffGrammarParser.attribute_element_return attribute_element() throws RecognitionException {
        ArffGrammarParser.attribute_element_return retval = new ArffGrammarParser.attribute_element_return();
        retval.start = input.LT(1);
        int attribute_element_StartIndex = input.index();
        Object root_0 = null;

        Token e1=null;
        Token ATTRIBUTE11=null;
        Token NEWLINE12=null;
        ArffGrammarParser.data_type_return e2 = null;

        ArffGrammarParser.ats_return ats10 = null;


        Object e1_tree=null;
        Object ATTRIBUTE11_tree=null;
        Object NEWLINE12_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // Resources/ArffGrammar.g:141:18: ( ats ATTRIBUTE e1= NAME e2= data_type ( NEWLINE )* )
            // Resources/ArffGrammar.g:141:20: ats ATTRIBUTE e1= NAME e2= data_type ( NEWLINE )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ats_in_attribute_element432);
            ats10=ats();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ats10.getTree());
            ATTRIBUTE11=(Token)match(input,ATTRIBUTE,FOLLOW_ATTRIBUTE_in_attribute_element434); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ATTRIBUTE11_tree = (Object)adaptor.create(ATTRIBUTE11);
            adaptor.addChild(root_0, ATTRIBUTE11_tree);
            }
            e1=(Token)match(input,NAME,FOLLOW_NAME_in_attribute_element438); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            e1_tree = (Object)adaptor.create(e1);
            adaptor.addChild(root_0, e1_tree);
            }
            pushFollow(FOLLOW_data_type_in_attribute_element442);
            e2=data_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, e2.getTree());
            if ( state.backtracking==0 ) {
              createNewAttribute((e1!=null?e1.getText():null), (e2!=null?e2.attrDataType:null));
            }
            // Resources/ArffGrammar.g:141:105: ( NEWLINE )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==NEWLINE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Resources/ArffGrammar.g:0:0: NEWLINE
            	    {
            	    NEWLINE12=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_attribute_element446); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    NEWLINE12_tree = (Object)adaptor.create(NEWLINE12);
            	    adaptor.addChild(root_0, NEWLINE12_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, attribute_element_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "attribute_element"

    public static class data_element_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "data_element"
    // Resources/ArffGrammar.g:142:1: data_element : ats e1= DATA ( NEWLINE )* (e2= ( . ) )* ;
    public final ArffGrammarParser.data_element_return data_element() throws RecognitionException {
        ArffGrammarParser.data_element_return retval = new ArffGrammarParser.data_element_return();
        retval.start = input.LT(1);
        int data_element_StartIndex = input.index();
        Object root_0 = null;

        Token e1=null;
        Token e2=null;
        Token NEWLINE14=null;
        Token wildcard15=null;
        ArffGrammarParser.ats_return ats13 = null;


        Object e1_tree=null;
        Object e2_tree=null;
        Object NEWLINE14_tree=null;
        Object wildcard15_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // Resources/ArffGrammar.g:142:14: ( ats e1= DATA ( NEWLINE )* (e2= ( . ) )* )
            // Resources/ArffGrammar.g:142:16: ats e1= DATA ( NEWLINE )* (e2= ( . ) )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ats_in_data_element454);
            ats13=ats();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ats13.getTree());
            e1=(Token)match(input,DATA,FOLLOW_DATA_in_data_element458); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            e1_tree = (Object)adaptor.create(e1);
            adaptor.addChild(root_0, e1_tree);
            }
            // Resources/ArffGrammar.g:142:28: ( NEWLINE )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==NEWLINE) ) {
                    int LA5_1 = input.LA(2);

                    if ( (synpred5_ArffGrammar()) ) {
                        alt5=1;
                    }


                }


                switch (alt5) {
            	case 1 :
            	    // Resources/ArffGrammar.g:142:29: NEWLINE
            	    {
            	    NEWLINE14=(Token)match(input,NEWLINE,FOLLOW_NEWLINE_in_data_element461); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    NEWLINE14_tree = (Object)adaptor.create(NEWLINE14);
            	    adaptor.addChild(root_0, NEWLINE14_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // Resources/ArffGrammar.g:142:41: (e2= ( . ) )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>=NUMERIC && LA6_0<=21)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // Resources/ArffGrammar.g:0:0: e2= ( . )
            	    {
            	    // Resources/ArffGrammar.g:142:42: ( . )
            	    // Resources/ArffGrammar.g:142:43: .
            	    {
            	    wildcard15=(Token)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    wildcard15_tree = (Object)adaptor.create(wildcard15);
            	    adaptor.addChild(root_0, wildcard15_tree);
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               System.out.println("Data found = " + (e2!=null?e2.getText():null)); dataStartLine = (e1!=null?e1.getLine():0);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, data_element_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "data_element"

    public static class data_type_return extends ParserRuleReturnScope {
        public AttributeDataType attrDataType;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "data_type"
    // Resources/ArffGrammar.g:143:1: data_type returns [AttributeDataType attrDataType] : ( NUMERIC | REAL | nominal_value_list | STRING | date_type );
    public final ArffGrammarParser.data_type_return data_type() throws RecognitionException {
        ArffGrammarParser.data_type_return retval = new ArffGrammarParser.data_type_return();
        retval.start = input.LT(1);
        int data_type_StartIndex = input.index();
        Object root_0 = null;

        Token NUMERIC16=null;
        Token REAL17=null;
        Token STRING19=null;
        ArffGrammarParser.nominal_value_list_return nominal_value_list18 = null;

        ArffGrammarParser.date_type_return date_type20 = null;


        Object NUMERIC16_tree=null;
        Object REAL17_tree=null;
        Object STRING19_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // Resources/ArffGrammar.g:143:52: ( NUMERIC | REAL | nominal_value_list | STRING | date_type )
            int alt7=5;
            switch ( input.LA(1) ) {
            case NUMERIC:
                {
                alt7=1;
                }
                break;
            case REAL:
                {
                alt7=2;
                }
                break;
            case START_CURLY:
                {
                alt7=3;
                }
                break;
            case STRING:
                {
                alt7=4;
                }
                break;
            case 20:
            case 21:
                {
                alt7=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // Resources/ArffGrammar.g:143:54: NUMERIC
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMERIC16=(Token)match(input,NUMERIC,FOLLOW_NUMERIC_in_data_type482); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC16_tree = (Object)adaptor.create(NUMERIC16);
                    adaptor.addChild(root_0, NUMERIC16_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.attrDataType = AttributeDataType.NUMERIC_INTEGER;
                    }

                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:144:16: REAL
                    {
                    root_0 = (Object)adaptor.nil();

                    REAL17=(Token)match(input,REAL,FOLLOW_REAL_in_data_type515); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REAL17_tree = (Object)adaptor.create(REAL17);
                    adaptor.addChild(root_0, REAL17_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.attrDataType = AttributeDataType.NUMERIC_REAL;
                    }

                    }
                    break;
                case 3 :
                    // Resources/ArffGrammar.g:145:55: nominal_value_list
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nominal_value_list_in_data_type589);
                    nominal_value_list18=nominal_value_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nominal_value_list18.getTree());
                    if ( state.backtracking==0 ) {
                      retval.attrDataType = AttributeDataType.NOMINAL_VALUE_LIST;
                    }

                    }
                    break;
                case 4 :
                    // Resources/ArffGrammar.g:146:55: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING19=(Token)match(input,STRING,FOLLOW_STRING_in_data_type649); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING19_tree = (Object)adaptor.create(STRING19);
                    adaptor.addChild(root_0, STRING19_tree);
                    }
                    if ( state.backtracking==0 ) {
                      retval.attrDataType = AttributeDataType.STRING;
                    }

                    }
                    break;
                case 5 :
                    // Resources/ArffGrammar.g:147:55: date_type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_date_type_in_data_type721);
                    date_type20=date_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, date_type20.getTree());
                    if ( state.backtracking==0 ) {
                      retval.attrDataType = AttributeDataType.DATE;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, data_type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "data_type"

    public static class nominal_value_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nominal_value_list"
    // Resources/ArffGrammar.g:149:1: nominal_value_list : ( START_CURLY e1= NAME ) ( nominal_value )* END_CURLY ;
    public final ArffGrammarParser.nominal_value_list_return nominal_value_list() throws RecognitionException {
        ArffGrammarParser.nominal_value_list_return retval = new ArffGrammarParser.nominal_value_list_return();
        retval.start = input.LT(1);
        int nominal_value_list_StartIndex = input.index();
        Object root_0 = null;

        Token e1=null;
        Token START_CURLY21=null;
        Token END_CURLY23=null;
        ArffGrammarParser.nominal_value_return nominal_value22 = null;


        Object e1_tree=null;
        Object START_CURLY21_tree=null;
        Object END_CURLY23_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // Resources/ArffGrammar.g:149:20: ( ( START_CURLY e1= NAME ) ( nominal_value )* END_CURLY )
            // Resources/ArffGrammar.g:149:22: ( START_CURLY e1= NAME ) ( nominal_value )* END_CURLY
            {
            root_0 = (Object)adaptor.nil();

            // Resources/ArffGrammar.g:149:22: ( START_CURLY e1= NAME )
            // Resources/ArffGrammar.g:149:23: START_CURLY e1= NAME
            {
            START_CURLY21=(Token)match(input,START_CURLY,FOLLOW_START_CURLY_in_nominal_value_list795); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            START_CURLY21_tree = (Object)adaptor.create(START_CURLY21);
            adaptor.addChild(root_0, START_CURLY21_tree);
            }
            e1=(Token)match(input,NAME,FOLLOW_NAME_in_nominal_value_list799); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            e1_tree = (Object)adaptor.create(e1);
            adaptor.addChild(root_0, e1_tree);
            }

            }

            if ( state.backtracking==0 ) {

              		addAttibuteValue((e1!=null?e1.getText():null));
                  
            }
            // Resources/ArffGrammar.g:152:7: ( nominal_value )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==COMMA) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // Resources/ArffGrammar.g:0:0: nominal_value
            	    {
            	    pushFollow(FOLLOW_nominal_value_in_nominal_value_list805);
            	    nominal_value22=nominal_value();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, nominal_value22.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            END_CURLY23=(Token)match(input,END_CURLY,FOLLOW_END_CURLY_in_nominal_value_list808); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            END_CURLY23_tree = (Object)adaptor.create(END_CURLY23);
            adaptor.addChild(root_0, END_CURLY23_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, nominal_value_list_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "nominal_value_list"

    public static class nominal_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nominal_value"
    // Resources/ArffGrammar.g:153:1: nominal_value : COMMA e1= NAME ;
    public final ArffGrammarParser.nominal_value_return nominal_value() throws RecognitionException {
        ArffGrammarParser.nominal_value_return retval = new ArffGrammarParser.nominal_value_return();
        retval.start = input.LT(1);
        int nominal_value_StartIndex = input.index();
        Object root_0 = null;

        Token e1=null;
        Token COMMA24=null;

        Object e1_tree=null;
        Object COMMA24_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // Resources/ArffGrammar.g:153:15: ( COMMA e1= NAME )
            // Resources/ArffGrammar.g:153:17: COMMA e1= NAME
            {
            root_0 = (Object)adaptor.nil();

            COMMA24=(Token)match(input,COMMA,FOLLOW_COMMA_in_nominal_value830); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COMMA24_tree = (Object)adaptor.create(COMMA24);
            adaptor.addChild(root_0, COMMA24_tree);
            }
            e1=(Token)match(input,NAME,FOLLOW_NAME_in_nominal_value834); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            e1_tree = (Object)adaptor.create(e1);
            adaptor.addChild(root_0, e1_tree);
            }
            if ( state.backtracking==0 ) {

                              addAttibuteValue((e1!=null?e1.getText():null));
                         
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, nominal_value_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "nominal_value"

    public static class date_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "date_type"
    // Resources/ArffGrammar.g:157:2: date_type : ( 'mm-dd-yy' | 'mm-dd-yyyy' );
    public final ArffGrammarParser.date_type_return date_type() throws RecognitionException {
        ArffGrammarParser.date_type_return retval = new ArffGrammarParser.date_type_return();
        retval.start = input.LT(1);
        int date_type_StartIndex = input.index();
        Object root_0 = null;

        Token set25=null;

        Object set25_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // Resources/ArffGrammar.g:157:12: ( 'mm-dd-yy' | 'mm-dd-yyyy' )
            // Resources/ArffGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set25=(Token)input.LT(1);
            if ( (input.LA(1)>=20 && input.LA(1)<=21) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set25));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, date_type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "date_type"

    // $ANTLR start synpred3_ArffGrammar
    public final void synpred3_ArffGrammar_fragment() throws RecognitionException {   
        // Resources/ArffGrammar.g:140:23: ( attribute_element )
        // Resources/ArffGrammar.g:140:23: attribute_element
        {
        pushFollow(FOLLOW_attribute_element_in_synpred3_ArffGrammar424);
        attribute_element();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_ArffGrammar

    // $ANTLR start synpred5_ArffGrammar
    public final void synpred5_ArffGrammar_fragment() throws RecognitionException {   
        // Resources/ArffGrammar.g:142:29: ( NEWLINE )
        // Resources/ArffGrammar.g:142:29: NEWLINE
        {
        match(input,NEWLINE,FOLLOW_NEWLINE_in_synpred5_ArffGrammar461); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_ArffGrammar

    // Delegated rules

    public final boolean synpred5_ArffGrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_ArffGrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_ArffGrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_ArffGrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_19_in_ats173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_arff392 = new BitSet(new long[]{0x0000000000082000L});
    public static final BitSet FOLLOW_relation_element_in_arff395 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_attribute_elements_in_arff397 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_data_element_in_arff399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ats_in_relation_element405 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RELATION_in_relation_element407 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_NAME_in_relation_element411 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_NEWLINE_in_relation_element415 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_attribute_element_in_attribute_elements424 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_ats_in_attribute_element432 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ATTRIBUTE_in_attribute_element434 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_NAME_in_attribute_element438 = new BitSet(new long[]{0x0000000000300930L});
    public static final BitSet FOLLOW_data_type_in_attribute_element442 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_NEWLINE_in_attribute_element446 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ats_in_data_element454 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DATA_in_data_element458 = new BitSet(new long[]{0x00000000003FFFF2L});
    public static final BitSet FOLLOW_NEWLINE_in_data_element461 = new BitSet(new long[]{0x00000000003FFFF2L});
    public static final BitSet FOLLOW_NUMERIC_in_data_type482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_data_type515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nominal_value_list_in_data_type589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_data_type649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_type_in_data_type721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_START_CURLY_in_nominal_value_list795 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_NAME_in_nominal_value_list799 = new BitSet(new long[]{0x0000000000001200L});
    public static final BitSet FOLLOW_nominal_value_in_nominal_value_list805 = new BitSet(new long[]{0x0000000000001200L});
    public static final BitSet FOLLOW_END_CURLY_in_nominal_value_list808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_nominal_value830 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_NAME_in_nominal_value834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_date_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_element_in_synpred3_ArffGrammar424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_synpred5_ArffGrammar461 = new BitSet(new long[]{0x0000000000000002L});

}