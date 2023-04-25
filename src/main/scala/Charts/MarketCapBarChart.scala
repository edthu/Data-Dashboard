package Charts

import TimeAndData.{TimeConversions, requestData}
import scalafx.collections.CollectionIncludes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.control.Tooltip


class MarketCapBarChart:
  // Initially current day
  private var date = TimeConversions.unixTimestampToddMMyyyyWithDash(System.currentTimeMillis())
  
  def getDate = date

  // Calls that return the market cap of Bitcoin and Ethereum at the given date
  private val bitcoinRequest = requestData(s"https://api.coingecko.com/api/v3/coins/bitcoin/history?date=$date&localization=false")
  private val ethereumRequest = requestData(s"https://api.coingecko.com/api/v3/coins/ethereum/history?date=$date&localization=false")
  
  
  private def bitcoinMarketCap = bitcoinRequest.json.obj("market_data")("market_cap")("usd").num.toLong / 1000000
  private def ethereumMarketCap = ethereumRequest.json.obj("market_data")("market_cap")("usd").num.toLong / 1000000

  private def marketCapPercentage = f"${(ethereumMarketCap.toDouble / bitcoinMarketCap.toDouble) * 100}%1.2f"

  private val xAxis = new CategoryAxis()
  // Max is the higher market cap
  private val yAxis = new NumberAxis("Millions (USD)", 0, Math.max(bitcoinMarketCap, ethereumMarketCap), 1)
  yAxis.setAutoRanging(true)

  // Values for the chart. Changes the data from api to BarChart compatible form (name as string for the x-axis and price 
  // as as javafx Number for the y-axis.
  def toChartData(name: String, mCap: Long) = XYChart.Data[String, Number](name, mCap)
  private def bitcoin = toChartData("Bitcoin", bitcoinMarketCap)
  private def ethereum = toChartData("Ethereum", ethereumMarketCap)
  
  private var values = Seq(bitcoin, ethereum)
  
  val dataSeries = new XYChart.Series[String, Number]:
    name = "Market Capitalisation"
    data = values

  // dataSeries is converted to a Observable buffer so that any changes made to it get automatically updated into the
  // chart
  val chart = new BarChart[String, Number](xAxis, yAxis, ObservableBuffer(dataSeries)):
    title = s"Ethereum market capitalisation against Bitcoin"

  // Update the values of the chart
  def updateData(newDate: Long) =
    date = TimeConversions.unixTimestampToddMMyyyyWithDash(newDate)
    bitcoinRequest.updateApi(s"https://api.coingecko.com/api/v3/coins/bitcoin/history?date=$date&localization=false")
    ethereumRequest.updateApi(s"https://api.coingecko.com/api/v3/coins/ethereum/history?date=$date&localization=false")
    // Updates the chart
    dataSeries.data = Seq(bitcoin, ethereum)
    // Height of the chart    
    yAxis.setUpperBound(Math.max(bitcoinMarketCap, ethereumMarketCap))
    addTooltips()


  // Tooltip shows market of ethereum and bitcoin at given date. Ethereum tooltip shows the percentage of bitcoins market
  // cap that it is/was at the given time
  def addTooltips() =
    for
      s <- chart.getData
      d <- s.getData
    do
      val ethPercentage = if d.getXValue == "Ethereum" then s" $marketCapPercentage% of Bitcoin market cap" else ""
      javafx.scene.control.Tooltip.install(d.getNode, new Tooltip(d.getXValue + "\n" + "Market Cap: " + d.getYValue + " million" + ethPercentage))