package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.UtilisateurService

import scala.concurrent.ExecutionContext

@Singleton
class UtilisateurController @Inject()(cc: ControllerComponents, utilisateurService: UtilisateurService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getAllUtilisateurs: Action[AnyContent] = Action.async { implicit request =>
    utilisateurService.getAllUtilisateurs.map(articles => Ok(Json.toJson(articles)))
  }

  def getAllSupplementsByRole(role: String): Action[AnyContent] = Action.async { implicit request =>
    utilisateurService.getAllUtilisateursByRole(role).map { supplements => Ok(Json.toJson(supplements))
    }.recover {
      case e: Exception => InternalServerError(s"An error occured ${e.getMessage} ")
    }
  }

}
