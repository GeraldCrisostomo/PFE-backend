package models.articles

import play.api.libs.json.{Json, OFormat}

case class PourcentagePatch(new_pourcentage: Int)

object PourcentagePatch {
  implicit val format: OFormat[PourcentagePatch] = Json.format[PourcentagePatch]
}
