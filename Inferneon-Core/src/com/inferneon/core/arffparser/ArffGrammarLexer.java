// $ANTLR 3.3 Nov 30, 2010 12:50:56 Resources/ArffGrammar.g 2015-04-29 14:37:50
 package com.inferneon.core.arffparser;
				import com.inferneon.core.Attribute;
               

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ArffGrammarLexer extends Lexer {
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

    public ArffGrammarLexer() {;} 
    public ArffGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ArffGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "Resources/ArffGrammar.g"; }

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:11:7: ( '@' )
            // Resources/ArffGrammar.g:11:9: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:12:7: ( 'mm-dd-yy' )
            // Resources/ArffGrammar.g:12:9: 'mm-dd-yy'
            {
            match("mm-dd-yy"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:13:7: ( 'mm-dd-yyyy' )
            // Resources/ArffGrammar.g:13:9: 'mm-dd-yyyy'
            {
            match("mm-dd-yyyy"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "NUMERIC"
    public final void mNUMERIC() throws RecognitionException {
        try {
            int _type = NUMERIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:103:9: ( 'numeric' | 'NUMERIC' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='n') ) {
                alt1=1;
            }
            else if ( (LA1_0=='N') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // Resources/ArffGrammar.g:103:11: 'numeric'
                    {
                    match("numeric"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:103:23: 'NUMERIC'
                    {
                    match("NUMERIC"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMERIC"

    // $ANTLR start "REAL"
    public final void mREAL() throws RecognitionException {
        try {
            int _type = REAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:104:6: ( 'real' | 'REAL' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='r') ) {
                alt2=1;
            }
            else if ( (LA2_0=='R') ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // Resources/ArffGrammar.g:104:8: 'real'
                    {
                    match("real"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:104:17: 'REAL'
                    {
                    match("REAL"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL"

    // $ANTLR start "RELATION"
    public final void mRELATION() throws RecognitionException {
        try {
            int _type = RELATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:105:10: ( 'relation' | 'RELATION' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='r') ) {
                alt3=1;
            }
            else if ( (LA3_0=='R') ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // Resources/ArffGrammar.g:105:12: 'relation'
                    {
                    match("relation"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:105:25: 'RELATION'
                    {
                    match("RELATION"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RELATION"

    // $ANTLR start "ATTRIBUTE"
    public final void mATTRIBUTE() throws RecognitionException {
        try {
            int _type = ATTRIBUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:106:11: ( 'attribute' | 'ATTRIBUTE' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='a') ) {
                alt4=1;
            }
            else if ( (LA4_0=='A') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // Resources/ArffGrammar.g:106:13: 'attribute'
                    {
                    match("attribute"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:106:27: 'ATTRIBUTE'
                    {
                    match("ATTRIBUTE"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ATTRIBUTE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:107:8: ( 'string' | 'STRING' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='s') ) {
                alt5=1;
            }
            else if ( (LA5_0=='S') ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // Resources/ArffGrammar.g:107:10: 'string'
                    {
                    match("string"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:107:21: 'STRING'
                    {
                    match("STRING"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:108:7: ( ',' )
            // Resources/ArffGrammar.g:108:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DATA"
    public final void mDATA() throws RecognitionException {
        try {
            int _type = DATA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:110:6: ( 'data' | 'DATA' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='d') ) {
                alt6=1;
            }
            else if ( (LA6_0=='D') ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // Resources/ArffGrammar.g:110:8: 'data'
                    {
                    match("data"); 


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:110:17: 'DATA'
                    {
                    match("DATA"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DATA"

    // $ANTLR start "START_CURLY"
    public final void mSTART_CURLY() throws RecognitionException {
        try {
            int _type = START_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:111:13: ( '{' )
            // Resources/ArffGrammar.g:111:15: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "START_CURLY"

    // $ANTLR start "END_CURLY"
    public final void mEND_CURLY() throws RecognitionException {
        try {
            int _type = END_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:112:11: ( '}' )
            // Resources/ArffGrammar.g:112:13: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "END_CURLY"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:113:9: ( ( '\\r' )? '\\n' )
            // Resources/ArffGrammar.g:113:11: ( '\\r' )? '\\n'
            {
            // Resources/ArffGrammar.g:113:11: ( '\\r' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\r') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // Resources/ArffGrammar.g:113:11: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:114:14: ( '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // Resources/ArffGrammar.g:114:16: '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match('%'); 
            // Resources/ArffGrammar.g:114:20: (~ ( '\\n' | '\\r' ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\u0000' && LA8_0<='\t')||(LA8_0>='\u000B' && LA8_0<='\f')||(LA8_0>='\u000E' && LA8_0<='\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // Resources/ArffGrammar.g:114:20: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // Resources/ArffGrammar.g:114:34: ( '\\r' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='\r') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // Resources/ArffGrammar.g:114:34: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:116:6: ( ( ALPHA | OTHER_CHARS )+ | '\\'' ( ALPHA | OTHER_CHARS )+ '\\'' )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='$'||(LA12_0>='-' && LA12_0<='.')||(LA12_0>='0' && LA12_0<='9')||(LA12_0>='<' && LA12_0<='>')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                alt12=1;
            }
            else if ( (LA12_0=='\'') ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // Resources/ArffGrammar.g:116:8: ( ALPHA | OTHER_CHARS )+
                    {
                    // Resources/ArffGrammar.g:116:8: ( ALPHA | OTHER_CHARS )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0=='$'||(LA10_0>='-' && LA10_0<='.')||(LA10_0>='0' && LA10_0<='9')||(LA10_0>='<' && LA10_0<='>')||(LA10_0>='A' && LA10_0<='Z')||LA10_0=='_'||(LA10_0>='a' && LA10_0<='z')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // Resources/ArffGrammar.g:
                    	    {
                    	    if ( input.LA(1)=='$'||(input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='<' && input.LA(1)<='>')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // Resources/ArffGrammar.g:117:9: '\\'' ( ALPHA | OTHER_CHARS )+ '\\''
                    {
                    match('\''); 
                    // Resources/ArffGrammar.g:117:14: ( ALPHA | OTHER_CHARS )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0=='$'||(LA11_0>='-' && LA11_0<='.')||(LA11_0>='0' && LA11_0<='9')||(LA11_0>='<' && LA11_0<='>')||(LA11_0>='A' && LA11_0<='Z')||LA11_0=='_'||(LA11_0>='a' && LA11_0<='z')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // Resources/ArffGrammar.g:
                    	    {
                    	    if ( input.LA(1)=='$'||(input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='<' && input.LA(1)<='>')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);

                    match('\''); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "ALPHA"
    public final void mALPHA() throws RecognitionException {
        try {
            // Resources/ArffGrammar.g:120:2: ( 'A' .. 'Z' | 'a' .. 'z' )
            // Resources/ArffGrammar.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ALPHA"

    // $ANTLR start "OTHER_CHARS"
    public final void mOTHER_CHARS() throws RecognitionException {
        try {
            // Resources/ArffGrammar.g:125:2: ( '$' | '_' | '-' | '0' .. '9' | '>' | '<' | '=' | '.' )
            // Resources/ArffGrammar.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='<' && input.LA(1)<='>')||input.LA(1)=='_' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "OTHER_CHARS"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Resources/ArffGrammar.g:135:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // Resources/ArffGrammar.g:135:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // Resources/ArffGrammar.g:135:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>='\t' && LA13_0<='\n')||(LA13_0>='\f' && LA13_0<='\r')||LA13_0==' ') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // Resources/ArffGrammar.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    public void mTokens() throws RecognitionException {
        // Resources/ArffGrammar.g:1:8: ( T__19 | T__20 | T__21 | NUMERIC | REAL | RELATION | ATTRIBUTE | STRING | COMMA | DATA | START_CURLY | END_CURLY | NEWLINE | LINE_COMMENT | NAME | WHITESPACE )
        int alt14=16;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // Resources/ArffGrammar.g:1:10: T__19
                {
                mT__19(); 

                }
                break;
            case 2 :
                // Resources/ArffGrammar.g:1:16: T__20
                {
                mT__20(); 

                }
                break;
            case 3 :
                // Resources/ArffGrammar.g:1:22: T__21
                {
                mT__21(); 

                }
                break;
            case 4 :
                // Resources/ArffGrammar.g:1:28: NUMERIC
                {
                mNUMERIC(); 

                }
                break;
            case 5 :
                // Resources/ArffGrammar.g:1:36: REAL
                {
                mREAL(); 

                }
                break;
            case 6 :
                // Resources/ArffGrammar.g:1:41: RELATION
                {
                mRELATION(); 

                }
                break;
            case 7 :
                // Resources/ArffGrammar.g:1:50: ATTRIBUTE
                {
                mATTRIBUTE(); 

                }
                break;
            case 8 :
                // Resources/ArffGrammar.g:1:60: STRING
                {
                mSTRING(); 

                }
                break;
            case 9 :
                // Resources/ArffGrammar.g:1:67: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 10 :
                // Resources/ArffGrammar.g:1:73: DATA
                {
                mDATA(); 

                }
                break;
            case 11 :
                // Resources/ArffGrammar.g:1:78: START_CURLY
                {
                mSTART_CURLY(); 

                }
                break;
            case 12 :
                // Resources/ArffGrammar.g:1:90: END_CURLY
                {
                mEND_CURLY(); 

                }
                break;
            case 13 :
                // Resources/ArffGrammar.g:1:100: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 14 :
                // Resources/ArffGrammar.g:1:108: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 15 :
                // Resources/ArffGrammar.g:1:121: NAME
                {
                mNAME(); 

                }
                break;
            case 16 :
                // Resources/ArffGrammar.g:1:126: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;

        }

    }


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\2\uffff\11\23\1\uffff\2\23\2\uffff\1\24\1\40\3\uffff\13\23\1\uffff"+
        "\20\23\1\76\1\23\1\76\5\23\2\105\3\23\1\uffff\6\23\1\uffff\7\23"+
        "\2\126\1\23\2\130\4\23\1\uffff\1\136\1\uffff\2\137\3\23\2\uffff"+
        "\2\143\1\144\2\uffff";
    static final String DFA14_eofS =
        "\145\uffff";
    static final String DFA14_minS =
        "\1\11\1\uffff\1\155\1\165\1\125\1\145\1\105\1\164\1\124\1\164\1"+
        "\124\1\uffff\1\141\1\101\2\uffff\1\12\1\11\3\uffff\1\55\1\155\1"+
        "\115\1\141\1\101\1\164\1\124\1\162\1\122\1\164\1\124\1\uffff\1\144"+
        "\1\145\1\105\1\154\1\141\1\114\1\101\1\162\1\122\1\151\1\111\1\141"+
        "\1\101\1\144\1\162\1\122\1\44\1\164\1\44\1\124\1\151\1\111\1\156"+
        "\1\116\2\44\1\55\1\151\1\111\1\uffff\1\151\1\111\1\142\1\102\1\147"+
        "\1\107\1\uffff\1\171\1\143\1\103\1\157\1\117\1\165\1\125\2\44\1"+
        "\171\2\44\1\156\1\116\1\164\1\124\1\uffff\1\44\1\uffff\2\44\1\145"+
        "\1\105\1\171\2\uffff\3\44\2\uffff";
    static final String DFA14_maxS =
        "\1\175\1\uffff\1\155\1\165\1\125\1\145\1\105\1\164\1\124\1\164"+
        "\1\124\1\uffff\1\141\1\101\2\uffff\1\12\1\40\3\uffff\1\55\1\155"+
        "\1\115\1\154\1\114\1\164\1\124\1\162\1\122\1\164\1\124\1\uffff\1"+
        "\144\1\145\1\105\1\154\1\141\1\114\1\101\1\162\1\122\1\151\1\111"+
        "\1\141\1\101\1\144\1\162\1\122\1\172\1\164\1\172\1\124\1\151\1\111"+
        "\1\156\1\116\2\172\1\55\1\151\1\111\1\uffff\1\151\1\111\1\142\1"+
        "\102\1\147\1\107\1\uffff\1\171\1\143\1\103\1\157\1\117\1\165\1\125"+
        "\2\172\1\171\2\172\1\156\1\116\1\164\1\124\1\uffff\1\172\1\uffff"+
        "\2\172\1\145\1\105\1\171\2\uffff\3\172\2\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\11\uffff\1\11\2\uffff\1\13\1\14\2\uffff\1\16\1\17"+
        "\1\20\13\uffff\1\15\35\uffff\1\5\6\uffff\1\12\20\uffff\1\10\1\uffff"+
        "\1\4\5\uffff\1\2\1\6\3\uffff\1\7\1\3";
    static final String DFA14_specialS =
        "\145\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\24\1\21\1\uffff\1\24\1\20\22\uffff\1\24\3\uffff\1\23\1\22"+
            "\1\uffff\1\23\4\uffff\1\13\2\23\1\uffff\12\23\2\uffff\3\23\1"+
            "\uffff\1\1\1\10\2\23\1\15\11\23\1\4\3\23\1\6\1\12\7\23\4\uffff"+
            "\1\23\1\uffff\1\7\2\23\1\14\10\23\1\2\1\3\3\23\1\5\1\11\7\23"+
            "\1\16\1\uffff\1\17",
            "",
            "\1\25",
            "\1\26",
            "\1\27",
            "\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "",
            "\1\36",
            "\1\37",
            "",
            "",
            "\1\21",
            "\2\24\1\uffff\2\24\22\uffff\1\24",
            "",
            "",
            "",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44\12\uffff\1\45",
            "\1\46\12\uffff\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\77",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\106",
            "\1\107",
            "\1\110",
            "",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\127",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\30\23\1\135\1\23",
            "",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\140",
            "\1\141",
            "\1\142",
            "",
            "",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "\1\23\10\uffff\2\23\1\uffff\12\23\2\uffff\3\23\2\uffff\32"+
            "\23\4\uffff\1\23\1\uffff\32\23",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__19 | T__20 | T__21 | NUMERIC | REAL | RELATION | ATTRIBUTE | STRING | COMMA | DATA | START_CURLY | END_CURLY | NEWLINE | LINE_COMMENT | NAME | WHITESPACE );";
        }
    }
 

}