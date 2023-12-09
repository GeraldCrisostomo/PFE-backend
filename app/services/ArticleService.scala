package services

import javax.inject._
import models.articles.{Article, PourcentagePatch}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ArticleService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class ArticleTable(tag: Tag) extends Table[Article](tag, Some("pfe"), "articles") {
    def id_article = column[Long]("id_article", O.PrimaryKey, O.AutoInc)
    def libelle = column[String]("libelle")
    def taille = column[Option[String]]("taille")
    def pourcentage = column[Int]("pourcentage")

    def * = (id_article, libelle, taille, pourcentage) <> ((Article.apply _).tupled, Article.unapply)
  }

  private val articles = TableQuery[ArticleTable]

  def getAllArticles: Future[List[Article]] =
    dbConfig.db.run(articles.to[List].result)

  def updatePourcentage(id: Long, patch: PourcentagePatch): Future[Option[Article]] = {
    val query = articles.filter(_.id_article === id).map(_.pourcentage)
    val updateAction = query.update(patch.new_pourcentage)
    val getUpdatedArticleAction = articles.filter(_.id_article === id).result.headOption

    dbConfig.db.run(updateAction.andThen(getUpdatedArticleAction))
  }
}
