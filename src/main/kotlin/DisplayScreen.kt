import DartGame.Companion.START_SCORE
import java.awt.*
import java.text.NumberFormat
import java.util.*
import javax.swing.*
import javax.swing.JLabel

/**
 * Created by Tom on 25-9-2018.
 *
 * This represents the displayer screen. It shows the score to the viewers.
 *
 * It acts as slave, it waits for commands from the ControllerScreen.
 */
class DisplayScreen {
    private val PROMO_IMG_WIDTH = 614
    private val PROMO_IMG_HEIGHT = 282

    //main score
    val scoreLabel: NumberJLabel

    //The stats labels. These are init in the createViewsInStatsPanel function
    lateinit var scoreThrown: NumberJLabel
    lateinit var averageLabel: NumberJLabel
    lateinit var dartsThrown: NumberJLabel
    lateinit var timeDarting: NumberJLabel
    lateinit var lastTurn: NumberJLabel

    val frame: JFrame


    init {
        //Create and set up the window.
        this.frame = JFrame("Score Keeper powered by DartBee")
        this.frame.setSize(1000, 1000)
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.frame.useDartBeeIcon()


        //The layout for this frame is a vertical list, where each item is the same size.
        frame.layout = GridLayout(3,1)

        val promoPanel = JPanel()
        frame.add(promoPanel)
        createViewsInPromoPanel(promoPanel)

        //since this is only one view, we can add it to the frame directly
        this.scoreLabel = NumberJLabel(SwingConstants.CENTER, START_SCORE.toDouble(), "2.000.000")
        this.scoreLabel.font = Font("Sans Serif", Font.BOLD, 350)
        frame.add(scoreLabel)

        //A panel nested inside the main panel that has the stats
        val statsPanel = JPanel()
        statsPanel.border = BorderFactory.createEmptyBorder(20,20,20,20)
        createViewsInStatsPanel(statsPanel)
        frame.add(statsPanel)
    }

    /**
     * This function fills the promo panel. It adds the views to the panel, but does not show or do anything else
     * with the panel.
     *
     * @param statsPanel the panel to add the views to
     */
    private fun createViewsInPromoPanel(panel: JPanel){
        panel.layout = BoxLayout(panel,BoxLayout.Y_AXIS)

        val img = Utils().readImg("DartBee.png")
        val scaled =   img.getScaledInstance(PROMO_IMG_WIDTH,PROMO_IMG_HEIGHT, Image.SCALE_DEFAULT)
        val icon = ImageIcon(scaled)
        val dartBeeIcon = JLabel(icon, SwingConstants.CENTER)

        dartBeeIcon.alignmentX = Component.CENTER_ALIGNMENT
        panel.add(dartBeeIcon)

        val downloadText = JLabel("Download vandaag uw persoonlijke Darts assistent, zoek naar DartBee in de Play Store!", SwingConstants.CENTER)
        downloadText.font = Font("Sans Serif", Font.PLAIN, 32)
        downloadText.alignmentX = Component.CENTER_ALIGNMENT
        downloadText.border = BorderFactory.createEmptyBorder(10,0,0,0)

        panel.add(downloadText )
    }


    /**
     * This function fills the stats panel. It adds the views to the panel, but does not show or do anything else
     * with the panel.
     *
     * @param statsPanel the panel to add the views to
     */
    private fun createViewsInStatsPanel(statsPanel:JPanel) {
        //init layout
        statsPanel.layout = GridBagLayout()
        val c = GridBagConstraints()
        c.fill = GridBagConstraints.BOTH

        //Local function that updates the constants to the next line.
        fun GridBagConstraints.nextLine() {
            this.gridy++
            this.gridx = 0
            c.weighty = 1.0
            c.weightx = 1.0
            c.gridwidth = 1
        }

        // creates a label and moves it to location X + 1
        //The text is added in the update function.
        fun createLabel(textStyle: Int = Font.PLAIN, textSize: Int = 40): NumberJLabel {
            val label = NumberJLabel(SwingConstants.CENTER)
            label.font = Font("Sans Serif", textStyle, textSize)
            statsPanel.add(label, c)
            c.gridx++ // increment gridx for the next label
            return label
        }
        //top row, only one item
        c.gridwidth = 2
        scoreThrown = createLabel(Font.BOLD, 100)
        c.nextLine()
        //add some extra space
        c.nextLine()

        //midle row
        averageLabel = createLabel()
        dartsThrown = createLabel()
        c.nextLine()

        //botom row
        lastTurn = createLabel()
        timeDarting = createLabel()
    }

    //starts the entire screen.
    fun start (){frame.isVisible = true}


    /**
     * This toggles the fullscreen functionality. It hides the bar form the window, at takes up the fulls screen.
     *
     * Extra advantage is that you cannot close the screen as an accident.
     */
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

    /**
     * Called by the controller screen. Updates everything on the screen, EXCEPT FOR THE TIMER
     */
    fun update(dartGame: DartGame) {
        //first do the number only ones
        //this function formats a intermediate float to a nice rounded Int with points seperating thousands
        val largeIntFormatter = fun (value:Double) = NumberFormat.getNumberInstance(Locale.GERMANY).format(value.round())

        scoreLabel.countWithAnimation(dartGame.scoreLeft.toDouble(), largeIntFormatter)
        scoreThrown.countWithAnimation(dartGame.scoreThrown.toDouble(), largeIntFormatter)


        //avg is a little harder, first take NAN into account
        val avg = if (dartGame.avg.isNaN()) 0f else dartGame.avg
        //format the float with some extra text
        val avgFormatter = fun (value:Double) = "Gemiddelde score: "+ "%.2f".format(value)
        averageLabel.countWithAnimation(avg.toDouble(), avgFormatter)

        //do the dartsthrown
        val dartsThrownFormatter = fun (value:Double) = "Aantal darts gegooid: " +  NumberFormat.getNumberInstance(Locale.GERMANY).format(value.round())
        dartsThrown.countWithAnimation(dartGame.dartsThrown.toDouble(), dartsThrownFormatter)

        val lastTurnFormatter = fun (value:Double)= "Laatste score: "+  NumberFormat.getNumberInstance(Locale.GERMANY).format(value.round())
        lastTurn.countWithAnimation(dartGame.lastScore.toDouble(), lastTurnFormatter)
    }

    /**
     * The timer has to be updated much more often, so it is done separately. Also called by the controller screen.
     */
    fun updateTimer(dartGame: DartGame) {
        timeDarting.text = dartGame.timeSpent
    }


}


