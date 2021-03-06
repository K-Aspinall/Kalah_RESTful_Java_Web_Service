package Kristian.Aspinall.Kalah;

/*import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;*/

import java.util.Arrays;
import java.util.HashMap;

/**
 * An simple attempt at representing a game of 6-Stone Kalah
 * to be hosted on a RESTful Web Service.
 * For instructions on how the game is played see
 * https://en.wikipedia.org/wiki/Kalah
 *
 * @author  Kristian Aspinall
 * @version 1.0
 * @since   2018-08-13
 */
public class Kalah {

    // @Id
    // @GeneratedValue(strategy=GenerationType.AUTO)
    private final long id;
    private int[] pits;
    private final String uri;
    private static final String BASE_URL = "http://localhost:8080/games";   //Localhost for testing,
                                                                    // replace with static IP for multiplayer game
    //Index of player 1's Kalah/House (7th Pit on board, anti-clockwise)
    private static final int PLAYER_1_KALAH_INDEX = 6;
    //Index of player 2's Kalah/House (14th Pit on board, anti-clockwise)
    private static final int PLAYER_2_KALAH_INDEX = 13;
    //Index's of all of player 1's pits
    private static final int[] PLAYER_1_PITS = new int[]{0,1,2,3,4,5};
    //Index's of all of player 2's pits
    private static final int[] PLAYER_2_PITS = new int[]{7,8,9,10,11,12};
    //Keeps track of who's turn it is currently
    private boolean player1Turn;

    /**
     * Constructor of Kalah Game, use the atomic long generated by the
     * controller method as it's unique id.
     * @param      id
     * @see KalahController
     */
    public Kalah(long id) {
        this.id = id;
        this.uri = BASE_URL + "/" + this.id;
        //Initialise pits for 6-Stone Kalah
        this.pits = new int[]{6,6,6,6,6,6,0,6,6,6,6,6,6,0};
        this.player1Turn = true;
    }

    /**
     * Returns the id of this Kalah game
     * @return      Kalah id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the uri of this Kalah game
     * @return      Kalah URI
     */
    public String getUri() {
        return uri;
    }
    /**
     * This method takes the pit selection of the user and, after checking it is a valid
     * selection, sows all the seeds from that pit towards the right. If the last seed
     * doesn't land in the current players Kalah/Home, updates the current players turn
     * (It is now the next players turn)
     *
     * @param  pit  The pit number given as given by a user (1-14)
     */
    protected void takeTurn(int pit){
        int pitIndex = pit-1;
        //Tests if this is a valid turn
        if(checkValidPit(pitIndex)){
            //If safe to do so, sow the seed and find where last stone was added
            int lastStoneIndex = sowSeeds(pitIndex);

            //If last seed wasn't in player's own Kalah, next players turn
            if( ! (player1Turn && lastStoneIndex == PLAYER_1_KALAH_INDEX ||
                !player1Turn && lastStoneIndex == PLAYER_2_KALAH_INDEX) ){
                player1Turn = !player1Turn;
            }
        }
    }

    /**
     * Returns an HashMap Key, Value representation of the current
     * status of the pits, so that is can be parsed and displayed
     * nicely in a JSON format.
     * <p>
     * This method uses the pit number a user would be familiar with,
     * and not the true index of the pit.
     * @return      HashMap of the pit number and pits content.
     * @see         DisplayKalah
     */
    public HashMap<Integer, Integer> getStatus(){
        HashMap<Integer, Integer> status = new HashMap<>();
        for (int i = 0; i < pits.length; i ++) {
            //Add to key map with pit number and it's content
            status.put(i+1, pits[i]);
        }
        return status;
    }

    /**
     * Checks that the selected pit exists, is not empty and belongs to
     * the current player. Returns false if any of these checks fail.
     * @param       pitIndex Used to identify the selected pit via its index
     *                       in the pits array.
     * @return      Whether the pit selected is a valid choice and belongs
     *              to the current player.
     */
    public boolean checkValidPit(int pitIndex){
        //Catch array out of bounds
        if(pitIndex >= pits.length || pitIndex < 0 ){
            return false;
        }

        //Check if selected pit is actually a Kalah
        if(pitIndex == PLAYER_1_KALAH_INDEX || pitIndex == PLAYER_2_KALAH_INDEX){
            return false;
        }

        //Check the selected pit is not empty
        if(pits[pitIndex] == 0){
            return false;
        }

        //Check if pit belongs to the current player
        if((player1Turn && Arrays.stream(PLAYER_1_PITS).anyMatch(i -> i == pitIndex)) ||
            (!player1Turn && Arrays.stream(PLAYER_2_PITS).anyMatch(i -> i == pitIndex))){
            return true;
        }
        //Pit does not belong to current player, invalid move
        return false;
    }

