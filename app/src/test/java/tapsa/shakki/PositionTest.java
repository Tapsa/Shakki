package tapsa.shakki;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tapsa on 13.11.2015.
 */
public class PositionTest {
    Position position;

    @Before
    public void setUp() throws Exception {
        position = new Position();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testChangeTurn() throws Exception {
        position.changeTurn();
        assertEquals(Owner.BLACK, position.tellTurn());
    }
}
