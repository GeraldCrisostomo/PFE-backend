package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente un article avec son identifiant, libellé, taille (optionnelle) et pourcentage.
 *
 * @param id_article   L'identifiant unique de l'article.
 * @param libelle      Le libellé de l'article.
 * @param taille       La taille de l'article (optionnelle).
 * @param pourcentage  Le pourcentage associé à l'article.
 */
case class Article(
                    id_article: Long,
                    libelle: String,
                    taille: Option[String],
                    pourcentage: Int
                  )

/**
 * Représente un article sans le pourcentage, avec son identifiant, libellé et taille (optionnelle).
 *
 * @param id_article   L'identifiant unique de l'article.
 * @param libelle      Le libellé de l'article.
 * @param taille       La taille de l'article (optionnelle).
 */
case class ArticleSansPourcentage(
                                   id_article: Long,
                                   libelle: String,
                                   taille: Option[String]
                                 )

/**
 * Représente une mise à jour de pourcentage pour un article.
 *
 * @param new_pourcentage   Le nouveau pourcentage à appliquer à l'article.
 */
case class PourcentagePatch(
                             new_pourcentage: Int
                           )

object Article {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Article.
  implicit val format: OFormat[Article] = Json.format[Article]
}

object ArticleSansPourcentage {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets ArticleSansPourcentage.
  implicit val format: OFormat[ArticleSansPourcentage] = Json.format[ArticleSansPourcentage]
}

object PourcentagePatch {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets PourcentagePatch.
  implicit val format: OFormat[PourcentagePatch] = Json.format[PourcentagePatch]
}
