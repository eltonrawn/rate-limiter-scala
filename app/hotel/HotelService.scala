package hotel

import javax.inject.Singleton

@Singleton
class HotelService {
  def get_csv_data(): List[List[String]] = {
    val bufferedSource = scala.io.Source.fromFile("public/hoteldb.csv")
    val bufferedData = bufferedSource.getLines.map(_.split(",").toList).toList
    bufferedData
  }

  val data: List[List[String]] = get_csv_data()

  def getColumnById(id: String, column: String):List[List[String]] = {
    data.filter(_(data(0).indexOf(column)) == id)
  }

}
