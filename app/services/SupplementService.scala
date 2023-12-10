package services


import models._
import services.ArticleService

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplementService @Inject()(dbConfigProvider: DatabaseConfigProvider, articleService: ArticleService, tourneeService: TourneeService)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._


  private class SupplementTable(tag: Tag) extends Table[Supplement](tag, Some("pfe"), "supplements"){
    def id_tournee = column[Long]("id_tournee")
    def id_article = column[Long]("id_article")
    def nb_unites = column[Option[Int]]("nb_unites", O.Default(Some(0)))
    def nb_caisses = column[Option[Int]]("nb_caisses", O.Default(Some(0)))

    def * = (id_tournee, id_article, nb_unites, nb_caisses) <> ((Supplement.apply _).tupled, Supplement.unapply)

    def id_supplement = primaryKey("id_tournee, id_article", (id_tournee, id_article))
  }

  private val supplements = TableQuery[SupplementTable]

  def getAllSupplements(id_tournee: Long): Future[List[Supplement]] = {
    val query = supplements.filter(_.id_tournee === id_tournee)
    dbConfig.db.run(query.to[List].result)
  }

  def updateSupplement(idTournee: Long, idArticle: Long, supplementUpdate: SupplementUpdate): Future[Boolean] = {
    val query = supplements
      .filter(s => s.id_tournee === idTournee && s.id_article === idArticle)
      .map(s => (s.nb_caisses, s.nb_unites))
      .update((supplementUpdate.nb_caisses, supplementUpdate.nb_unites))

    dbConfig.db.run(query).map(_ > 0)
  }


}
