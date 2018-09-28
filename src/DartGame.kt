import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Tom on 25-9-2018.
 *
 *
 *
 */

val START_SCORE =  2000000
val LOG_FOLDEN_NAME = "DartLogs"
class DartGame{

    private var writer: BufferedWriter

    private var states: MutableList<State>

    constructor(){
        this.states = mutableListOf()
        val startState = State(
                START_SCORE,
                0,
                System.currentTimeMillis()
        )
        val loggerPath = findNewLoggerPath()
        this.writer = createWriter(loggerPath)

        states.add(startState)
        loggButDontAddState(startState)

    }
    constructor(logFilePath: String){
        this.states = readStates(logFilePath)
        this.writer = createWriter(logFilePath)
    }

    //easy access properties
    val scoreLeft: Int get() = states.last().scoreLeft
    val turns : Int get() = states.last().turns
    val dartsThrown: Int get() = turns * 3
    val scoreThrown: Int get() = START_SCORE - scoreLeft

    //Calculates the average. Startscore - scoreleft is the amount thrown so far
    val avg: Float get() = (START_SCORE - scoreLeft).toFloat() /  turns.toFloat()

    val timeSpent: String get() {
        if (states.size <= 1){
            return "00:00:00"
        }

        val millisSpent =  System.currentTimeMillis() - states[1].timeStamp

        var timeLeft = Duration.ofMillis(millisSpent)

        val hours = timeLeft.toHours()
        timeLeft = timeLeft.minusHours(hours)
        val minutes = timeLeft.toMinutes()
        timeLeft = timeLeft.minusMinutes(minutes)
        val seconds = timeLeft.seconds

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    val lastScore: Int get(){
        if (dartsThrown==0){return 0}

        val prevRemPoints = states[states.lastIndex -1].scoreLeft
        return prevRemPoints - scoreLeft
    }



    //adds a new score
    fun processNewScore(score: Int){
        val newScore = scoreLeft - score
        val newTurns = turns + 1
        val newState = State(
                newScore,
                newTurns,
                System.currentTimeMillis()
        )
        states.add(newState)
        loggButDontAddState(newState)
    }


    fun undoLast() {
        //DO NOT REMOVE THE FIRST STATE!
        if (states.lastIndex > 0){
            states.removeAt(states.lastIndex)
        }
        loggButDontAddState(states.last())
    }






    private fun loggButDontAddState(newState: State) {
        //write it to the log file
        writer.write(states.last().toString())
        writer.newLine()
        writer.flush()
    }

    /**
     * reads the states from a previously written log file
     */
    private fun readStates(pathString: String): MutableList<State>{
        //todo error handling
        val path = Paths.get(pathString)
        val allLines = Files.readAllLines(path)

        val states:MutableList<State> = mutableListOf()
        for (line in allLines){
            //split the line into a list of strings
            val keyValue: List<String> = line.split(",")
            //split on the equals sign, to get a PROPNAME and a PROPVALUE
            //get the second value( at index 1), to get the propvalue. Then trim it to remove spaces
            val values = keyValue.map { it.split("=")[1].trim() }
            val result = State(values[0].toInt(), values[1].toInt(), values[2].toLong())
            states.add(result)
        }
        return states
    }

    /**
     * Finds a filename that is not used yet. In the form of DartLogs/logX.txt
     */
    private fun findNewLoggerPath():String {
        //little helper function to create the file name.
        val time =  LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd -- HH-:mm-ss-SSS"))
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

data class State(val scoreLeft: Int, val turns: Int, val timeStamp:Long){
    override fun toString(): String = "Points = $scoreLeft, Turns = $turns, Timestamp = $timeStamp"
}


