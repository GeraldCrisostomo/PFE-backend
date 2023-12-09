package services

import models.{Tournee, TourneeCreation, TourneeUpdate, TourneeResume, LivreurModification, Resume, Article}

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TourneeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class TourneeTable(tag: Tag) extends Table[Tournee](tag, Some("pfe"), "tournees") {
    def id_tournee = column[Long]("id_tournee", O.PrimaryKey, O.AutoInc)
    def date = column[String]("date")
    def livreur = column[Long]("livreur")
    def nom = column[String]("nom")
    def statut = column[String]("statut")

    def * = (id_tournee, date, livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  def getAllTourneesForDate(date: String): Future[List[Tournee]] =
    dbConfig.db.run(tournees.filter(_.date === date).to[List].result)

  def createTournee(tourneeCreation: TourneeCreation): Future[Long] = {
    val newIdQuery = (tournees.map(_.id_tournee).max + 1).asColumnOf[Long]
    val insertTournee = tournees returning tournees.map(_.id_tournee) += Tournee(0, tourneeCreation.date, tourneeCreation.livreur, "", "en attente")
    dbConfig.db.run((newIdQuery, insertTournee).map((id, _) => id))
  }

  def getTourneeById(id_tournee: Long): Future[Option[Tournee]] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).result.headOption)

  def updateTournee(id_tournee: Long, tourneeUpdate: TourneeUpdate): Future[Boolean] = {
    val updateQuery = tournees.filter(_.id_tournee === id_tournee).map(t => (t.nom, t.statut)).update((tourneeUpdate.nom, tourneeUpdate.statut))
    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def deleteTournee(id_tournee: Long): Future[Boolean] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).delete).map(_ > 0)

  def getTourneeResume(id_tournee: Long): Future[Option[TourneeResume]] = {
    val query = for {
      (t, a) <- tournees.filter(_.id_tournee === id_tournee) joinLeft articles on (_.id_article === _.id_article)
    } yield (t, a)

    dbConfig.db.run(query.result).map { result =>
      result.headOption.map { case (tournee, articles) =>
        // Remplacez cela par la logique réelle pour obtenir les données du résumé
        val resume = Resume(Article(1, "Article 1", Some("Taille 1")), 5, 10)
        TourneeResume(Seq(resume))
      }
    }
  }

  def modifierLivreur(id_tournee: Long, livreurModification: LivreurModification): Future[Boolean] = {
    val updateQuery = tournees.filter(_.id_tournee === id_tournee).map(_.livreur).update(livreurModification.id_livreur)
    dbConfig.db.run(updateQuery).map(_ > 0)
  }
}
