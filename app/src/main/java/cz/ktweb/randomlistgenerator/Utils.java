package cz.ktweb.randomlistgenerator;

public class Utils {
    public static String serializeString(String str)
    {
        return "\"" + str.replace("_", "__"). replace(" ", "_") + "\"";
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
}
