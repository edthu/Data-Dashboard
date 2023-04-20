// An object shared between the opened window and the base
final class IntervalData private():
  private var startDate: Long = 0
  private var endDate: Long = 0

  def setDates(newStartDate: Long, newEndDate: Long) =
    startDate = newStartDate
    endDate = newEndDate

  def getDates = (startDate, endDate)

object IntervalData:
  private val dateObject: IntervalData = IntervalData()

  def getDateObject = dateObject