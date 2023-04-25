package TestClasses

import Charts.StatWindow
import TestClasses.GuiTest.stage
import TimeAndData.requestData
import com.sun.javafx.scene.EventHandlerProperties
import javafx.event.EventHandler
import javafx.scene.control.{DateCell, DatePicker}
import javafx.util.Callback
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Orientation.Horizontal
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.Scene
import scalafx.scene.chart.*
import scalafx.scene.control.*
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, TextFlow}

import java.awt.Color
import java.time.{LocalDate, Month}
import scala.language.postfixOps

object GuiTest extends JFXApp3 {
  /*val text: String = jsontest.TimeAndData.requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum").toString
  val someTextOnTheStage = new Label("tee")
  someTextOnTheStage.textFill = Blue
  someTextOnTheStage.font = Font.font(36) */

  val paneWidth = 800
  val paneHeight = 550

  def start():Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "DataDashboard Test"
      width = paneWidth
      height = paneHeight

    /*
    Create root gui component, add it to a Scene
    and set the current window scene.
    */

    val apiData = requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")

    val root = new BorderPane()

    val menuBar = new MenuBar()
    val windowMenu = new Menu("Window Options")
    val loadMenu = new Menu("Save/Load")
    menuBar.getMenus.addAll(windowMenu, loadMenu)
    root.setTop(menuBar)
    val secondMenuBar = new MenuBar()
    secondMenuBar.getMenus.addAll(new Menu("jee"), new Menu(" huhuh"))

    // button for adding another pane into the view (into the menus)
    // TODO: make it do something
    val newWindow = new MenuItem("päivitä chart")
    val newRow = new MenuItem(text = "add a row")


    windowMenu.getItems.addAll(newWindow, newRow)

    // split panes inside the main window. PaneInsidePane is the main one that divides the view into two
    // vertically. The other are the bottom and top ones. Adding views means adding to the first and second rows.
    val myDatePicker = new DatePicker // This DatePicker is shown to user
    val minDate = LocalDate.of(2015, 8, 7) // Ethereum network launch date

    val dayCellFactory: Callback[DatePicker, DateCell] = (datePicker: DatePicker) =>
      new DateCell {
        override def updateItem(item: LocalDate, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          if (item.isBefore(minDate)) { // Disable all dates earlier than the required date
            setDisable(true)
            setStyle("-fx-background-color: #ffc0cb;") //To set background on a different color
          }
        }
      }
    //Finally, we just need to update our DatePicker cell factory as follow:
    myDatePicker.setDayCellFactory(dayCellFactory)
    myDatePicker.setValue(LocalDate.now)


    val firstRow = new SplitPane:
      dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal
      items.add(myDatePicker)

    val secondRow = new SplitPane:
      // dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal
      // dividerPositions = 0.7

    // secondRow.setDividerPositions(0.6)
    val thirdRow = new SplitPane:
      dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal

    val paneInsidePane = new SplitPane:
      orientation = scalafx.geometry.Orientation.Vertical
      dividerPositions = 0.5
      items.addAll(firstRow)

    val statStuff = new StatWindow()
    val statLabel = new Label:
      text = statStuff.text

    statLabel.font = new Font("DejaVu Sans", 18)
    statLabel.setStyle("-fx-text-fill: #ea43e8; -fx-background-color: #39ec24;")
    // statLabel.setStyle("-fx-background-color: #39ec24")
    statLabel.hgrow = Always
    val onceAgainStackPane = new StackPane()
    onceAgainStackPane.children += statLabel
    firstRow.getItems.addAll(onceAgainStackPane)
    /*
    paneInsidePane.children += firstRow
    paneInsidePane.children += secondRow
    */
    val rectangle = new Rectangle:
      x = 100
      y = 100
      width = 50
      height = 50
      fill = Blue //scalafx.scene.paint.Color

    // add stuff to the topWindow as a test.
    val coolLabel = new Label(text = "jui")
    val labelTwo = new Label:
      text = "tämän kuuluisi olla keskellä äääääääääääääääääääääääääääääääääää \nja monella rivillä tekstiä ja silleen \nlaitetaan vielä kolmannellekin"
      alignment = BaselineCenter

