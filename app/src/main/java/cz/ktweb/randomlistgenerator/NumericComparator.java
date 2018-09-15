package cz.ktweb.randomlistgenerator;

import java.util.Comparator;


public class NumericComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        double a = Double.parseDouble(o1);
        double b = Double.parseDouble(o2);
        return Double.compare(a, b);
    }
}