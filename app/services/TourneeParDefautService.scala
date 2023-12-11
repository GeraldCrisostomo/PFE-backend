package services

import models.{TourneeParDefaut, TourneeParDefautCreate, TourneeParDefautUpdate}

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TourneeParDefautService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class TourneeParDefautTable(tag: Tag) extends Table[TourneeParDefaut](tag, Some("pfe"), "tournees_par_defaut") {
    def id_tournee_par_defaut = column[Long]("id_tournee_par_defaut", O.PrimaryKey, O.AutoInc)
    def nom_par_defaut = column[Option[String]]("nom_par_defaut")
    def * = (id_tournee_par_defaut, nom_par_defaut) <> ((TourneeParDefaut.apply _).tupled, TourneeParDefaut.unapply)
  }

  private val tourneesParDefaut = TableQuery[TourneeParDefautTable]

  def getTourneesParDefaut: Future[List[TourneeParDefaut]] = {
    dbConfig.db.run(tourneesParDefaut.result).map(_.toList)
  }

  def createTourneeParDefaut(tourneeParDefautCreate: TourneeParDefautCreate): Future[Long] = {
    val insertTourneeParDefaut = (tourneesParDefaut returning tourneesParDefaut.map(_.id_tournee_par_defaut)) +=
      TourneeParDefaut(0, tourneeParDefautCreate.nom_par_defaut)

    dbConfig.db.run(insertTourneeParDefaut)
  }

  def getTourneeParDefautById(id_tournee_par_defaut: Long): Future[Option[TourneeParDefaut]] = {
    val query = tourneesParDefaut.filter(_.id_tournee_par_defaut === id_tournee_par_defaut)
    dbConfig.db.run(query.result.headOption)
  }

  def updateTourneeParDefaut(id_tournee_par_defaut: Long, tourneeParDefautUpdate: TourneeParDefautUpdate): Future[Boolean] = {
    val updateQuery = tourneesParDefaut.filter(_.id_tournee_par_defaut === id_tournee_par_defaut)
      .map(_.nom_par_defaut)
      .update(tourneeParDefautUpdate.new_nom_par_defaut)

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def deleteTourneeParDefaut(id_tournee_par_defaut: Long): Future[Boolean] = {
    val deleteQuery = tourneesParDefaut.filter(_.id_tournee_par_defaut === id_tournee_par_defaut).delete
    dbConfig.db.run(deleteQuery).map(_ > 0)
  }

}
