package Kristian.Aspinall.Kalah;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


/**
 * The controller class for the Kalah game, so
 * that it can be interacted with in as a RESTful
 * API Service.
 *
 * @author  Kristian Aspinall
 * @version 1.0
 * @since   2018-08-13
 * @see     Kalah
 */
@RestController
public class KalahController{

    /*
    This would be use to allow persistence of the games if require on a
     larger scale than for the simple coding exercise.

    @Autowired
    private KalahRepository repository;
    */

    /*
    Simple in-memory solution for persistence
     */
    private HashMap<Long, Kalah> repository = new HashMap<>();
    private AtomicLong counter = new AtomicLong();

    /**
     * Creates a new Kalah game using a Atomic counter and stores
     * it in the in-memory repository.
     *
     * @return      The newly created Kalah game (id and URI)
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    public Kalah create() {

        Kalah kalah = new Kalah(counter.get());
        repository.put(counter.get(), kalah);
        counter.getAndIncrement();
        return kalah;
    }

    /**
     * Perfoms a players turn on a game of Kalah using the game id to identify.
     * The selected pit has it's contents removed and added to the adjacent pits
     * (Anti-Clockwise). Returns error messages in the cases that either the game
     * does not exist in memory or that the selected pit is not a valid choice.
     * Otherwise the status of the game after the turn is taken is returned
     * @param gameId    The unique id of the game
     * @param pitId     The selected pit for the turn.
     * @return          The status of the game after the turn is made.
     */
    @PutMapping("/games/{gameId}/pits/{pitId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Kalah.DisplayKalah> takeTurn(@PathVariable(value="gameId") long gameId,
                                                       @PathVariable(value="pitId") int pitId ){
        /*
        This code would be utilised if a full database solution was utilised, instead of in-memory option

        Optional<Kalah> optionalKalah = repository.findById(gameId);


        if(optionalKalah.isPresent()){
            Kalah kalah = optionalKalah.get();
            kalah.takeTurn(pitId);

            return (kalah.showStatus());
        }
        */

        //Attempt to find game
        Kalah kalah;
        try {
            kalah = repository.get(gameId);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Check if pit is a valid selection
        if (kalah.checkValidPit(pitId-1)) {
            //If so, take the turn
            kalah.takeTurn(pitId);
        }else{
            //Inform user that is not a valid pit selection
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //Check if game is over and if so display a winning message
        if (kalah.checkGameOver()) {
            int winningPlayerNumber = kalah.calculateWinner();

            //TODO Implement a winning game message
            return new ResponseEntity<>(new Kalah.DisplayKalah(kalah), HttpStatus.OK);
        }

        return new ResponseEntity<>(new Kalah.DisplayKalah(kalah), HttpStatus.OK);

    }
}
