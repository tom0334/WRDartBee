import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Tom on 25-9-2018.
 *
 * A class that represents a DartGame. It is built for performance with very large games. It has only O(1) functions.
 *
 * It works using the concept of a state, that is written to a file for backup purposes after every turn.(
 * Scores are input per turn, NOT per dart)
 *
 *It can also read back a file that was written to previously. It will then create a new file with the -m suffix,
 * where it will write its modified version to.
 *
 */
class DartGame{
    //Constants. In  a companion object for 'static' access.
    companion object {
        val START_SCORE =  2000000
        val LOG_FOLDEN_NAME = "DartLogs"
    }


    //Writer that is used for the files after each action
    private var writer: BufferedWriter

    //Main list of states. Keep in mind that this is NOT equal to what is stored on the disk necessarily,
    //as that contains the undone states as wel.
    private var states: MutableList<State>

    /**
     * Primary constructor for creating a new game. It inserts a empty state at the start of the game and writes it to
     * the file.
     */
    constructor(){
        this.states = mutableListOf()
        val startState = State(
                scoreLeft = START_SCORE,
                darts = 0,
                turns = 0,
                timeStamp = System.currentTimeMillis(),
                pauseTime = 0
        )
        val loggerPath = findNewLoggerPath()
        this.writer = createWriter(loggerPath)

        states.add(startState)
        loggButDontAddState(startState)
    }

    /**
     * Secondary constructor for use when restoring from a file.
     *
     * @param logFilePath the path to the file,
     */
    constructor(logFilePath: String){
        this.states = readStates(logFilePath)

        //add a -m to the the file name to denote it is modified
        val withoutExtension = logFilePath.split(".")[0]
        val newName = withoutExtension + "-M"  +".txt"

        this.writer = createWriter(newName)
    }

    /**
     * Below are a few shortcuts to the last state for easier access. These are public.
     */
    val scoreLeft: Int get() = states.last().scoreLeft
    val turns : Int get() = states.last().turns
    val dartsThrown: Int get() = states.last().darts
    val pauseTime: Long get() = states.last().pauseTime

    //This returns the total score thrown over the entire game
    val scoreThrown: Int get() = START_SCORE - scoreLeft

    /**
    * *returns the TURN average. This means that when throwing with more than 3 darts, the average is higher than
    *normally when darting
    */
    val avg: Float get() = (START_SCORE - scoreLeft).toFloat() /  turns.toFloat()

    /**
     * Returns the time spent in total formatted as HH:MM:SS, keeping in mind the pauses that were taken. To add a pause,
     * see the addPause function.
     *
     */
    val timeSpent: String get() {
        val millisSpent = System.currentTimeMillis() - states.first().timeStamp - pauseTime

        var timeLeft = Duration.ofMillis(millisSpent)

        val hours = timeLeft.toHours()
        timeLeft = timeLeft.minusHours(hours)
        val minutes = timeLeft.toMinutes()
        timeLeft = timeLeft.minusMinutes(minutes)
        val seconds = timeLeft.seconds

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Returns the last score that was thrown by the player. Returns 0 if there is no score yet.
     */
    val lastScore: Int get(){
        if (dartsThrown==0){return 0}

        val prevRemPoints = states[states.lastIndex -1].scoreLeft
        return prevRemPoints - scoreLeft
    }


    /**
     * This adds a new score to this game. It does so by adding the new state to the states list, and then logging
     * that to the file.
     * @param score the total score that was thrown this turn.
     * @param numOfDarts the amount of darts used this turn. This can change, as the player can attempt to use more
     * darts after the first WR is achieved.
     */
    fun processNewScore(score: Int, numOfDarts: Int){
        val newScore = scoreLeft - score
        val newDarts = dartsThrown + numOfDarts
        val newTurns = turns + 1

        val newState = State(
                scoreLeft = newScore,
                darts = newDarts,
                turns = newTurns,
                timeStamp = System.currentTimeMillis(),
                pauseTime = pauseTime
        )
        states.add(newState)
        loggButDontAddState(newState)
    }

    /**
     * This notifies the game of a pause. This class does NOT keep track of that itself, and it needs to be notified
     * by using this function.
     *
     * @param timePassed the time passed in millis during the pause. This will be subtracted from the time spent darting
     */
    fun addPause(timePassed:Long){
        val newState =State(
                    scoreLeft = scoreLeft,
                    darts = dartsThrown,
                    turns = turns,
                    timeStamp = System.currentTimeMillis(),
                    pauseTime = pauseTime +timePassed
            )

        states.add(newState)
        loggButDontAddState(newState)
        println("Current pausTime, $pauseTime")
    }


    /**
     * This removes the last state from the list, to effectively undo the last score. It then writes the last state to
     * the file.
     *
     * When undoing, the pausetime is preserved. It would be weird if you can undo time.
     */
    fun undoLast() {
        //DO NOT REMOVE THE FIRST STATE!
        if (states.lastIndex > 0){
            //save it to restore the pausetime
            val toBeRemoved = states.last()

            states.removeAt(states.lastIndex)

            //keep the time paused, you don't want that undone.
            if (states.size > 0)
                states.last().pauseTime = toBeRemoved.pauseTime

        }
        loggButDontAddState(states.last())
    }


    /**
     * Uses the bufferedWriter to write the newstate to the file.
     *
     * @param newState the state to write
     */
    private fun loggButDontAddState(newState: State) {
        //write it to the log file
        writer.write(newState.toString())
        writer.newLine()
        writer.flush()
    }

    /**
     * reads the states from a previously written log file
     *
     * @param pathString a string representing the path to the file to read. This string is provided by the java swing
     * filepicker.
     */
    private fun readStates(pathString: String): MutableList<State>{
        val path = Paths.get(pathString)
        val allLines = Files.readAllLines(path)

        //keep it in a array to easily edit by index.
        //This is needed, because of the way undo's work.
        // when undoing, the value in the file is NOT overwritten. That means that we need to update a turn
        //multiple times when it was undone and corrected.
        val states: Array<State?> = arrayOfNulls(allLines.size)

        //keep track of which index was updated last so we can remove all turns after that one later.
        //again, caused by the undo thing. When undoing, the line is NOT removed from the file. Since it was undone,
        //it is not valid.
        var lastTurnIndex= 0

        //loop over indices, so we can print on which line an error occurred.
        for (i in allLines.indices){
            val line = allLines[i]

            try {
                val result = State.fromString(line)
                //the turns value is an index, it is incremented when a new turn is added.
                states[result.turns] = result

                //just keep track of it by now
                lastTurnIndex = result.turns

            }catch (e: Exception){
                println("Something is wrong with line $i of the file. Continueing!")
                println("The line looks like this:$line")
                println("The exception that occured is:")
                e.printStackTrace()
            }
        }

        return states.indices
                //remove the nulls from the list. They can appear when undo's are in the list.
                .mapNotNull { states[it] }
                //remove the last turns that are later than the last one found
                .filter { it.turns <= lastTurnIndex }
                .toMutableList()
    }

    /**
     * Finds a filename that is not used yet. In the form of DartLogs/logX.txt
     */
    private fun findNewLoggerPath():String {
        //little helper function to create the file name.
        val time =  LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"))
        return "$LOG_FOLDEN_NAME/log$time.txt"

    }

    /**
     * Creates a bufferedWriter to the path specified,
     * and makes the folder if needed.
     */
    private fun createWriter(path : String): BufferedWriter{
        Files.createDirectories(Paths.get(LOG_FOLDEN_NAME))
        val writer = BufferedWriter(FileWriter(path,true))
        return writer
    }



}



