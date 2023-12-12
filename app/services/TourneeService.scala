package services

import models.{LivreurModification, ResumeTournee, Tournee, TourneeAvecLivreur, TourneeCreation, TourneeCreationComplete, TourneeUpdate, TourneeUpdateDate, Utilisateur}

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{GetResult, JdbcProfile, PositionedResult}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TourneeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table des tournées
  private class TourneeTable(tag: Tag) extends Table[Tournee](tag, Some("pfe"), "tournees") {
    def id_tournee = column[Long]("id_tournee", O.PrimaryKey, O.AutoInc)
    def date = column[LocalDate]("date")
    def id_livreur = column[Option[Long]]("id_livreur")
    def nom = column[Option[String]]("nom")
    def statut = column[String]("statut")

    def * = (id_tournee, date, id_livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  // Définition de la table des utilisateurs
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

  /**
   * Récupère la liste des tournées avec livreur pour une date donnée.
   *
   * @param date Date des tournées à récupérer.
   * @return Future[List[TourneeAvecLivreur]] contenant la liste des tournées avec livreur.
   */
  def getTourneesByDate(date: LocalDate): Future[List[TourneeAvecLivreur]] = {
    val query = for {
      (t, u) <- tournees.filter(_.date === date) joinLeft utilisateurs on (_.id_livreur === _.id_utilisateur)
    } yield (t.id_tournee, t.date, t.id_livreur, u.map(_.nom), u.map(_.prenom), t.statut)

    dbConfig.db.run(query.to[List].result).map(_.map {
      case (id, dt, idLivreur, nom, prenom, statut) =>
        TourneeAvecLivreur(id, dt, idLivreur, nom, prenom, nom, statut)
    })
  }

  /**
   * Crée une nouvelle tournée.
   *
   * @param tourneeCreation Données de création de la tournée.
   * @return Future[Long] contenant l'ID de la nouvelle tournée.
   */
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

  /**
   * Crée une nouvelle tournée avec un nom.
   *
   * @param tourneeCreationComplete Données de création de la tournée.
   * @return Future[Long] contenant l'ID de la nouvelle tournée.
   */
  def createTourneeWithName(tourneeCreationComplete: TourneeCreationComplete): Future[Long] = {
    val insertTournee = (tournees returning tournees.map(_.id_tournee)) += Tournee(
      id_tournee = 0, // La valeur exacte n'importe pas ici, car elle sera générée par PostgreSQL
      date = tourneeCreationComplete.date,
      id_livreur = null,
      nom = tourneeCreationComplete.nom,
      statut = "en attente"
    )

    dbConfig.db.run(insertTournee)
  }

  /**
   * Récupère une tournée avec livreur par son ID.
   *
   * @param id_tournee ID de la tournée à récupérer.
   * @return Future[Option[TourneeAvecLivreur]] contenant la tournée avec livreur, ou None si elle n'existe pas.
   */
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

  /**
   * Met à jour une tournée.
   *
   * @param id_tournee ID de la tournée à mettre à jour.
   * @param tourneeUpdate Données de mise à jour de la tournée.
   * @return Future[Boolean] indiquant si la mise à jour a réussi.
   */
  def updateTournee(id_tournee: Long, tourneeUpdate: TourneeUpdate): Future[Boolean] = {
    val updateQuery = tournees
      .filter(_.id_tournee === id_tournee)
      .map(t => (t.nom, t.statut))
      .update((tourneeUpdate.nom, tourneeUpdate.statut))

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  /**
   * Met à jour la date d'une tournée existante dans la base de données.
   *
   * @param id_tournee        L'ID de la tournée à mettre à jour.
   * @param tourneeUpdateDate Données pour la mise à jour de la date de la tournée.
   * @return Un Future[Boolean] indiquant si la mise à jour de la date a réussi.
   */
  def updateTourneeDate(id_tournee: Long, tourneeUpdateDate: TourneeUpdateDate): Future[Boolean] = {
    val updateQuery = tournees
      .filter(_.id_tournee === id_tournee)
      .map(t => t.date)
      .update(tourneeUpdateDate.new_date)

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  /**
   * Supprime une tournée.
   *
   * @param id_tournee ID de la tournée à supprimer.
   * @return Future[Boolean] indiquant si la suppression a réussi.
   */
  def deleteTournee(id_tournee: Long): Future[Boolean] =
    dbConfig.db.run(tournees.filter(_.id_tournee === id_tournee).delete).map(_ > 0)

  // Définition de la table des résumés de tournées
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

  // Résultat personnalisé pour le mapping des résumés de tournées
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

  /**
   * Récupère le résumé d'une tournée par son ID.
   *
   * @param id_tournee ID de la tournée.
   * @return Future[List[ResumeTournee]] contenant la liste des résumés de la tournée.
   */
  def getTourneeResume(id_tournee: Long): Future[List[ResumeTournee]] = {
    val sqlQuery =
      s"""
         |SELECT id_tournee, id_article, libelle, taille, nb_caisses, nb_unites
         |FROM public.ResumesTournees
         |WHERE id_tournee = $id_tournee
         |""".stripMargin

    dbConfig.db.run(sql"""#$sqlQuery""".as[ResumeTournee]).map(_.toList)
  }

  /**
   * Modifie le livreur associé à une tournée.
   *
   * @param id_tournee ID de la tournée à mettre à jour.
   * @param livreurModification Données de modification du livreur.
   * @return Future[Boolean] indiquant si la mise à jour a réussi.
   */
  def modifierLivreur(id_tournee: Long, livreurModification: LivreurModification): Future[Boolean] = {
    val updateQuery = tournees.filter(_.id_tournee === id_tournee).map(_.id_livreur).update(livreurModification.id_livreur)
    dbConfig.db.run(updateQuery).map(_ > 0)
  }
}
