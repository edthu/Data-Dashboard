import TimeAndData.RequestData
import javafx.scene.control.DatePicker

@ main def thing() =
  val r = RequestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  //println(r.request.statusMessage)
  //println(r.json.arr(0).obj.keys)
  //println(r.json.arr(0).obj("low_24h"))

  val historicalCall = RequestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1523229288&to=1680995696")

  //println(historicalCall.formatData(historicalCall.timedata))
  //println(r.json(0)("fully_diluted_valuation"))
  //println(historicalCall.json.obj("prices"))
  var startTime: Long = System.currentTimeMillis() / 1000 - 604800
  // By default the current day
  var endTime: Long = System.currentTimeMillis() / 1000
  val request = RequestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=$startTime&to=$endTime")
  println(request.timedata)

  def maxPrice = request.timedata.maxBy(_._2)._2
  def minPrice = request.timedata.minBy(_._2)._2
  println(s"$maxPrice + $minPrice")

  println(s"$startTime + $endTime")
  val newRequest = RequestData(s"https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1587417696&to=1681982178291")
  println(request.formatData(request.timedata, endTime - startTime).mkString("\n"))






