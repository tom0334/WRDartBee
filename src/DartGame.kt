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
 *
 *
 */
class DartGame{

    private var writer: BufferedWriter

    private var states: MutableList<State>

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
    constructor(logFilePath: String){
        this.states = readStates(logFilePath)

        //add a -m to the the file name to denote it is modified
        val withoutExtension = logFilePath.split(".")[0]
        val newName = withoutExtension + "-M"  +".txt"

        this.writer = createWriter(newName)
    }

    //easy access properties
    val scoreLeft: Int get() = states.last().scoreLeft
    val turns : Int get() = states.last().turns
    val dartsThrown: Int get() = states.last().darts

    val pauseTime: Long get() = states.last().pauseTime

    val scoreThrown: Int get() = START_SCORE - scoreLeft



    //Calculates the average. Startscore - scoreleft is the amount thrown so far
    val avg: Float get() = (START_SCORE - scoreLeft).toFloat() /  turns.toFloat()

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

    val lastScore: Int get(){
        if (dartsThrown==0){return 0}

        val prevRemPoints = states[states.lastIndex -1].scoreLeft
        return prevRemPoints - scoreLeft
    }



    //adds a new score
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



    fun undoLast() {
        //DO NOT REMOVE THE FIRST STATE!
        if (states.lastIndex > 0){
            val toBeRemoved = states.last()
            states.removeAt(states.lastIndex)
            if (states.size > 0)
                states.last().pauseTime = toBeRemoved.pauseTime

        }
        loggButDontAddState(states.last())
    }






    private fun loggButDontAddState(newState: State) {
        //write it to the log file
        writer.write(newState.toString())
        writer.newLine()
        writer.flush()
    }

    /**
     * reads the states from a previously written log file
     */
    private fun readStates(pathString: String): MutableList<State>{
        val path = Paths.get(pathString)
        val allLines = Files.readAllLines(path)

        //keep it in a array to easily edit by index.
        //the index is essentially the turn value.
        val states: Array<State?> = arrayOfNulls(allLines.size)


        var lastTurn= 0

        for (i in allLines.indices){
            val line = allLines[i]

            try {
                //split the line into a list of strings
                val keyValue: List<String> = line.split(",")
                //split on the equals sign, to get a PROPNAME and a PROPVALUE
                //get the second value( at index 1), to get the propvalue. Then trim it to remove spaces

                val values = keyValue.map { it.split("=")[1].trim() }

                val result = State(values[0].toInt(), values[1].toInt(),values[2].toInt(), values[3].toLong(), values[4].toLong())

                states[result.turns] = result
                lastTurn = result.turns

            }catch (e: Exception){
                println("Something is wrong with line $i of the file. Continueing!")
                println("The line looks like this:$line")
                println("The exception that occured is:")
                e.printStackTrace()
            }
        }

        return states.indices
                .mapNotNull { states[it] }
                .filter { it.turns <= lastTurn }
                .toMutableList()

    }

    /**
     * Finds a filename that is not used yet. In the form of DartLogs/logX.txt
     */
    private fun findNewLoggerPath():String {
        //little helper function to create the file name.
        val time =  LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-mm-dd--HH-mm-ss"))
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



