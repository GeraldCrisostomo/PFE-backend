package services

import models.{Article, PourcentagePatch}

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * Service gérant les opérations liées aux articles.
 *
 * @param dbConfigProvider Fournisseur de configuration de base de données Slick.
 * @param ec               Contexte d'exécution pour les opérations asynchrones.
 */
@Singleton
class ArticleService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  // Configuration de la base de données
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table "articles"
  private class ArticleTable(tag: Tag) extends Table[Article](tag, Some("pfe"), "articles") {
    def id_article = column[Long]("id_article", O.PrimaryKey, O.AutoInc)
    def libelle = column[String]("libelle")
    def taille = column[Option[String]]("taille")
    def pourcentage = column[Int]("pourcentage")

    def * = (id_article, libelle, taille, pourcentage) <> ((Article.apply _).tupled, Article.unapply)
  }

  // Représentation de la table "articles"
  private val articles = TableQuery[ArticleTable]

  /**
   * Récupère tous les articles de la base de données.
   *
   * @return Future[List[Article]] contenant la liste des articles.
   */
  def getAllArticles: Future[List[Article]] =
    dbConfig.db.run(articles.to[List].result)

  /**
   * Met à jour le pourcentage d'un article par ID.
   *
   * @param id    ID de l'article à mettre à jour.
   * @param patch Patch de pourcentage à appliquer.
   * @return Future[Option[Article]] contenant l'article mis à jour, s'il existe.
   */
  def updatePourcentage(id: Long, patch: PourcentagePatch): Future[Option[Article]] = {
    // Construction de la requête de mise à jour du pourcentage
    val query = articles.filter(_.id_article === id).map(_.pourcentage)
    val updateAction = query.update(patch.new_pourcentage)

    // Construction de la requête pour récupérer l'article mis à jour
    val getUpdatedArticleAction = articles.filter(_.id_article === id).result.headOption

    // Exécution des deux requêtes de manière transactionnelle
    dbConfig.db.run(updateAction.andThen(getUpdatedArticleAction))
  }
}
