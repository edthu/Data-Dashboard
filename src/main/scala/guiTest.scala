import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Font

import java.awt.Color

object HelloStageDemo extends JFXApp3 {
  /*val text: String = jsontest.requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum").toString
  val someTextOnTheStage = new Label("tee")
  someTextOnTheStage.textFill = Blue
  someTextOnTheStage.font = Font.font(36) */

  def start():Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Hello Stage"
      width = 800
      height = 550

    /*
    Create root gui component, add it to a Scene
    and set the current window scene.
    */

    val root = Pane() // Simple pane component
    val scene = new Scene(parent = root) // Scene acts as a container for the scene graph
    // val scene = new Scene(new Label("text"))
    stage.scene = scene // Assigning the new scene as the current scene for the stage

    val rectangle = new Rectangle:
      x = 100
      y = 100
      width = 50
      height = 50
      fill = Blue //scalafx.scene.paint.Color

    root.children += rectangle
    //root.children += someTextOnTheStage
    (0 until 2).foreach(root.children += new Label(text = "späm"))
    //root.children += new Label(text = "Jee")

}

