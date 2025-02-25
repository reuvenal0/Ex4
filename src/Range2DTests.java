import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Range2D.
 * This class tests the construction, validation, and methods of Range2D.
 */
public class Range2DTests {

    /**
     * Test valid range creation and method checks.
     */
    @Test
    public void testValidRange() {
        Range2D range = new Range2D("A1:B2");
        // Range should be valid
        assertTrue(range.isValidRange());
        // Start X should be 1
        assertEquals(0, range.getStartX());
        // End X should be 2
        assertEquals(1, range.getEndX());
        // Start Y should be 1
        assertEquals(1, range.getStartY());
        // End Y should be 2
        assertEquals(2, range.getEndY());
        // Point should be inside range
        assertTrue(range.insideRange(1, 1));
        // Point should be outside range
        assertFalse(range.insideRange(3, 3));
    }

    /**
     * Test invalid range construction with an empty string.
     */
    @Test
    public void testEmptyRange() {
        //Expect an exception for empty range
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D("");
        });
        assertEquals("Invalid range", exception.getMessage());
    }

    /**
     * Test invalid range construction with null input.
     */
    @Test
    public void testNullRange() {
        //Expect an exception for null input
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D(null);
        });
        assertEquals("Invalid range", exception.getMessage());
    }

    /**
     * Test invalid range where start is after end.
     */
    @Test
    public void testStartAfterEnd() {
        //Expect an exception for start being after end
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D("B2:A1");
        });
        assertEquals("Invalid range", exception.getMessage());
    }

    /**
     * Test invalid range with incorrect format.
     */
    @Test
    public void testInvalidFormat() {
        // Expect an exception for incorrect format - "A1B2"
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D("A1B2");
        });
        assertEquals("Invalid range", exception.getMessage());

        // Expect an exception for incorrect format - "A999:C1"
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D("A999:C1");
        });
        assertEquals("Invalid range", exception2.getMessage());

        // Expect an exception for incorrect format - "A1:BZ3"
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            new Range2D("A1:BZ3");
        });
        assertEquals("Invalid range", exception3.getMessage());

    }
}
