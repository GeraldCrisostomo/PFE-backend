import models.{LivreurModification, Tournee, TourneeCreation, TourneeUpdate}
import play.api.libs.json.{JsValue, Json}

import javax.inject._
import play.api.mvc._
import services.TourneeService
import play.api.http.Writeable
import play.api.http.ContentTypes
import play.api.mvc.Codec
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class TourneeController @Inject()(cc: ControllerComponents, tourneeService: TourneeService) extends AbstractController(cc) {

  implicit def writeableOf_Result_ListOfTournee(implicit codec: Codec): Writeable[Future[List[Tournee]]] = {
    Writeable(result => codec.encode(result.toString), Some(ContentTypes.JSON))
  }
  def getAllTourneesForDate(date: String): Action[AnyContent] = Action {
    Ok(tourneeService.getAllTourneesForDate(date))
  }

  def createTournee(): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeCreation = request.body.as[TourneeCreation]

    // Utilisez map pour transformer le Future[Long] en Future[JsValue]
    val id_tournee: Future[JsValue] = tourneeService.createTournee(tourneeCreation).map { id =>
      Json.obj("id_tournee" -> id)
    }

    // Utilisez flatMap pour obtenir le Future[JsValue] final
    id_tournee.map(Created(_))
  }

  def getTourneeById(id_tournee: Long): Action[AnyContent] = Action.async {
    tourneeService.getTourneeById(id_tournee).map {
      case Some(tournee) => Ok(Json.toJson(tournee))
      case None => NotFound
    }
  }

  def updateTournee(id_tournee: Long): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeUpdate = request.body.as[TourneeUpdate]
    tourneeService.updateTournee(id_tournee, tourneeUpdate).map { updated =>
      if (updated) {
        Ok("Tournee updated successfully.")
      } else {
        NotFound("Tournee not found.")
      }
    }
  }

  def deleteTournee(id_tournee: Long): Action[AnyContent] = Action.async { _ =>
    tourneeService.deleteTournee(id_tournee).map { deleted =>
      if (deleted) {
        Ok("Tournee deleted successfully.")
      } else {
        NotFound("Tournee not found.")
      }
    }
  }

  def getTourneeResume(id_tournee: Long): Action[AnyContent] = Action.async { _ =>
    tourneeService.getTourneeResume(id_tournee).map { maybeResume =>
      maybeResume.map(resume => Ok(Json.toJson(resume))).getOrElse(NotFound)
    }
  }

  def modifierLivreur(id_tournee: Long): Action[JsValue] = Action.async(parse.json) { request =>
    val livreurModification = request.body.as[LivreurModification]
    tourneeService.modifierLivreur(id_tournee, livreurModification).map { success =>
      if (success) {
        Ok("Livreur modified successfully.")
      } else {
        NotFound("Tournee not found.")
      }
    }
  }
}
