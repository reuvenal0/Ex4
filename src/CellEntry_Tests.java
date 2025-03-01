import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CellEntry_Tests - JUnit tests for the CellEntry class.
 * - Verifies the correct parsing and validation of cell entries.
 * - Tests retrieval of X and Y coordinates from valid and invalid entries.
 * - Ensures proper handling of invalid inputs and edge cases.
 * - Checks the correct string representation of cell entries.
 */
class CellEntry_Tests {

    // When we create the following objects, we will invoke the constructors and the 'parseEntry' method

    // Valid CellEntry objects
    CellEntry A0 = new CellEntry("A0");
    CellEntry z99 = new CellEntry("z99");
    CellEntry D4 = new CellEntry(3,4);
    CellEntry Y87 = new CellEntry(24,87);

    // Invalid CellEntry objects
    CellEntry A101 = new CellEntry(0,101);
    CellEntry B101 = new CellEntry("B101");
    CellEntry h999 = new CellEntry("h999");
    CellEntry c40 = new CellEntry("c-40");
    CellEntry a = new CellEntry("a");
    CellEntry AA30 = new CellEntry("AA30");
    CellEntry empty_c = new CellEntry("");
    CellEntry null_c = new CellEntry(null);
    CellEntry A1_withSpaces = new CellEntry(" A1 ");
    CellEntry B2_withSpaces2 = new CellEntry("  B2  ");
    CellEntry value_44 = new CellEntry("  4  4  ");
    CellEntry value_888 = new CellEntry("888");
    CellEntry invalidSymbols = new CellEntry("A@1");
    CellEntry invalidSymbols2 = new CellEntry("A$5");
    CellEntry negativeCords = new CellEntry(-1, 5);

    //isValid test: Tests the validity of CellEntry objects
    @Test
    void isValid_Test() {
        // valid index:
        assertTrue(A0.isValid());
        assertTrue(z99.isValid());
        assertTrue(D4.isValid());
        assertTrue(Y87.isValid());

        //invalid index:
        assertFalse(A101.isValid());
        assertFalse(B101.isValid());
        assertFalse(h999.isValid());
        assertFalse(c40.isValid());
        assertFalse(a.isValid());
        assertFalse(AA30.isValid());
        assertFalse(empty_c.isValid());
        assertFalse(null_c.isValid());
        assertFalse(value_44.isValid());
        assertFalse(value_888.isValid());

        assertFalse(A1_withSpaces.isValid());
        assertFalse(B2_withSpaces2.isValid());
        assertFalse(invalidSymbols.isValid());
        assertFalse(invalidSymbols2.isValid());
        assertFalse(negativeCords.isValid());
    }

    // Tests the x-coordinate retrieval for CellEntry objects:
    @Test
    void getX_Test() {
        // valid index:
        assertEquals(A0.getX(),0);
        assertEquals(z99.getX(),25);
        assertEquals(D4.getX(),3);
        assertEquals(Y87.getX(),24);

        //invalid index:
        assertEquals(A101.getX(),Ex2Utils.ERR);
        assertEquals(B101.getX(),Ex2Utils.ERR);
        assertEquals(h999.getX(),Ex2Utils.ERR);
        assertEquals(c40.getX(),Ex2Utils.ERR);
        assertEquals(a.getX(),Ex2Utils.ERR);
        assertEquals(AA30.getX(),Ex2Utils.ERR);
        assertEquals(empty_c.getX(),Ex2Utils.ERR);
        assertEquals(null_c.getX(),Ex2Utils.ERR);
        assertEquals(value_44.getX(),Ex2Utils.ERR);
        assertEquals(value_888.getX(),Ex2Utils.ERR);
        assertEquals(invalidSymbols.getX(),Ex2Utils.ERR);
        assertEquals(invalidSymbols2.getX(),Ex2Utils.ERR);
        assertEquals(negativeCords.getX(),Ex2Utils.ERR);
        assertEquals(A1_withSpaces.getX(),Ex2Utils.ERR);
        assertEquals(B2_withSpaces2.getX(),Ex2Utils.ERR);

    }

    // Tests the y-coordinate retrieval for CellEntry objects
    @Test
    void getY_Test() {
        // valid index:
        assertEquals(A0.getY(),0);
        assertEquals(z99.getY(),99);
        assertEquals(D4.getY(),4);
        assertEquals(Y87.getY(),87);

        //invalid index:
        assertEquals(A101.getY(),Ex2Utils.ERR);
        assertEquals(B101.getY(),Ex2Utils.ERR);
        assertEquals(h999.getY(),Ex2Utils.ERR);
        assertEquals(c40.getY(),Ex2Utils.ERR);
        assertEquals(a.getY(),Ex2Utils.ERR);
        assertEquals(AA30.getY(),Ex2Utils.ERR);
        assertEquals(empty_c.getY(),Ex2Utils.ERR);
        assertEquals(null_c.getY(),Ex2Utils.ERR);
        assertEquals(value_44.getY(),Ex2Utils.ERR);
        assertEquals(value_888.getY(),Ex2Utils.ERR);
        assertEquals(invalidSymbols.getY(),Ex2Utils.ERR);
        assertEquals(invalidSymbols2.getY(),Ex2Utils.ERR);
        assertEquals(negativeCords.getY(),Ex2Utils.ERR);
        assertEquals(A1_withSpaces.getY(),Ex2Utils.ERR);
        assertEquals(B2_withSpaces2.getY(),Ex2Utils.ERR);
    }

    // Tests the string representation of CellEntry objects
    @Test
    void ToString_Test() {
        // valid index:
        assertEquals(A0.toString(),"A0");
        assertEquals(z99.toString(),"Z99");
        assertEquals(D4.toString(),"D4");
        assertEquals(Y87.toString(),"Y87");

        //invalid index:
        assertEquals(A101.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(B101.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(h999.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(c40.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(a.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(AA30.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(empty_c.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(null_c.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(value_44.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(value_888.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(invalidSymbols.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(invalidSymbols2.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(negativeCords.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(A1_withSpaces.toString(),Ex2Utils.EMPTY_CELL);
        assertEquals(B2_withSpaces2.toString(),Ex2Utils.EMPTY_CELL);
    }
}