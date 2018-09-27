import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.border.Border


/**
 * Created by Tom on 25-9-2018.
 */
class ControllerScreen (val slave:DisplayScreen ){


    private val dartGame: DartGame
    private val frame: JFrame

    private lateinit var statusLabel: JLabel


    init {
        this.dartGame = DartGame()

        //init frame
        frame = JFrame("Controller")
        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        frame.setSize(500, 200)

        //init layout
        frame.layout = BoxLayout(frame.contentPane, X_AXIS)


        val left = JPanel()
        left.setSize(200,200)
        addLeftColumn(left)
        frame.add(left)

        val right = JPanel()
        right.setSize(200,200)
        addRightColumn(right)
        frame.add(right)


    }

    private fun addRightColumn(panel: JPanel){
        panel.layout = GridLayout(2,1,10,10)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)

        val undoButton = JButton("Undo turn")
        panel.add(undoButton)

        val fullscreenButton = JButton("Togle fullscreen")
        panel.add(fullscreenButton)


        fullscreenButton.addActionListener {
            slave.toggleFulscreen()
        }

        undoButton.addActionListener {
            val scoreBefore = dartGame.scoreLeft
            dartGame.undoLast()
            val undoneScore = dartGame.scoreLeft - scoreBefore
            update( "Undo $undoneScore")
        }
    }

    private fun addLeftColumn(panel: JPanel){
        panel.layout = BoxLayout(panel, Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)

        val explanation = JLabel("Type score below, press enter to confirm", SwingConstants.CENTER)
        panel.add(explanation)
        val textField = JTextField("",1)
        textField.font = Font("Sans Serif", Font.BOLD, 50)
        panel.add(textField)


        statusLabel = JLabel("")
        panel.add(statusLabel)

        textField.addActionListener { ActionEvent ->
            try {
                val score = textField.text.toString().toInt()
                dartGame.processNewScore(score)
                update("score $score")

            }catch (e: Exception){
                println("failed to parse")
            }
            textField.text = ""
        }
    }


    private fun update(actionDescription: String) {
        this.statusLabel.text = "Current score: ${dartGame.scoreLeft} Last action: $actionDescription"

        slave.setText( dartGame.scoreLeft)
        println("Setting score to ${dartGame.scoreLeft}")
    }

    fun start (){
        frame.isVisible = true
    }





}