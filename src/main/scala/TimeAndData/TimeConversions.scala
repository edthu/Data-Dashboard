package TimeAndData

import java.util.Date

// Used to change Unix time (seconds since January 1 1970. Epoch is used as synonym for this time
// The formats of the dates are specified in SimpleDateFormats pattern parameter.
object TimeConversions:
  def unixTimestampToNormalTime(epoch: Long, dataIntervalLength: Long): String =
    if dataIntervalLength <= 7776000 then
      java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(epoch))
    else
      java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date(epoch))

  def timeInUnixTimeStamp(time: String): Long = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(time).getTime

  def unixTimestampToddMMyyyy(epoch: Long) = java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date(epoch))
  
  def unixTimestampToddMMyyyyWithDash(epoch: Long) = java.text.SimpleDateFormat("dd-MM-yyyy").format(new Date(epoch))