import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.Buffer
import java.nio.file.Files
import java.nio.file.Paths

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
        addAndLogState(startState)

    }
    constructor(logFilePath: String){
        this.states = readStates(logFilePath)
        this.writer = createWriter(logFilePath)
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
        addAndLogState(newState)
    }


    fun undoLast() {
        //DO NOT REMOVE THE FIRST STATE!
        if (states.lastIndex > 0){
            states.removeAt(states.lastIndex)
        }
        addAndLogState(states.last())
    }





    private fun addAndLogState(newState: State) {
        states.add(newState)
        //write it to the log file
        writer.write(states.last().toString())
        writer.newLine()
        writer.flush()
    }

    /**
     * reads the states from a previously written log file
     */
    private fun readStates(path: String): MutableList<State>{
        //todo error handling
        val path = Paths.get(path)
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
    private fun findNewLoggerPath():String{
        //little helper function to create the file name.
        fun toFileName(num: Int) = "$LOG_FOLDEN_NAME/log$num.txt"
        var count = 0
        //keep checking if the file with that number already exists.
        do {
            var file = File(toFileName(count))
            count++
        }
        while (file.exists())
        return toFileName(count)
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


