// A class that contains data for the Stat card

class StatWindow:
  // assumes that the following request has been made
  val req = requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  private val JSONobject = req.json(0)

  val fdv = JSONobject("fully_diluted_valuation")
  val marketCap = JSONobject("market_cap")
  val transactionsSinceOpening = ???
  val currentPrice = JSONobject("current_price")
  val ath = JSONobject("ath")


  

