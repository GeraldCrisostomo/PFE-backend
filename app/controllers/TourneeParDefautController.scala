package controllers

import models.{TourneeParDefautCreate, TourneeParDefautUpdate}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject._
import services.TourneeParDefautService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les actions liées aux tournées par défaut.
 *
 * @param cc                       Composants du contrôleur fournis par Play Framework.
 * @param tourneeParDefautService Service pour la gestion des tournées par défaut.
 * @param ec                       Contexte d'exécution implicite pour les opérations asynchrones.
 */
@Singleton
class TourneeParDefautController @Inject()(cc: ControllerComponents, tourneeParDefautService: TourneeParDefautService)
                                          (implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Récupère toutes les tournées par défaut.
   */
  def getTourneesParDefaut: Action[AnyContent] = Action.async { _ =>
    tourneeParDefautService.getTourneesParDefaut.map { tournees =>
      Ok(Json.toJson(tournees))
    }.recover {
      case e: Exception => InternalServerError(s"An error occurred: ${e.getMessage}")
    }
  }

  /**
   * Crée une nouvelle tournée par défaut.
   */
  def createTourneeParDefaut(): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeParDefautCreate = request.body.as[TourneeParDefautCreate]

    tourneeParDefautService.createTourneeParDefaut(tourneeParDefautCreate).map { id =>
      Created(Json.obj("id_tournee_par_defaut" -> id))
    }
  }

  /**
   * Récupère une tournée par défaut par son identifiant.
   *
   * @param id_tournee_par_defaut Identifiant de la tournée par défaut.
   */
  def getTourneeParDefautById(id_tournee_par_defaut: Long): Action[AnyContent] = Action.async { _ =>
    tourneeParDefautService.getTourneeParDefautById(id_tournee_par_defaut).map {
      case Some(tourneeParDefaut) => Ok(Json.toJson(tourneeParDefaut))
      case None => NotFound
    }
  }

  /**
   * Met à jour une tournée par défaut par son identifiant.
   *
   * @param id_tournee_par_defaut Identifiant de la tournée par défaut à mettre à jour.
   */
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

  /**
   * Supprime une tournée par défaut par son identifiant.
   *
   * @param id_tournee_par_defaut Identifiant de la tournée par défaut à supprimer.
   */
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
