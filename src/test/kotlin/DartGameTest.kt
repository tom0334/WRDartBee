import DartGame.Companion.START_SCORE
import org.junit.Assert
import org.junit.Before
import org.junit.Test


//Unuesed if the stresstest is commented out
import java.util.Random

/**
 *Created by Tom on 27-9-2018.
 *
 * Contains some basic unit tests for the DartGame class.
 *
 * It also has a stresstest that is normally commented out.
 */

class DartGameTests {

    //DO NOT RUN THIS NORMALLY, it is a performance test!
/*    @Test
    fun stressTest(){
        val rand = Random()
        for (i in 0..100000){
            freshGame.processNewScore(rand.nextInt(181))
        }
    }*/


    lateinit var freshGame: DartGame
    lateinit var readBackGame: DartGame

    private val TEST_READ_GAME_SCORE = 1999740

    @Before
    fun init(){
        this.freshGame = DartGame()

        //10-50-100-UNDO-200
        //so the correct score left should be 1999 740
        this.readBackGame = DartGame("DartLogs/testLog.txt")
        Assert.assertEquals(TEST_READ_GAME_SCORE, readBackGame.scoreLeft)
    }


    /**
     * Checks if the normal init function works as expected
     */
    @Test fun testInit(){
        Assert.assertEquals(START_SCORE, freshGame.scoreLeft)
    }

    /**
     * Checks if the score is subtracted properly
     */
    @Test fun testScore(){
        freshGame.processNewScore(20,3)
        Assert.assertEquals(START_SCORE - 20, freshGame.scoreLeft)
    }


    /**
     * Checks if a single undo works, the score should be reset.
     */
    @Test fun singleUndo(){
        freshGame.processNewScore(10,3)
        freshGame.undoLast()

        Assert.assertEquals(START_SCORE, freshGame.scoreLeft)

    }

    /**
     * Checks if the freshGame does not produce any errors when there is nothing to undo
     */
    @Test fun startWithUndo(){
        freshGame.undoLast()
        Assert.assertEquals(START_SCORE, freshGame.scoreLeft)
    }

    /**
     * Checks the last score, if the undo is not handled properly this might be negative!
     */
    @Test fun checkReadBackLastScore() = Assert.assertEquals(200,readBackGame.lastScore)

    /**
     * Checks if the read back game can also handle scoring a new score.
     */
    @Test fun testReadStatesAndUpdate(){
        readBackGame.processNewScore(10,3)
        Assert.assertEquals(TEST_READ_GAME_SCORE - 10, readBackGame.scoreLeft)
    }

    /**
     * Tests the average. The readbackGame is used for ease of use.
     * Note that the avg is a float, not a double
     */
    @Test fun checkAvg(){
        //the readback game has an average of 10 + 50 + 200 = 260 /3
        Assert.assertEquals(260.0f/3.0f, readBackGame.avg)
    }

    /**
     * Checks that a undo does NOT delete the pauses
     */
    @Test fun undoPreservesTime(){
        //create new game for timing
        val game = DartGame()
        //values don't matter here
        game.processNewScore(10,3)
        game.addPause(1000)
        game.undoLast()
        Assert.assertEquals(1000,game.pauseTime)
    }

    /**
     * tests the last score value, for a weird 5 dart turn.
     */
    @Test fun testLastScore(){
        freshGame.processNewScore(20,5)
        Assert.assertEquals(20,freshGame.lastScore)
    }


}