import scalafx.scene.chart.{BarChart, LineChart}
import scalafx.scene.layout.BorderPane

sealed trait ChartPanel extends scalafx.scene.Node

case class ChartPane(chart: LineChart[String, Number], private val priceChart: PriceChart) extends BorderPane, ChartPanel {
  center = chart

  def getCenter: LineChart[String, Number] = chart

  def getChartObject = priceChart
}


case class BarChartPane(chart: BarChart[String, Number], private val barChart: MarketCapBarChart) extends BorderPane, ChartPanel {
  def getCenter: BarChart[String, Number] = chart

  def getChartObject = barChart
}


case class StatWindowPane(private val stats: StatWindow) extends BorderPane, ChartPanel{
  def getStatWindow = stats
}
