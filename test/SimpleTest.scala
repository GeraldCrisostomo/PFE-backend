import org.scalatest.funsuite.AnyFunSuite

/**
 * Classe de test simple avec un test réussi toujours.
 */
class SimpleTest extends AnyFunSuite {

  /**
   * Test : Toujours réussi.
   * Vérifie si l'assertion `true` est vraie, ce qui réussit toujours.
   */
  test("Always succeeds") {
    assert(true)
  }

  /**
   * Test : Toujours raté.
   * Vérifie si l'assertion `false` est vraie, ce qui ne réussit jamais.
   */
  test("Never succeeds") {
    assert(false)
  }
}
