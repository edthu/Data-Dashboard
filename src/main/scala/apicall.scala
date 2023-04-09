import scala.collection.mutable.ArrayBuffer
import java.time.*
import java.util.Date

// A class for making http-requests and changing the data into a format so that it can be used in the GUI components
class requestData(api: String):
  def request = requests.get(api)

  val json = ujson.read(request.text())

  // The historical data from the Coingecko API is in a form where the time is in UNIX-time.
  // This method changes it to normal time (timezone of the machine). The price returned by the call is
  // a double. This method rounds it to an Int.
  def formatData(data: ArrayBuffer[(Double, Double)]) =
    def unixTimestampToNormalTime(epoch: Long) = java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date(epoch))
    data.map((timestamp, price) => (unixTimestampToNormalTime(timestamp.toLong), price.toInt))

  // Use this only after a request is made for historical data.
  // Converts the ujson.value objects from the apicall
  // https://api.coingecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=[timestamp]]&to=[timestamp]
  // to doubles.
  def timedata = json.obj("prices").arr.map(value => (value(0).num, value(1).num))
