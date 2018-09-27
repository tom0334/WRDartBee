import java.awt.Button
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS


/**
 * Created by Tom on 25-9-2018.
 */
class ControllerScreen (val slave:DisplayScreen ){


    val dartGame: DartGame
    val frame: JFrame

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
        panel.layout = BoxLayout(panel, Y_AXIS)
        val b = Button("Button 1 ")
        panel.add(b)
        val b2 = Button("Button 2")
        panel.add(b2)
        val b3 = Button("Button 3")
        panel.add(b3)
        b.addActionListener { e ->
            dartGame.processNewScore(10)
            update()
        }
    }

    private fun addLeftColumn(panel: JPanel){
        panel.layout = BoxLayout(panel, Y_AXIS)
        val textField = JTextField("",1)
        panel.add(textField)

        textField.addActionListener { ActionEvent ->
            try {
                val score = textField.text.toString().toInt()
                dartGame.processNewScore(score)
                update()


            }catch (e: Exception){
                println("failed to parse")
            }
            textField.text = ""
        }



    }

    private fun update() {
        slave.setText( dartGame.scoreLeft)
    }

    fun start (){
        frame.isVisible = true
    }





}