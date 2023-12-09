package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class Tournee(id_tournee: Long,
                   date: LocalDate,
                   id_livreur: Option[Long],
                   nom: Option[String],
                   statut: String)
object Tournee {
  implicit val format: OFormat[Tournee] = Json.format[Tournee]
}

case class TourneeCreation(date: LocalDate)
object TourneeCreation {
  implicit val format: OFormat[TourneeCreation] = Json.format[TourneeCreation]
}

case class TourneeUpdate(nom: Option[String],
                         statut: String)
object TourneeUpdate {
  implicit val format: OFormat[TourneeUpdate] = Json.format[TourneeUpdate]
}

case class TourneeResume(components: Seq[Resume])
object TourneeResume {
  implicit val format: OFormat[TourneeResume] = Json.format[TourneeResume]
}

case class Resume(article: ArticleSansPourcentage,
                  nb_caisses: Int,
                  nb_unites: Int)
object Resume {
  implicit val format: OFormat[Resume] = Json.format[Resume]
}

case class LivreurModification(id_livreur: Option[Long])
object LivreurModification {
  implicit val format: OFormat[LivreurModification] = Json.format[LivreurModification]
}

case class ArticleSansPourcentage(id_article: Long,
                   libelle: String,
                   taille: Option[String])
object ArticleSansPourcentage {
  implicit val format: OFormat[ArticleSansPourcentage] = Json.format[ArticleSansPourcentage]
}