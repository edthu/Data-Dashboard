import com.sun.javafx.scene.EventHandlerProperties
import javafx.event.EventHandler
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Orientation.Horizontal
import scalafx.scene.Scene
import scalafx.scene.chart.{Axis, CategoryAxis, LineChart, NumberAxis, ValueAxis, XYChart}
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, SplitPane, TextArea}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, TextFlow}
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.input.MouseEvent

import java.awt.Color
import scala.language.postfixOps

object DataDashboard extends JFXApp3 {


  val paneWidth = 800
  val paneHeight = 550

  def start():Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "DataDashboard Test"
      width = paneWidth
      height = paneHeight


    // The root node of the GUI is a BorderPane. The top of this BorderPane is a
    // MenuBar. The MenuBar has options to add new Nodes to the root.
    val root = new BorderPane()

    // Top menu
    val menuBar = new MenuBar()
    root.setCenter(new SplitPane())

    val dashboardMenu = new Menu("Dashboard")
    val newRow = new MenuItem("Add a row")
    val newChart = new MenuItem("Add a chart")
    val newStatWindow = new MenuItem("Add a stat window")
    dashboardMenu.getItems.addAll(newRow, newChart, newStatWindow)
    // newChart need another menu where the user can choose the row the chart is going to be added to


    val optionMenu = new Menu("Options")

    val saveMenu = new Menu("Save/Load")
    val saveMenuItem = new MenuItem("Save the current layout")
    val loadMenuItem = new MenuItem("Load a layout")
    saveMenu.getItems.addAll(saveMenuItem, loadMenuItem)

    // val currency = new Menu("€")

    menuBar.getMenus.addAll(dashboardMenu, optionMenu, saveMenu)
    root.setTop(menuBar)

    // ChartView


    val scene = new Scene(parent = root)
    stage.scene = scene
    scene.fill = Blue
}

