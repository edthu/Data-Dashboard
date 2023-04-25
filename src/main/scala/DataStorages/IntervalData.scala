package DataStorages

// Only a single instance of the IntervalData can be created. It can be accessed through its companion object.
// This class is used as a container to transfer data between two windows. The data is updated from window A. After
// it is closed this class can be accessed in window B. 

// This class is used with the popup windows in PopupWindow.scala and GUI. startDate and endDate are dates chosen in
// a popup window and passed into the charts updateData method to change the interval of the chart
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