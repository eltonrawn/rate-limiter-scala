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
      case e: Exception => None
    } finally { bufferedSource.close() }
  }

  val csv_data: Option[List[List[String]]] = get_csv_data()

  def getColumnById(id: String, column: String): Try[List[List[String]]] = {
    csv_data match {
      case Some(data) => Try(data.filter(_(data(0).indexOf(column)) == id))
      case None => Failure(new FileNotFoundException)
    }
  }

}
