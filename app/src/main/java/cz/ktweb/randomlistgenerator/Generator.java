package cz.ktweb.randomlistgenerator;

import java.util.Arrays;
import java.util.Random;

public class Generator {
    private int min;
    private int max;
    private int q;
    private boolean sort;
    private boolean unique;
    private int[] values;

    public Generator(int min, int max, int q, boolean sort, boolean unique) {
        this.min = min;
        this.max = max;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        Generate();
    }

    public Generator(int min, int max, int q, boolean sort, boolean unique, int[] values) {
        this.min = min;
        this.max = max;
        this.q = q;
        this.sort = sort;
        this.unique = unique;
        this.values = values;
    }

    public Generator() {
        this.min = 1;
        this.max = 100;
        this.q = 5;
        this.sort = false;
        this.unique = false;
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
        String[] slices = str.split(" ");
        int min = Integer.parseInt(slices[1]);
        int max = Integer.parseInt(slices[2]);
        int q = Integer.parseInt(slices[3]);
        int sort = Integer.parseInt(slices[4]);
        int unique = Integer.parseInt(slices[5]);
        int[] array  = new int[q];
        int arrayIdx = 7;
        for(int i = 0; i < q; i++)
        {
            array[i] = Integer.parseInt(slices[i+arrayIdx]);
        }
        return new Generator(min, max, q, sort > 0, unique > 0, array);
    }

    public String toString()
    {
        String serial = "N " + min + " " + max + " " + q + " " + (sort ? 1 : 0 ) + " " + (unique ? 1 : 0) + " | ";
        for (int i = 0; i < q; i++)
        {
            serial += values[i] + " ";
        }
        return serial;
    }

    public String viewString()
    {
        String serial = "";
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
