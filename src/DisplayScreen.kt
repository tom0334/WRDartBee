import java.awt.Dimension
import java.awt.Font

import javax.swing.JFrame
import javax.swing.JLabel
import java.awt.event.ActionEvent
import javax.swing.Timer
import java.awt.event.ActionListener
import javax.swing.SwingConstants


/**
 * Created by Tom on 25-9-2018.
 */
class DisplayScreen {

    val scoreLabel: JLabel
    val frame: JFrame

    init {
        //Create and set up the window.
        this.frame = JFrame("ScoreKeeper")
        this.frame.setSize(1000, 1000)
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        this.scoreLabel = JLabel(START_SCORE.toString(), SwingConstants.CENTER)
        this.scoreLabel.font = Font("Sans Serif", Font.BOLD, 200)

        this.frame.contentPane.add(scoreLabel)

        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);
    }
    fun start (){frame.isVisible = true}

    fun setText(score: Int) {
        val original = scoreLabel.text.toInt()


        val toBeDone = Math.abs(original - score)

        var currentFrame = 0
        val framesToDo = (0.75 * toBeDone.toFloat() +30).toInt()

        val timer = Timer(16, ActionListener { e ->
            currentFrame++
            val timer = (e.source as Timer)
            if (currentFrame > framesToDo) {
                println("stopping")
                timer.stop()
            }
            else{
                val done: Double = currentFrame.toDouble() / framesToDo.toDouble()
                //the bigger a is, the steeper the function becomes in the midle.
                val a : Double = 2.0
                val sineResult: Double = (done.toThePowerOf(a)) / (done.toThePowerOf(a) + (1.0 - done).toThePowerOf(a))

                val shouldBeSubtracedNow = sineResult * toBeDone.toDouble()
                val result =  original - shouldBeSubtracedNow.toInt()
                this.scoreLabel.text = result.toString()
            }
        })
        timer.start()
    }
}

fun Double.toThePowerOf(pow: Double) = Math.pow(this, pow)