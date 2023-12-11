package controllers

import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CommandeService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les opérations liées aux commandes.
 *
 * @param cc               Les composants du contrôleur.
 * @param commandeService  Le service de commandes injecté.
 * @param ec               L'exécution implicite contextuelle.
 */
class CommandeController @Inject()(cc: ControllerComponents, commandeService: CommandeService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
   * Récupère les commandes liées à une tournée spécifiée par son ID.
   *
   * @param idTournee   L'identifiant de la tournée.
   * @return Une action asynchrone renvoyant la liste des commandes au format JSON.
   */
  def getCommandesByTourneeId(idTournee: Long): Action[AnyContent] = Action.async {
    commandeService.getCommandesByTourneeId(idTournee).map { commandes =>
      Ok(Json.toJson(commandes))
    }
  }

  /**
   * Crée une nouvelle commande pour une tournée spécifiée par son ID.
   *
   * @param idTournee   L'identifiant de la tournée.
   * @return Une action asynchrone renvoyant l'ID de la commande créée au format JSON.
   */
  def createCommande(idTournee: Long): Action[CommandeCreate] = Action.async(parse.json[CommandeCreate]) { request =>
    commandeService.createCommande(idTournee, request.body).map { id =>
      Created(Json.obj("id_commande" -> id))
    }
  }

  /**
   * Récupère une commande par son ID.
   *
   * @param idCommande   L'identifiant de la commande.
   * @return Une action asynchrone renvoyant la commande au format JSON, ou NotFound si la commande n'est pas trouvée.
   */
  def getCommandeById(idCommande: Long): Action[AnyContent] = Action.async {
    commandeService.getCommandeById(idCommande).map {
      case Some(commande) => Ok(Json.toJson(commande))
      case None => NotFound
    }
  }

  /**
   * Supprime une commande par son ID.
   *
   * @param idCommande   L'identifiant de la commande à supprimer.
   * @return Une action asynchrone renvoyant un message indiquant le succès ou l'échec de la suppression.
   */
  def deleteCommande(idCommande: Long): Action[AnyContent] = Action.async {
    commandeService.deleteCommande(idCommande).map {
      case true => Ok("Commande deleted successfully.")
      case false => NotFound("Commande not found.")
    }
  }

  /**
   * Met à jour le statut d'une commande spécifiée par son ID.
   *
   * @param idCommande   L'identifiant de la commande à mettre à jour.
   * @return Une action asynchrone renvoyant un message indiquant le succès ou l'échec de la mise à jour du statut.
   */
  def updateStatut(idCommande: Long): Action[CommandeUpdateStatut] = Action.async(parse.json[CommandeUpdateStatut]) { request =>
    commandeService.updateStatut(idCommande, request.body).map {
      case true => Ok("Statut updated successfully.")
      case false => NotFound("Commande not found.")
    }
  }

  /**
   * Met à jour une commande spécifiée par son ID.
   *
   * @param idCommande   L'identifiant de la commande à mettre à jour.
   * @return Une action asynchrone renvoyant un message indiquant le succès ou l'échec de la mise à jour de la commande.
   */
  def updateCommande(idCommande: Long): Action[CommandeFullUpdate] = Action.async(parse.json[CommandeFullUpdate]) { request =>
    commandeService.updateCommande(idCommande, request.body).map {
      case true => Ok("Commande updated successfully.")
      case false => NotFound("Commande not found.")
    }
  }
}
