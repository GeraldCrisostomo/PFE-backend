package services

import models._
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplementService @Inject()(dbConfigProvider: DatabaseConfigProvider, articleService: ArticleService, tourneeService: TourneeService)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  // Définition de la table des supplements
  private class SupplementTable(tag: Tag) extends Table[Supplement](tag, Some("pfe"), "supplements") {
    def id_tournee = column[Long]("id_tournee")
    def id_article = column[Long]("id_article")
    def nb_unites = column[Int]("nb_unites", O.Default(0))
    def nb_caisses = column[Int]("nb_caisses", O.Default(0))

    def * = (id_tournee, id_article, nb_unites, nb_caisses) <> ((Supplement.apply _).tupled, Supplement.unapply)

    def id_supplement = primaryKey("id_tournee, id_article", (id_tournee, id_article))
  }

  private val supplements = TableQuery[SupplementTable]

  /**
   * Récupère tous les supplements pour une tournée donnée.
   *
   * @param id_tournee ID de la tournée.
   * @return Future[List[Supplement]] contenant la liste des supplements.
   */
  def getAllSupplements(id_tournee: Long): Future[List[Supplement]] = {
    val query = supplements.filter(_.id_tournee === id_tournee)
    dbConfig.db.run(query.to[List].result)
  }

  /**
   * Met à jour les informations d'un supplement pour une tournée et un article donnés.
   *
   * @param idTournee ID de la tournée.
   * @param idArticle ID de l'article associé au supplement.
   * @param supplementUpdate Données de mise à jour du supplement.
   * @return Future[Option[Supplement]] contenant le supplement mis à jour, ou None s'il n'existe pas.
   */
  def updateSupplement(idTournee: Long, idArticle: Long, supplementUpdate: SupplementUpdate): Future[Option[Supplement]] = {
    // Requête pour mettre à jour les informations du supplement
    val query = supplements
      .filter(s => s.id_tournee === idTournee && s.id_article === idArticle)
      .map(s => (s.nb_caisses, s.nb_unites))
      .update((supplementUpdate.nb_caisses, supplementUpdate.nb_unites))

    // Requête pour récupérer le supplement mis à jour
    val updatedSupplement = supplements.filter(s => s.id_tournee === idTournee && s.id_article === idArticle).result.headOption

    // Exécution de la mise à jour et récupération du supplement mis à jour
    dbConfig.db.run(query.andThen(updatedSupplement))
  }
}
