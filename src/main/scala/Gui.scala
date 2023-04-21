import com.sun.javafx.scene.EventHandlerProperties
import javafx.event.EventHandler
import javafx.scene.control
import javafx.scene.control.DatePicker
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Orientation.{Horizontal, Vertical}
import scalafx.scene.{Node, Scene}
import scalafx.scene.chart.{Axis, CategoryAxis, LineChart, NumberAxis, ValueAxis, XYChart}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem, SplitPane, TextArea}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, TextFlow}
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.input.MouseEvent
import scalafx.stage.{Modality, Stage}
import java.time.{LocalDate, Month}
import javafx.scene.control.{DateCell, DatePicker}
import javafx.util.Callback
import java.time.format.DateTimeFormatter
import java.awt.Color
import java.time.LocalDate
import scala.language.postfixOps

object DataDashboard extends JFXApp3 {


  val paneWidth = 800
  val paneHeight = 550

  def start():Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "DataDashboard"
      width = paneWidth
      height = paneHeight


    // The root node of the GUI is a BorderPane. The top of this BorderPane is a
    // MenuBar. The MenuBar has options to add new Nodes to the root.
    val root = new BorderPane()

    // Top menu
    val menuBar = new MenuBar()

    // Menu where you can add new chart to the different rows of the gui.
    val dashboardMenu = new Menu("Dashboard")
    val newRow = new MenuItem("Add a row")
    val newChartMenu = new Menu("Add a chart")
    val newStatWindowMenu = new Menu("Add a stat window")
    dashboardMenu.getItems.addAll(newRow, newChartMenu, newStatWindowMenu)
    // newChartMenu need another menu where the user can choose the row the chart is going to be added to



    val optionMenu = new Menu("Options")

    val saveMenu = new Menu("Save/Load")
    val saveMenuItem = new MenuItem("Save the current layout")
    val loadMenuItem = new MenuItem("Load a layout")
    saveMenu.getItems.addAll(saveMenuItem, loadMenuItem)

    // Add this if there is enough time
    // val currency = new Menu("€")

    // All menus to the top of gui. This is not modified anywhere.
    menuBar.getMenus.addAll(dashboardMenu, optionMenu, saveMenu)
    root.setTop(menuBar)

    // ChartView


    // Different windows and rows

    // The initial layout of the gui. Initially there is only one row
    val firstRow = new SplitPane:
      orientation = Horizontal

    val stackOfRows = new SplitPane:
      orientation = Vertical
      items.addOne(firstRow)

    // This is where everything will be placed
    root.setCenter(stackOfRows)

    // New rows
    def aRow = new SplitPane:
      orientation = Horizontal



    // Refactor to so that not every button needs its own funtion thing? Match their names?
    // foreach of the things. Handles for everything but

    // Creates a chart object and place it into the panels that are going to be added to the gui
    def createLineChartPane(): Node =
      val basePanel = new BorderPane()
      val chartObject = new PriceChart()
      val chartMenu = new MenuBar()
      val optionsMenu = new Menu("Options")
      chartMenu.getMenus.add(optionsMenu)
      basePanel.setTop(chartMenu)
      basePanel.setCenter(chartObject.chart)

      // Takes a new interval from the user and gets the appropriate data
      val changeInterval = new MenuItem("Change interval")
      optionsMenu.getItems.add(changeInterval)

      changeInterval.setOnAction(new EventHandler[javafx.event.ActionEvent]() {

        // For that we create a new object that is shared between the new menu in a different window and this one
        def handle(actionEvent: javafx.event.ActionEvent) =
          // Create a new popup window where the user can choose a new interval to be displayed in the chart
          IntervalPopup(chartObject).display()
          //
          val startTime: Long = IntervalData.getDateObject.getDates._1
          val endTime: Long = IntervalData.getDateObject.getDates._2
          //chartObject.changeStartTime(IntervalData.getDateObject.getDates._1)
          //chartObject.changeEndTime(IntervalData.getDateObject.getDates._2)

          chartObject.changeChartData(startTime, endTime)
      })

      basePanel





    // Adds a menuItem to a menu and makes it so that pressing that menu item adds a given chart to a given row in the
    // GUI.
    
    // always adds the same chart to the same row. Need a way to create a new instance of this chart to add to the row.
    // Adding the same istance just moves the chart from the initial location to the new window. A single row can only
    // have one instance at all times.
    def addButton(row: SplitPane, rowNum: Int, chartFunction: () => Node, menu: Menu) =
      val menuItem = new MenuItem(s"Add to row $rowNum")
      menuItem.setOnAction(new EventHandler[javafx.event.ActionEvent](){
        def handle(actionEvent: javafx.event.ActionEvent) =
          val chart: Node = chartFunction.apply()
          row.getItems.addAll(chart)
      })
      menu.getItems.addAll(menuItem)

    // Add buttons to add charts to the first row
    addButton(firstRow, 1, createLineChartPane, newChartMenu)

    // Used when a row is deleted. Removes the option in the menus to add things to the deleted row
    def deleteButton(rowNum: Int) =
      newChartMenu.items.remove(rowNum - 1)
      newStatWindowMenu.items.remove(rowNum - 1)

    def deleteRowButtonCheck() =
      if stackOfRows.items.length == 1 then
        val deleteRow = new MenuItem("Delete a row")
        dashboardMenu.getItems.addAll(deleteRow)
        deleteRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
          def handle(actionEvent: javafx.event.ActionEvent) =
            stackOfRows.getItems.remove(stackOfRows.items.length - 1)
            if stackOfRows.items.length == 1 then
              dashboardMenu.items.remove(dashboardMenu.items.length - 1)
        })



    // Actions for menuItems
    newRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) =
        deleteRowButtonCheck()
        // A new horizontal SplitPane
        val newRowToBeAdded = aRow
        stackOfRows.getItems.addAll(newRowToBeAdded)
        val indexOfTheNewRow = stackOfRows.getItems.length
        // Add a new MenuItem to the other menus where you add other types of charts
        addButton(newRowToBeAdded, indexOfTheNewRow, createLineChartPane, newChartMenu)
    })


    // need to specify on which row the window is going to be added to
    newStatWindowMenu.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) = ???
    })


    val scene = new Scene(parent = root)
    stage.scene = scene
}

