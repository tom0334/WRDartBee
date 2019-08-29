import java.awt.*
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS


/**
 * Created by Tom on 25-9-2018.
 *
 * This screen controls the DisplayScreen. It lets the user input new scores, pause the game and toggle the fullscreen
 * mode of the DisplayScreen.
 *
 */

class ControllerScreen(private val slave: DisplayScreen, private val dartGame: DartGame = DartGame()) {

    private val frame: JFrame

    //keeps track of the paused state. When pausing, the controller notifies the DisplayScreen. It starts paused.
    private var paused: Boolean = true
    //the time in millis when the game was last paused. This is used to calculate how low the game was paused.
    //It is initialized to the current time, because the game starts as paused.
    private var pauseStart: Long = System.currentTimeMillis()


    //This screen switches between two Jpanels. The pausedPanel only shows a resume button.
    //when te game is paused, the visibility of these is changed.
    val mainPanel: JPanel
    val pausedPanel: JPanel


    //the Label that shows tha last action done. It helps the user know what is happening.
    //This is initialized in the AddRightColumn function.
    private lateinit var statusLabel:JLabel


    init {
        frame = JFrame("Controller")
        //the progam should only be closable from the displayScreen. This prevents unwanted closing of the program
        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        frame.setSize(800, 400)
        frame.useDartBeeIcon()

        //The layout for this frame is simple: two panels of equal width.
        frame.layout = BoxLayout(frame.contentPane, X_AXIS)

        //craete the mainPanel. This contains two panels, one on the left with the textfield and one on the right
        //for some buttons
        this.mainPanel = createMainPanel()
        frame.add(mainPanel)

        this.pausedPanel = createPausedPanel()
        frame.add(pausedPanel)

        mainPanel.isVisible = false

        //Update the timer in the displayScreen every second if not paused.
        Timer(1000,{
            if (!paused) {
                slave.updateTimer(dartGame)
            }
        }).start()

        //update with inital values
        slave.updateTimer(dartGame)
        slave.update(dartGame)
    }

    /**
     * Function that creates the main panel to the init block, so the init block can add it to the frame.
     *
     * Internally, it uses 2 functions that create the left and right column.
     * The left column contains the input field and status field, the right one contains all the buttons.
     *
     *
     *@return The main panel that contains the buttons and input field to operate the progam.
     *
     */
    private fun createMainPanel(): JPanel{
        val inUsePanel = JPanel()
        inUsePanel.layout =  BoxLayout(inUsePanel, X_AXIS)

        //left panel is initialized by a function
        val left = createLeftColumn()
        inUsePanel.add(left)

        //right panel is also initialized by a function
        val right = createRightColumn()
        inUsePanel.add(right)
        return inUsePanel
    }
    /**
     * This creates the left column.
     * @return a panel that is ready to be added to the main panel, with textFields for inputting score and amount
     * of darts.
     */
    private fun createLeftColumn():JPanel{
        val panel = JPanel()
        panel.setSize(200,200)
        panel.layout = BoxLayout(panel, Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)

        //Start with a explanation text of how the top textfield should be used.
        val dartsExplanation = JLabel("Type AMOUNT OF DARTS below", SwingConstants.CENTER)
        panel.add(dartsExplanation)

        //This textfield lets the user type in the number of darts thrown. This is NOT always 3, as with this WR
        //attempt it is allowed to throw as many darts as desired in a single turn.
        val numDartsTextField= JTextField("3",1)
        numDartsTextField.font = Font("Sans Serif", Font.BOLD, 25)
        panel.add(numDartsTextField)

        //Similar for the score, here the user can type in the total sccore for this turn
        val scoreExplanation = JLabel("Type SCORE below, press enter to confirm", SwingConstants.CENTER)
        panel.add(scoreExplanation)

        val scoreTextField = JTextField("",1)
        scoreTextField.font = Font("Sans Serif", Font.BOLD, 50)
        panel.add(scoreTextField)

        //init it to just a space. If it is an empty string it will not take up any UI space.
        //this label will show the last action
        this.statusLabel = JLabel(" ")
        panel.add(statusLabel)

        //this is executed when enter is pressed. Do it in a try catch block to prevent parsing exceptions.
        //when that happens, just clear the textfield and let the user try again.
        scoreTextField.addActionListener {
            try {
                val score = scoreTextField.text.toString().toInt()
                val darts = numDartsTextField.text.toString().toInt()
                dartGame.processNewScore(score,darts)
                update("score $score")


            }catch (e: Exception){
                println("failed to parse the input. Please only input numbers")
            }
            //reset the textfield to let him try again.
            scoreTextField.text = ""
        }
        return panel
    }

    /**
     * Creates the right side of the UI, with all the buttons.
     *
     * @return a panel that is ready to be added to the main screen, containing undo, pause and fullscreen buttons.
     */
    private fun createRightColumn(): JPanel{
        val panel = JPanel()
        panel.setSize(200,200)
        panel.layout = GridLayout(2,1,10,10)
        panel.border = BorderFactory.createEmptyBorder(20,20,20,20)

        val undoButton = JButton("Undo turn")
        panel.add(undoButton)

        val fullscreenButton = JButton("Toggle fullscreen")
        panel.add(fullscreenButton)

        val pauseButton = JButton("Pause game")
        panel.add(pauseButton)

        fullscreenButton.addActionListener {
            slave.toggleFulscreen()
        }

        undoButton.addActionListener {
            //first capture the score undoing
            val scoreBefore = dartGame.scoreLeft
            //undo the score
            dartGame.undoLast()
            //show the undone score in the status label
            val undoneScore = dartGame.scoreLeft - scoreBefore
            update("Undo $undoneScore")
        }

        pauseButton.addActionListener({
            togglePause()
        })
        return panel
    }

    /**
     * Creates a simple panel that can be swapped in for the main panel. It contains only a return button
     * @return the simple paused state panel
     */
    private fun createPausedPanel():JPanel{
        val pausedPanel = JPanel()
        //a gridbaglayout centers the button by default.
        pausedPanel.layout = GridBagLayout()
        val resumeButton =  JButton("Resume/Start Game")
        pausedPanel.add(resumeButton,GridBagConstraints())

        resumeButton.addActionListener({
            togglePause()
            }
        )
        return pausedPanel
    }

    /**
     * Toggles the paused state and notifies the dartGame of a pause when the pause state ends.
     *
     * Also updates the UI to show the correct panel.
     */
    private fun togglePause(){
        //toggle paused state
        this.paused = ! paused

        //switch visibility
        mainPanel.isVisible = ! paused
        pausedPanel.isVisible = paused

        //update the game with a new pause with a calculated duration of
        if ( ! paused){
            val timePassed = System.currentTimeMillis() - pauseStart
            dartGame.addPause(timePassed)
        }
        if (paused){
            pauseStart = System.currentTimeMillis()
        }

    }

    /**
     * Updates the statusLabel and the slave, IF NOT PAUSED.
     */
    private fun update(actionDescription: String) {
        if (paused){
            return
        }

        this.statusLabel.text = "Current score: ${dartGame.scoreLeft} Last action: $actionDescription"
        slave.update(this.dartGame)
    }

    /**
     * Shows this screen.
     */
    fun start (){
        frame.isVisible = true
    }



}