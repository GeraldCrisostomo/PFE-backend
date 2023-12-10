package models

import play.api.libs.json.{Json, OFormat}

case class LigneCommande(
                          id_commande: Long,
                          id_article: Long,
                          nb_caisses: Int,
                          nb_unites: Int
                        )

case class LigneCommandeUpdate(
                                id_commande: Long,
                                id_article: Long,
                                new_nb_caisses: Int,
                                new_nb_unites: Int
                              )

object LigneCommande {
  implicit val format: OFormat[LigneCommande] = Json.format[LigneCommande]
}

object LigneCommandeUpdate {
  implicit val format: OFormat[LigneCommandeUpdate] = Json.format[LigneCommandeUpdate]
}
