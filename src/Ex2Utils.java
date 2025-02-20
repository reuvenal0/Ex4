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
}
