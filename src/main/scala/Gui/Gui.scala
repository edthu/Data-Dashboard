package Gui

import Charts.*
import DataStorages.IntervalData
import Gui.DataDashboard.stage
import com.sun.javafx.scene.EventHandlerProperties
import javafx.event.EventHandler
import javafx.scene.control.{DateCell, DatePicker, Tooltip}
import javafx.util.Callback
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.event
import scalafx.event.ActionEvent
import scalafx.geometry.Orientation.{Horizontal, Vertical}
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.chart.*
import scalafx.scene.control.*
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Line, Rectangle}
import scalafx.scene.text.{Font, TextFlow}
import scalafx.scene.{Node, Scene}
import scalafx.stage.{Modality, Stage}

import java.awt.Color
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month}
import scala.io.Source
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
    // MenuBar. The MenuBar has options to add new ChartPanels and rows to the root.

    // The layout of the gui is explained more throughly in the project document.
    val root = new BorderPane()

    // Top menu
    val menuBar = new MenuBar()

    // Menu where you can add new chart to the different rows of the gui.
    val dashboardMenu = new Menu("Dashboard")
    val clearFirstRow = new MenuItem("Clear first row")
    val newRow = new MenuItem("Add a row")
    val newChartMenu = new Menu("Add a chart")
    val newStatWindowMenu = new Menu("Add a stat window")
    val newBarChartMenu = new Menu("Add a bar chart")
    dashboardMenu.getItems.addAll(newRow, clearFirstRow, newChartMenu, newStatWindowMenu, newBarChartMenu)


    // All menus to the top of gui. This is not modified anywhere.
    menuBar.getMenus.addAll(dashboardMenu)
    root.setTop(menuBar)


    // The initial layout of the gui. Initially there is only one row
    val firstRow = new ChartSplitPane:
      orientation = Horizontal

    val stackOfRows = new StackOfSplitPanes:
      orientation = Vertical
      items.addOne(firstRow)

    // This is where everything will be placed
    root.setCenter(stackOfRows)

    clearFirstRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(event: javafx.event.ActionEvent) =
        firstRow.items.clear()
    })


    // New rows
    def aRow = new ChartSplitPane:
      orientation = Horizontal


    // Creates a chart object and place it into the panels that are going to be added to the gui
    def createLineChartPane(): ChartPane =

      val chartObject = new PriceChart()
      val basePanel = new ChartPane(chartObject.chart, chartObject)
      val chartMenu = new MenuBar()
      val optionsMenu = new Menu("Options")
      chartMenu.getMenus.add(optionsMenu)
      basePanel.setTop(chartMenu)


      // Takes a new interval from the user and gets the appropriate data
      val changeInterval = new MenuItem("Change interval")
      val toggleTooltips = new MenuItem("Toggle tooltips")
      optionsMenu.getItems.addAll(changeInterval, toggleTooltips)

      changeInterval.setOnAction(new EventHandler[javafx.event.ActionEvent]() {

        // For that we create a new object that is shared between the new menu in a different window and this one
        def handle(actionEvent: javafx.event.ActionEvent) =
          // Create a new popup window where the user can choose a new interval to be displayed in the chart
          // The values are stored in the only instance of IntervalData
          IntervalPopup().display()

          // and accessed here to update the data
          val startTime: Long = IntervalData.getDateObject.getDates._1
          val endTime: Long = IntervalData.getDateObject.getDates._2

          chartObject.changeChartData(startTime, endTime)
      })

      // Turn tooltips on or of
      toggleTooltips.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
        def handle(actionEvent: javafx.event.ActionEvent) =
          if !chartObject.chart.createSymbols.apply() then
            chartObject.addTooltips()
          else
            chartObject.chart.createSymbols = false
      })

      basePanel


    def createStatWindowPane(): StatWindowPane =
      val statWindowPane = new StackPane()
      val statWindow = new StatWindow()
      val basePanel = new StatWindowPane(statWindow)
      val statLabel = new Label(statWindow.text)
      statWindowPane.getChildren.addAll(statLabel)
      val menuBar = new MenuBar()
      val optionMenu = new Menu("Options")
      menuBar.getMenus.add(optionMenu)
      val changeInterval = new MenuItem("Change interval")
      optionMenu.getItems.add(changeInterval)
      basePanel.setCenter(statWindowPane)
      basePanel.setTop(menuBar)

      changeInterval.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
        def handle(actionEvent: javafx.event.ActionEvent) =
          // Create a new popup window where the user can choose a new interval to be displayed in the chart
          // The values are stored in the only instance of IntervalData
          IntervalPopup().display()

          // and accessed here to update the data
          val startTime: Long = IntervalData.getDateObject.getDates._1
          val endTime: Long = IntervalData.getDateObject.getDates._2

          statWindow.updateData(startTime, endTime)
          statLabel.text = statWindow.text
      })

      basePanel


    def createBarChartPane(): BarChartPane =
      val barChart = new MarketCapBarChart()
      val basePanel = new BarChartPane(barChart.chart, barChart)
      val menuBar = new MenuBar()
      val optionsMenu = new Menu("Options")
      menuBar.getMenus.add(optionsMenu)
      val changeDate = new MenuItem("Change Date")
      val showTooltips = new MenuItem("Show tooltips")
      optionsMenu.getItems.addAll(changeDate, showTooltips)
      basePanel.setTop(menuBar)
      basePanel.setCenter(barChart.chart)


      changeDate.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
        def handle(actionEvent: javafx.event.ActionEvent) =
          SingleDatePopUp().display()

          val startTime: Long = IntervalData.getDateObject.getDates._1

          barChart.updateData(startTime)
      })

      var tooltipsShown = false

      // Turn tooltips on if they are off
      showTooltips.setOnAction(new EventHandler[javafx.event.ActionEvent](){
        def handle(actionEvent: javafx.event.ActionEvent) =
          if !tooltipsShown then
            barChart.addTooltips()
      })

      basePanel



    // Adds a button to a given menu. This button adds a given chart to a given row.
    // This function is called for each chart type each time a new row is added
    def addButton(row: ChartSplitPane, rowNum: Int, chartFunction: () => ChartPanel, menu: Menu) =
      val menuItem = new MenuItem(s"Add to row $rowNum")
      menuItem.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
        def handle(actionEvent: javafx.event.ActionEvent) =
          val chart = chartFunction.apply()
          row.items.addAll(chart)
      })
      menu.getItems.addAll(menuItem)

    // Buttons that add charts to the first row
    addButton(firstRow, 1, createLineChartPane, newChartMenu)
    addButton(firstRow, 1, createStatWindowPane, newStatWindowMenu)
    addButton(firstRow, 1, createBarChartPane, newBarChartMenu)

    // Used when a row is deleted. Removes the option in the menus to add charts to the deleted row
    def deleteButton(rowNum: Int) =
      newChartMenu.items.remove(rowNum - 1)
      newStatWindowMenu.items.remove(rowNum - 1)
      newBarChartMenu.items.remove(rowNum - 1)

    // Adds the option to delete a row if there are more than two of them
    // When only one row remains removes that option
    def deleteRowButtonCheck() =
      if stackOfRows.items.length == 1 then
        val deleteRow = new MenuItem("Delete a row")
        dashboardMenu.getItems.addAll(deleteRow)
        deleteRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
          def handle(actionEvent: javafx.event.ActionEvent) =
            deleteButton(stackOfRows.items.length)
            stackOfRows.items.remove(stackOfRows.items.length - 1)
            if stackOfRows.items.length == 1 then
              dashboardMenu.items.remove(dashboardMenu.items.length - 1)
        })



    // Creates a new row. Adds a new menuItem for the menus used to add new charts
    newRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) =
        deleteRowButtonCheck()
        // A new horizontal SplitPane
        val newRowToBeAdded = aRow
        stackOfRows.items.addAll(newRowToBeAdded)
        val indexOfTheNewRow = stackOfRows.items.length
        // Add a new MenuItem to the other menus where you add other types of charts
        addButton(newRowToBeAdded, indexOfTheNewRow, createLineChartPane, newChartMenu)
        addButton(newRowToBeAdded, indexOfTheNewRow, createStatWindowPane, newStatWindowMenu)
        addButton(newRowToBeAdded, indexOfTheNewRow, createBarChartPane, newBarChartMenu)
    })


    val scene = new Scene(parent = root)
    stage.scene = scene
}




