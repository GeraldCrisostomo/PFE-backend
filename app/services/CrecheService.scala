package services

import javax.inject.Inject
import models.{Creche, CrecheCreate, CrecheUpdate, CrecheWithDetails, LigneCommandeParDefaut}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CrecheService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table "creches"
  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  // Définition de la table "lignes_commande_par_defaut"
  private class LigneCommandeParDefautTable(tag: Tag) extends Table[LigneCommandeParDefaut](tag, Some("pfe"), "lignes_commande_par_defaut") {
    def id_creche = column[Long]("id_creche")
    def id_article = column[Long]("id_article")
    def nb_caisses = column[Int]("nb_caisses", O.Default(0))
    def nb_unites = column[Int]("nb_unites", O.Default(0))
    def pk = primaryKey("pk_lignes_commande_par_defaut", (id_creche, id_article))
    def * = (id_creche, id_article, nb_caisses, nb_unites) <> ((LigneCommandeParDefaut.apply _).tupled, LigneCommandeParDefaut.unapply)
  }

  private val lignesCommandeParDefaut = TableQuery[LigneCommandeParDefautTable]

  /**
   * Récupère toutes les crèches depuis la base de données.
   *
   * @return Future[List[Creche]] contenant la liste des crèches.
   */
  def getAllCreches: Future[List[Creche]] =
    dbConfig.db.run(creches.to[List].result)

  /**
   * Récupère les détails d'une crèche par son ID, y compris les lignes de commande par défaut qui lui sont associées.
   *
   * @param id ID de la crèche.
   * @return Future[Option[CrecheWithDetails]] contenant les détails de la crèche.
   */
  def getCrecheById(id: Long): Future[Option[CrecheWithDetails]] = {
    // Requête pour récupérer les détails de la crèche
    val crecheQuery = creches
      .filter(_.id_creche === id)
      .result.headOption

    // Requête pour récupérer les lignes de commande par défaut associées à la crèche
    val lignesQuery = getLignesCommandeParDefautByIdCreche(id)

    // Exécution des deux requêtes en parallèle
    val result = for {
      crecheOption <- dbConfig.db.run(crecheQuery)
      lignesList <- lignesQuery
    } yield (crecheOption, lignesList)

    // Traitement des résultats
    result.map {
      case (Some(creche), lignesList) =>
        // Conversion des lignes de commande par défaut pour les détails
        val lignesParDefaut = lignesList.map { ligne =>
          LigneCommandeParDefaut(
            id_creche = ligne.id_creche,
            id_article = ligne.id_article,
            nb_caisses = ligne.nb_caisses,
            nb_unites = ligne.nb_unites
          )
        }

        // Création de l'objet CrecheWithDetails avec les données récupérées
        Some(
          CrecheWithDetails(
            id_creche = Some(creche.id_creche),
            nom = creche.nom,
            ville = creche.ville,
            rue = creche.rue,
            lignes_par_defaut = lignesParDefaut
          )
        )
      case _ =>
        // Aucune crèche trouvée, renvoie None
        None
    }
  }

  /**
   * Récupère les lignes de commande par défaut associées à une crèche par son ID.
   *
   * @param idCreche ID de la crèche.
   * @return Future[List[LigneCommandeParDefaut]] contenant la liste des lignes de commande par défaut.
   */
  def getLignesCommandeParDefautByIdCreche(idCreche: Long): Future[List[LigneCommandeParDefaut]] = {
    val query = lignesCommandeParDefaut
      .filter(_.id_creche === idCreche)
      .result

    dbConfig.db.run(query).map(_.toList)
  }

  /**
   * Crée une nouvelle crèche dans la base de données.
   *
   * @param crecheCreate Données pour la création de la nouvelle crèche.
   * @return Future[Long] contenant l'ID de la nouvelle crèche.
   */
  def createCreche(crecheCreate: CrecheCreate): Future[Long] =
    dbConfig.db.run((creches returning creches.map(_.id_creche)) += Creche(0, crecheCreate.nom, crecheCreate.ville, crecheCreate.rue))

  /**
   * Met à jour une crèche existante dans la base de données, y compris ses lignes de commande par défaut.
   *
   * @param id           ID de la crèche à mettre à jour.
   * @param crecheUpdate Données pour la mise à jour de la crèche.
   * @return Future[Boolean] indiquant si la mise à jour a réussi.
   */
  def updateCreche(id: Long, crecheUpdate: CrecheUpdate): Future[Boolean] = {
    // Requête pour mettre à jour les données de la crèche
    val updateCrecheQuery = creches
      .filter(_.id_creche === id)
      .map(c => (c.nom, c.ville, c.rue))
      .update((crecheUpdate.nom, crecheUpdate.ville, crecheUpdate.rue))

    // Met à jour les lignes_commande_par_defaut existantes avec les nouvelles valeurs
    val updateLignesCommandeParDefautQuery = DBIO.seq(
      crecheUpdate.new_lignes_par_defaut.map { ligne =>
        lignesCommandeParDefaut
          .filter(l => l.id_creche === id && l.id_article === ligne.id_article)
          .map(l => (l.nb_caisses, l.nb_unites))
          .update((ligne.nb_caisses, ligne.nb_unites))
      }: _*
    )

    // Transaction pour exécuter les deux mises à jour en une seule opération
    val transaction = DBIO.seq(
      updateCrecheQuery,
      updateLignesCommandeParDefautQuery
    )

    // Exécution de la transaction et renvoi du résultat (true si réussi)
    dbConfig.db.run(transaction).map(_ => true)
  }

  /**
   * Supprime une crèche de la base de données par son ID.
   *
   * @param id ID de la crèche à supprimer.
   * @return Future[Boolean] indiquant si la suppression a réussi.
   */
  def deleteCreche(id: Long): Future[Boolean] =
    dbConfig.db.run(creches.filter(_.id_creche === id).delete).map(_ > 0)
}
