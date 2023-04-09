
@ main def thing() =
  val r = requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  println(r.request.statusMessage)
  println(r.json.arr(0).obj.keys)
  println(r.json.arr(0).obj("low_24h"))

  val historicalCall = requestData("https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=1523229288&to=1680995696")

  println(historicalCall.formatData(historicalCall.timedata))



