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
    void ValidRange_Test() {
        Range2D range_One = new Range2D("A1:B2");
        // Range should be valid
        assertTrue(range_One.isValidRange());
        // Start X should be 1
        assertEquals(0, range_One.getStartX());
        // End X should be 2
        assertEquals(1, range_One.getEndX());
        // Start Y should be 1
        assertEquals(1, range_One.getStartY());
        // End Y should be 2
        assertEquals(2, range_One.getEndY());
        // Point should be inside range_One
        assertTrue(range_One.insideRange(1, 1));
        // Point should be outside range_One
        assertFalse(range_One.insideRange(3, 3));

        Range2D rangeTWO = new Range2D("Y1:Z88");
        // Range should be valid
        assertTrue(rangeTWO.isValidRange());
        // Start X should be 1
        assertEquals(24, rangeTWO.getStartX());
        // End X should be 2
        assertEquals(25, rangeTWO.getEndX());
        // Start Y should be 1
        assertEquals(1, rangeTWO.getStartY());
        // End Y should be 2
        assertEquals(88, rangeTWO.getEndY());
        // Point should be inside rangeTWO
        assertTrue(rangeTWO.insideRange(24, 40));
        // Point should be outside rangeTWO
        assertFalse(rangeTWO.insideRange(0, 1));
    }

    /**
     * Test invalid range construction with an empty string.
     */
    @Test
    void EmptyRange_Test() {
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
    void NullRange_Test() {
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
    void StartAfterEnd_Test() {
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
    void InvalidFormat_Test() {
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
