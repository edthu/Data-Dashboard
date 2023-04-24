import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.control.Tooltip
import scalafx.collections.CollectionIncludes.observableList2ObservableBuffer

class MarketCapBarChart:
  // Initially current day
  private var date = TimeConversions.unixTimestampToddMMyyyyWithDash(System.currentTimeMillis())

  private val bitcoinRequest = requestData(s"https://api.coingecko.com/api/v3/coins/bitcoin/history?date=$date&localization=false")
  private val ethereumRequest = requestData(s"https://api.coingecko.com/api/v3/coins/ethereum/history?date=$date&localization=false")


  private def bitcoinMarketCap = bitcoinRequest.json.obj("market_data")("market_cap")("usd").num.toLong / 1000000
  private def ethereumMarketCap = ethereumRequest.json.obj("market_data")("market_cap")("usd").num.toLong / 1000000

  private def marketCapPercentage = f"${(ethereumMarketCap.toDouble / bitcoinMarketCap.toDouble) * 100}%1.2f"

  private val xAxis = new CategoryAxis()
  // Max is the higher market cap
  private val yAxis = new NumberAxis("Millions (USD)", 0, Math.max(bitcoinMarketCap, ethereumMarketCap), 1)
  yAxis.setAutoRanging(true)

  // Values for the chart
  def toChartData(name: String, mCap: Long) = XYChart.Data[String, Number](name, mCap)
  private def bitcoin = toChartData("Bitcoin", bitcoinMarketCap)
  private def ethereum = toChartData("Ethereum", ethereumMarketCap)

  private var values = Seq(bitcoin, ethereum)

  val dataSeries = new XYChart.Series[String, Number]:
    name = "Market Capitalisation"
    data = values

  val chart = new BarChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = s"Ethereum market capitalisation against Bitcoin"


  def updateData(newDate: Long) =
    date = TimeConversions.unixTimestampToddMMyyyyWithDash(newDate)
    bitcoinRequest.updateApi(s"https://api.coingecko.com/api/v3/coins/bitcoin/history?date=$date&localization=false")
    ethereumRequest.updateApi(s"https://api.coingecko.com/api/v3/coins/ethereum/history?date=$date&localization=false")
    dataSeries.data = Seq(bitcoin, ethereum)
    yAxis.setUpperBound(Math.max(bitcoinMarketCap, ethereumMarketCap))
    addTooltips()


  def addTooltips() =
    for
      s <- chart.getData
      d <- s.getData
    do
      val ethPercentage = if d.getXValue == "Ethereum" then s" $marketCapPercentage% of Bitcoin market cap" else ""
      javafx.scene.control.Tooltip.install(d.getNode, new Tooltip(d.getXValue + "\n" + "Market Cap: " + d.getYValue + " million" + ethPercentage))


      //d.setNode(node)
      d.getNode.setOnMouseEntered(event => d.getNode.getStyleClass.add("onHover"))

      //Removing class on exit
      d.getNode.setOnMouseExited(event => d.getNode.getStyleClass.remove("onHover"))