import java.awt.Font
import java.awt.GridLayout

import java.awt.event.ActionListener
import javax.swing.*


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

        val panel = JPanel()
        frame.add(panel)
        panel.layout = GridLayout(2,1)

        this.scoreLabel = JLabel(START_SCORE.toString(), SwingConstants.CENTER)
        this.scoreLabel.font = Font("Sans Serif", Font.BOLD, 400)
        panel.add(scoreLabel)

        val dartbeeText = JLabel("Powered by DartBee for Android", SwingConstants.CENTER  )
        dartbeeText.font = Font("Sans Serif", Font.BOLD, 25)
        panel.add(dartbeeText)

    }


    fun start (){frame.isVisible = true}

    fun setText(score: Int) {
        val original = scoreLabel.text.toInt()
        val toBeDone = (original - score)

        val absScoreDiff = Math.abs(toBeDone).toFloat()
        val uncappedFrames = (0.75 * absScoreDiff +30).toInt()
        val framesToDo = Math.min(uncappedFrames, 1000/16)// cap max animation frames


        //counts what frame we currently are
        var currentFrame = 0
        val timer = Timer(16, ActionListener { e ->
            currentFrame++
            val timer = (e.source as Timer)
            if (currentFrame > framesToDo) {
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

    fun toggleFulscreen(){
        //for the undecorated thing to work, we need to dispose the entire frame to create it again later.
        frame.dispose()
        if (frame.isUndecorated){
            frame.extendedState = JFrame.NORMAL
            frame.setSize(1000,1000)
            frame.isUndecorated = false

        }else {
            frame.extendedState = JFrame.MAXIMIZED_BOTH
            frame.isUndecorated = true
        }
        //restart frame
        frame.isVisible= true

    }
}

fun Double.toThePowerOf(pow: Double) = Math.pow(this, pow)