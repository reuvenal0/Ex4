import java.util.Collections;
import java.util.List;

/**
 * This class contains a set of constants for Ex2 (I2CS, ArielU 2025A),
 * As defined in: https://docs.google.com/document/d/1-18T-dj00apE4k1qmpXGOaqttxLn-Kwi/edit?usp=sharing&ouid=113711744349547563645&rtpof=true&sd=true
 * Do NOT change this class!
 *
 */
public class Ex2Utils {
    public static final int TEXT=1, NUMBER=2, FORM=3, ERR_FORM_FORMAT=-2, ERR_CYCLE_FORM=-1, ERR=-1,FUCN_TYPE=4 , ERR_FUNC=-4 ,IF_TYPE=5, ERR_IF=-5;
    public static final String ERR_CYCLE = "ERR_CYCLE!", ERR_FORM = "ERR_FORM!", ERR_IF_str = "ERR_IF!", ERR_FUCN_str = "ERR_FUNC!";
    public static final int WIDTH = 9, HEIGHT=17, MAX_CHARS=8, WINDOW_WIDTH=1200, WINDOW_HEIGHT=600;
    public static final int WAIT_TIME_MS = 10, MAX_X=20;
    public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2, PEN_RADIUS = 0.001;
    public static final double GUI_X_SPACE = 2, GUI_X_START = 3, GUI_Y_TEXT_START = 0.4;
    public static final boolean Debug = false;
    public static final String[] M_OPS = {"+", "-", "*", "/"};
    public static final String EMPTY_CELL = "";
    public static  final String[] ABC= {"A","B","C","D","E","F","G","H","I","J","K","L","O","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final String[] FUNCTIONS = {"sum" ,"average", "min", "max"};
    public static String[] B_OPS = {"<=", ">=", "<", ">", "==","!="};

    /**
     * Calculates the sum of all numerical values in the provided list.
     * This method iterates over a list of Double values and accumulates their sum.
     * If the list is null or empty, it returns 0.0.
     * @param AllCellRange A List of Double values to be summed.
     * @return The sum of all elements in the list, or 0.0 if the list is empty.
     */
    public static double sum (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0; // In an empty range list, return 0.

        double sum = 0; // The initial value for the sum is 0.

        // Easily loop through the list in a FOR-EACH loop and add each value to our sum variable.
        for (Double CellVal : AllCellRange) {
            sum += CellVal;
        }

        // return the sum result:
        return sum;
    }

    /**
     * Calculates the average of all numerical values in the provided list.
     * This method sums all elements in the list and divides the total by the number of elements.
     * If the list is empty, it returns 0.0.
     * @param AllCellRange A List of Double values to calculate the average of.
     * @return The average of all elements in the list, or 0.0 if the list is null or empty.
     */
    public static double average (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0;  // In an empty range list, return 0.

        double sum = sum(AllCellRange); // We will use our sum method

        // We will return the sum divided by the number of elements in our list
        // (there can be no division by 0 because we have already returned 0 in case there are no elements in the list)
        return sum/AllCellRange.size();
    }

    /**
     * Finds the minimum value among all numerical values in the provided list.
     * If the list is empty, it returns 0.0.
     * @param AllCellRange A List of Double values to find the minimum from.
     * @return The minimum value in the list, or 0.0 if the list is empty.
     */
    public static double min (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0; // In an empty range list, return 0.
        return Collections.min(AllCellRange); // using the collections method
    }

    /**
     * Finds the maximum value among all numerical values in the provided list.
     * If the list is empty, it returns 0.0.
     * @param AllCellRange A List of Double values to find the minimum from.
     * @return The maximum value in the list, or 0.0 if the list is empty.
     */
    public static double max (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0; // In an empty range list, return 0.
        return Collections.max(AllCellRange); // using the collections method
    }
}
