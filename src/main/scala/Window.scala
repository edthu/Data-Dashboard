// A class that contains data for the Stat card

class StatWindow(val currency: String):
  // assumes that the following request has been made
  private val req = requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  private val JSONobject = req.json(0)

  // These are stats directly from the apicall
  val fdv = JSONobject("fully_diluted_valuation")
  val marketCap = JSONobject("market_cap")
  // val transactionsSinceOpening = ???
  val currentPrice = JSONobject("current_price")
  val ath = JSONobject("ath")

  // As text
  val text =
    s"FDV:                $fdv\n" +
    s"Market Cap:    $marketCap\n" +
    s"Current Price: $currentPrice\n" +
    s"All-Time High  $ath"



  

