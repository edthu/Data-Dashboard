package Gui

import DataStorages.IntervalData
import TimeAndData.TimeConversions
import javafx.event.EventHandler
import javafx.scene.control.{DateCell, DatePicker}
import javafx.util.Callback
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import scalafx.stage.{Modality, Stage}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month}
import scala.io.Source
// Shows the user a menu where they can choose the interval from which the chart is drawn from.
// In the menu the user can choose the day, year and month where the interval begins and ends


// Popup window for changing the interval of the datachart

class IntervalPopup():
  def display() =
    val popupStage = new Stage:
      title = "Select New Interval"
      width = 250
      height = 200
      resizable = false

    // The main window can only be accessed after interacting with the popup
    popupStage.initModality(Modality.ApplicationModal)

    val pane = new VBox

    // Choose the start and end points of the interval that is going to be displayed on the chart
    val startDatePicker = new DatePicker
    val endDatePicker = new DatePicker
    // Ethereum network launch date
    val minDate = LocalDate.of(2015, 8, 7)


    // Disables the dates before Ethereum launch date and after current day
    val dayCellFactory: Callback[DatePicker, DateCell] = (datePicker: DatePicker) =>
      new DateCell {
        override def updateItem(item: LocalDate, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          // Disable all dates earlier than the required date
          if (item.isBefore(minDate) || item.isAfter(java.time.LocalDate.now())) {
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
      text = "This window can only be closed with\nthe above button"
      alignment = Pos.Center

    // The changes are only committed if this button is pressed
    val exitWindow = new Button("Set interval and return to chart")

    exitWindow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) =
        // Converts the date from yyyy-MM-dd (datepicker default) to dd/MM/yyyy (used in the apicall)
        def convertDateFormat(sourceDate: String): String =
          // Define input and output formats
          val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
          val targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

          // Parse String "sourceDate" to LocalDate, then format to "dd/MM/yyyy" format
          LocalDate.parse(sourceDate, sourceFormatter).format(targetFormatter)

        val startTime = TimeConversions.timeInUnixTimeStamp(convertDateFormat(startDatePicker.getValue.toString)) / 1000
        val endTime = TimeConversions.timeInUnixTimeStamp(convertDateFormat(endDatePicker.getValue.toString)) / 1000

        // Changes the dates of the chart to the currently selected options
        // if the dates are valid. Else rejects the options and does not yet quit
        if endTime <= startTime then
          message.text = "End date cannot be earlier or the \nsame date as start date"
        else
          IntervalData.getDateObject.setDates(startTime, endTime)
          popupStage.close()
    })

    pane.getChildren.addAll(startDatePicker, to, endDatePicker, exitWindow, message)

    val popupScene = new Scene(parent = pane)
    popupStage.scene = popupScene
    // prevents the window from being closed with x from top bar of the window
    popupStage.onCloseRequest = (e => e.consume())
    popupStage.showAndWait()

// A popup window for choosing a single date
class SingleDatePopUp():
  def display() =
    val popupStage = new Stage:
      title = "Select a date to visualise"
      width = 250
      height = 150
      resizable = false

    popupStage.initModality(Modality.ApplicationModal)

    val pane = new VBox
    // This DatePicker is shown to user
    val datePicker = new DatePicker

    // Ethereum network launch date
    val minDate = LocalDate.of(2015, 8, 7)

    // Disables the dates before Ethereum launch date and after current day
    val dayCellFactory: Callback[DatePicker, DateCell] = (datePicker: DatePicker) =>
      new DateCell {
        override def updateItem(item: LocalDate, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          // Disable all dates earlier than the required date
          if (item.isBefore(minDate) || item.isAfter(java.time.LocalDate.now())) {
            setDisable(true)
            //To set background on a different color
            setStyle("-fx-background-color: #ffc0cb;")
          }
        }
      }
    // Set the disabled states and set default value to current time
    datePicker.setDayCellFactory(dayCellFactory)
    datePicker.setValue(LocalDate.now)

    val message = new Label:
      text = "This window can only be closed with\nthe above button"
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

        val startTime = TimeConversions.timeInUnixTimeStamp(convertDateFormat(datePicker.getValue.toString))

        // The interval data class is used to transfer data between the closed popup window and main window
        IntervalData.getDateObject.setDates(startTime, 0)
        popupStage.close()
    })

    pane.getChildren.addAll(datePicker, exitWindow, message)

    val popupScene = new Scene(parent = pane)
    popupStage.scene = popupScene
    popupStage.onCloseRequest = (e => e.consume())
    popupStage.showAndWait()

/*
class SaveNamingPopUp():
  def display() =
    val popupStage = new Stage:
      title = "Name the savefile"
      width = 250
      height = 150
      resizable = false

    popupStage.initModality(Modality.ApplicationModal)

    val pane = new VBox

    val textField = new TextField()

    val message = new Label:
      text = "This window can only be closed with\nthe above button"
      alignment = Pos.Center

    // The changes are only committed if this button is pressed
    val exitWindow = new Button("Save")

    exitWindow.setOnAction(new EventHandler[javafx.event.ActionEvent]() {
      def handle(actionEvent: javafx.event.ActionEvent) =
        // Check if the selected name is already taken
        val enteredName = textField.getText
        val pathToJSON = "src/main/scala/savefiles.json"
        val jsonSource = Source.fromFile(pathToJSON)
        val jsonString = jsonSource.getLines().mkString
        jsonSource.close()
        val json = ujson.read(jsonString)
        val isNameTaken = json.obj.keySet.contains(enteredName)

        if isNameTaken then
          message.text = "There is already a save file with\nthat name"
        else
          DataStorages.SaveFileName.getSaveFileName.changeName(enteredName)
          popupStage.close()
        // if the name does not exist then ok otherwise reject
        textField.getText
    })

    pane.getChildren.addAll(textField, exitWindow, message)

    val popupScene = new Scene(parent = pane)
    popupStage.scene = popupScene
    popupStage.onCloseRequest = (e => e.consume())
    popupStage.showAndWait()
*/