/*
    saveMenuItem.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(event: javafx.event.ActionEvent) =

        // SaveNamingPopUp().display()
        // savefile: "name" -> ujson.Arr[String, Value] Value is an array (statWindow only takes one date)

        // ujson.Arr[ujson.Arr[ujson.Obj]]
        var windowArray = ujson.Arr()

        //Files.write(Paths.get("/src/main/scala/savefiles.json"), "uhahahaha".getBytes(StandardCharsets.UTF_8))
        //new PrintWriter("savefiles.json") { write("file contents"); close() }

        var rowNumber = 0

        scala.Predef.println(stackOfRows.getItems)
        scala.Predef.println(stackOfRows.items)
        for
          row <- stackOfRows.getItems
        do
          rowNumber += 1
          var rowArray = ujson.Arr()
          for pane <- row.getItems do
            var jsonString = ""
            val separator = if jsonString.isBlank then "" else ", "
            // Adds the type of Pane and its current time values into the json Array
            pane match
              case c: Charts.ChartPane =>
                val startTime = c.getChartObject.getStartTime
                val endTime = c.getChartObject.getEndTime
                val saveObject = ujson.Obj(("type", "Charts.ChartPane"), ("row", rowNumber), ("start_time", startTime), ("end_time", endTime))
                rowArray = ujson.Arr(rowArray.value :+ saveObject)
                // jsonString = jsonString + s"""$separator[Charts.ChartPane, ${startTime.toString}, ${endTime.toString}]"""
              case s: Charts.StatWindowPane =>
                val startTime = s.getStatWindow.startDate
                val endTime = s.getStatWindow.endDate
                val saveObject = ujson.Obj(("type", "Charts.StatWindowPane"), ("row", rowNumber), ("start_time", startTime), ("end_time", endTime))
                rowArray = ujson.Arr(rowArray.value :+ saveObject)
                //jsonString = jsonString + s"""$separator[Charts.StatWindowPane, $startTime, $endTime]"""
              case b: Charts.BarChartPane =>
                val date = b.getChartObject.getDate
                val saveObject = ujson.Obj(("type", "Charts.BarChartPane"), ("row", rowNumber), ("date", date))
                rowArray = ujson.Arr(rowArray.value :+ saveObject)
                // jsonString = jsonString + s"""$separator["Charts.BarChartPane", $date]"""
          windowArray = ujson.Arr(windowArray.value :+ rowArray)

    })
    */