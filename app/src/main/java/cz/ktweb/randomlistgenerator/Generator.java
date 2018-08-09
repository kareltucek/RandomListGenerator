package cz.ktweb.randomlistgenerator;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

public class Generator {
    private int min;
    private int max;
    private int q;
    private boolean sort;
    private boolean unique;
    private String label;
    private int[] values;

    public Generator(int min, int max, int q, boolean sort, boolean unique, String label) {
        this.min = min;
        this.max = max;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        this.label = label;
        Generate();
    }

    public Generator(int min, int max, int q, boolean sort, boolean unique, String label, int[] values) {
        this.min = min;
        this.max = max;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        this.values = values;
        this.label = label;
    }

    public Generator() {
        this.min = 1;
        this.max = 100;
        this.q = 5;
        this.sort = false;
        this.unique = false;
        this.label = "";
        Generate();
    }

    public Generator(String lab) {
        this.min = 0;
        this.max = 0;
        this.q = 0;
        this.sort = false;
        this.unique = false;
        this.label = lab;
        Generate();
    }

    public void Generate() {
        Random rand = new Random();

        values = new int[q];
        for ( int i = 0; i < q; i++)
        {
            boolean is_unique = false;
            while(!is_unique)
            {
                values[i] = rand.nextInt((max - min) + 1) + min;
                is_unique = true;
                if ( this.unique && i < (max - min) + 1 )
                {
                    for (int j = 0; j < i; j++)
                    {
                        is_unique &= values[i] != values[j];
                    }
                }
            }
        }
        if(sort)
        {
            Arrays.sort(values);
        }
    }

    public static Generator fromString(String str)
    {
        try {
            String[] slices = str.split(" ");
            int min = Integer.parseInt(slices[1]);
            int max = Integer.parseInt(slices[2]);
            int q = Integer.parseInt(slices[3]);
            int sort = Integer.parseInt(slices[4]);
            int unique = Integer.parseInt(slices[5]);
            String label = "";
            if (slices[6].startsWith("\"")) {
                label = slices[6].replace("_", " ").replace("\"".toString(), "");
            }
            int arrayIdx = 7;
            while (!slices[arrayIdx - 1].equals("|")) {
                arrayIdx++;
            }
            int a = 5;
            int[] array = new int[q];
            for (int i = 0; i < q; i++) {
                array[i] = Integer.parseInt(slices[i + arrayIdx]);
            }
            return new Generator(min, max, q, sort > 0, unique > 0, label, array);
        }
        catch(Exception e)
        {
            return new Generator("Deserialization failed.");
        }
    }

    public String toString()
    {
        String label = "\"" + this.label.replace(" ", "_") + "\"";
        String serial = "N " + min + " " + max + " " + q + " " + (sort ? 1 : 0 ) + " " + (unique ? 1 : 0) + " " + label + " | ";
        for (int i = 0; i < q; i++)
        {
            serial += values[i] + " ";
        }
        return serial;
    }

    public String viewString()
    {
        String serial = "";
        if( !this.label.isEmpty())
        {
            serial = this.label + ": ";
        }
        for (int i = 0; i < q; i++)
        {
            if(i != 0)
            {
                serial += ", ";
            }
            serial += values[i];
        }
        return serial;
    }
}
