package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def testDatabaseConnection() = Action.async { implicit request =>
    // Exemple de requête pour tester la connexion à la base de données
    val query = sql"SELECT 1".as[Int]

    db.run(query).map { result =>
      Ok(s"Connexion à la base de données réussie. Résultat de la requête : $result")
    }.recover {
      case ex: Exception =>
        InternalServerError(s"Erreur lors de la connexion à la base de données : ${ex.getMessage}")
    }
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
