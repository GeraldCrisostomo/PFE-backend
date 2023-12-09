package models

import play.api.libs.json.{Json, OFormat}

case class Article(id_article: Long,
                   libelle: String,
                   taille: Option[String],
                   pourcentage: Int)
object Article {
  implicit val format: OFormat[Article] = Json.format[Article]
}

case class PourcentagePatch(new_pourcentage: Int)

object PourcentagePatch {
  implicit val format: OFormat[PourcentagePatch] = Json.format[PourcentagePatch]
}