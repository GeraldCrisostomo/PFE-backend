package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.{ArticleService, SupplementService}

import scala.concurrent.ExecutionContext


@Singleton
class SupplementController @Inject()(cc: ControllerComponents, supplementService: SupplementService)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def getAllSupplements(id_tournee: Long): Action[AnyContent] = Action.async { implicit request =>
    supplementService.getAllSupplements(id_tournee).map(articles => Ok(Json.toJson(articles)))
  }
}


