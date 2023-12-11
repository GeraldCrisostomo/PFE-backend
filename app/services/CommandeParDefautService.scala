// services/CommandeParDefautService.scala
package services

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CommandeParDefautService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class CommandeParDefautTable(tag: Tag)
    extends Table[CommandeParDefaut](tag, Some("pfe"), "commandes_par_defaut") {
    def id_commande_par_defaut = column[Long]("id_commande_par_defaut", O.PrimaryKey, O.AutoInc)
    def id_tournee_par_defaut = column[Long]("id_tournee_par_defaut")
    def id_creche = column[Long]("id_creche")
    def ordre = column[Int]("ordre")
    def * = (id_commande_par_defaut, id_tournee_par_defaut, id_creche, ordre) <>
      ((CommandeParDefaut.apply _).tupled, CommandeParDefaut.unapply)
  }

  private val commandesParDefaut = TableQuery[CommandeParDefautTable]

  private class TourneeParDefautTable(tag: Tag) extends Table[TourneeParDefaut](tag, Some("pfe"), "tournees_par_defaut") {
    def id_tournee_par_defaut = column[Long]("id_tournee_par_defaut", O.PrimaryKey, O.AutoInc)
    def nom_par_defaut = column[Option[String]]("nom_par_defaut")
    def * = (id_tournee_par_defaut, nom_par_defaut) <> ((TourneeParDefaut.apply _).tupled, TourneeParDefaut.unapply)
  }

  private val tourneesParDefaut = TableQuery[TourneeParDefautTable]

  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  def getCommandesParDefaut(idTourneeParDefaut: Long): Future[List[CommandeParDefautWithCreche]] = {
    val query = commandesParDefaut
      .filter(_.id_tournee_par_defaut === idTourneeParDefaut)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .result

    dbConfig.db.run(query).map { result =>
      result.map {
        case (commande, creche) =>
          CommandeParDefautWithCreche(
            id_commande_par_defaut = commande.id_commande_par_defaut,
            id_tournee_par_defaut = commande.id_tournee_par_defaut,
            creche = Creche(
              id_creche = creche.id_creche,
              nom = creche.nom,
              ville = creche.ville,
              rue = creche.rue
            ),
            ordre = commande.ordre
          )
      }.toList
    }
  }

  def createCommandeParDefaut(idTourneeParDefaut: Long, commandeCreate: CommandeParDefautCreate): Future[Long] = {
    val commandeParDefaut = CommandeParDefaut(0, idTourneeParDefaut, commandeCreate.id_creche, commandeCreate.ordre)

    val action = (commandesParDefaut returning commandesParDefaut.map(_.id_commande_par_defaut)) += commandeParDefaut

    dbConfig.db.run(action)
  }

  def getCommandeParDefautById(idCommandeParDefaut: Long): Future[Option[CommandeParDefautWithCrecheAndTourneeParDefaut]] = {
    val query = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .join(tourneesParDefaut)
      .on(_._1.id_tournee_par_defaut === _.id_tournee_par_defaut)
      .result
      .headOption

    dbConfig.db.run(query).map {
      case Some(((commande, creche), tourneeParDefaut)) =>
        Some(
          CommandeParDefautWithCrecheAndTourneeParDefaut(
            id_commande_par_defaut = commande.id_commande_par_defaut,
            tournee_par_defaut = TourneeParDefaut(
              id_tournee_par_defaut = tourneeParDefaut.id_tournee_par_defaut,
              nom_par_defaut = tourneeParDefaut.nom_par_defaut
            ),
            creche = Creche(
              id_creche = creche.id_creche,
              nom = creche.nom,
              ville = creche.ville,
              rue = creche.rue
            ),
            ordre = commande.ordre
          )
        )
      case _ => None
    }
  }

  def updateCommandeParDefaut(idCommandeParDefaut: Long, updateCommandeParDefaut: CommandeParDefautUpdate): Future[Boolean] = {
    val updateQuery = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .map(_.ordre)
      .update(updateCommandeParDefaut.new_ordre)

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def deleteCommandeParDefaut(idCommandeParDefaut: Long): Future[Boolean] = {
    val action = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .delete

    dbConfig.db.run(action).map(_ > 0)
  }
}
