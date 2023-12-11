package controllers

import models.{TourneeParDefaut, TourneeParDefautCreate, TourneeParDefautUpdate}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject._
import services.TourneeParDefautService

import scala.concurrent.ExecutionContext

@Singleton
class TourneeParDefautController @Inject()(cc: ControllerComponents, tourneeParDefautService: TourneeParDefautService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getTourneesParDefaut: Action[AnyContent] = Action.async { _ =>
    tourneeParDefautService.getTourneesParDefaut.map { tournees =>
      Ok(Json.toJson(tournees))
    }.recover {
      case e: Exception => InternalServerError(s"An error occurred: ${e.getMessage}")
    }
  }

  def createTourneeParDefaut(): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeParDefautCreate = request.body.as[TourneeParDefautCreate]

    tourneeParDefautService.createTourneeParDefaut(tourneeParDefautCreate).map { id =>
      Created(Json.obj("id_tournee_par_defaut" -> id))
    }
  }

  def getTourneeParDefautById(id_tournee_par_defaut: Long): Action[AnyContent] = Action.async { _ =>
    tourneeParDefautService.getTourneeParDefautById(id_tournee_par_defaut).map {
      case Some(tourneeParDefaut) => Ok(Json.toJson(tourneeParDefaut))
      case None => NotFound
    }
  }

  def updateTourneeParDefaut(id_tournee_par_defaut: Long): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeParDefautUpdate = request.body.as[TourneeParDefautUpdate]

    tourneeParDefautService.updateTourneeParDefaut(id_tournee_par_defaut, tourneeParDefautUpdate).map { updated =>
      if (updated) {
        Ok("Tournee par defaut updated successfully.")
      } else {
        NotFound("Tournee par defaut not found.")
      }
    }
  }

  def deleteTourneeParDefaut(id_tournee_par_defaut: Long): Action[AnyContent] = Action.async { _ =>
    tourneeParDefautService.deleteTourneeParDefaut(id_tournee_par_defaut).map { deleted =>
      if (deleted) {
        Ok("Tournee par defaut deleted successfully.")
      } else {
        NotFound("Tournee par defaut not found.")
      }
    }
  }
}
