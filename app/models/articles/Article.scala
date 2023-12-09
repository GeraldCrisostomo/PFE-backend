package models.articles

import play.api.libs.json.{Json, OFormat}

case class Article(id_article: Long,
                   libelle: String,
                   taille: Option[String],
                   pourcentage: Int)

object Article {
  implicit val format: OFormat[Article] = Json.format[Article]
}