package services

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

/**
 * Service gérant les opérations liées aux commandes.
 *
 * @param dbConfigProvider Fournisseur de configuration de base de données Slick.
 * @param ec               Contexte d'exécution pour les opérations asynchrones.
 */
class CommandeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table "commandes"
  private class CommandeTable(tag: Tag) extends Table[Commande](tag, Some("pfe"), "commandes") {
    def id_commande = column[Long]("id_commande", O.PrimaryKey, O.AutoInc)
    def id_tournee = column[Long]("id_tournee")
    def id_creche = column[Long]("id_creche")
    def ordre = column[Int]("ordre")
    def statut = column[String]("statut")
    def * = (id_commande, id_tournee, id_creche, ordre, statut) <> ((Commande.apply _).tupled, Commande.unapply)
  }

  private val commandes = TableQuery[CommandeTable]

  // Définition de la table "lignes_commande"
  private class LigneCommandeTable(tag: Tag) extends Table[LigneCommande](tag, Some("pfe"), "lignes_commande") {
    def id_commande = column[Long]("id_commande")
    def id_article = column[Long]("id_article")
    def nb_caisses = column[Int]("nb_caisses")
    def nb_unites = column[Int]("nb_unites")
    def * = (id_commande, id_article, nb_caisses, nb_unites) <> ((LigneCommande.apply _).tupled, LigneCommande.unapply)
  }

  private val lignesCommande = TableQuery[LigneCommandeTable]

  // Définition de la table "creches"
  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  // Définition de la table "tournees"
  private class TourneeTable(tag: Tag) extends Table[Tournee](tag, Some("pfe"), "tournees") {
    def id_tournee = column[Long]("id_tournee", O.PrimaryKey, O.AutoInc)
    def date = column[LocalDate]("date")
    def id_livreur = column[Option[Long]]("id_livreur")
    def nom = column[Option[String]]("nom")
    def statut = column[String]("statut")
    def * = (id_tournee, date, id_livreur, nom, statut) <> ((Tournee.apply _).tupled, Tournee.unapply)
  }

  private val tournees = TableQuery[TourneeTable]

  // Définition de la table "articles"
  private class ArticleTable(tag: Tag) extends Table[Article](tag, Some("pfe"), "articles") {
    def id_article = column[Long]("id_article", O.PrimaryKey, O.AutoInc)
    def libelle = column[String]("libelle")
    def taille = column[Option[String]]("taille")
    def pourcentage = column[Int]("pourcentage")
    def * = (id_article, libelle, taille, pourcentage) <> ((Article.apply _).tupled, Article.unapply)
  }

  private val articles = TableQuery[ArticleTable]

  /**
   * Récupère les commandes avec des détails associés à une tournée.
   *
   * @param idTournee ID de la tournée.
   * @return Future[List[CommandeWithDetails]] contenant les détails des commandes.
   */
  def getCommandesByTourneeId(idTournee: Long): Future[List[CommandeWithDetails]] = {
    // Jointure des tables commandes, creches et tournees pour récupérer les détails des commandes
    val query = commandes
      .filter(_.id_tournee === idTournee)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .join(tournees)
      .on(_._1.id_tournee === _.id_tournee)
      .result

    // Exécution de la requête et mapping des résultats dans la structure CommandeWithDetails
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

  /**
   * Crée une nouvelle commande pour une tournée donnée.
   *
   * @param idTournee       ID de la tournée associée à la commande.
   * @param commandeCreate  Données de création de la commande.
   * @return Future[Long]   contenant l'ID de la nouvelle commande.
   */
  def createCommande(idTournee: Long, commandeCreate: CommandeCreate): Future[Long] = {
    val commande = Commande(0, idTournee, commandeCreate.id_creche, commandeCreate.ordre, "en attente")

    val action = (for {
      commandeId <- (commandes returning commandes.map(_.id_commande)) += commande
    } yield commandeId).transactionally

    dbConfig.db.run(action)
  }

  /**
   * Récupère les détails d'une commande en fonction de son ID.
   *
   * @param idCommande ID de la commande.
   * @return Future[Option[CommandeWithAllDetails]] contenant les détails de la commande.
   */
  def getCommandeById(idCommande: Long): Future[Option[CommandeWithAllDetails]] = {
    // Requête pour récupérer les détails de la commande
    val commandesQuery = commandes
      .filter(_.id_commande === idCommande)
      .result.headOption

    // Appel à la méthode pour récupérer les détails des lignes de commande associées à la commande
    val lignesCommandeQuery = getLignesCommandeByIdCommande(idCommande)

    // Exécution des requêtes et traitement des résultats
    val detailsQuery = for {
      commandeOption <- dbConfig.db.run(commandesQuery)
      lignesList <- lignesCommandeQuery
    } yield (commandeOption, lignesList)

    detailsQuery.flatMap {
      case (Some(commande), lignesList) =>
        // Requête pour récupérer les détails de la crèche associée à la commande
        val crecheQuery = creches
          .filter(_.id_creche === commande.id_creche)
          .result.headOption

        // Requête pour récupérer les détails de la tournée associée à la commande
        val tourneeQuery = tournees
          .filter(_.id_tournee === commande.id_tournee)
          .result.headOption

        // Exécution des requêtes et traitement des résultats
        val crecheAndTourneeQuery = for {
          crecheOption <- dbConfig.db.run(crecheQuery)
          tourneeOption <- dbConfig.db.run(tourneeQuery)
        } yield (crecheOption, tourneeOption)

        crecheAndTourneeQuery.map {
          case (Some(creche), Some(tournee)) =>
            // Création de l'objet CommandeWithAllDetails avec les détails récupérés
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

  /**
   * Récupère les lignes de commande avec des détails associés à une commande.
   *
   * @param idCommande ID de la commande.
   * @return Future[List[LigneCommandeWithDeltails]] contenant les détails des lignes de commande.
   */
  def getLignesCommandeByIdCommande(idCommande: Long): Future[List[LigneCommandeWithDeltails]] = {
    // Requête pour récupérer les lignes de commande avec détails
    val query = lignesCommande
      .filter(_.id_commande === idCommande)
      .join(articles).on(_.id_article === _.id_article)
      .result

    // Exécution de la requête et traitement des résultats
    dbConfig.db.run(query).map(_.toList.map {
      case (ligneCommande, article) =>
        // Création de l'objet LigneCommandeWithDeltails avec les détails récupérés
        LigneCommandeWithDeltails(
          ligneCommande.id_commande,
          ArticleSansPourcentage(article.id_article, article.libelle, article.taille),
          ligneCommande.nb_caisses,
          ligneCommande.nb_unites
        )
    })
  }


  /**
   * Supprime une commande et ses lignes de commande associées.
   *
   * @param idCommande ID de la commande à supprimer.
   * @return Future[Boolean] indiquant si la suppression a réussi.
   */
  def deleteCommande(idCommande: Long): Future[Boolean] = {
    val action = for {
      _ <- lignesCommande.filter(_.id_commande === idCommande).delete
      deletedRows <- commandes.filter(_.id_commande === idCommande).delete
    } yield deletedRows > 0

    dbConfig.db.run(action)
  }

  /**
   * Met à jour le statut d'une commande.
   *
   * @param idCommande   Identifiant de la commande à mettre à jour.
   * @param updateStatut Objet contenant le nouveau statut de la commande.
   * @return Un Future indiquant si la mise à jour a réussi (true) ou non (false).
   */
  def updateStatut(idCommande: Long, updateStatut: CommandeUpdateStatut): Future[Boolean] = {
    // Crée une requête pour mettre à jour le statut de la commande.
    val updateQuery = commandes
      .filter(_.id_commande === idCommande)
      .map(_.statut)
      .update(updateStatut.new_Statut)

    // Exécute la requête et renvoie un Future indiquant si la mise à jour a réussi.
    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  /**
   * Met à jour les détails d'une commande, y compris l'ordre, le statut et les lignes de commande.
   *
   * @param idCommande Identifiant de la commande à mettre à jour.
   * @param fullUpdate Objet contenant les nouvelles informations pour la mise à jour.
   * @return Un Future indiquant si la mise à jour a réussi (true) ou non (false).
   */
  def updateCommande(idCommande: Long, fullUpdate: CommandeFullUpdate): Future[Boolean] = {
    // Crée une requête pour mettre à jour l'ordre et le statut de la commande.
    val updateQuery = commandes
      .filter(_.id_commande === idCommande)
      .map(c => (c.ordre, c.statut))
      .update((fullUpdate.new_ordre, fullUpdate.new_statut))

    // Crée une séquence de requêtes pour mettre à jour les lignes de commande.
    val lignesUpdateQuery = DBIO.seq(fullUpdate.new_lignes_commande.map { ligneUpdate =>
      lignesCommande
        .filter(l => l.id_commande === idCommande && l.id_article === ligneUpdate.id_article)
        .map(l => (l.nb_caisses, l.nb_unites))
        .update((ligneUpdate.new_nb_caisses, ligneUpdate.new_nb_unites))
    }: _*)

    // Combine les requêtes en une seule transaction.
    val combinedQuery = updateQuery.andThen(lignesUpdateQuery).transactionally

    // Exécute la transaction et renvoie un Future indiquant si la mise à jour a réussi.
    dbConfig.db.run(combinedQuery).map(_ => true)
  }

}
