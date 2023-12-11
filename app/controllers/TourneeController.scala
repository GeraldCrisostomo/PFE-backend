package controllers

import models.{LivreurModification, TourneeCreation, TourneeUpdate}
import play.api.libs.json.{JsValue, Json}

import javax.inject._
import play.api.mvc._
import services.TourneeService

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contrôleur pour gérer les actions liées aux tournées.
 *
 * @param cc             Composants du contrôleur fournis par Play Framework.
 * @param tourneeService Service pour la gestion des tournées.
 */
@Singleton
class TourneeController @Inject()(cc: ControllerComponents, tourneeService: TourneeService) extends AbstractController(cc) {

  /**
   * Récupère les tournées par date.
   *
   * @param date Date au format ISO_LOCAL_DATE.
   */
  def getTourneesByDate(date: String): Action[AnyContent] = Action.async {
    val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)

    tourneeService.getTourneesByDate(localDate).map { tournees =>
      Ok(Json.toJson(tournees))
    }.recover {
      case e: Exception => InternalServerError(s"An error occurred: ${e.getMessage}")
    }
  }

  /**
   * Crée une nouvelle tournée.
   */
  def createTournee(): Action[JsValue] = Action.async(parse.json) { request =>
    val tourneeCreation = request.body.as[TourneeCreation]

    // Utilisez map pour transformer le Future[Long] en Future[JsValue]
    val id_tournee: Future[JsValue] = tourneeService.createTournee(tourneeCreation).map { id =>
      Json.obj("id_tournee" -> id)
    }

    // Utilisez flatMap pour obtenir le Future[JsValue] final
    id_tournee.map(Created(_))
  }

  /**
   * Récupère une tournée par son identifiant.
   *
   * @param id_tournee Identifiant de la tournée.
   */
  def getTourneeById(id_tournee: Long): Action[AnyContent] = Action.async {
    tourneeService.getTourneeById(id_tournee).map {
      case Some(tournee) => Ok(Json.toJson(tournee))
      case None => NotFound
    }
  }

  /**
   * Met à jour une tournée par son identifiant.
   *
   * @param id_tournee Identifiant de la tournée à mettre à jour.
   */
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

  /**
   * Supprime une tournée par son identifiant.
   *
   * @param id_tournee Identifiant de la tournée à supprimer.
   */
  def deleteTournee(id_tournee: Long): Action[AnyContent] = Action.async { _ =>
    tourneeService.deleteTournee(id_tournee).map { deleted =>
      if (deleted) {
        Ok("Tournee deleted successfully.")
      } else {
        NotFound("Tournee not found.")
      }
    }
  }

  /**
   * Récupère le résumé d'une tournée par son identifiant.
   *
   * @param id_tournee Identifiant de la tournée.
   */
  def getTourneeResume(id_tournee: Long): Action[AnyContent] = Action.async { _ =>
    tourneeService.getTourneeResume(id_tournee).map { tourneeResumeList =>
      Ok(Json.toJson(tourneeResumeList))
    }.recover {
      case e: Exception => InternalServerError(s"An error occurred: ${e.getMessage}")
    }
  }

  /**
   * Modifie le livreur associé à une tournée.
   *
   * @param id_tournee Identifiant de la tournée.
   */
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
