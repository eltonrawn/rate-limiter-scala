package hotel

import java.io.FileNotFoundException
import javax.inject.Singleton
import scala.util.{Failure, Try}

@Singleton
class HotelService {
  def get_csv_data(): Option[List[List[String]]] = {
    val bufferedSource = scala.io.Source.fromFile("public/hoteldb.csv")

    try {
      Some(bufferedSource.getLines.map(_.split(",").toList).toList)
    } catch {
      case _: Exception => None
    } finally { bufferedSource.close() }
  }

  val csv_data: Option[List[List[String]]] = get_csv_data()

  def getColumnById(id: String, column: String, isAsc: Option[Boolean]): Try[List[List[String]]] = {
    csv_data match {
      case Some(data) => {
        isAsc match {
          case Some(true) => Try((data.filter(_(data(0).indexOf(column)) == id)).sortBy(_(data(0).indexOf("PRICE")).toInt))
          case Some(false) => Try((data.filter(_(data(0).indexOf(column)) == id)).sortBy(_(data(0).indexOf("PRICE")).toInt)(Ordering[Int].reverse))
          case None => Try(data.filter(_(data(0).indexOf(column)) == id))
        }
      }
      case None => Failure(new FileNotFoundException)
    }
  }

}
