package cz.ktweb.randomlistgenerator;

import android.util.Log;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import cz.ktweb.randomlistgenerator.ExpressionEvaluator.*;



public class Generator {
    private String expr;
    private int q;
    private boolean sort;
    private boolean unique;
    private String label;
    private String value;

    public Generator(String expr, int q, boolean sort, boolean unique, String label) {
        this.expr = expr;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        this.label = label;
        this.value = "";
        Generate();
    }

    public Generator(String expr, int q, boolean sort, boolean unique, String label, String value) {
        this.expr = expr;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        this.label = label;
        this.value = value;
    }

    public Generator() {
        this.expr = "1-100";
        this.q = 5;
        this.sort = false;
        this.unique = false;
        this.label = "";
        this.value = "";
        Generate();
    }

    public Generator(String lab) {
        this.expr = "";
        this.q = 0;
        this.sort = false;
        this.unique = false;
        this.label = lab;
        this.value = "";
        Generate();
    }

    public void SortStrings(String[] strings, ExpressionEvaluator.Type t)
    {
        if(t == Type.Int || t == Type.Double)
        {
            Arrays.sort(strings, new NumericComparator());
        }
        if(t == Type.String)
        {
            Arrays.sort(strings);
        }
    }

    public void Generate() {
        String[] results = new String[q];
        Type t = Type.Int;
        for(int i = 0; i < q; i++)
        {
            results[i] = "";
            boolean success = false;
            for(int j = 0; j < 100; j++)
            {
                boolean isUnique = true;
                Result r = ExpressionEvaluator.evaluate(expr);
                String newVal = r.value;
                t = t == t.Error ? t.Error : r.type;
                if(this.unique)
                {
                    for(int k = 0; k < i; k++)
                    {
                        if(results[k].equals(newVal))
                        {
                            isUnique = false;
                            break;
                        }
                    }
                }
                if(isUnique)
                {
                    results[i] = newVal;
                    success = true;
                    break;
                }
            }
            if(!success)
            {
                t = Type.Error;
                results[i] = "Failed generating unique value.";
            }
        }
        if(this.sort && q > 0)
        {
            SortStrings(results, t);
        }
        value = viewNumString(this.label, results, 0, results.length);
    }

    public static String[] resolveNumericGenerator(String[] slices)
    {
        if(slices[0].equals("N"))
        {
            String[] newSlices = new String[slices.length - 1];
            newSlices[0] = "E";
            newSlices[1] = slices[1] + "-" + slices[2];
            newSlices[2] = slices[3];
            newSlices[3] = slices[4];
            newSlices[4] = slices[5];
            int startIdx;
            if(slices[6].startsWith("\""))
            {
                newSlices[5] = slices[6];
                startIdx = 8;
            }
            else
            {
                newSlices[5] = "";
                startIdx = 7;
            }
            newSlices[6] = viewNumString(Utils.deserializeString(newSlices[5]), slices, startIdx, slices.length);
            return newSlices;
        }
        return slices;
    }


    public static Generator fromString(String str)
    {
        try {
            String[] slices = str.split(" ");
            slices = resolveNumericGenerator(slices);
            String expr = Utils.deserializeString(slices[1]);
            int q = Integer.parseInt(slices[2]);
            int sort = Integer.parseInt(slices[3]);
            int unique = Integer.parseInt(slices[4]);
            String label = Utils.deserializeString(slices[5]);
            String values = Utils.deserializeString(slices[6]);
            return new Generator(expr, q, sort > 0, unique > 0, label, values);
        }
        catch(Exception e)
        {
            return new Generator("Deserialization failed.");
        }
    }

    public String toString()
    {
        String serial = "E " + Utils.serializeString(this.expr) + " " + q + " " + (sort ? 1 : 0 ) + " " + (unique ? 1 : 0) + " " + Utils.serializeString(this.label) + " " + Utils.serializeString(this.value);
        return serial;
    }

    public static String viewNumString(String label, String[] values, int startIdx, int endIdx)
    {
        String serial = "";
        if( !label.isEmpty())
        {
            serial = label + ": ";
        }
        for (int i = startIdx; i < endIdx; i++)
        {
            if(i != startIdx && !values[i].equals(""))
            {
                serial += ", ";
            }
            serial += values[i];
        }
        return serial;
    }

    public String viewString()
    {
        return value;
    }

    public String getExpr() { return expr;};
    public String getLabel() { return label;};
    public int getQ() { return q;};
    public boolean getUnique() { return unique;};
    public boolean getSort() {return sort;};
}
