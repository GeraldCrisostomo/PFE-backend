package models

import play.api.libs.json.{Json, OFormat}

case class Tournee(id_tournee: Long,
                   date: String,
                   livreur: Long,
                   nom: String,
                   statut: String)
object Tournee {
  implicit val format: OFormat[Tournee] = Json.format[Tournee]
}

case class TourneeCreation(date: String)
object TourneeCreation {
  implicit val format: OFormat[TourneeCreation] = Json.format[TourneeCreation]
}

case class TourneeUpdate(nom: String,
                         statut: String)
object TourneeUpdate {
  implicit val format: OFormat[TourneeUpdate] = Json.format[TourneeUpdate]
}

case class TourneeResume(components: Seq[Resume])
object TourneeResume {
  implicit val format: OFormat[TourneeResume] = Json.format[TourneeResume]
}

case class Resume(article: Article,
                  nb_caisses: Int,
                  nb_unites: Int)
object Resume {
  implicit val format: OFormat[Resume] = Json.format[Resume]
}

case class LivreurModification(id_livreur: Long)
object LivreurModification {
  implicit val format: OFormat[LivreurModification] = Json.format[LivreurModification]
}