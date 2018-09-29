import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JFrame
/**
 * Created by Tom on 25-9-2018.
 */
object Main {

    @JvmStatic fun main(args: Array<String>) {
        //init frame
        val frame = JFrame("StartScreen")
        frame.setSize(500, 200)

        //init layout
        frame.layout = GridLayout(1,2)

        val newGameButton = JButton("New Game")
        frame.add(newGameButton)
        val loadGameButton = JButton("Load game")
        frame.add(loadGameButton)


        newGameButton.addActionListener({
            frame.isVisible = false
            startFresh()
        })

        loadGameButton.addActionListener({


            val fc = JFileChooser()
            val result = fc.showOpenDialog(frame)

            if (result == JFileChooser.APPROVE_OPTION){
                val path = fc.selectedFile.absolutePath
                println("Opening file $path")
                startWithPreviousData(path)
                frame.isVisible = false
            }
            else{
                println("Not approved")
            }
        })

        frame.isVisible = true
    }


    fun startFresh(){
        val displayer = DisplayScreen()
        displayer.start()

        val secondScreen = ControllerScreen(displayer)
        secondScreen.start()
    }

    fun startWithPreviousData(path: String) {
        val game = DartGame(path)

        val displayer = DisplayScreen()
        displayer.start()

        val secondScreen = ControllerScreen(displayer, game)
        secondScreen.start()

    }



}
