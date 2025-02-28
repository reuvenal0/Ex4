import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for Ex2Sheet class using JUnit
class Ex2SheetTest {

    // let's create a spreadsheet for our tests - according to the maximum dimensions we require: A-Z [0-25] on the X-axis, and 100 [0-99] on the Y-axis:
    Ex2Sheet TestSheet = new Ex2Sheet(26,100);

    // Tests for Ex2Sheet constructors
    @Test
    void constructors_Test() {
        // invalid spreadsheet dimensions
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(5, -1));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(27, 5));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(5, 101));

        // default spreadsheet constructor:
        Ex2Sheet DefaultSheet = new Ex2Sheet(); // Ex2Utils.WIDTH = 9 Ex2Utils.HEIGHT = 17:
        assertEquals(DefaultSheet.width(), 9);
        assertEquals(DefaultSheet.height(), 17);
    }

    // adding a Cells to TestSheet spreadsheet, and test the following Methods:
    // set, value, eval(), eval(int x, int y), computeForm(String form, int x, int y),
    // indexOfMainOp(String form), BracketEndInd(String form), getOperatorPriority(char operator)
    // more complex test on computeForm will be in the following tests method (taken form first stage tests...)
    @Test
    void set_Test() {
        TestSheet.set(8,2,"text cell"); // text type cell

        TestSheet.set(0, 0, "100"); // A0
        TestSheet.set(25, 99, "200"); // Z99
        assertEquals("100.0", TestSheet.value(0, 0));
        assertEquals("200.0", TestSheet.value(25, 99));
//
        TestSheet.set(1, 1, "=A0+Z99");       // B1 = A0 + Z99
        TestSheet.set(2, 2, "=B1*2");         // C2 = B1 * 2
        TestSheet.set(3, 3, "=INVALID");      // D2 (invalid formula)

        assertEquals("300.0", TestSheet.value(1, 1));
        assertEquals("600.0", TestSheet.value(2, 2));
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(3, 3));


        // Test complex formula chain
        TestSheet.set(0, 1, "10"); // A1
        TestSheet.set(1, 1, "=A1*2"); // B1
        TestSheet.set(2, 1, "=B1+A1"); // C1
        TestSheet.set(3, 1, "=C1/10"); // D1

        assertEquals("10.0", TestSheet.value(0, 1));
        assertEquals("20.0", TestSheet.value(1, 1));
        assertEquals("30.0", TestSheet.value(2, 1));
        assertEquals("3.0", TestSheet.value(3, 1));

        // Test nested formulas
        TestSheet.set(5, 5, "=(A1*2+B1*2)/2");    // F5
        assertEquals("30.0", TestSheet.value(5, 5));

        // Test long reference chain
        TestSheet.set(0, 10, "2"); // A10 = 2
        for (int i = 1; i < 10; i++)
        {
            char c = (char)('A' + i - 1);
            String input2cell = "=(" + c + "10 " + " * 2) + 2";
            TestSheet.set(i , 10, input2cell);
        }
        assertEquals("6.0", TestSheet.value(1, 10));
        assertEquals("14.0", TestSheet.value(2, 10));
        assertEquals("30.0", TestSheet.value(3, 10));
        assertEquals("2046.0", TestSheet.value(9, 10));

        // Test a cell that depends on an invalid formula and then an update is performed on one cell -
        // we will check that the dependent cell is also updated.
        TestSheet.set(0, 0, "=abc");
        TestSheet.set(0, 1, "=A0*2");
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(0, 1));
        TestSheet.set(0, 0, "=2");
        assertEquals("2.0", TestSheet.value(0, 0));
        assertEquals("4.0", TestSheet.value(0, 1));

        // test invalid Cell reference:
        TestSheet.set(4, 4, "=Z101+4");
        TestSheet.set(4, 5, "=A-1");
        TestSheet.set(4, 6, "=C9999*2");
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(4, 4));
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(4, 5));
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(4, 6));


        // Test empty cells
        assertEquals("", TestSheet.value(15, 15));
        TestSheet.set(15, 15, "test");
        TestSheet.set(15, 15, "");
        assertEquals("", TestSheet.value(15, 15));

        // Test circular reference cells - are below in the 'depth()' test method.
    }

    // Tests for retrieving cells by X and Y coordinates
    @Test
    void getByIndex_Test() {
        // Test valid coordinates with different cell types
        TestSheet.set(0, 0, "Hello"); // Text cell
        TestSheet.set(0, 1, "123"); // Number cell
        TestSheet.set(0, 2, "=A1"); // Formula cell
        TestSheet.set(0, 3, ""); // Empty cell

        // Verify cell retrieval and content
        assertNotNull(TestSheet.get(0, 0));
        assertEquals("Hello", TestSheet.get(0, 0).getData());

        assertNotNull(TestSheet.get(0, 1));
        assertEquals("123", TestSheet.get(0, 1).getData());

        assertNotNull(TestSheet.get(0, 2));
        assertEquals("=A1", TestSheet.get(0, 2).getData());

        assertNotNull(TestSheet.get(0, 3));
        assertEquals("", TestSheet.get(0, 3).getData());

        // Test boundary coordinates
        assertNotNull(TestSheet.get(0, 0)); // Top left
        assertNotNull(TestSheet.get(25, 99)); // Bottom right

        // Test invalid coordinates
        assertNull(TestSheet.get(-1, 0));
        assertNull(TestSheet.get(0, -1));
        assertNull(TestSheet.get(26, 0));
        assertNull(TestSheet.get(0, 100));
        assertNull(TestSheet.get(-1, -1));
        assertNull(TestSheet.get(26, 100));
    }

    // Tests for retrieving cells by string coordinates (e.g., "A0", "Z99")
    @Test
    void getByCord_Test() {
        // Set up test cells with different types of content
        TestSheet.set(0, 0, "Text"); // A0
        TestSheet.set(1, 0, "123"); // B0
        TestSheet.set(2, 0, "=A1"); // C0
        TestSheet.set(25, 99, "Last cell"); // Z99

        // Test valid cell references
        assertNotNull(TestSheet.get("A0"));
        assertEquals("Text", TestSheet.get("A0").getData());

        assertNotNull(TestSheet.get("B0"));
        assertEquals("123", TestSheet.get("B0").getData());

        assertNotNull(TestSheet.get("C0"));
        assertEquals("=A1", TestSheet.get("C0").getData());

        assertNotNull(TestSheet.get("Z99"));
        assertEquals("Last cell", TestSheet.get("Z99").getData());

        // Test case sensitivity
        assertNotNull(TestSheet.get("a0")); // Lower case column
        assertNotNull(TestSheet.get("A0")); // Upper case column

        // Test invalid references
        assertNull(TestSheet.get("")); // Empty string
        assertNull(TestSheet.get("A")); // Missing row number
        assertNull(TestSheet.get("1")); // Missing column letter
        assertNull(TestSheet.get("AA1")); // Column index too large
        assertNull(TestSheet.get("A101")); // Row index too large
        assertNull(TestSheet.get("A-1")); // Zero row index
        assertNull(TestSheet.get("@1")); // Invalid column character
        assertNull(TestSheet.get("A-1")); // Negative row index
        assertNull(TestSheet.get("12A")); // Invalid format
        assertNull(TestSheet.get("ABC")); // Invalid format
        assertNull(TestSheet.get(" A1 ")); // Spaces in reference
    }

    // Test for spreadsheet width
    // default spreadsheet constructor was tested already checked before...So there we check these functions again:
    @Test
    void width_Test() {
        assertEquals(TestSheet.width(), 26);
    }

    // Test for spreadsheet height
    // default spreadsheet constructor was tested already checked before...So there we check these functions again:
    @Test
    void height_Test() {
        assertEquals(TestSheet.height(), 100);
    }

    // Tests for checking if coordinates are within the spreadsheet bounds
    @Test
    void isIn_Test() {
        assertTrue(TestSheet.isIn(0, 0)); // Valid top-left
        assertTrue(TestSheet.isIn(25, 98)); // Valid bottom-right

        assertFalse(TestSheet.isIn(-1, 0)); // Negative X
        assertFalse(TestSheet.isIn(0, -1)); // Negative Y
        assertFalse(TestSheet.isIn(26, 0)); // X out of range
        assertFalse(TestSheet.isIn(0, 101)); // Y out of range
    }

    // Tests for depth calculation in formula dependencies.
    // Checks calculation depth for simple and nested formulas.
    // Validates detection of circular references.
    @Test
    void depth_Test() {
        // Test depth calculations
        TestSheet.set(0, 20, "5"); // A20: depth 0
        TestSheet.set(1, 20, "=A20+1"); // B20: depth 1
        TestSheet.set(2, 20, "=B20*2"); // C20: depth 2
        // Test circular reference cells
        TestSheet.set(4, 0, "=E1"); // E0 references E1
        TestSheet.set(4, 1, "=E2"); // E1 references E2
        TestSheet.set(4, 2, "=E0"); // E2 references E0
        TestSheet.set(4, 3, "=E3"); // E3 references E3


        int[][] depths = TestSheet.depth();

        assertEquals(0, depths[0][20]); // A20
        assertEquals(1, depths[1][20]); // B20
        assertEquals(2, depths[2][20]); // C20

        assertEquals(-1, depths[4][0]); //E0
        assertEquals(-1, depths[4][1]); //E1
        assertEquals(-1, depths[4][2]); //E2
        assertEquals(-1, depths[4][3]); // E3

        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 0)); //E0
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 1)); //E1
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 2)); //E2
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 3)); //E3

        // Circular Error Detection
        TestSheet.set(0, 0, "=B0");
        TestSheet.set(1, 0, "=C0");
        TestSheet.set(2, 0, "=A0");
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(1, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(2, 0));

        //depth With Range
        // - Check depth calculation with range references on function cells
        TestSheet.set(5, 0, "10"); // F0: depth 0
        TestSheet.set(6, 0, "20"); // G0: depth 0
        TestSheet.set(7, 0, "=SUM(F0:G0)"); // H0: depth 1
        TestSheet.set(8, 0, "=H0+1"); // I0: depth 2

        depths = TestSheet.depth();
        assertEquals(0, depths[5][0]); // F0
        assertEquals(0, depths[6][0]); // G0
        assertEquals(1, depths[7][0]); // H0
        assertEquals(2, depths[8][0]); // I0

        //depth With IF
        // - Check depth calculation with condition cells
        TestSheet.set(5, 0, "10"); // F0: depth 0
        TestSheet.set(6, 0, "20"); // G0: depth 0
        TestSheet.set(7, 0, "=IF(F0 > 5, G0, 0)"); // H0: depth 1
        TestSheet.set(8, 0, "=IF(H0 > 10, H0+1, F0)"); // I0: depth 2

        depths = TestSheet.depth();
        assertEquals(0, depths[5][0]); // F0
        assertEquals(0, depths[6][0]); // G0
        assertEquals(1, depths[7][0]); // H0
        assertEquals(2, depths[8][0]); // I0

    }

    /**
     * Tests for computeForm() method - tests taken form the first stage
     * - Validates arithmetic operations, parentheses, and complex calculations.
     * - Tests decimal handling, negative numbers, and invalid expressions.
     */
    @Test
    void computeForm_Test() {
        // Basic operations
        TestSheet.set(0, 0, "1");  // A0 = 1
        TestSheet.set(1, 0, "2");  // B0 = 2
        TestSheet.set(3, 0, "2");  // D0 = 2

        assertEquals(1.0, TestSheet.computeForm("=1", 0, 0));
        assertEquals(3.0, TestSheet.computeForm("=1+2", 0, 0));
        assertEquals(-1.0, TestSheet.computeForm("=1-2", 0, 0));
        assertEquals(6.0, TestSheet.computeForm("=2*3", 0, 0));
        assertEquals(2.0, TestSheet.computeForm("=4/2", 0, 0));

        // Complex expressions
        assertEquals(7.0, TestSheet.computeForm("=1+2*3", 0, 0));
        assertEquals(9.0, TestSheet.computeForm("=(1+2)*3", 0, 0));
        assertEquals(14.0, TestSheet.computeForm("=2*3+2*4", 0, 0));
        assertEquals(0.0, TestSheet.computeForm("=1-2+3-2", 0, 0));
        assertEquals(16.0, TestSheet.computeForm("=11+10-3*2+1", 0, 0));
        assertEquals(8.0, TestSheet.computeForm("=(1+2)*((3))-1", 0, 0));
        assertEquals(7.0, TestSheet.computeForm("=1+2*3", 0, 0));

        // Nested parentheses
        assertEquals(21.0, TestSheet.computeForm("=(1+2)*(3+4)", 0, 0));
        assertEquals(7.0, TestSheet.computeForm("=((1+2)*(3+4))/3", 0, 0));
        assertEquals(15.0, TestSheet.computeForm("=(((1+2)*3)+6)", 0, 0));

        // Decimal numbers
        assertEquals(3.5, TestSheet.computeForm("=1.5+2", 0, 0));
        assertEquals(0.09999999999999987, TestSheet.computeForm("=1.2-1.1", 0, 0));
        assertEquals(3.75, TestSheet.computeForm("=1.5*2.5", 0, 0));
        assertEquals(2.5, TestSheet.computeForm("=5/2", 0, 0));

        // Expressions with spaces
        assertEquals(3.0, TestSheet.computeForm("= 1 + 2", 0, 0));
        assertEquals(6.0, TestSheet.computeForm("= ( 1 + 2 ) * 2", 0, 0));

        // Division edge cases and negative numbers
        assertEquals(Double.POSITIVE_INFINITY, TestSheet.computeForm("=1/0", 0, 0));
        assertEquals(-50.0, TestSheet.computeForm("=-100/2", 0, 0));
        assertEquals(Double.NEGATIVE_INFINITY, TestSheet.computeForm("=-1/0", 0, 0));
        assertEquals(-466.0, TestSheet.computeForm("=-500+20*2-6", 0, 0));
        assertEquals(653.0, TestSheet.computeForm("=+669-50/5-6", 0, 0));

        // Complex calculations
        assertEquals(101.0, TestSheet.computeForm("=(404+404)/8", 0, 0));
        assertEquals(5.0, TestSheet.computeForm("=((1+2)*2)-1", 0, 0));
        assertEquals(4.0, TestSheet.computeForm("=((1+1)+(1+1))", 0, 0));
        assertEquals(1.0, TestSheet.computeForm("=((((1+2)*2)-1)/5)", 0, 0));
        assertEquals(5.0, TestSheet.computeForm("=(3+9)/(6/2)+1", 0, 0));
        assertEquals(40015997999.8, TestSheet.computeForm("=((((0000100000+200000000)*9999)-10)/50)", 0, 0));
        assertEquals(2.0, TestSheet.computeForm("=4+4+(4*4/(4*4/(4+4)-(4*4)/4-(4*4)+4*4)/4-4)", 0, 0));

        // Invalid expressions
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm("=(test)", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm("=(10", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm("=(((2))", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm("", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm(null, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.computeForm("2+2", 0, 0));
    }

    // , on mainOpTests:
    /**
     * Tests for indexOfMainOp() method - tests taken form the first stage
     * - Checks correct index of the main operator considering operator precedence.
     * - Validates handling of parentheses.
     */
    @Test
    void mainOp_Tests() {
        // Testing the index of the main operator
        assertEquals(1, TestSheet.indexOfMainOp("1+2"));
        assertEquals(1, TestSheet.indexOfMainOp("1-2"));
        assertEquals(1, TestSheet.indexOfMainOp("1*2"));
        assertEquals(1, TestSheet.indexOfMainOp("1/2"));

        // Testing operator precedence
        assertEquals(1, TestSheet.indexOfMainOp("1+2*3")); // Should find the +
        assertEquals(3, TestSheet.indexOfMainOp("1*2+3")); // Should find the +
        assertEquals(3, TestSheet.indexOfMainOp("1*2*3")); // Should find last *

        // Testing with parentheses
        assertEquals(5, TestSheet.indexOfMainOp("(1+2)+3")); // Should find the + outside parentheses
        assertEquals(-1, TestSheet.indexOfMainOp("(1+2)")); // No main operator outside parentheses
        assertEquals(7, TestSheet.indexOfMainOp("(1+2*3)+(4-5)")); // Should find the + between parentheses
    }

    /**
     * Tests for BracketEndInd() method - tests taken form the first stage, on mainOpTests:
     * - Checks correct detection of matching closing parentheses.
     * - Ensures exceptions are thrown for unbalanced parentheses.
     */
    @Test
    void bracketEndInd_Test() {
        assertEquals(3, TestSheet.BracketEndInd("1+2)"));
        assertEquals(5, TestSheet.BracketEndInd("(1+2))"));
        assertEquals(7, TestSheet.BracketEndInd("((1+2)))"));

        // Unbalanced parentheses should throw exceptions
        assertThrows(IllegalArgumentException.class, () -> TestSheet.BracketEndInd("(1+2"));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.BracketEndInd("1+2"));
    }

    /**
     * Tests for built-in spreadsheet functions (SUM, AVERAGE, MAX, MIN) - Ex4 special!
     * - Validates function calculation with ranges and single cells.
     * - Checks handling of invalid ranges and circular references.
     */
    @Test
    void Functions_Test() {
        // testing standard function
        // (We have empty cells on purpose, we have defined that we do not take these empty cells into account):
        TestSheet.set(0, 0, "25");
        TestSheet.set(0, 1, "=A0*3");
        TestSheet.set(1, 0, "=40+5.5"); // ==45.5
        TestSheet.set(1, 1, "=55.5*4/2-55.5"); // ==55.5

        TestSheet.set(4, 1, "=sum(A0:C1)");
        assertEquals("201.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=AVERAGE(A0:C1)");
        assertEquals("50.25", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=maX(A0:C1)");
        assertEquals("75.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=Min(A0:C1)");
        assertEquals("25.0", TestSheet.value(4, 1));

        // Single Cell range test:
        TestSheet.set(0, 0, "-404");

        TestSheet.set(4, 1, "=sum(A0:A0)");
        assertEquals("-404.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=AVERAGE(A0:A0)");
        assertEquals("-404.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=maX(A0:A0)");
        assertEquals("-404.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=Min(A0:A0)");
        assertEquals("-404.0", TestSheet.value(4, 1));

        // Adding a test cell Within the range, causes an error:
        TestSheet.set(0, 0, "Text!!");

        TestSheet.set(4, 1, "=sum(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=AVERAGE(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=maX(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=Min(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(4, 1));

        // Checking for circular errors - if we define a cell with a range that includes the cell itself - we will get a circular error (like IF type Cell, we wil print function error, and not circularity error MSG)
        TestSheet.set(0, 0, "=sum(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(0, 0));
        TestSheet.set(0, 0, "=average(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(0, 0));
        TestSheet.set(1, 1, "=max(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(1, 1));
        TestSheet.set(0, 0, "=min(A0:C1)");
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(0, 0));

        // Extreme Values and Overflow test:
        TestSheet.set(0, 0, String.valueOf(Double.MAX_VALUE));
        TestSheet.set(0, 1, String.valueOf(Double.MAX_VALUE));
        TestSheet.set(4, 1, "=sum(A0:A1)");
        assertEquals("Infinity", TestSheet.value(4, 1));

        //  Empty value Range test
        TestSheet.set(0, 0, "");
        TestSheet.set(0, 1, "");

        TestSheet.set(4, 1, "=sum(A0:A1)");
        assertEquals("0.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=average(A0:A1)");
        assertEquals("0.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=min(A0:A1)");
        assertEquals("0.0", TestSheet.value(4, 1));
        TestSheet.set(4, 1, "=max(A0:A1)");
        assertEquals("0.0", TestSheet.value(4, 1));

        // invalid Range test:
        TestSheet.set(2, 0, "=SUM(A0:A1000)"); // C0
        TestSheet.set(2, 1, "=AVERAGE(A!0:A-100)"); // C1
        TestSheet.set(2, 2, "=MAX(A0::A1000)"); // C2
        TestSheet.set(2, 3, "=MIN(A0,A1000)"); // C3

        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(2, 0));
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(2, 1));
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(2, 2));
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(2, 3));

        // function circular reference error test:
        TestSheet.set(5, 0, "=SUM(G0:H1)"); // F0 depends on G0, G1, H0, H1
        TestSheet.set(6, 0, "=F0*2"); // G0 depends on F0
        TestSheet.set(7, 1, "=G0+3"); // H1 depends on G0

        // the function cell should return ERR_FUNC, the formula cycle should return ERR_CYCLE
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(5, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(6, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(7, 1));

        // overlapping ranges causing circular reference:
        TestSheet.set(5, 0, "=SUM(G0:H0)"); // F0
        TestSheet.set(6, 0, "=SUM(F0:H0)"); // G0
        // Both should detect circular reference = ERR_FUNC
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(5, 0));
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(6, 0));

        // typo in function name - cell type is set as formula:
        TestSheet.set(4, 1, "=summm(A0:C1)");
        assertEquals(Ex2Utils.ERR_FORM, TestSheet.value(4, 1));

        // testing range that is out of the table, let's create a smaller table (additional range tests are on the 'Range2DTest' file):
        Ex2Sheet SmallTable = new Ex2Sheet(10,10);
        SmallTable.set(0,0,"1");
        SmallTable.set(2,0,"=max(A0:A11)"); // index out of table
        SmallTable.set(2,1,"=max(R0:Y08)"); // index out of table
        assertEquals(Ex2Utils.ERR_FUCN_str, SmallTable.value(2, 0));
        assertEquals(Ex2Utils.ERR_FUCN_str, SmallTable.value(2, 1));

//        todo:
//        TestSheet.set(6, 1, "=MAX(F0:F2) - MIN(F0:F2)");
//        assertEquals("20.0", TestSheet.value(6, 1));

    }

    /**
     * Tests for IF conditions in formulas - Ex4 special!
     * - Checks basic conditional logic with comparison operators.
     * - Validates nested conditions and references.
     * - Tests handling of invalid IF statements.
     */
    @Test
    void Condition_Test() {
        // Basic IF conditions - valid results (with cell references):
        TestSheet.set(20, 0, "=if(1<2,1,2)"); //U0 = 1.0
        TestSheet.set(20, 1, "=if(U0>3, big,small)"); // U1 = small
        TestSheet.set(20, 5, "=if(1000 <= 2000 ,10,2)"); //U5 = 10.0
        TestSheet.set(20, 6, "=if(101+1==102,20,0)"); //U6 = 20.0
        TestSheet.set(20, 7, "=if(2*3 > 5, 100, 200)"); // U7 = 100.0
        TestSheet.set(20, 2, "=if(U6/U5 == U0, =U0+1, =U7*3)"); // U2 = 300.0

        assertEquals("1.0", TestSheet.value(20, 0));
        assertEquals("small", TestSheet.value(20, 1));
        assertEquals("10.0", TestSheet.value(20, 5));
        assertEquals("20.0", TestSheet.value(20, 6));
        assertEquals("100.0", TestSheet.value(20, 7));
        assertEquals("300.0", TestSheet.value(20, 2));

        // More tests:
        TestSheet.set(1, 0, "=if(1+1==2, 100, 200)"); // B0 = 100.0
        TestSheet.set(1, 1, "=if(2*3>5, 300, 400)"); // B1 = 300.0
        TestSheet.set(1, 2, "=if((2+2)*2<=8, 500, 600)"); // B2 = 500.0

        assertEquals("100.0", TestSheet.value(1, 0));
        assertEquals("300.0", TestSheet.value(1, 1));
        assertEquals("500.0", TestSheet.value(1, 2));

        // More IF cells with references
        TestSheet.set(20, 7, "=if(U5<U6,30,-101)"); //U7 = 30
        TestSheet.set(20, 8, "=if(U6 != U7,3,777)"); //U8 = 3
        assertEquals("30.0", TestSheet.value(20, 7));
        assertEquals("3.0", TestSheet.value(20, 8));


        // Invalid IF statements should return error
        TestSheet.set(20, 0, "=if(1,2,3"); //U0
        TestSheet.set(20, 1, "=if(u6>1, 1 )"); // U1
        TestSheet.set(20, 2, "=if(u566<u6, 1, 0)"); // U2
        TestSheet.set(20, 3, "=if(test<2, 1, 0)"); // U3
        TestSheet.set(20, 4, "=if(u5<u6,  ==Z999, 12)"); // U4
        TestSheet.set(20, 5, "=if(u0>3,2,4)"); //U5
        TestSheet.set(20, 7, "=if(, 5, 10)"); // U7
        TestSheet.set(20, 8, "=if(A1>0, B1)"); // U8
        TestSheet.set(20, 9, "=if(A1,, 10)"); // U9

        // Validate error messages for invalid IF statements:
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 0));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 1));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 2));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 3));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 4));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 5));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 7));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20, 8));
        assertEquals(Ex2Utils.ERR_IF_str, TestSheet.value(20,9));
    }

    /**
     * Tests for saving and loading spreadsheets.
     * - Verifies data persistence by saving and loading spreadsheet content.
     * - Checks consistency of text, numbers, formulas, and functions.
     * - Ensures invalid content is not loaded.
     */
    @Test
    void Save_Load_Test() throws IOException {
        String test_file = "Save_LoadTest.txt";

        // Set up test data with various types of cells
        TestSheet.set(0, 0, "100"); // A0 - Number
        TestSheet.set(0, 1, "Hello"); // A1 - Text
        TestSheet.set(0, 10, "=SUM(A11:C11)"); // A10 - Sum function with range
        TestSheet.set(0, 11, "100"); // A11 - Number
        TestSheet.set(1, 0, "=A0*2"); // B0 - Formula
        TestSheet.set(1, 11, "=IF(1==1,Hi,not)"); // B11 - Condition
        TestSheet.set(2, 2, "800"); // C2 - Number
        TestSheet.set(2, 3, "=MIN(A0:B0)"); // C3 - MIN function
        TestSheet.set(2, 11, "22"); // C11 - Number
        TestSheet.set(3, 2, "=((C2/4)+5)*3"); // D2 - Complex formula
        TestSheet.set(3, 3, "=IF(A0 <= B0, 404, 0)"); // D3 - Condition
        TestSheet.set(5, 11, "this is a demo!"); // F11 - Text

        // Save the test spreadsheet
        TestSheet.save(test_file);

        // testing if the cells are reset when loading a file
        TestSheet.set(2, 0, "should not be there");


        // Load the file we just saved:
        TestSheet.load(test_file);

        // Verify the loaded data is consistent with saved data
        assertEquals("100.0", TestSheet.value(0, 0)); // A0 - Number
        assertEquals("Hello", TestSheet.value(0, 1)); // A1 - Text
        assertEquals("100.0", TestSheet.value(0, 11)); // A11 - Number
        assertEquals("Hi", TestSheet.value(1, 11)); // B11 - Condition result
        assertEquals("800.0", TestSheet.value(2, 2)); // C2 - Number
        assertEquals("100.0", TestSheet.value(2, 3)); // C3 - MIN function result
        assertEquals("22.0", TestSheet.value(2, 11)); // C11 - Number
        assertEquals("615.0", TestSheet.value(3, 2)); // D2 - Complex formula result
        assertEquals("404.0", TestSheet.value(3, 3)); // D3 - Condition result
        assertEquals("this is a demo!", TestSheet.value(5, 11)); // F11 - Text
        assertEquals(Ex2Utils.ERR_FUCN_str, TestSheet.value(0, 10)); // A10 - Sum(A11:C11) result
    }
}