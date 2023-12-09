package filters

import org.apache.pekko.stream.Materializer

import javax.inject.Inject
import play.api.http.HttpFilters
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class CorsFilter @Inject()(implicit val mat: Materializer) extends EssentialFilter {
  override def apply(next: EssentialAction): EssentialAction = {
    EssentialAction { requestHeader =>
      next(requestHeader).map { result =>
        result.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE",
          "Access-Control-Allow-Headers" -> "Authorization, Content-Type, Accept, Origin, X-Requested-With"
        )
      }
    }
  }
}

class Filters @Inject()(corsFilter: CorsFilter) extends HttpFilters {
  override def filters: Seq[CorsFilter] = Seq(corsFilter)
}
