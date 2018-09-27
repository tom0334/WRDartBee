
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/**
 * Created by Tom on 27-9-2018.
 */
class DartGameTests {

    lateinit var game: DartGame

    @Before
    fun init(){
        this.game = DartGame()
    }

    @Test
    fun testInit(){
        Assert.assertEquals(START_SCORE, game.scoreLeft)
    }

    @Test
    fun testScore(){
        game.processNewScore(20)
        Assert.assertEquals(START_SCORE - 20, game.scoreLeft)
    }

    @Test
    fun singleUndo(){
        game.processNewScore(10)
        game.undoLast()

        Assert.assertEquals(START_SCORE, game.scoreLeft)

    }

    @Test
    fun startWithUndo(){
        game.undoLast()
        Assert.assertEquals(START_SCORE, game.scoreLeft)
    }

/*    @Test
    fun stressTest(){
        val rand = Random()
        for (i in 0..100000){
            game.processNewScore(rand.nextInt(181))
        }

    }*/

    @Test
    fun testReadStates(){
        val game = DartGame("DartLogs/testLog.txt")
        Assert.assertEquals(1999800, game.scoreLeft)

    }




}