    val labelThree = new Label  (text = "apiData.json")
    // firstRow.getItems.addAll(coolLabel, labelTwo, labelThree)

    // everything needed to create a linechart
    // TODO: make it stretch over the entire available space
    // and fit the data to the chart
    val xAxis = CategoryAxis("aika") //("aika", 1, 100, 3)
    // xAxis.autoRanging = false
    var yAxis = NumberAxis("hinta", 80, 5000, 1)
    val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

    // val values = Seq((1.0, 1.0), (2.0, 3.0), (7.0, 4.0), (10.0, 5.0), (20.0, 6.0)).map(toChartData(_))
    val request = requestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1523229288&to=1680995696")
    var newValues = request.formatData(request.timedata, 1680995696 - 1523229288).map(toChartData(_)).toSeq

    val dataSeries = new XYChart.Series[String, Number]:
      name = "hyvää dataa"
      data = newValues

    val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
      title = "Hieno chart"
      createSymbols = false
      // change styling with css?

    val stackPane = new StackPane()
    stackPane.getChildren.addAll(chart)

    // to fill the entire panel with the chart
    val paneForChart = new BorderPane()
    paneForChart.setTop(secondMenuBar)
    paneForChart.setCenter(stackPane)
    // 2016 1460582496   2018 1586387688
    val reqq = requestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1460582496&to=1617923688")

    newWindow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) = {
        //paneInsidePane.items.addAll(secondRow)
        //firstRow.getItems.addAll(new HBox(new Label(text = "cool")))
        newValues = reqq.formatData(reqq.timedata, 1617923688 - 1586387688).map(toChartData(_)).toSeq
        // yAxis = NumberAxis("hinta", 80, 3000, 10)

        dataSeries.data = newValues
        println(newValues)
        yAxis.setUpperBound(5000)
        xAxis.setMaxWidth(100)

        xAxis.getCategories.clear() // clear previous categories
        for (data <- dataSeries.data()) { // add new categories
          xAxis.getCategories.add(data.XValue.toString)
          
          
          
          // chartObject
        }
        // dataSeries.setData(dataSeries)items
      }
    })

    newRow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) = {
        paneInsidePane.items.addAll(secondRow)
        val items = paneInsidePane.getItems.toSeq
        secondRow.items.addAll(new Label(items.mkString))
      }
    })

    secondRow.getItems.addAll(new HBox(rectangle), paneForChart, new HBox(labelThree))
    secondRow.setDividerPositions(0.6, 0.7)
    // paneInsidePane.children += new Label("jihuu")
    root.setCenter(paneInsidePane)

    val scene = new Scene(parent = root) // Scene acts as a container for the scene graph
    // val scene = new Scene(new Label("text"))
    stage.scene = scene // Assigning the new scene as the current scene for the stage
    scene.fill = Blue




    /*
    firstRow.children += rectangle
    secondRow.children += new Label(text = "jee")
    for i <- 0 until 30 do
      firstRow.children += new Label(text = "späm")
      */
    // Seems like there is a limeted amount of times you can add stuff to a single pane
    // 2 times was max for this loop. foreach does not work but for loop does??
    // (0 until 2).foreach(root.children += new Label(text = "späm"))
    /*

    root.children += new Label(text = "jee")
    // but the same thing can be added later. (they all stack on top of each other)

    root.children += new Label(text = "späm")
    root.children += new Label(text = "späm")
    */
    /*
    when adding stuff to StackPane with root.getChildren.addAll(..) they all seem to stack on top of each other
    this is intentional since the stack is in z-coordinate format (the children stack on top of each other instead
    of stackking on top of each other like i thought they would.
    */
    // root.getChildren.addAll(new Label(text = "hukka"), new Label(text = "jipididuu"))
    // for some reason it is not possible to add a rectangle after adding the labels

  //root.children += new Label(text = "Jee")

}