    /**
     * This method removes all the seeds from the selected pit and adds
     * them, one at a time, to the adjacent pits anti-clockwise. It then
     * returns the pit the last was added to, which is used to determine if
     * an extra turn is awarded.
     * <p>
     * If the next pit selected is the opposing players Kalah, that is skipped
     * over and the seeds continue to be sowed in the next pits onwards.
     *
     * @param       pitIndex Used to identify the selected pit via its index
     *                       in the pits array.
     * @return      The index of the pit the last seed was added to.
     */
    private int sowSeeds(int pitIndex){
        //Check number of seeds to sow
        int seeds = pits[pitIndex];
        //Remove seeds from pit
        pits[pitIndex] = 0;
        //Sow seeds
        for(int i = 0; i < seeds; i++){
            //Increment pitIndex (Next pit on board)
            pitIndex++;
            //TODO RETHINK THIS LOGIC
            //Check if next pit is a Kalah/House
            if(pitIndex == PLAYER_1_KALAH_INDEX || pitIndex == PLAYER_2_KALAH_INDEX) {
                //If current pit belongs to current player, add seed to kalah
                if (pitIndex == PLAYER_1_KALAH_INDEX && player1Turn) {
                    pits[PLAYER_1_KALAH_INDEX]++;
                } else if (pitIndex == PLAYER_2_KALAH_INDEX && !player1Turn) {
                    pits[PLAYER_2_KALAH_INDEX]++;
                    //Also at end of board, so resets index location
                    //TODO rethink this?
                    // -1 so that becomes 0 when incremented at start of loop
                    pitIndex = -1;
                } else if (pitIndex == PLAYER_2_KALAH_INDEX && player1Turn) {
                    //Also at end of board, so resets index location
                    //TODO rethink this?
                    // -1 so that becomes 0 when incremented at start of loop
                    pitIndex = -1;
                    //Skip over this pit
                    i--;
                } else {
                    //Skip over this pit
                    i--;
                }
            } else {
                //Add Seed
                pits[pitIndex]++;
            }
        }
        //Pit last seed was added to
        return pitIndex;
    }

    /**
     * Checks if the game is complete (One player has no more seeds),
     * and if so, adds all the remaining players seeds to their Kalah.
     * @return      The completion status of the game
     */
    public boolean checkGameOver(){
        int[] player1pits = getPlayersPits(PLAYER_1_PITS, pits);
        int[] player2pits = getPlayersPits(PLAYER_2_PITS, pits);

        //If game is over, tidy up the board
        if(checkAllEmpty(player1pits) ||  checkAllEmpty(player2pits)){
            endGameTidy();
            return true;
        }
        return false;
    }

    /**
     * For each player, adds all their remaining seeds in their pits to
     * their Kalah
     */
    private void endGameTidy(){
        int[] player1pits = getPlayersPits(PLAYER_1_PITS, pits);
        int[] player2pits = getPlayersPits(PLAYER_2_PITS, pits);

        //Add all remaining stones to Kalahs
        pits[PLAYER_1_KALAH_INDEX] += Arrays.stream(player1pits).sum();
        pits[PLAYER_2_KALAH_INDEX] += Arrays.stream(player2pits).sum();
    }

    /**
     * Counts the number of seeds in each player's Kalah and the winner
     * is the player with the most seeds. A draw is declared if both have
     * the same number.
     * @return      The player number of the winner. 0 is returned for a
     *              draw.
     */
    public int calculateWinner(){
        if(pits[PLAYER_1_KALAH_INDEX] > pits[PLAYER_2_KALAH_INDEX]){
            return 1;
        }else if (pits[PLAYER_1_KALAH_INDEX] < pits[PLAYER_2_KALAH_INDEX]){
            return 2;
        }
        return 0;
    }

    /**
     * Fetches the subset of pits which belong to a given player.
     * @param       playerIndexs An array containg the indexs of each
     *              one of the players pits.
     * @param       pits The current pits of the game.
     * @return      An array where each value contained is the contents
     *              of a players pits.
     */
    private int[] getPlayersPits(int[] playerIndexs, int[] pits){
        int[] playersPits = new int[6];
        int counter = 0;
        for (int i : playerIndexs){
            playersPits[counter] = pits[i];
            counter++;
        }
        return playersPits;
    }

    /**
     * Checks if every pit provided contains no seeds. If any contain a seed
     * returns false. This is determined by simply added all the contents together.
     *
     * @param       pits The contents of a selection of pits.
     * @return      Whether the provided pits are empty.
     */
    private boolean checkAllEmpty(int[] pits){
        //If the sum of the array is 0, we know every pit is empty
        return Arrays.stream(pits).sum() == 0;
    }

    /**
     * The DisplayKalah class provides a simplifed representation
     * of the Kalah Game, so that it can be easy parsed into a JSON
     * object for returning as a response.
     *
     * @author  Kristian Aspinall
     * @version 1.0
     * @since   2018-08-13
     */
    public static class DisplayKalah{
        private final long id;
        private HashMap<Integer, Integer> status;
        private final String uri;

        public DisplayKalah(Kalah kalah){
            this.id = kalah.getId();
            this.uri = kalah.getUri();
            this.status = kalah.getStatus();
        }

        /**
         * Returns the id of this Kalah game
         * @return      Kalah id
         */
        public long getId() {
            return id;
        }

        /**
         * Returns the URI of this Kalah game
         * @return      Kalah URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the status of this Kalah game
         * @return      Key,Value representation of the current
         *              pit status of the game
         */
        public HashMap<Integer, Integer> getStatus() {
            return status;
        }

    }

}
