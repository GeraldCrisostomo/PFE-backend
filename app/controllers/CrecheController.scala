package controllers

import javax.inject.Inject
import models.{CrecheCreate, CrecheUpdate}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CrecheService

import scala.concurrent.ExecutionContext

class CrecheController @Inject()(cc: ControllerComponents, crecheService: CrecheService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getAllCreches: Action[AnyContent] = Action.async {
    crecheService.getAllCreches.map { creches =>
      Ok(Json.toJson(creches))
    }
  }

  def getCrecheById(id: Long): Action[AnyContent] = Action.async {
    crecheService.getCrecheById(id).map {
      case Some(creche) => Ok(Json.toJson(creche))
      case None => NotFound
    }
  }

  def createCreche: Action[CrecheCreate] = Action.async(parse.json[CrecheCreate]) { request =>
    crecheService.createCreche(request.body).map { id =>
      Created(Json.obj("id_creche" -> id))
    }
  }

  def updateCreche(id: Long): Action[CrecheUpdate] = Action.async(parse.json[CrecheUpdate]) { request =>
    crecheService.updateCreche(id, request.body).map {
      case true => Ok("Creche updated successfully.")
      case false => NotFound("Creche not found.")
    }
  }

  def deleteCreche(id: Long): Action[AnyContent] = Action.async {
    crecheService.deleteCreche(id).map {
      case true => Ok("Creche deleted successfully.")
      case false => NotFound("Creche not found.")
    }
  }

}
