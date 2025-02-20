import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class Ex2SheetTest {

    // let's create a spreadsheet for our tests - according to the maximum dimensions we require: A-Z [0-25] on the X-axis, and 100 [0-99] on the Y-axis:
    Ex2Sheet TestSheet = new Ex2Sheet(26,100);

    // let's test the constructors:
    @Test
    void constructors() {
        // invalid spreadsheet dimensions
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(5, -1));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(27, 5));
        assertThrows(IllegalArgumentException.class, () -> new Ex2Sheet(5, 101));

        // default spreadsheet constructor:
        Ex2Sheet DefaultSheet = new Ex2Sheet();
        //Ex2Utils.WIDTH = 9 Ex2Utils.HEIGHT = 17:
        assertEquals(DefaultSheet.width(), 9);
        assertEquals(DefaultSheet.height(), 17);
    }

    // adding a Cells to TestSheet spreadsheet, and test the following Methods:
    // set, value, eval(), eval(int x, int y), computeForm(String form, int x, int y),
    // indexOfMainOp(String form), BracketEndInd(String form), getOperatorPriority(char operator)
    // more complex test on computeForm will be in the following tests method (taken form first stage tests...)
    @Test
    void set() {
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

    @Test
    void getByIndexTest() {
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

    @Test
    void getByCordTest() {
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

    // default spreadsheet constructor was tested already checked before...So there we check these functions again:
    @Test
    void width() {
        assertEquals(TestSheet.width(), 26);
    }
    @Test
    void height() {
        assertEquals(TestSheet.height(), 100);
    }

    @Test
    void isIn() {
        assertTrue(TestSheet.isIn(0, 0));
        assertTrue(TestSheet.isIn(25, 98));

        assertFalse(TestSheet.isIn(-1, 0));
        assertFalse(TestSheet.isIn(0, -1));
        assertFalse(TestSheet.isIn(26, 0));
        assertFalse(TestSheet.isIn(0, 101));
    }

    @Test
    void depth() {
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


        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 0)); //E0
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 1)); //E1
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 2)); //E2


        assertEquals(-1, depths[4][3]); // E3
        assertEquals(Ex2Utils.ERR_CYCLE, TestSheet.value(4, 3));
    }

    // tests taken form the first stage, testing 'computeForm' method:
    @Test
    void computeFormTest() throws ConditionCalculationException, FuncCalculationException {
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

    // tests taken form the first stage, on mainOpTests:
    @Test
    void mainOpTests() {
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

    // tests taken form the first stage, on bracketEndIndTest:
    @Test
    void bracketEndIndTest() {
        assertEquals(3, TestSheet.BracketEndInd("1+2)"));
        assertEquals(5, TestSheet.BracketEndInd("(1+2))"));
        assertEquals(7, TestSheet.BracketEndInd("((1+2)))"));

        assertThrows(IllegalArgumentException.class, () -> TestSheet.BracketEndInd("(1+2"));
        assertThrows(IllegalArgumentException.class, () -> TestSheet.BracketEndInd("1+2"));
    }

//    @Test
//    void functiontest() {
//        TestSheet.set(0, 0, "98.299");
//        TestSheet.set(0, 1, "1.701");
//        TestSheet.set(1, 0, "45.5");
//        TestSheet.set(1, 1, "55.5");
//
//        System.out.println(TestSheet.computeFun("=sum(A0:B1)", 1,1));
//
//    }

    @Test
    void IFex4Test() {
        // Basic IF conditions
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

        // IF with references
        TestSheet.set(20, 7, "=if(U5<U6,30,-101)"); //U7 = 30
        TestSheet.set(20, 8, "=if(U6 != U7,3,777)"); //U8 = 3
        assertEquals("30.0", TestSheet.value(20, 7));
        assertEquals("3.0", TestSheet.value(20, 8));

        //IF ERR
        TestSheet.set(20, 0, "=if(1,2,3"); //U0
        TestSheet.set(20, 1, "=if(u6>1, 1 )"); // U1
        TestSheet.set(20, 2, "=if(u566<u6, 1, 0)"); // U2
        TestSheet.set(20, 3, "=if(test<2, 1, 0)"); // U3
        TestSheet.set(20, 4, "=if(u5<u6,  ==Z999, 12)"); // U4
        TestSheet.set(20, 5, "=if(u0>3,2,4)"); //U5
        TestSheet.set(20, 7, "=if(, 5, 10)"); // U7
        TestSheet.set(20, 8, "=if(A1>0, B1)"); // U8
        TestSheet.set(20, 9, "=if(A1,, 10)"); // U9

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

    // let's test the I/O methods:
    @Test
    void Save_LoadTest() throws IOException {
        String test_file = "Save_LoadTest.txt";
        // Set up some test data:
        TestSheet.set(0, 0, "100"); // Number
        TestSheet.set(0, 1, "Hello"); // Text
        TestSheet.set(1, 0, "=A0*2"); // Formula

        // Save the test spreadsheet
        TestSheet.save(test_file);

        // testing if the cells are reset when loading a file
        TestSheet.set(2, 0, "should not be there");


        // Load the file we just saved:
        TestSheet.load(test_file);

        // Verify the loaded data
        assertEquals("100.0", TestSheet.value(0, 0));
        assertEquals("Hello", TestSheet.value(0, 1));
        assertEquals("200.0", TestSheet.value(1, 0));
        assertEquals(Ex2Utils.EMPTY_CELL, TestSheet.value(2, 0));
    }
}