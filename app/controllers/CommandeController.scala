package controllers

import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.CommandeService

import scala.concurrent.ExecutionContext

class CommandeController @Inject()(cc: ControllerComponents, commandeService: CommandeService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getCommandesByTourneeId(idTournee: Long) = Action.async {
    commandeService.getCommandesByTourneeId(idTournee).map { commandes =>
      Ok(Json.toJson(commandes))
    }
  }

  def createCommande(idTournee: Long) = Action.async(parse.json[CommandeCreate]) { request =>
    commandeService.createCommande(idTournee, request.body).map { id =>
      Created(Json.obj("id_commande" -> id))
    }
  }

  def getCommandeById(idCommande: Long) = Action.async {
    commandeService.getCommandeById(idCommande).map {
      case Some(commande) => Ok(Json.toJson(commande))
      case None => NotFound
    }
  }

  def deleteCommande(idCommande: Long) = Action.async {
    commandeService.deleteCommande(idCommande).map {
      case true => Ok("Commande deleted successfully.")
      case false => NotFound("Commande not found.")
    }
  }

  def updateStatut(idCommande: Long) = Action.async(parse.json[CommandeUpdateStatut]) { request =>
    commandeService.updateStatut(idCommande, request.body).map {
      case true => Ok("Statut updated successfully.")
      case false => NotFound("Commande not found.")
    }
  }

  def updateCommande(idCommande: Long) = Action.async(parse.json[CommandeFullUpdate]) { request =>
    commandeService.updateCommande(idCommande, request.body).map {
      case true => Ok("Commande updated successfully.")
      case false => NotFound("Commande not found.")
    }
  }
}
