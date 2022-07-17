package mlp;

import java.util.ArrayList;

public class Dataset {
    private static  ArrayList <ArrayList <Double>> _patterns;
 
    /**
     * @return the _patterns
     */
    public static ArrayList <ArrayList <Double>> getPatterns() {
        return _patterns;
    }

    /**
     * @param _patterns the _patterns to set
     */
    public static void setPatterns(ArrayList <ArrayList <Double>> _patterns) {
        Dataset._patterns = _patterns;
    }
    
}
