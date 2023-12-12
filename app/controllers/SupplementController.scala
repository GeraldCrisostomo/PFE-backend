package controllers

import models._
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.SupplementService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les actions liées aux supplements.
 *
 * @param cc               Composants du contrôleur fournis par Play Framework.
 * @param supplementService Service pour la gestion des supplements.
 * @param ec               Contexte d'exécution pour les opérations asynchrones.
 */
@Singleton
class SupplementController @Inject()(cc: ControllerComponents, supplementService: SupplementService)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  /**
   * Récupère la liste de tous les supplements pour une tournée donnée.
   *
   * @param id_tournee Identifiant de la tournée.
   */
  def getAllSupplements(id_tournee: Long): Action[AnyContent] = Action.async { implicit _ =>
    supplementService.getAllSupplements(id_tournee).map { supplements =>
      Ok(Json.toJson(supplements))
    }.recover {
      case e: Exception => InternalServerError(s"An error occurred: ${e.getMessage}")
    }
  }

  /**
   * Met à jour un supplement pour une tournée et un article donnés.
   *
   * @param idTournee Identifiant de la tournée.
   * @param idArticle Identifiant de l'article.
   */
  def updateSupplement(idTournee: Long, idArticle: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val supplementUpdate = request.body.as[SupplementUpdate]
    supplementService.updateSupplement(idTournee, idArticle, supplementUpdate).map {
      case Some(updatedSupplement) => Ok(Json.toJson(updatedSupplement))
      case None => NotFound
    }
  }
}
