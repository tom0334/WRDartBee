/**
 * Created by Tom on 25-9-2018.
 */
object
Main {

    @JvmStatic fun main(args: Array<String>) {
        val displayer = DisplayScreen()
        displayer.start()

        val secondScreen = ControllerScreen(displayer)
        secondScreen.start()
    }


}
