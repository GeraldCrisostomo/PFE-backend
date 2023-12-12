package services

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * Service gérant les opérations liées aux commandes par défaut.
 *
 * @param dbConfigProvider Fournisseur de configuration de base de données Slick.
 * @param ec               Contexte d'exécution pour les opérations asynchrones.
 */
class CommandeParDefautService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table "commandes_par_defaut"
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

  // Définition de la table "tournees_par_defaut"
  private class TourneeParDefautTable(tag: Tag) extends Table[TourneeParDefaut](tag, Some("pfe"), "tournees_par_defaut") {
    def id_tournee_par_defaut = column[Long]("id_tournee_par_defaut", O.PrimaryKey, O.AutoInc)
    def nom_par_defaut = column[Option[String]]("nom_par_defaut")
    def * = (id_tournee_par_defaut, nom_par_defaut) <> ((TourneeParDefaut.apply _).tupled, TourneeParDefaut.unapply)
  }

  private val tourneesParDefaut = TableQuery[TourneeParDefautTable]

  // Définition de la table "creches"
  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  /**
   * Récupère toutes les commandes par défaut associées à une tournée par défaut.
   *
   * @param idTourneeParDefaut ID de la tournée par défaut.
   * @return Future[List[CommandeParDefautWithCreche]] contenant la liste des commandes par défaut avec leurs crèches associées.
   */
  def getCommandesParDefaut(idTourneeParDefaut: Long): Future[List[CommandeParDefautWithCreche]] = {
    // Requête pour récupérer les commandes par défaut avec détails de la crèche associée
    val query = commandesParDefaut
      .filter(_.id_tournee_par_defaut === idTourneeParDefaut)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .result

    // Exécution de la requête et traitement des résultats
    dbConfig.db.run(query).map { result =>
      result.map {
        case (commande, creche) =>
          // Création de l'objet CommandeParDefautWithCreche avec les détails récupérés
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

  /**
   * Crée une nouvelle commande par défaut.
   *
   * @param idTourneeParDefaut ID de la tournée par défaut.
   * @param commandeCreate     Commande par défaut à créer.
   * @return Future[Long] contenant l'ID de la nouvelle commande par défaut.
   */
  def createCommandeParDefaut(idTourneeParDefaut: Long, commandeCreate: CommandeParDefautCreate): Future[Long] = {
    val commandeParDefaut = CommandeParDefaut(0, idTourneeParDefaut, commandeCreate.id_creche, commandeCreate.ordre)

    val action = (commandesParDefaut returning commandesParDefaut.map(_.id_commande_par_defaut)) += commandeParDefaut

    dbConfig.db.run(action)
  }

  /**
   * Récupère une commande par défaut par ID avec des détails supplémentaires.
   *
   * @param idCommandeParDefaut ID de la commande par défaut.
   * @return Future[Option[CommandeParDefautWithCrecheAndTourneeParDefaut]] contenant la commande par défaut avec ses détails.
   */
  def getCommandeParDefautById(idCommandeParDefaut: Long): Future[Option[CommandeParDefautWithCrecheAndTourneeParDefaut]] = {
    // Requête pour récupérer la commande par défaut avec détails de la crèche et de la tournée par défaut associées
    val query = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .join(creches)
      .on(_.id_creche === _.id_creche)
      .join(tourneesParDefaut)
      .on(_._1.id_tournee_par_defaut === _.id_tournee_par_defaut)
      .result
      .headOption

    // Exécution de la requête et traitement des résultats
    dbConfig.db.run(query).map {
      case Some(((commande, creche), tourneeParDefaut)) =>
        // Création de l'objet CommandeParDefautWithCrecheAndTourneeParDefaut avec les détails récupérés
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

  /**
   * Met à jour une commande par défaut par ID.
   *
   * @param idCommandeParDefaut   ID de la commande par défaut à mettre à jour.
   * @param updateCommandeParDefaut Nouvelles données de la commande par défaut.
   * @return Future[Boolean] indiquant si la mise à jour a réussi.
   */
  def updateCommandeParDefaut(idCommandeParDefaut: Long, updateCommandeParDefaut: CommandeParDefautUpdate): Future[Boolean] = {
    val updateQuery = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .map(_.ordre)
      .update(updateCommandeParDefaut.new_ordre)

    dbConfig.db.run(updateQuery).map(_ > 0)
  }

  /**
   * Supprime une commande par défaut par ID.
   *
   * @param idCommandeParDefaut ID de la commande par défaut à supprimer.
   * @return Future[Boolean] indiquant si la suppression a réussi.
   */
  def deleteCommandeParDefaut(idCommandeParDefaut: Long): Future[Boolean] = {
    val action = commandesParDefaut
      .filter(_.id_commande_par_defaut === idCommandeParDefaut)
      .delete

    dbConfig.db.run(action).map(_ > 0)
  }
}
