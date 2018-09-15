package cz.ktweb.randomlistgenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    public static String serializeString(String str)
    {
        return "\"" + str.replace("_", "__"). replace(" ", "_") + "\"";
    }

    public static String formatDouble(double d, int precision)
    {
        return BigDecimal
                .valueOf(d)
                .setScale(precision, RoundingMode.HALF_UP)
                .toString();
    }

    public static String deserializeString(String str)
    {
        return str
                .replaceAll("__", "\n")
                .replaceAll("_", " ")
                .replaceAll("\n", "_")
                .replaceAll("^\"", "")
                .replaceAll("\"$", "");
    }

    public static String normalizeExpression(String str)
    {
        boolean inQuote = false;

        char[] strArray = str.toCharArray();
        String buffer = "";
        for(int i = 0; i < strArray.length; i++)
        {
            if(strArray[i] == '"')
            {
                inQuote = !inQuote;
            }
            if(strArray[i] == ' ' && inQuote)
            {
                buffer += '_';
            }
            else if(strArray[i] == '_' && inQuote)
            {
                buffer += "__";
            }
            else
            {
                buffer += strArray[i];
            }
        }
        return buffer;
    }
}
