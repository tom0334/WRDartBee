
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




}