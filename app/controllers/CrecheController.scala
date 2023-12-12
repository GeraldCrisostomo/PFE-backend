package controllers

import javax.inject.Inject
import models.{CrecheCreate, CrecheUpdate}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CrecheService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les actions liées aux crèches.
 *
 * @param cc            Composants du contrôleur fournis par Play Framework.
 * @param crecheService Service pour la gestion des crèches.
 * @param ec            Contexte d'exécution pour les opérations asynchrones.
 */
class CrecheController @Inject()(cc: ControllerComponents, crecheService: CrecheService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
   * Récupère la liste de toutes les crèches.
   */
  def getAllCreches: Action[AnyContent] = Action.async {
    crecheService.getAllCreches.map { creches =>
      Ok(Json.toJson(creches))
    }
  }

  /**
   * Récupère une crèche par son identifiant.
   *
   * @param id Identifiant de la crèche.
   */
  def getCrecheById(id: Long): Action[AnyContent] = Action.async {
    crecheService.getCrecheById(id).map {
      case Some(creche) => Ok(Json.toJson(creche))
      case None => NotFound
    }
  }

  /**
   * Crée une nouvelle crèche.
   */
  def createCreche: Action[CrecheCreate] = Action.async(parse.json[CrecheCreate]) { request =>
    crecheService.createCreche(request.body).map { id =>
      Created(Json.obj("id_creche" -> id))
    }
  }

  /**
   * Met à jour une crèche existante par son identifiant.
   *
   * @param id Identifiant de la crèche à mettre à jour.
   */
  def updateCreche(id: Long): Action[CrecheUpdate] = Action.async(parse.json[CrecheUpdate]) { request =>
    crecheService.updateCreche(id, request.body).map {
      case true => Ok("Creche updated successfully.")
      case false => NotFound("Creche not found.")
    }
  }

  /**
   * Supprime une crèche par son identifiant.
   *
   * @param id Identifiant de la crèche à supprimer.
   */
  def deleteCreche(id: Long): Action[AnyContent] = Action.async {
    crecheService.deleteCreche(id).map {
      case true => Ok("Creche deleted successfully.")
      case false => NotFound("Creche not found.")
    }
  }
}
