package cz.ktweb.randomlistgenerator;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Whoever you are, please do not judge me by this code!
 *
 * For sake of simplicity, I am commiting many crimes here. The expression is first split into tokens
 * using simple regex tokenizer. Then, the expression is evaluated in a single run, in bottom up fashion.
 * This means that we throw dice for all nodes of the syntax tree, irrespectively of whether they
 * contribute to the result or not.
 *
 * The returned values also contain type of the result. The type is the most special common ancestor
 * of the expression's elements. Unless "Error" type is returned, corectness of expression is guaranteed
 * as well as its overall type.
 */
public class ExpressionEvaluator {
    public enum Type {Int, Double, String, Error}

    public class Result {
        Type type;
        String value;
        double weight;

        public Result(Type t, String v, double w){
            type = t;
            value = v;
            weight = w;
        }
    }

    private String[] tokens;
    private int idx;
    private Random rand;


    private ExpressionEvaluator(String str) {
        tokens = tokenize(str);
        idx = 0;
        rand = new Random();
    }

    public static String[] tokenize(String str)
    {
        return str
                .replaceAll("([()|])", " $1 ")
                .replaceAll("\\[ *", " [")
                .replaceAll(" *\\]", "] ")
                .replaceAll("- *([0-9])", " -$1")
                .replaceAll("([0-9]) *- *([-0-9])", "$1 - $2")
                .replaceAll(",", " ")
                .replaceAll("  *", " ")
                .replaceAll("^  *", "")
                .replaceAll("  *$", "")
                .split(" ");
    }

    public static Result evaluate(String str)
    {
            ExpressionEvaluator evaluator = new ExpressionEvaluator(str);
            return evaluator.evaluateGroup();
    }

    private Result pick(List<Result> list) {
        Type t = Type.Int;
        int total_weight = 0;
        for ( Result r : list ) {
            if(t == Type.Int && r.type == Type.Double)
            {
                t = Type.Double;
            }
            if(r.type == Type.String)
            {
                t = Type.String;
            }
            if(r.type == Type.Error)
            {
                return r;
            }
            total_weight += r.weight;
        }
        int roulette = rand.nextInt(total_weight);
        int sum = 0;
        for(int i = 0; i < list.size(); i++)
        {
            sum += list.get(i).weight;
            if(sum > roulette)
            {
                Result r = list.get(i);
                r.weight = total_weight;
                return r;
            }
        }
        return new Result(Type.Error, "Error: Our roulette has broken. This should have never happened.", 1);
    }

    private Result evaluateToken() {
        String tok = tokens[idx];
        idx++;
        try
        {
            if(tok.contains("."))
            {
                double r = Double.parseDouble(tok);
                return new Result(Type.Double, tok, 1);
            }
            else {
                int i = Integer.parseInt(tok);
                return new Result(Type.Int, tok, 1);
            }
        }
        catch(Exception e)
        {
            return new Result(Type.String, tok, 1);
        }
    }

    private Result evaluatePair(Result left, Result right) {
        if(left.type == Type.Int && right.type == Type.Int)
        {
            int l = Integer.parseInt(left.value);
            int r = Integer.parseInt(right.value);
            int min = l < r ? l : r;
            int max = l > r ? l : r;
            return new Result(Type.Int, Integer.toString(rand.nextInt((max - min) + 1) + min), max-min + 1);
        }
        if((left.type == Type.Int || left.type == Type.Double) && (right.type == Type.Int || right.type == Type.Double))
        {
            double l = Double.parseDouble(left.value);
            double r = Double.parseDouble(right.value);
            double min = l < r ? l : r;
            double max = l > r ? l : r;
            return new Result(Type.Double, Double.toString(min + rand.nextDouble()*(max - min)), 1);

        }
        return new Result(Type.Error, "Error: Non-numeric value found as parameter to range.", 1);
    }

    private Result evaluateSingleItem() {
        Result item;
        if(tokens[idx].equals("("))
        {
            idx++;
            item = evaluateGroup();
            item.weight = 1;
            if(!tokens[idx].equals(")"))
            {
                return new Result(Type.Error, "Error: Closing parenthesis not found when expected.", 1);
            }
            idx++;
        }
        else
        {
            item = evaluateToken();
        }

        while(idx < tokens.length && tokens[idx].equals("-"))
        {
            idx++;
            if(idx >= tokens.length)
            {
                return new Result(Type.Error, "Error: Out of range while complementing range syntax.", 1);
            }
            Result right = evaluateSingleItem();
            item = evaluatePair(item, right);
        }
        return item;
    }

    private Result squash(List<Result> list)
    {
        boolean error = false;
        if(list.size() == 1)
        {
            return list.get(0);
        }
        String s = "";
        for(int i = 0; i < list.size(); i++)
        {
            s = s + list.get(i).value;
            error |= list.get(i).type == Type.Error;
        }
        return new Result(error ? Type.Error : Type.String, s, 1);
    }

    private Result evaluateGroup()
    {
        List<Result> results = new Vector<>();
        List<Result> buffer = new Vector<>();
        while ( idx < tokens.length )
        {
            if(tokens[idx].equals("|"))
            {
                results.add(pick(buffer));
                results.add(new Result(Type.String, "|", 1));
                buffer = new Vector<>();
                idx++;
            }
            else if(tokens[idx].startsWith("\""))
            {
                results.add(pick(buffer));
                results.add(new Result(Type.String, Utils.deserializeString(tokens[idx]), 1));
                buffer = new Vector<>();
                idx++;
            }
            else if(tokens[idx].equals(")"))
            {
                break;
            }
            else if(tokens[idx].startsWith("["))
            {
                try {
                    String num = tokens[idx].replaceAll("[\\[\\]]", "");
                    double weight = Double.parseDouble(num);
                    buffer.get(buffer.size()-1).weight = weight;
                }
                catch (Exception e)
                {
                    buffer.add(new Result(Type.Error, "Error: failed to process [] expression.", 1));
                }
                idx++;
            }
            else
            {
                buffer.add(evaluateSingleItem());
            }
        }
        if(!buffer.isEmpty()) {
            results.add(pick(buffer));
        }
        return squash(results);
    }
}
