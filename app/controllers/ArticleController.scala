package controllers

import models.PourcentagePatch

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.ArticleService

import scala.concurrent.ExecutionContext

/**
 * Contrôleur pour gérer les opérations liées aux articles.
 *
 * @param cc               Les composants du contrôleur.
 * @param articleService   Le service d'articles injecté.
 * @param ec               L'exécution implicite contextuelle.
 */
@Singleton
class ArticleController @Inject()(cc: ControllerComponents, articleService: ArticleService)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  /**
   * Récupère tous les articles.
   *
   * @return Une action asynchrone renvoyant la liste des articles au format JSON.
   */
  def getAllArticles: Action[AnyContent] = Action.async { implicit _ =>
    articleService.getAllArticles.map(articles => Ok(Json.toJson(articles)))
  }

  /**
   * Met à jour le pourcentage d'un article spécifié par son ID.
   *
   * @param id      L'identifiant de l'article à mettre à jour.
   * @return Une action asynchrone renvoyant l'article mis à jour au format JSON, ou NotFound si l'article n'est pas trouvé.
   */
  def updatePourcentage(id: Long): Action[PourcentagePatch] = Action.async(parse.json[PourcentagePatch]) { implicit request =>
    val patch = request.body
    articleService.updatePourcentage(id, patch).map {
      case Some(updatedArticle) => Ok(Json.toJson(updatedArticle))
      case None => NotFound
    }
  }
}
