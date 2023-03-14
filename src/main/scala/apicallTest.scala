def requestData(api: String) =
  def request = requests.get(api)
@ main def test() =
  val r = requests.get("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
  println(r.text())
  // miten handlataan jsonit tolla requestjutulla
  