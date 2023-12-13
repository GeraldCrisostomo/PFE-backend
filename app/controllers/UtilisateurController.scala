package controllers

import models.{ConnexionInfo, UtilisateurCreate, UtilisateurUpdate}

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.UtilisateurService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les opérations liées aux utilisateurs.
 *
 * @param cc                  Composants du contrôleur fournis par Play Framework.
 * @param utilisateurService  Service fournissant les opérations liées aux utilisateurs.
 * @param ec                  Contexte d'exécution nécessaire pour les opérations asynchrones.
 */
@Singleton
class UtilisateurController @Inject()(cc: ControllerComponents, utilisateurService: UtilisateurService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
   * Récupère tous les utilisateurs.
   *
   * @return Action[AnyContent] Action résultant en la liste des utilisateurs au format JSON.
   */
  def getAllUtilisateurs: Action[AnyContent] = Action.async {
    utilisateurService.getAllUtilisateurs.map(utilisateurs => Ok(Json.toJson(utilisateurs)))
  }

  /**
   * Récupère tous les utilisateurs d'un rôle spécifique.
   *
   * @param role Le rôle des utilisateurs à récupérer.
   * @return Action[AnyContent] Action résultant en la liste des utilisateurs au format JSON.
   */
  def getAllUtilisateursByRole(role: String): Action[AnyContent] = Action.async {
    utilisateurService.getAllUtilisateursByRole(role).map { utilisateurs =>
      Ok(Json.toJson(utilisateurs))
    }.recover {
      case e: Exception => InternalServerError(s"Une erreur s'est produite : ${e.getMessage}")
    }
  }

  /**
   * Récupère les informations d'un utilisateur spécifique.
   *
   * @param id_utilisateur L'identifiant unique de l'utilisateur à récupérer.
   * @return Action[AnyContent] Action résultant en les informations de l'utilisateur au format JSON.
   */
  def getUtilisateur(id_utilisateur: Long): Action[AnyContent] = Action.async {
    utilisateurService.getUtilisateur(id_utilisateur).map { utilisateur =>
      Ok(Json.toJson(utilisateur))
    }.recover {
      case e: Exception => InternalServerError(s"Une erreur s'est produite : ${e.getMessage}")
    }
  }

  /**
   * Crée un nouvel utilisateur.
   *
   * @return Action[UtilisateurCreate] Action résultant en la création de l'utilisateur.
   */
  def createUtilisateur: Action[UtilisateurCreate] = Action.async(parse.json[UtilisateurCreate]) { implicit request =>
    utilisateurService.createUtilisateur(request.body).map { userId =>
      Created(Json.obj("id_utilisateur" -> userId))
    }
  }

  /**
   * Modifie les informations d'un utilisateur spécifique.
   *
   * @param idUtilisateur L'identifiant unique de l'utilisateur à mettre à jour.
   * @return Action[UtilisateurUpdate] Action résultant en la mise à jour des informations de l'utilisateur.
   */
  def updateUtilisateur(idUtilisateur: Long): Action[UtilisateurUpdate] = Action.async(parse.json[UtilisateurUpdate]) { implicit request =>
    utilisateurService.updateUtilisateur(idUtilisateur, request.body).map {
      case true => Ok
      case false => NotFound
    }
  }

  /**
   * Supprime un utilisateur spécifique.
   *
   * @param idUtilisateur L'identifiant unique de l'utilisateur à supprimer.
   * @return Action[AnyContent] Action résultant en la suppression de l'utilisateur.
   */
  def deleteUtilisateur(idUtilisateur: Long): Action[AnyContent] = Action.async {
      utilisateurService.deleteUtilisateur(idUtilisateur).map {
        case true => Ok("Utilisateur supprimé avec succès")
        case false => NotFound("Utilisateur non trouvé")
      }
  }

  /**
   * Connecte un utilisateur en vérifiant les informations d'identification.
   *
   * @return Action[ConnexionInfo] Action résultant en la connexion de l'utilisateur.
   */
  def connectUtilisateur: Action[ConnexionInfo] = Action.async(parse.json[ConnexionInfo]) { implicit request =>
    utilisateurService.connectUtilisateur(request.body).map {
      case Some(utilisateur) => Ok(Json.toJson(utilisateur))
      case None => Unauthorized
    }
  }
}
