import java.awt.event.ActionListener
import java.text.NumberFormat
import java.util.*
import javax.swing.JLabel
import javax.swing.Timer

//Constants
val START_SCORE =  2000000
val LOG_FOLDEN_NAME = "DartLogs"



//Extensions
fun Double.toThePowerOf(pow: Double) = Math.pow(this, pow)
fun Double.round(): Long = Math.round(this)

/**
 * This is an extension of Jlabel to allow for counting down.
 *
 * The currentValue parameter is used to generate the animation. It is the startvalue when passed to the constructor.
 *
 * The startText is the text to show before calling any function that sets the text.
 *
 */
class NumberJLabel(centering: Int, var currentShownValue: Double = 0.0, startText: String= "") : JLabel(startText, centering) {

    /**
     * This makes the label text count down or up to the goal value. The value is updated every 16 milliseconds,
     * aprox. 16 fps.
     *
     * @param goal the number to move towards. It may take a while before this value is actually displayed.
     * @param format a function that maps the current number that needs to be shown to the actual text you want
     * displayed on the screen. This can be a rounding function for example, or a function that adds a prefix.
     * Or both of course. The parameter is the currently showing value that needs to be formatted
     */
    fun countWithAnimation(goal: Double, format:(value:Double)-> String  ) {
        //save the original value when starting the animation
        val valueShownOnAnimationStart: Double = currentShownValue
        val toBeDone:Double = (valueShownOnAnimationStart - goal)

        //abs value to be used to calculate the time to take
        val absScoreDiff: Double = Math.abs(toBeDone)
        //calculate for how long to animate depending on the difference plus some extra to add a minumum
        val uncappedFrames: Int = (0.75 * absScoreDiff + 30).toInt()
        //cap max animation frames to not take some ridiculous time
        val framesToDo: Int = Math.min(uncappedFrames, 1000 / 16)

        //counts what frame we currently are
        var currentFrame: Int = 0
        val timer = Timer(16, {
            //THIS IS EXECUTED ON UPDATE
            currentFrame++
            val timer = (it.source as Timer)

            //we are done, we can stop this timer.
            if (currentFrame >= framesToDo) {
                timer.stop()
                //to avoid any rounding errors, update the text one more time
                this.text = format(goal)
            }
            //update the value
            else {
                val done: Double = currentFrame.toDouble() / framesToDo.toDouble()
                //the bigger a is, the steeper the function becomes in the midle.
                val a: Double = 2.0

                //this returns a value between 0 and 1, basicallyy how far we need to be done now
                val fractionToDoNow: Double = (done.toThePowerOf(a)) / (done.toThePowerOf(a) + (1.0 - done).toThePowerOf(a))

                val shouldBeDoneNow = fractionToDoNow * toBeDone

                val valueToShow = valueShownOnAnimationStart - shouldBeDoneNow
                //save the curren value for later
                currentShownValue = valueToShow
                //use the formatting function to format the text of this label
                this.text =  format(valueToShow)
            }
        })
        timer.start()
    }
}


//Small Data Classes
data class State(val scoreLeft: Int, val turns: Int, val timeStamp:Long){
    override fun toString(): String = "Points = $scoreLeft, Turns = $turns, Timestamp = $timeStamp"
}
