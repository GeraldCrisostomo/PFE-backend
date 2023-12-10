package controllers

import models._

import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{ArticleService, SupplementService}

import scala.concurrent.ExecutionContext


@Singleton
class SupplementController @Inject()(cc: ControllerComponents, supplementService: SupplementService)(
  implicit ec: ExecutionContext
) extends AbstractController(cc) {

  def getAllSupplements(id_tournee: Long): Action[AnyContent] = Action.async { implicit request =>
    supplementService.getAllSupplements(id_tournee).map { supplements => Ok(Json.toJson(supplements))
    }.recover{
      case e: Exception => InternalServerError(s"An error occured ${e.getMessage} ")
    }
  }

  def updateSupplement(idTournee: Long, idArticle: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val supplementUpdate = request.body.as[SupplementUpdate]
    supplementService.updateSupplement(idTournee, idArticle, supplementUpdate).map {
      case Some(updatedSupplement) => Ok(Json.toJson(updatedSupplement))
      case None => NotFound
    }

  }
}

