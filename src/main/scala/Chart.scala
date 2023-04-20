import scalafx.scene.chart.{CategoryAxis, NumberAxis, LineChart, XYChart}
import scalafx.collections.ObservableBuffer
import scalafx.Includes.observableList2ObservableBuffer

class PriceChart:
  def timeInUnixTimeStamp(time: String): Long = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(time).getTime

  // The api call is based on these times
  // By the default the last week
  private var startTime: Long = System.currentTimeMillis() / 1000 - 604800
  // By default the current day
  private var endTime: Long = System.currentTimeMillis() / 1000

  def changeStartTime(newTime: Long) = startTime = newTime
  def changeEndTime(newTime: Long) = endTime = newTime

  // By default the Chart is of the prices of the last week
  // def api = s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime"
  val request = requestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime")
  val xAxis = CategoryAxis("time")
  def maxPrice = request.timedata.maxBy(_._2)._2.toInt
  def minPrice = request.timedata.minBy(_._2)._2.toInt
  val yAxis = NumberAxis("price", minPrice, maxPrice, 1)
  val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  var newValues = request.formatData(request.timedata, endTime - startTime).map(toChartData(_)).toSeq

  val dataSeries = new XYChart.Series[String, Number]:
    name = "Price"
    data = newValues

  def changeChartData(newStart: Long, newEnd: Long) =
    val newApi = (s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$newStart&to=$newEnd")
    request.updateApi(newApi)
    // Make a new call and convert the data to chartData.
    newValues = request.formatData(request.timedata, newEnd - newStart).map(toChartData(_)).toSeq
    dataSeries.data = newValues
    // Update the charts to match the new data
    val numberOfDataPoints = newValues.length
    xAxis.tickMarkVisible = true
    println(newValues)
    //val maxPrice = newValues.maxBy(_.getYValue.asInstanceOf[scala.Int]).getYValue.asInstanceOf[scala.Double]
    //val minPrice = newValues.minBy(_.getYValue.asInstanceOf[scala.Int]).getYValue.asInstanceOf[scala.Double]
    yAxis.setUpperBound(maxPrice)
    yAxis.setLowerBound(minPrice)
    xAxis.setMaxWidth(numberOfDataPoints)

    xAxis.getCategories.clear() // clear previous categories
    for data <- dataSeries.data() do // add new categories
      xAxis.getCategories.add(data.getXValue)


  val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = "Ethereum price history (USD)"
    createSymbols = false



