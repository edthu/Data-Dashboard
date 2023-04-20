import javafx.event.EventHandler
import scalafx.scene.layout.VBox
import scalafx.stage.Stage
import scalafx.stage.{Modality, Stage}

import java.time.{LocalDate, Month}
import javafx.scene.control.{DateCell, DatePicker}
import javafx.util.Callback
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Label

import java.time.format.DateTimeFormatter
import scalafx.scene.control.Button
// Shows the user a menu where they can choose the interval from which the chart is drawn from.

// In the menu the user can choose the day, year and month where the interval begins and ends
// create the stage
// Popup window for changing the interval of the datachart

class IntervalPopup(private val chart: PriceChart):
  def display() =
    val popupStage = new Stage:
      title = "Select New Interval"
      width = 250
      height = 200
      resizable = false


    popupStage.initModality(Modality.ApplicationModal)

    val pane = new VBox

    // Choose the start and end points of the interval that is going to be displayed on the chart
    val startDatePicker = new DatePicker // This DatePicker is shown to user
    val endDatePicker = new DatePicker
    // Ethereum network launch date
    val minDate = LocalDate.of(2015, 8, 7)

    val dayCellFactory: Callback[DatePicker, DateCell] = (datePicker: DatePicker) =>
      new DateCell {
        override def updateItem(item: LocalDate, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          // Disable all dates earlier than the required date
          if (item.isBefore(minDate)) {
            setDisable(true)
            //To set background on a different color
            setStyle("-fx-background-color: #ffc0cb;")
          }
        }
      }
    //Finally, we just need to update our DatePicker cell factory as follow:
    startDatePicker.setDayCellFactory(dayCellFactory)
    startDatePicker.setValue(LocalDate.now)
    endDatePicker.setDayCellFactory(dayCellFactory)
    endDatePicker.setValue(LocalDate.now)

    val to = new Label:
      text = "to"
      alignment = Pos.Center

    val message = new Label:
      text = ""
      alignment = Pos.Center

    // The changes are only committed if this button is pressed
    val exitWindow = new Button("Set interval and return to chart")

    exitWindow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) =
        // Changes the dates of the chart to the currently selected options
        // if the dates are valid. Else rejects the options and does not yet quit
        def convertDateFormat(sourceDate: String): String =
          // Define input and output formats
          val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
          val targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

          // Parse String "sourceDate" to LocalDate, then format to "dd/MM/yyyy" format
          LocalDate.parse(sourceDate, sourceFormatter).format(targetFormatter)

        val startTime = chart.timeInUnixTimeStamp(convertDateFormat(startDatePicker.getValue.toString)) / 1000
        val endTime = chart.timeInUnixTimeStamp(convertDateFormat(endDatePicker.getValue.toString)) / 1000

        //
        if endTime < startTime then
          message.text = "End date cannot be earlier than \nstart date"
        else
          IntervalData.getDateObject.setDates(startTime, endTime)
          popupStage.close()
    })


    pane.getChildren.addAll(startDatePicker, to, endDatePicker, exitWindow, message)

    val popupScene = new Scene(parent = pane)
    popupStage.scene = popupScene
    popupStage.showAndWait()
