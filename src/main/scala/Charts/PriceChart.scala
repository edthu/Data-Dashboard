package Charts

import TimeAndData.RequestData
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

  def getStartTime: Long = startTime
  def getEndTime: Long = endTime


  // By default the Chart is of the prices of the last week
  val request = RequestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime")
  def maxPrice: Int = request.timedata.maxBy(_._2)._2.toInt
  def minPrice: Int = request.timedata.minBy(_._2)._2.toInt

  private val yAxis = NumberAxis("Price", minPrice, maxPrice, 1)
  private val xAxis = CategoryAxis("time")

  private val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)


  private var newValues = request.formatData(request.timedata, endTime - startTime).map(toChartData(_)).toSeq


  val dataSeries = new XYChart.Series[String, Number]:
    name = "Price"
    data = newValues


  // dataSeries is converted to a Observable buffer so that any changes made to it get automatically updated into the
  // chart
  val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = "Ethereum price history (USD)"
    createSymbols = false
    animated = false

  // Changes the start and end dates. Automatically updated to the chart through the dataSeries variable
  def changeChartData(newStart: Long, newEnd: Long): Unit =
    val newApi = (s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$newStart&to=$newEnd")
    request.updateApi(newApi)
    // Make a new call and convert the data to chartData.
    newValues = request.formatData(request.timedata, newEnd - newStart).map(toChartData(_)).toSeq
    dataSeries.data = newValues
    // Update the size of the chart to match new values
    def numberOfDataPoints = newValues.length
    // Change the tickUnit (visible numbers on the y-axis)
    if maxPrice - minPrice > 2000 then
      val tickUnit = (maxPrice - minPrice) / 10
      val tickUnitScale = if numberOfDataPoints < 1000 then 1000 else numberOfDataPoints
      yAxis.tickUnit = Math.ceil(tickUnit / (tickUnitScale / 1000))
    // Chart height & width
    yAxis.setUpperBound(maxPrice)
    yAxis.setLowerBound(minPrice)
    xAxis.setMaxWidth(numberOfDataPoints)


  // Tooltips that show the time of the datapoint and the price of Ethereum at that time
  def addTooltips(): Unit =
    chart.createSymbols = true
    for
      s <- chart.getData
      d <- s.getData
    do
      javafx.scene.control.Tooltip.install(d.getNode, new Tooltip(d.getXValue + "\n" + "Price: $" + d.getYValue))



