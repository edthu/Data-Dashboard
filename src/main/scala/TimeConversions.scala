import java.util.Date
object TimeConversions:
  def unixTimestampToNormalTime(epoch: Long, dataIntervalLength: Long): String =
    if dataIntervalLength <= 7776000 then
      java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(epoch))
    else
      java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date(epoch))

  def timeInUnixTimeStamp(time: String): Long = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(time).getTime

  def unixTimestampToddMMyyyy(epoch: Long) = java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date(epoch))