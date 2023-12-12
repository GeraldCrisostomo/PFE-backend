package controllers

import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CommandeParDefautService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les opérations liées aux commandes par défaut.
 *
 * @param cc                        Les composants du contrôleur.
 * @param commandeParDefautService  Le service de commandes par défaut injecté.
 * @param ec                        L'exécution implicite contextuelle.
 */
class CommandeParDefautController @Inject()(cc: ControllerComponents,
                                            commandeParDefautService: CommandeParDefautService)
                                           (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
   * Récupère les commandes par défaut liées à une tournée par défaut spécifiée par son ID.
   *
   * @param idTourneeParDefaut   L'identifiant de la tournée par défaut.
   * @return Une action asynchrone renvoyant la liste des commandes par défaut au format JSON.
   */
  def getCommandesParDefaut(idTourneeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.getCommandesParDefaut(idTourneeParDefaut).map { commandes =>
      Ok(Json.toJson(commandes))
    }
  }

  /**
   * Crée une nouvelle commande par défaut pour une tournée par défaut spécifiée par son ID.
   *
   * @param idTourneeParDefaut   L'identifiant de la tournée par défaut.
   * @return Une action asynchrone renvoyant l'ID de la commande par défaut créée au format JSON.
   */
  def createCommandeParDefaut(idTourneeParDefaut: Long): Action[CommandeParDefautCreate] =
    Action.async(parse.json[CommandeParDefautCreate]) { request =>
      commandeParDefautService.createCommandeParDefaut(idTourneeParDefaut, request.body).map { id =>
        Created(Json.obj("id_commande_par_defaut" -> id))
      }
    }

  /**
   * Récupère une commande par défaut par son ID.
   *
   * @param idCommandeParDefaut   L'identifiant de la commande par défaut.
   * @return Une action asynchrone renvoyant la commande par défaut au format JSON, ou NotFound si la commande par défaut n'est pas trouvée.
   */
  def getCommandeParDefautById(idCommandeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.getCommandeParDefautById(idCommandeParDefaut).map {
      case Some(commande) => Ok(Json.toJson(commande))
      case None => NotFound
    }
  }

  /**
   * Met à jour une commande par défaut spécifiée par son ID.
   *
   * @param idCommandeParDefaut   L'identifiant de la commande par défaut à mettre à jour.
   * @return Une action asynchrone renvoyant un message indiquant le succès ou l'échec de la mise à jour de la commande par défaut.
   */
  def updateCommandeParDefaut(idCommandeParDefaut: Long): Action[CommandeParDefautUpdate] =
    Action.async(parse.json[CommandeParDefautUpdate]) { request =>
      commandeParDefautService.updateCommandeParDefaut(idCommandeParDefaut, request.body).map {
        case true => Ok("Commande par défaut mise à jour avec succès.")
        case false => NotFound("Commande par défaut non trouvée.")
      }
    }

  /**
   * Supprime une commande par défaut par son ID.
   *
   * @param idCommandeParDefaut   L'identifiant de la commande par défaut à supprimer.
   * @return Une action asynchrone renvoyant un message indiquant le succès ou l'échec de la suppression de la commande par défaut.
   */
  def deleteCommandeParDefaut(idCommandeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.deleteCommandeParDefaut(idCommandeParDefaut).map {
      case true => Ok("Commande par défaut supprimée avec succès.")
      case false => NotFound("Commande par défaut non trouvée.")
    }
  }
}
