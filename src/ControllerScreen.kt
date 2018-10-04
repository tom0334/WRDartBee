import java.awt.*
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS


/**
 * Created by Tom on 25-9-2018.
 */
class ControllerScreen(private val slave: DisplayScreen, private val dartGame: DartGame = DartGame()) {

    private val frame: JFrame
    private var paused: Boolean = true
    private var pauseStart: Long = System.currentTimeMillis()

    val inputPanel: JPanel
    val pausedPanel: JPanel

    private lateinit var statusLabel: JLabel
    private lateinit var pauseButton: JButton
    private lateinit var pauseStateLabel: JLabel

    init {
        frame = JFrame("Controller")
        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        frame.setSize(800, 400)
        frame.useDartBeeIcon()


        frame.layout = BoxLayout(frame.contentPane, X_AXIS)

        //craete the inputPanel
        this.inputPanel = JPanel()
        inputPanel.layout =  BoxLayout(inputPanel, X_AXIS)
        val left = JPanel()
        left.setSize(200, 200)
        addLeftColumn(left)
        inputPanel.add(left)
        val right = JPanel()
        right.setSize(200, 200)
        addRightColumn(right)
        inputPanel.add(right)
        update("-")
        frame.add(inputPanel)

        //craete the paused panel
        this.pausedPanel = JPanel()
        pausedPanel.layout = BoxLayout(pausedPanel, X_AXIS)
        val resumeButton =  JButton("Resume")
        resumeButton.addActionListener({togglePause()})
        pausedPanel.add(resumeButton)


        frame.add(pausedPanel)


        inputPanel.isVisible = false
        Timer(1000,{
            if (!paused) {
                slave.updateTimer(dartGame)
            }
        }).start()

    }

    fun togglePause(){
        this.paused = ! paused

        inputPanel.isVisible = ! paused
        pausedPanel.isVisible = paused

        if ( ! paused){
            val timePassed = System.currentTimeMillis() - pauseStart!!
            dartGame.addPause(timePassed)
        }
        if (paused){
            pauseStart = System.currentTimeMillis()
        }

    }




    private fun addRightColumn(panel: JPanel){
        panel.layout = GridLayout(2,1,10,10)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)

        val undoButton = JButton("Undo turn")
        panel.add(undoButton)

        val fullscreenButton = JButton("Toggle fullscreen")
        panel.add(fullscreenButton)

        this.pauseButton = JButton("Pause game")
        panel.add(pauseButton)

        fullscreenButton.addActionListener {
            slave.toggleFulscreen()
        }

        undoButton.addActionListener {
            val scoreBefore = dartGame.scoreLeft
            dartGame.undoLast()
            val undoneScore = dartGame.scoreLeft - scoreBefore
            update( "Undo $undoneScore")
        }

        pauseButton.addActionListener({
            togglePause()
        })
    }



    private fun addLeftColumn(panel: JPanel){
        panel.layout = BoxLayout(panel, Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)


        val dartsExplanation = JLabel("Type AMOUNT OF DARTS below", SwingConstants.CENTER)
        panel.add(dartsExplanation)

        val numDartsTextField= JTextField("3",1)
        numDartsTextField.font = Font("Sans Serif", Font.BOLD, 25)
        panel.add(numDartsTextField)

        val scoreExplanation = JLabel("Type SCORE below, press enter to confirm", SwingConstants.CENTER)
        panel.add(scoreExplanation)

        val scoreTextField = JTextField("",1)
        scoreTextField.font = Font("Sans Serif", Font.BOLD, 50)
        panel.add(scoreTextField)


        statusLabel = JLabel("")
        panel.add(statusLabel)

        scoreTextField.addActionListener { ActionEvent ->
            try {
                val score = scoreTextField.text.toString().toInt()
                val darts = numDartsTextField.text.toString().toInt()
                dartGame.processNewScore(score,darts)
                update("score $score")


            }catch (e: Exception){
                println("failed to parse")
            }
            scoreTextField.text = ""
        }
    }


    private fun update(actionDescription: String) {
        if (paused ){
            return
        }
        this.statusLabel.text = "Current score: ${dartGame.scoreLeft} Last action: $actionDescription"
        slave.update(this.dartGame)
    }

    fun start (){
        frame.isVisible = true
    }



}