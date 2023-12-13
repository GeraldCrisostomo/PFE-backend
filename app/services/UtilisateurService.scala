package services

import models._

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UtilisateurService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

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
   * Récupère tous les utilisateurs.
   *
   * @return Une Future[List[Utilisateur]] représentant la liste de tous les utilisateurs.
   */
  def getAllUtilisateurs: Future[List[Utilisateur]] =
    dbConfig.db.run(utilisateurs.to[List].result)

  /**
   * Récupère tous les utilisateurs par rôle.
   *
   * @param role Le rôle des utilisateurs à récupérer.
   * @return Une Future[List[Utilisateur]] représentant la liste de tous les utilisateurs ayant le rôle spécifié.
   */
  def getAllUtilisateursByRole(role: String): Future[List[Utilisateur]] = {
    val query = utilisateurs.filter(_.role === role)
    dbConfig.db.run(query.to[List].result)
  }

  /**
   * Récupère un utilisateur par son identifiant unique.
   *
   * @param id_utilisateur L'identifiant unique de l'utilisateur.
   * @return Une Future[Option[Utilisateur]] représentant l'utilisateur s'il existe, None sinon.
   */
  def getUtilisateur(id_utilisateur: Long): Future[Option[Utilisateur]] = {
    val query = utilisateurs.filter(_.id_utilisateur === id_utilisateur).result.headOption
    dbConfig.db.run(query)
  }

  /**
   * Crée un nouvel utilisateur avec le mot de passe hashé en utilisant l'algorithme BCrypt.
   *
   * @param utilisateurCreate Les informations nécessaires pour créer un nouvel utilisateur.
   * @return Future[Long] L'identifiant unique de l'utilisateur créé.
   */
  def createUtilisateur(utilisateurCreate: UtilisateurCreate): Future[Long] = {
    // Hasher le mot de passe avec l'algorithme BCrypt
    val hashedPassword = BCrypt.hashpw(utilisateurCreate.mot_de_passe, BCrypt.gensalt())

    // Créer l'objet Utilisateur avec le mot de passe hashé
    val utilisateurToInsert = Utilisateur(
      id_utilisateur = 0,
      nom = utilisateurCreate.nom,
      prenom = utilisateurCreate.prenom,
      identifiant = utilisateurCreate.identifiant,
      mot_de_passe = hashedPassword, // Utiliser le mot de passe hashé
      role = utilisateurCreate.role
    )

    // Exécuter l'action d'insertion dans la base de données
    val insertAction = (utilisateurs returning utilisateurs.map(_.id_utilisateur)) += utilisateurToInsert
    dbConfig.db.run(insertAction)
  }

  /**
   * Met à jour les informations d'un utilisateur.
   *
   * @param id_utilisateur L'identifiant unique de l'utilisateur à mettre à jour.
   * @param utilisateurUpdate Les nouvelles informations pour l'utilisateur spécifié.
   * @return Une Future[Boolean] indiquant si la mise à jour a réussi (true) ou non (false).
   */
  def updateUtilisateur(id_utilisateur: Long, utilisateurUpdate: UtilisateurUpdate): Future[Boolean] = {
    val updateQuery = utilisateurs
      .filter(_.id_utilisateur === id_utilisateur)
      .map(u => (u.nom, u.prenom, u.identifiant, u.mot_de_passe, u.role))
      .update((utilisateurUpdate.nom, utilisateurUpdate.prenom, utilisateurUpdate.identifiant, utilisateurUpdate.mot_de_passe, utilisateurUpdate.role))

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  /**
   * Supprime un utilisateur spécifique.
   *
   * @param id_utilisateur L'identifiant unique de l'utilisateur à supprimer.
   * @return Une Future[Boolean] indiquant si la suppression a réussi (true) ou non (false).
   */
  def deleteUtilisateur(id_utilisateur: Long): Future[Boolean] = {
    val deleteQuery = utilisateurs.filter(_.id_utilisateur === id_utilisateur).delete
    dbConfig.db.run(deleteQuery).map(_ > 0)
  }

  /**
   * Connecte un utilisateur.
   *
   * @param connexionInfo Les informations d'identification de l'utilisateur.
   * @return Une Future[Option[Utilisateur]] représentant l'utilisateur connecté s'il existe, None sinon.
   */
  def connectUtilisateur(connexionInfo: ConnexionInfo): Future[Option[Utilisateur]] = {
    val query = utilisateurs
      .filter(u => u.identifiant === connexionInfo.identifiant)
      .result.headOption

    dbConfig.db.run(query).map {
      case Some(utilisateur) if BCrypt.checkpw(connexionInfo.mot_de_passe, utilisateur.mot_de_passe) =>
        Some(utilisateur)
      case _ =>
        None
    }
  }
}
