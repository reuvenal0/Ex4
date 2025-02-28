import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SCellTests - Unit tests for the SCell class.
 *
 * - Tests the behavior of SCell with different types of data.
 * - Verifies the classification of data as NUMBER or TEXT.
 * - Ensures accurate detection of invalid data types.
 */
public class SCellTests {

    SCell test_cell = new SCell("123"); // a Cell for our tests

    //testing an Number value inside Scell
    @Test
    public void Number_tests()
    {
        // Valid number value:
        test_cell.setData("123"); // testing this value inside the cell
        assertEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("1.7976931348623"); // testing this value inside the cell
        assertEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("-101.20"); // testing this value inside the cell
        assertEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("0000123.11999"); // testing this value inside the cell
        assertEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("9007199254740990"); // testing this value inside the cell
        assertEquals(Ex2Utils.NUMBER, test_cell.getType());

        // False value:
        test_cell.setData("=ABC123"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=101"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=101+A2"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=ATYA101"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("ABC1%$%23&^"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("112 87"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=(404+404)/8"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=A0*2 + 2"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData("=1+1"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData(""); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());

        test_cell.setData(null); // testing this value inside the cell
        assertNotEquals(Ex2Utils.NUMBER, test_cell.getType());
    }

    //testing isText method
    @Test
    public void Text_test(){
        // Valid text value:
        test_cell.setData("ABC1%$%23&^"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("test"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("1+1"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("ABC BCD"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("ABC 123"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("123 456"); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("ABC 123   !!  &&^__("); // testing this value inside the cell
        assertEquals(Ex2Utils.TEXT, test_cell.getType());

        // False value:
        test_cell.setData("=ABC123"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=101"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=AA101"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=(404+404)/8"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=123"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=1.7976931348623"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=-101.20"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());

        test_cell.setData("=9007199254740990"); // testing this value inside the cell
        assertNotEquals(Ex2Utils.TEXT, test_cell.getType());
    }

//    Tests on formulas (computeForm), function (computeFun) and condition(computeIF)
//    are located in 'Ex2SheetTest.java' !!!
}