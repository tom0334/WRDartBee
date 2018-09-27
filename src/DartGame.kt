/**
 * Created by Tom on 25-9-2018.
 *
 *
 *
 */

val START_SCORE =  2000000
class DartGame{

    var scoreLeft = START_SCORE

    fun processNewScore(score: Int){
        scoreLeft -= score

    }


}