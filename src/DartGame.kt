/**
 * Created by Tom on 25-9-2018.
 *
 *
 *
 */

val START_SCORE =  2000000
class DartGame{


    val states: MutableList<State> = mutableListOf()

    init {
        val startState = State(
                START_SCORE,
                0,
                System.currentTimeMillis()
        )
        this.states.add( startState)
    }

    val scoreLeft: Int
        get() = states.last().scoreLeft


    //adds a new score
    fun processNewScore(score: Int){
        val newScore = states.last().scoreLeft - score
        val newTurns = states.last().turns + 1
        val newState = State(
                newScore,
                newTurns,
                System.currentTimeMillis()
        )
        logStateNow(newState)
    }


    fun undoLast() {
        //DO NOT REMOVE THE FIRST STATE!
        if (states.lastIndex > 0){
            states.removeAt(states.lastIndex)
        }
    }





    private fun logStateNow(newState: State) {
        states.add(newState)
        //todo write to file
    }
}

data class State(val scoreLeft: Int, val turns: Int, val timeStamp:Long)
