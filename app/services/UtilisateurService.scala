package services

import models._

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{GetResult, JdbcProfile, PositionedResult}

import java.time.LocalDate
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

  def getAllUtilisateurs: Future[List[Utilisateur]] =
    dbConfig.db.run(utilisateurs.to[List].result)

  def getAllUtilisateursByRole(role: String) :Future[List[Utilisateur]] ={
    val query = utilisateurs.filter(_.role === role)
    dbConfig.db.run(query.to[List].result)
  }

  def getUtilisateur(id_utilisateur: Long): Future[Option[Utilisateur]] = {
    val query = utilisateurs.filter(_.id_utilisateur === id_utilisateur).result.headOption
    dbConfig.db.run(query)
  }

}
