package services

import models.{LivreurModification, ResumeTournee, Tournee, TourneeAvecLivreur, TourneeCreation, TourneeUpdate, Utilisateur}

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
    def statut = column[Option[String]]("statut", O.Default(Some("en attente")))

    def * = (id_tournee, date, id_livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  private class UtilisateurTable(tag: Tag) extends Table[Utilisateur](tag, Some("pfe"), "utilisateurs") {
    def id_utilisateur = column[Long]("id_utilisateur", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def prenom = column[String]("prenom")
    def identifiant = column[String]("identifiant")
    def mot_de_passe = column[String]("mot_de_passe")
    def role = column[String]("role")
    def * = (id_utilisateur, nom, prenom, identifiant, mot_de_passe, role) <> ((Utilisateur.apply _).tupled, Utilisateur.unapply)
  }

  private val utilisateurs = TableQuery[UtilisateurTable]

  def getTourneesByDate(date: LocalDate): Future[List[TourneeAvecLivreur]] = {
    val query = for {
      t <- tournees.filter(_.date === date)
      u <- utilisateurs.filter(_.id_utilisateur === t.id_livreur)
    } yield (t.id_tournee, t.date, t.id_livreur, u.nom, u.prenom, t.statut)

    dbConfig.db.run(query.to[List].result).map(_.map {
      case (id, dt, idLivreur, nom, prenom, statut) =>
        TourneeAvecLivreur(id, dt, idLivreur, Some(nom), Some(prenom), Some(nom), statut)
    })
  }

  def createTournee(tourneeCreation: TourneeCreation): Future[Long] = {
    val insertTournee = (tournees returning tournees.map(_.id_tournee)) += Tournee(
      id_tournee = 0, // La valeur exacte n'importe pas ici, car elle sera générée par PostgreSQL
      date = tourneeCreation.date,
      id_livreur = null,
      nom = null,
      statut = Some("en attente")
    )

    dbConfig.db.run(insertTournee)
  }

  def getTourneeById(id_tournee: Long): Future[Option[TourneeAvecLivreur]] = {
    val query = for {
      t <- tournees.filter(_.id_tournee === id_tournee)
      u <- utilisateurs.filter(_.id_utilisateur === t.id_livreur)
    } yield (t.id_tournee, t.date, t.id_livreur, u.nom, u.prenom, t.statut)

    dbConfig.db.run(query.result.headOption).map(_.map {
      case (id, dt, idLivreur, nom, prenom, statut) =>
        TourneeAvecLivreur(id, dt, idLivreur, Some(nom), Some(prenom), Some(nom), statut)
    })
  }


  def updateTournee(id_tournee: Long, tourneeUpdate: TourneeUpdate): Future[Boolean] = {
    val updateQuery = tournees
      .filter(_.id_tournee === id_tournee)
      .map(t => (t.nom, t.statut))
      .update((tourneeUpdate.nom, tourneeUpdate.statut))

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def deleteTournee(id_tournee: Long): Future[Boolean] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).delete).map(_ > 0)

  private class ResumesTourneesTable(tag: Tag) extends Table[ResumeTournee](tag, Some("public"), "ResumesTournees") {
    def id_tournee = column[Long]("id_tournee")
    def id_article = column[Long]("id_article")
    def libelle = column[String]("libelle")
    def taille = column[Option[String]]("taille")
    def nb_caisses = column[Int]("nb_caisses")
    def nb_unites = column[Int]("nb_unites")
    def * = (id_tournee, id_article, libelle, taille, nb_caisses, nb_unites) <> ((ResumeTournee.apply _).tupled, ResumeTournee.unapply)
  }

  private val resumesTournees = TableQuery[ResumesTourneesTable]

  implicit val getResumeTourneeResult: GetResult[ResumeTournee] = new GetResult[ResumeTournee] {
    def apply(r: PositionedResult): ResumeTournee =
      ResumeTournee(
        r.nextLong(),
        r.nextLong(),
        r.nextString(),
        r.nextStringOption(),
        r.nextInt(),
        r.nextInt()
      )
  }

  def getTourneeResume(id_tournee: Long): Future[List[ResumeTournee]] = {
    val sqlQuery =
      s"""
         |SELECT id_tournee, id_article, libelle, taille, nb_caisses, nb_unites
         |FROM public.ResumesTournees
         |WHERE id_tournee = $id_tournee
         |""".stripMargin

    dbConfig.db.run(sql"""#$sqlQuery""".as[ResumeTournee]).map(_.toList)
  }


  def modifierLivreur(id_tournee: Long, livreurModification: LivreurModification): Future[Boolean] = {
    val updateQuery = tournees.filter(_.id_tournee === id_tournee).map(_.id_livreur).update(livreurModification.id_livreur)
    dbConfig.db.run(updateQuery).map(_ > 0)
  }
}
