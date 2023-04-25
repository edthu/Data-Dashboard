package Charts

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, LineChart}
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane

abstract class ChartPanel extends BorderPane

case class BarChartPane(chart: BarChart[String, Number], private val barChart: MarketCapBarChart) extends ChartPanel {
  def getCenter: BarChart[String, Number] = chart

  def getChartObject = barChart
}
case class StatWindowPane(private val stats: StatWindow) extends ChartPanel {
  def getStatWindow = stats
}

case class ChartPane(chart: LineChart[String, Number], private val priceChart: PriceChart) extends ChartPanel {
  center = chart

  def getCenter: LineChart[String, Number] = chart

  def getChartObject = priceChart
}

class ChartSplitPane extends SplitPane {
  def getItems: ObservableBuffer[ChartPanel] =
    val allItems = super.items.filter(_.isInstanceOf[ChartPanel])
    val itemWithCorrectClass = allItems.map(_.asInstanceOf[ChartPanel])
    itemWithCorrectClass
}

class StackOfSplitPanes extends SplitPane {
  def getItems: ObservableBuffer[ChartSplitPane] =
    val allSplitPanes = super.items.filter(_.isInstanceOf[ChartSplitPane])
    val withCorrectClass = allSplitPanes.map(_.asInstanceOf[ChartSplitPane])
    withCorrectClass
}
