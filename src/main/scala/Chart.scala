import scalafx.scene.chart.{CategoryAxis, NumberAxis, LineChart, XYChart}
import scalafx.collections.ObservableBuffer

class PriceChart(val currency: String, private var startDate: String, private var endDate: String):
  def timeInUnixTimeStamp(time: String) = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(time).getTime

  val request = requestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1523229288&to=1680995696")
  val xAxis = CategoryAxis("time")
  val yAxis = NumberAxis("price", 80, 5000, 1)
  val toChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  var newValues = request.formatData(request.timedata).map(toChartData(_)).toSeq

  val dataSeries = new XYChart.Series[String, Number]:
    name = "hyvää dataa"
    data = newValues

  def changeChartData(api: String) =
    request.updateApi(api)
    var newValues = request.formatData(request.timedata).map(toChartData(_)).toSeq
    dataSeries.data = newValues

  val chart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = "Hieno chart"
    createSymbols = false



