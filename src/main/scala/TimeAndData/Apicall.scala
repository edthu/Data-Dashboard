package TimeAndData

import TimeAndData.TimeConversions

import java.time.*
import java.util.Date
import scala.collection.mutable.ArrayBuffer

// A class for making http-requests and changing the data into a format so that it can be used in the GUI components
class requestData(private var api: String):
  // Makes a http request with the given url
  def request = requests.get(api)


  private var currentReq = request
  def updateApi(newApi: String) = 
    api = newApi
    currentReq = request
  
  // Changes the contents of the response to a ujson.value object
  def json = ujson.read(currentReq.text())

  // Use this only after a request is made for historical data.
  // Converts the ujson.value objects from the api call
  // https://api.coivalngecko.com/api/v3/coins/ethereum/market_chart/range?vs_currency=usd&from=[timestamp]]&to=[timestamp]
  // to doubles.
  // Structure of the JSON returned from request:
  // prices -> Array[(timeStamp, price)],
  // market_caps -> Array[(timeStamp, marketCap)],
  // total_volumes -> Array[(timeStamp, totalVolume)]
  def timedata = json.obj("prices").arr.map(value => (value(0).num, value(1).num))

  def volumedata = json.obj("total_volumes").arr.
    map(value => (TimeConversions.unixTimestampToddMMyyyy((value(0).num.toLong)), (value(1).num.toLong / 1000000000).toInt))

  // The historical data from the Coingecko API is in a form where the time is in UNIX-time.
  // This method changes it to normal time (timezone is based on the systems timezone). The price returned by the call is
  // a double. This method rounds it to an Int.

  def formatData(data: ArrayBuffer[(Double, Double)], dataIntervalLength: Long): ArrayBuffer[(String, Int)] =
    // If the interval of the data is less than or equal to 90 days (7776000 seconds) then the data is given in
    // 1 hour intervals. For longer timeframes the datapoints are single days.
    data.map((timestamp, price) => (TimeConversions.unixTimestampToNormalTime(timestamp.toLong, dataIntervalLength), price.toInt))
