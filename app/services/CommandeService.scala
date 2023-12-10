package services

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

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

  def getCommandesByTourneeId(idTournee: Long): Future[List[CommandeWithDetails]] = {
    val query = commandes
      .filter(_.id_tournee === idTournee)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .result

    dbConfig.db.run(query).map { result =>
      result.map {
        case (commande, creche) =>
          CommandeWithDetails(
            id_commande = commande.id_commande,
            tournee = Tournee(idTournee, ""), // Assurez-vous de fournir les détails appropriés de la tournée
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
    val commande = Commande(0, idTournee, commandeCreate.id_creche, commandeCreate.ordre, StatutEnum.EnAttente)
    val lignesCommandeList = commandeCreate.lignes_commande.map { ligneCreate =>
      LigneCommande(0, ligneCreate.id_article, ligneCreate.nb_caisses, ligneCreate.nb_unites)
    }

    val action = (for {
      commandeId <- (commandes returning commandes.map(_.id_commande)) += commande
      _ <- lignesCommande ++= lignesCommandeList.map(_.copy(id_commande = commandeId))
    } yield commandeId).transactionally

    dbConfig.db.run(action)
  }

  def getCommandeById(idCommande: Long): Future[Option[CommandeWithDetails]] = {
    val query = commandes
      .filter(_.id_commande === idCommande)
      .joinLeft(lignesCommande)
      .on(_.id_commande === _.id_commande)
      .result

    dbConfig.db.run(query).map { result =>
      result.headOption.map {
        case (commande, lignes) =>
          val lignesCommande = lignes.collect {
            case (_, Some(ligne)) => LigneCommande(ligne.id_article, ligne.nb_caisses, ligne.nb_unites)
          }.toList

          CommandeWithDetails(
            id_commande = commande.id_commande,
            tournee = Tournee(commande.id_tournee, ""), // Provide appropriate Tournee details
            creche = Creche(commande.id_creche, "", "", ""), // Provide appropriate Creche details
            ordre = commande.ordre,
            statut = commande.statut,
            lignes_commande = lignesCommande
          )
      }
    }
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

    dbConfig.db.run(combinedQuery).map(_ > 0)
  }
}
