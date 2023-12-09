package services

import models.{ArticleSansPourcentage, LivreurModification, Resume, Tournee, TourneeCreation, TourneeResume, TourneeUpdate}

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{GetResult, JdbcProfile, PositionedResult}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TourneeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class TourneeTable(tag: Tag) extends Table[Tournee](tag, Some("pfe"), "tournees") {
    def id_tournee = column[Long]("id_tournee", O.PrimaryKey, O.AutoInc)
    def date = column[LocalDate]("date")
    def id_livreur = column[Option[Long]]("id_livreur")
    def nom = column[Option[String]]("nom")
    def statut = column[String]("statut")

    def * = (id_tournee, date, id_livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  def getTourneesByDate(date: LocalDate): Future[List[Tournee]] = {
    val query = tournees.filter(_.date === date)
    dbConfig.db.run(query.to[List].result)
  }

  def createTournee(tourneeCreation: TourneeCreation): Future[Long] = {
    val insertTournee = (tournees returning tournees.map(_.id_tournee)) += Tournee(
      id_tournee = 0, // La valeur exacte n'importe pas ici, car elle sera générée par PostgreSQL
      date = tourneeCreation.date,
      id_livreur = null,
      nom = null,
      statut = "en attente"
    )

    dbConfig.db.run(insertTournee)
  }

  def getTourneeById(id_tournee: Long): Future[Option[Tournee]] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).result.headOption)

  def updateTournee(id_tournee: Long, tourneeUpdate: TourneeUpdate): Future[Boolean] = {
    val updateQuery = tournees
      .filter(_.id_tournee === id_tournee)
      .map(t => (t.nom, t.statut))
      .update((tourneeUpdate.nom, tourneeUpdate.statut))

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def deleteTournee(id_tournee: Long): Future[Boolean] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).delete).map(_ > 0)

  case class ResumesTourneesResult(id_article: Long,
                                   libelle: String,
                                   taille: Option[String],
                                   nb_caisses: Int,
                                   nb_unites: Int)

  implicit val getResumesTourneesResult: GetResult[ResumesTourneesResult] = new GetResult[ResumesTourneesResult] {
    def apply(rs: PositionedResult): ResumesTourneesResult =
      ResumesTourneesResult(
        rs.nextLong(),
        rs.nextString(),
        rs.nextStringOption(),
        rs.nextInt(),
        rs.nextInt()
      )
  }

  def getTourneeResume(id_tournee: Long): Future[Option[TourneeResume]] = {
    val sqlQuery =
      s"""
         |SELECT id_article, libelle, taille, nb_caisses, nb_unites
         |FROM ResumesTournees
         |WHERE id_tournee = $id_tournee
         |""".stripMargin

    // Exécuter la requête SQL et mapper les résultats à la classe ResumesTourneesResult
    dbConfig.db.run(sql"""#$sqlQuery""".as[ResumesTourneesResult]).map { result =>
      result.headOption.map { case ResumesTourneesResult(id_article, libelle, taille, nb_caisses, nb_unites) =>
        // Créer un objet TourneeResume à partir des résultats
        val resume = Resume(ArticleSansPourcentage(id_article, libelle, taille), nb_caisses, nb_unites)
        TourneeResume(Seq(resume))
      }
    }
  }

  def modifierLivreur(id_tournee: Long, livreurModification: LivreurModification): Future[Boolean] = {
    val updateQuery = tournees.filter(_.id_tournee === id_tournee).map(_.id_livreur).update(livreurModification.id_livreur)
    dbConfig.db.run(updateQuery).map(_ > 0)
  }
}
