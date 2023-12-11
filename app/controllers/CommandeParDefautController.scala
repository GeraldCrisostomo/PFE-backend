package controllers

import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CommandeParDefautService

import scala.concurrent.ExecutionContext

class CommandeParDefautController @Inject()(cc: ControllerComponents,
                                             commandeParDefautService: CommandeParDefautService)
                                           (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getCommandesParDefaut(idTourneeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.getCommandesParDefaut(idTourneeParDefaut).map { commandes =>
      Ok(Json.toJson(commandes))
    }
  }

  def createCommandeParDefaut(idTourneeParDefaut: Long): Action[CommandeParDefautCreate] =
    Action.async(parse.json[CommandeParDefautCreate]) { request =>
      commandeParDefautService.createCommandeParDefaut(idTourneeParDefaut, request.body).map { id =>
        Created(Json.obj("id_commande_par_defaut" -> id))
      }
    }

  def getCommandeParDefautById(idCommandeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.getCommandeParDefautById(idCommandeParDefaut).map {
      case Some(commande) => Ok(Json.toJson(commande))
      case None           => NotFound
    }
  }

  def updateCommandeParDefaut(idCommandeParDefaut: Long): Action[CommandeParDefautUpdate] =
    Action.async(parse.json[CommandeParDefautUpdate]) { request =>
      commandeParDefautService.updateCommandeParDefaut(idCommandeParDefaut, request.body).map {
        case true  => Ok("Commande par défaut mise à jour avec succès.")
        case false => NotFound("Commande par défaut non trouvée.")
      }
    }

  def deleteCommandeParDefaut(idCommandeParDefaut: Long): Action[AnyContent] = Action.async {
    commandeParDefautService.deleteCommandeParDefaut(idCommandeParDefaut).map {
      case true  => Ok("Commande par défaut supprimée avec succès.")
      case false => NotFound("Commande par défaut non trouvée.")
    }
  }
}
