import java.awt.*

import java.awt.event.ActionListener
import java.text.NumberFormat
import java.util.*
import javax.swing.*
import javax.swing.Timer
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import javax.swing.JLabel






/**
 * Created by Tom on 25-9-2018.
 */
class DisplayScreen {

    var currentShownValue:Int = START_SCORE

    val scoreLabel: JLabel

    lateinit var averageLabel: JLabel
    lateinit var dartsThrown: JLabel
    lateinit var timeDarting: JLabel
    lateinit var lastTurn: JLabel
    lateinit var scoreThrown: JLabel

    val frame: JFrame


    init {
        //Create and set up the window.
        this.frame = JFrame("ScoreKeeper")
        this.frame.setSize(1000, 1000)
        this.frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val panel = JPanel()
        frame.add(panel)
        panel.layout = GridLayout(3,1)



        val promoPanel = JPanel()
        panel.add(promoPanel)
        createPromoView(promoPanel)

/*        val dartbeeText = JLabel("Powered by DartBee for Android", SwingConstants.CENTER  )
        dartbeeText.font = Font("Sans Serif", Font.PLAIN, 25)
        panel.add(dartbeeText)*/

        this.scoreLabel = JLabel(START_SCORE.toString(), SwingConstants.CENTER)
        this.scoreLabel.font = Font("Sans Serif", Font.BOLD, 350)
        panel.add(scoreLabel)

        //A panel nested inside the main panel that has the stats
        val statsPanel = JPanel()
        statsPanel.border = BorderFactory.createEmptyBorder(20,20,20,20)
        createViewsInStatsPanel(statsPanel)
        panel.add(statsPanel)

    }

    fun startUpdatingTimer(dartGame: DartGame) {
        val timer = Timer(16, ActionListener {
            timeDarting.text = dartGame.timeSpent
        })
        timer.start()

    }

    private fun createPromoView(panel: JPanel){
        panel.layout = BoxLayout(panel,BoxLayout.Y_AXIS)


        //create top left icon
        val img = ImageIO.read(File("DartBee.png"))
        val scaled =   img.getScaledInstance(614,282, Image.SCALE_DEFAULT)
        val icon = ImageIcon(scaled)
        val dartBeeIcon = JLabel(icon, SwingConstants.CENTER)

        dartBeeIcon.alignmentX = Component.CENTER_ALIGNMENT
        panel.add(dartBeeIcon)

        val downloadText = JLabel("Download vandaag uw persoonlijke Darts Assistent, zoek naar DartBee in de Play Store!", SwingConstants.CENTER)
        downloadText.font = Font("Sans Serif", Font.PLAIN, 25)
        downloadText.alignmentX = Component.CENTER_ALIGNMENT
        downloadText.border = BorderFactory.createEmptyBorder(20,0,0,0)

        panel.add(downloadText )
    }


    private fun createViewsInStatsPanel(statsPanel:JPanel){
        //init layout
        statsPanel.layout = GridBagLayout()
        val c =  GridBagConstraints()
        c.fill = GridBagConstraints.BOTH

        fun GridBagConstraints.nextLine(){
            this.gridy++
            this.gridx = 0
            c.weighty = 1.0
            c.weightx = 1.0
            c.gridwidth = 1
        }

        fun createLabel( textStyle: Int = Font.PLAIN, textSize: Int = 40): JLabel {
            val label = JLabel("", SwingConstants.CENTER)
            label.font = Font("Sans Serif", textStyle, textSize)
            statsPanel.add(label,c)
            c.gridx++ // increment gridx for the next label
           return label
        }
        //top row, only one item
        c.gridwidth = 2
        scoreThrown = createLabel(Font.BOLD, 100)
        c.nextLine()
        c.nextLine()

        //midle row
        averageLabel = createLabel()
        dartsThrown = createLabel()
        c.nextLine()

        //botom row
        lastTurn = createLabel()
        timeDarting = createLabel()
    }


    fun start (){frame.isVisible = true}

    fun setText(score: Int) {
        val original = currentShownValue
        val toBeDone = (original - score)

        val absScoreDiff = Math.abs(toBeDone).toFloat()
        val uncappedFrames = (0.75 * absScoreDiff +30).toInt()
        val framesToDo = Math.min(uncappedFrames, 1000/16)// cap max animation frames


        //counts what frame we currently are
        var currentFrame = 0
        val timer = Timer(16, ActionListener {
            currentFrame++
            val timer = (it.source as Timer)
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
                currentShownValue = result
                this.scoreLabel.text =  NumberFormat.getNumberInstance(Locale.GERMANY).format(result)
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

    fun update(dartGame: DartGame) {
        setText( dartGame.scoreLeft)
        println("Setting score to ${dartGame.scoreLeft}")

        scoreThrown.text = "${dartGame.scoreThrown}"

        val avg = if (dartGame.avg.isNaN()) 0f else dartGame.avg
        averageLabel.text = "Gemiddelde score: "+ "%.2f".format(avg)

        dartsThrown.text = "Aantal darts gegooid: ${dartGame.dartsThrown}"

        lastTurn.text = "Laatste score: ${dartGame.lastScore}"


    }
}

fun Double.toThePowerOf(pow: Double) = Math.pow(this, pow)
