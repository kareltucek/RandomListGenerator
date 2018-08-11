package cz.ktweb.randomlistgenerator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void tokenizer_test() {
        assertEquals( new String[]{"a","b","c","(","d","|","e",")","[5]"}, ExpressionEvaluator.tokenize("a, b, c( d|e)[5]"));
        assertEquals( new String[]{"-5","-","5", "\"conc\"", "5","-","-5"}, ExpressionEvaluator.tokenize("-5-5 \"conc\" 5--5"));
    }

    @Test
    public void serializer_test() {
        String test1 = "abc def_gah \"test\" test__ abc";
        String test2 = "\"test\"";
        String test3 = Utils.serializeString(test1);
        assertEquals( test1, Utils.deserializeString(Utils.serializeString(test1)));
        assertEquals( test2, Utils.deserializeString(Utils.serializeString(test2)));
        assertEquals( test3, Utils.deserializeString(Utils.serializeString(test3)));
    }

}