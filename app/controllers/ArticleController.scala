package controllers

import models.articles.PourcentagePatch

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.ArticleService

import scala.concurrent.ExecutionContext

@Singleton
class ArticleController @Inject()(cc: ControllerComponents, articleService: ArticleService)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def getAllArticles: Action[AnyContent] = Action.async { implicit request =>
    articleService.getAllArticles.map(articles => Ok(Json.toJson(articles)))
  }

  def updatePourcentage(id: Long): Action[PourcentagePatch] = Action.async(parse.json[PourcentagePatch]) { implicit request =>
    val patch = request.body
    articleService.updatePourcentage(id, patch).map {
      case Some(updatedArticle) => Ok(Json.toJson(updatedArticle))
      case None => NotFound
    }
  }
}
