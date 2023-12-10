package services

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class CommandeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class CommandeTable(tag: Tag) extends Table[Commande](tag, Some("pfe"), "commandes") {
    def id_commande = column[Long]("id_commande", O.PrimaryKey, O.AutoInc)
    def id_tournee = column[Long]("id_tournee")
    def id_creche = column[Long]("id_creche") // Added id_creche column
    def ordre = column[Int]("ordre")
    def statut = column[String]("statut")
    def * = (id_commande, id_tournee, id_creche, ordre, statut) <> ((Commande.apply _).tupled, Commande.unapply)
  }

  private val commandes = TableQuery[CommandeTable]

  private class LigneCommandeTable(tag: Tag) extends Table[LigneCommande](tag, Some("pfe"), "lignes_commande") {
    def id_commande = column[Long]("id_commande")
    def id_article = column[Long]("id_article")
    def nb_caisses = column[Int]("nb_caisses")
    def nb_unites = column[Int]("nb_unites")
    def * = (id_commande, id_article, nb_caisses, nb_unites) <> ((LigneCommande.apply _).tupled, LigneCommande.unapply)
  }

  private val lignesCommande = TableQuery[LigneCommandeTable]

  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  private class TourneeTable(tag: Tag) extends Table[Tournee](tag, Some("pfe"), "tournees") {
    def id_tournee = column[Long]("id_tournee", O.PrimaryKey, O.AutoInc)
    def date = column[LocalDate]("date")
    def id_livreur = column[Option[Long]]("id_livreur")
    def nom = column[Option[String]]("nom")
    def statut = column[String]("statut")
    def * = (id_tournee, date, id_livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  def getCommandesByTourneeId(idTournee: Long): Future[List[CommandeWithDetails]] = {
    val query = commandes
      .filter(_.id_tournee === idTournee)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .join(tournees)
      .on(_._1.id_tournee === _.id_tournee)
      .result

    dbConfig.db.run(query).map { result =>
      result.map {
        case ((commande, creche), tournee) =>
          CommandeWithDetails(
            id_commande = commande.id_commande,
            tournee = Tournee(
              id_tournee = tournee.id_tournee,
              date = tournee.date,
              id_livreur = tournee.id_livreur,
              nom = tournee.nom,
              statut = tournee.statut
            ),
            creche = Creche(
              id_creche = creche.id_creche,
              nom = creche.nom,
              ville = creche.ville,
              rue = creche.rue
            ),
            ordre = commande.ordre,
            statut = commande.statut
          )
      }.toList
    }
  }


  def createCommande(idTournee: Long, commandeCreate: CommandeCreate): Future[Long] = {
    val commande = Commande(0, idTournee, commandeCreate.id_creche, commandeCreate.ordre, "en attente")

    val action = (for {
      commandeId <- (commandes returning commandes.map(_.id_commande)) += commande
    } yield commandeId).transactionally

    dbConfig.db.run(action)
  }

  def getCommandeById(idCommande: Long): Future[Option[CommandeWithAllDetails]] = {
    val commandesQuery = commandes
      .filter(_.id_commande === idCommande)
      .result.headOption

    val lignesCommandeQuery = getLignesCommandeByIdCommande(idCommande)

    val detailsQuery = for {
      commandeOption <- dbConfig.db.run(commandesQuery)
      lignesList <- lignesCommandeQuery
    } yield (commandeOption, lignesList)

    detailsQuery.flatMap {
      case (Some(commande), lignesList) =>
        val crecheQuery = creches
          .filter(_.id_creche === commande.id_creche)
          .result.headOption

        val tourneeQuery = tournees
          .filter(_.id_tournee === commande.id_tournee)
          .result.headOption

        val crecheAndTourneeQuery = for {
          crecheOption <- dbConfig.db.run(crecheQuery)
          tourneeOption <- dbConfig.db.run(tourneeQuery)
        } yield (crecheOption, tourneeOption)

        crecheAndTourneeQuery.map {
          case (Some(creche), Some(tournee)) =>
            Some(
              CommandeWithAllDetails(
                id_commande = commande.id_commande,
                tournee = Tournee(
                  id_tournee = tournee.id_tournee,
                  date = tournee.date,
                  id_livreur = tournee.id_livreur,
                  nom = tournee.nom,
                  statut = tournee.statut
                ),
                creche = Creche(
                  id_creche = creche.id_creche,
                  nom = creche.nom,
                  ville = creche.ville,
                  rue = creche.rue
                ),
                ordre = commande.ordre,
                statut = commande.statut,
                lignes_commande = lignesList
              )
            )
          case _ => None
        }
      case _ => Future.successful(None)
    }
  }

  def getLignesCommandeByIdCommande(idCommande: Long): Future[List[LigneCommande]] = {
    val query = lignesCommande
      .filter(_.id_commande === idCommande)
      .result

    dbConfig.db.run(query).map(_.toList)
  }

  def deleteCommande(idCommande: Long): Future[Boolean] = {
    val action = for {
      _ <- lignesCommande.filter(_.id_commande === idCommande).delete
      deletedRows <- commandes.filter(_.id_commande === idCommande).delete
    } yield deletedRows > 0

    dbConfig.db.run(action)
  }

  def updateStatut(idCommande: Long, updateStatut: CommandeUpdateStatut): Future[Boolean] = {
    val updateQuery = commandes
      .filter(_.id_commande === idCommande)
      .map(_.statut)
      .update(updateStatut.new_Statut)

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  def updateCommande(idCommande: Long, fullUpdate: CommandeFullUpdate): Future[Boolean] = {
    val updateQuery = commandes
      .filter(_.id_commande === idCommande)
      .map(c => (c.ordre, c.statut))
      .update((fullUpdate.new_ordre, fullUpdate.new_statut))

    val lignesUpdateQuery = DBIO.seq(fullUpdate.new_lignes_commande.map { ligneUpdate =>
      lignesCommande
        .filter(l => l.id_commande === idCommande && l.id_article === ligneUpdate.id_article)
        .map(l => (l.nb_caisses, l.nb_unites))
        .update((ligneUpdate.new_nb_caisses, ligneUpdate.new_nb_unites))
    }: _*)

    val combinedQuery = updateQuery.andThen(lignesUpdateQuery).transactionally

    dbConfig.db.run(combinedQuery).map(_ => true)
  }

}
