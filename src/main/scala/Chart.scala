import scalafx.Includes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{CategoryAxis, LineChart, NumberAxis, XYChart}
import scalafx.scene.control.Tooltip

import scala.language.postfixOps

class PriceChart:

  // The api call is based on these times
  // By the default the last week
  private var startTime: Long = System.currentTimeMillis() / 1000 - 604800
  // By default the current day
  private var endTime: Long = System.currentTimeMillis() / 1000

  def changeStartTime(newTime: Long): Unit = startTime = newTime
  def changeEndTime(newTime: Long): Unit = endTime = newTime

  // By default the Chart is of the prices of the last week
  val request = requestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime")
  val xAxis = CategoryAxis("time")

  def maxPrice: Int = request.timedata.maxBy(_._2)._2.toInt
  def minPrice: Int = request.timedata.minBy(_._2)._2.toInt
  val yAxis = NumberAxis("Price", minPrice, maxPrice, 1)
  val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  var newValues = request.formatData(request.timedata, endTime - startTime).map(toChartData(_)).toSeq

  val dataSeries = new XYChart.Series[String, Number]:
    name = "Price"
    data = newValues

  // Add tooltips to all the values in the chart



  def changeChartData(newStart: Long, newEnd: Long): Unit =
    val newApi = (s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$newStart&to=$newEnd")
    request.updateApi(newApi)
    // Make a new call and convert the data to chartData.
    newValues = request.formatData(request.timedata, newEnd - newStart).map(toChartData(_)).toSeq
    dataSeries.data = newValues
    // Update the charts to match the new data
    def numberOfDataPoints = newValues.length
    if maxPrice - minPrice > 2000 then
      val tickUnit = (maxPrice - minPrice) / 10
      yAxis.tickUnit = Math.ceil(tickUnit / (numberOfDataPoints / 1000))
    yAxis.setUpperBound(maxPrice)
    yAxis.setLowerBound(minPrice)
    xAxis.setMaxWidth(numberOfDataPoints)
    addTooltips()

  val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = "Ethereum price history (USD)"
    createSymbols = false
    animated = false

  def addTooltips() =
    chart.createSymbols = true
    for
      s <- chart.getData
      d <- s.getData
    do
      javafx.scene.control.Tooltip.install(d.getNode, new Tooltip(d.getXValue + "\n" + "Price: " + d.getYValue))

      //d.setNode(node)
      d.getNode.setOnMouseEntered(event => d.getNode.getStyleClass.add("onHover"))

      //Removing class on exit
      d.getNode.setOnMouseExited(event => d.getNode.getStyleClass.remove("onHover"))



