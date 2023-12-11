package models

import play.api.libs.json.{Json, OFormat}

case class Article(
                    id_article: Long,
                    libelle: String,
                    taille: Option[String],
                    pourcentage: Int
                  )

case class ArticleSansPourcentage(
                                    id_article: Long,
                                    libelle: String,
                                    taille: Option[String]
                                 )

case class PourcentagePatch(
                             new_pourcentage: Int
                           )

object Article {
  implicit val format: OFormat[Article] = Json.format[Article]
}

object ArticleSansPourcentage {
  implicit val format: OFormat[ArticleSansPourcentage] = Json.format[ArticleSansPourcentage]
}

object PourcentagePatch {
  implicit val format: OFormat[PourcentagePatch] = Json.format[PourcentagePatch]
}