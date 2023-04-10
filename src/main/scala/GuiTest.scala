import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Orientation.Horizontal
import scalafx.scene.Scene
import scalafx.scene.chart.{Axis, CategoryAxis, LineChart, NumberAxis, ValueAxis, XYChart}
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, SplitPane, TextArea}
import scalafx.scene.layout.{AnchorPane, BorderPane, FlowPane, HBox, Pane, StackPane, TilePane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, TextFlow}
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos.{BaselineCenter, BaselineRight}

import java.awt.Color

object guiTest extends JFXApp3 {
  /*val text: String = jsontest.requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum").toString
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
    val newWindow = new MenuItem("item 1")
    windowMenu.getItems.addAll(newWindow)

    // split panes inside the main window. PaneInsidePane is the main one that divides the view into two
    // vertically. The other are the bottom and top ones. Adding views means adding to the first and second rows.
    val firstRow = new SplitPane:
      dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal

    val secondRow = new SplitPane:
      // dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal
      dividerPositions = 0.7

    // secondRow.setDividerPositions(0.6)
    val thirdRow = new SplitPane:
      dividerPositions = 0.5
      orientation = scalafx.geometry.Orientation.Horizontal

    val paneInsidePane = new SplitPane:
      orientation = scalafx.geometry.Orientation.Vertical
      dividerPositions = 0.5
      items.addAll(firstRow, secondRow)

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
    val yAxis = NumberAxis("hinta", 80, 5000, 10)
    val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

    // val values = Seq((1.0, 1.0), (2.0, 3.0), (7.0, 4.0), (10.0, 5.0), (20.0, 6.0)).map(toChartData(_))
    val request = requestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1523229288&to=1680995696")
    val newValues = request.formatData(request.timedata).map(toChartData(_)).toSeq

    val dataSeries = new XYChart.Series[String, Number]:
      name = "hyvää dataa"
      data = newValues


    val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
      title = "Hieno chart"
      createSymbols = false
      // change styling with css?

    val stackPane = new StackPane()
    stackPane.getChildren.addAll(labelTwo)

    // to fill the entire panel with the chart
    val paneForChart = new StackPane()
    paneForChart.getChildren.addAll(chart)

    firstRow.getItems.addAll(stackPane, new HBox(coolLabel))
    secondRow.getItems.addAll(new HBox(rectangle), new VBox(secondMenuBar, paneForChart), new HBox(labelThree))
    secondRow.setDividerPositions(0.6, 0.7)
    // paneInsidePane.children += new Label("jihuu")
    root.setCenter(paneInsidePane)

    val scene = new Scene(parent = root) // Scene acts as a container for the scene graph
    // val scene = new Scene(new Label("text"))
    stage.scene = scene // Assigning the new scene as the current scene for the stage



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

