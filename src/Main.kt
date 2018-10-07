import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JFrame
/**
 * Created by Tom on 25-9-2018.
 *
 * Main Object for running the program. It first creates the Welcome screen, that has 2 buttons on it. One to create a
 * new game and one to read an existing one.
 *
 * It Also handles opening the file and providing it to the actual main program.
 */
object Main {

    @JvmStatic fun main(args: Array<String>) {
        //init frame
        val frame = JFrame("StartScreen")
        frame.useDartBeeIcon()
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

            //this opens a window that lets the user pick a file. It then returns when the user picked one.
            val result = fc.showOpenDialog(frame)
            if (result == JFileChooser.APPROVE_OPTION){
                val path = fc.selectedFile.absolutePath
                println("Opening file $path")
                startWithPreviousData(path)
                frame.isVisible = false
            }
            else{
                //Nothing happens when the file was not approved. The window chooser window just closes,
                //and the user can try again.
                println("File not selected!")
            }
        })

        frame.isVisible = true
    }


    /**
     * Starts a new game.
     */
    private fun startFresh(){
        val displayer = DisplayScreen()
        displayer.start()

        val secondScreen = ControllerScreen(displayer)
        secondScreen.start()
    }

    /**
     * Uses previous data from a file to reopen the game.
     */
    private fun startWithPreviousData(path: String) {
        val game = DartGame(path)

        val displayer = DisplayScreen()
        displayer.start()

        val secondScreen = ControllerScreen(displayer, game)
        secondScreen.start()
    }



}
