// A class that contains data for the Stat card

class StatWindow(val currency: String):
  private var startTime: Long = System.currentTimeMillis() / 1000 - 604800
  // By default the current day
  private var endTime: Long = System.currentTimeMillis() / 1000

  // assumes that the following request has been made
  private val currentData = requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  private def JSONobject = currentData.json(0)

  private val intervalRequest =
    requestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime")


  // Current statistics
  private def fdv = JSONobject("fully_diluted_valuation")
  private def marketCap = s"${(JSONobject("market_cap").num / 1000000).toInt} million"
  // val transactionsSinceOpening = ???
  private def currentPrice = JSONobject("current_price")
  private def ath = JSONobject("ath")


  // Statistics based on a chosen interval
  def highestAndLowestPrice =
    val highest = intervalRequest.timedata.maxBy(_._2)._2.toInt
    val lowest = intervalRequest.timedata.minBy(_._2)._2.toInt
    s"Lowest price: $lowest\n" +
    s"Highest price: $highest"
  // difference between highest and lowest price
  // volatility ?

  private def mostAndLeastVolume =
    val mostVolume = intervalRequest.volumedata.maxBy(_._2)
    val leastVolume = intervalRequest.volumedata.minBy(_._2)
    val mostVolString = s"${mostVolume._1}: ${mostVolume._2}"
    val leastVolString = s"${leastVolume._1}: ${leastVolume._2}"
    (mostVolString, leastVolString)


  def startDate = TimeConversions.unixTimestampToddMMyyyy(startTime*1000)

  def endDate = TimeConversions.unixTimestampToddMMyyyy(endTime*1000)
  // As text
  def text =
    s"Current Ethereum statistics\n" +
    s"Market Cap:    $marketCap\n" +
    s"Current Price: $currentPrice\n" +
    s"All-Time High  $ath\n\n" +
      s"Statistics between\n" +
    s"$startDate - " +
    s"$endDate\n" +
      s"Most volume: ${mostAndLeastVolume._1}\n" +
      s"Least volume: ${mostAndLeastVolume._2}"

  def updateData(newStart: Long, newEnd: Long) =
    // eri api viel tohon
    val newApi = (s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$newStart&to=$newEnd")
    intervalRequest.updateApi(newApi)
    startTime = newStart
    endTime = newEnd



  

