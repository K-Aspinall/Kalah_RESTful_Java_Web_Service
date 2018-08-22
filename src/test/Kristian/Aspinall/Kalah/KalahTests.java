package Kristian.Aspinall.Kalah;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KalahTests {

    /**
     * Tests that a pit that cannot exist is handled correctly
     *  (checkValidPit returns false)
     */
    @Test
    public void testNonExistingPit(){
        Kalah testGame = new Kalah(0);

        //Negative pit
        assertFalse(testGame.checkValidPit(-1));
        //Non-existent pit
        assertFalse(testGame.checkValidPit(15));
    }

    /**
     * Tests that an empty pit cannot be selected
     *  (checkValidPit returns false)
     */
    @Test
    public void testEmptyPit(){
        Kalah testGame = new Kalah(0);
        //Empty pit number 2 (followed by pit 9 for next turn)
        //so that the empty pit 9 can be tested
        testGame.takeTurn(2);
        testGame.takeTurn(9);

        //Empty pit 9 (Index is 8)
        assertFalse(testGame.checkValidPit(8));
    }

    /**
     * Tests that a Kalah cannot be selected as a pit
     *  (checkValidPit returns false)
     */
    @Test
    public void testSelectedPitIsKalah(){
        Kalah testGame = new Kalah(0);

        //Player 1 Kalah
        assertFalse(testGame.checkValidPit(6));
        //Player 2 Kalah
        assertFalse(testGame.checkValidPit(13));
    }


    /**
     * Tests that a player can only select own pits
     *  (checkValidPit returns false)
     */
    @Test
    public void testPitBelongsToPlayer(){
        Kalah testGame = new Kalah(0);

        //Player 1 does not own this pit
        assertFalse(testGame.checkValidPit(9));
        //Player 1 does own this pit
        assertTrue(testGame.checkValidPit(2));
    }

    /**
     * Tests that a player can only select own pits
     *  (checkValidPit returns false)
     */
    @Test
    public void testWinnerIsSelectedByContentsOfKalah(){
        Kalah testGame1 = new Kalah(0);
        Kalah testGame2 = new Kalah(1);

        testGame1.takeTurn(1);
        //Player 1 should be winner of this scenario
        assertEquals(1, testGame1.calculateWinner());
        //This game should be declared a draw (0)
        assertEquals(0, testGame2.calculateWinner());
    }



}
