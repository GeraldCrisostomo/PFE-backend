package models

import play.api.libs.json.{Json, OFormat}

case class CommandeParDefaut(
                              id_commande_par_defaut: Long,
                              id_tournee_par_defaut: Long,
                              id_creche: Long,
                              ordre: Int
                            )

case class CommandeParDefautWithCreche(
                                        id_commande_par_defaut: Long,
                                        id_tournee_par_defaut: Long,
                                        creche: Creche,
                                        ordre: Int
                                      )

case class CommandeParDefautWithCrecheAndTourneeParDefaut(
                                                            id_commande_par_defaut: Long,
                                                            tournee_par_defaut: TourneeParDefaut,
                                                            creche: Creche,
                                                            ordre: Int
                                                          )

case class CommandeParDefautCreate(
                                    id_creche: Long,
                                    ordre: Int
                                  )

case class CommandeParDefautUpdate(
                                    new_ordre: Int
                                  )

object CommandeParDefaut {
  implicit val format: OFormat[CommandeParDefaut] = Json.format[CommandeParDefaut]
}

object CommandeParDefautWithCreche {
  implicit val format: OFormat[CommandeParDefautWithCreche] = Json.format[CommandeParDefautWithCreche]
}

object CommandeParDefautWithCrecheAndTourneeParDefaut {
  implicit val format: OFormat[CommandeParDefautWithCrecheAndTourneeParDefaut] = Json.format[CommandeParDefautWithCrecheAndTourneeParDefaut]
}

object CommandeParDefautCreate {
  implicit val format: OFormat[CommandeParDefautCreate] = Json.format[CommandeParDefautCreate]
}

object CommandeParDefautUpdate {
  implicit val format: OFormat[CommandeParDefautUpdate] = Json.format[CommandeParDefautUpdate]
}
