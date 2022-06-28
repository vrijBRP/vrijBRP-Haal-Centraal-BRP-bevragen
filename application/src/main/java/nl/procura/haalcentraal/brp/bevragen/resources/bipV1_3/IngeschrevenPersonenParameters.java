/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

public class IngeschrevenPersonenParameters {

  public static final String EXPAND                                        = "expand";
  public static final String FIELDS                                        = "fields";
  public static final String BURGERSERVICENUMMER                           = "burgerservicenummer";
  public static final String GEBOORTE__DATUM                               = "geboorte__datum";
  public static final String GEBOORTE__PLAATS                              = "geboorte__plaats";
  public static final String GESLACHTSAANDUIDING                           = "geslachtsaanduiding";
  public static final String INCLUSIEFOVERLEDENPERSONEN                    = "inclusiefOverledenPersonen";
  public static final String NAAM__GESLACHTSNAAM                           = "naam__geslachtsnaam";
  public static final String NAAM__VOORVOEGSEL                             = "naam__voorvoegsel";
  public static final String NAAM__VOORNAMEN                               = "naam__voornamen";
  public static final String VERBLIJFPLAATS__GEMEENTEVANINSCHRIJVING       = "verblijfplaats__gemeenteVanInschrijving";
  public static final String VERBLIJFPLAATS__HUISLETTER                    = "verblijfplaats__huisletter";
  public static final String VERBLIJFPLAATS__HUISNUMMER                    = "verblijfplaats__huisnummer";
  public static final String VERBLIJFPLAATS__HUISNUMMERTOEVOEGING          = "verblijfplaats__huisnummertoevoeging";
  public static final String VERBLIJFPLAATS__NUMMERAANDUIDINGIDENTIFICATIE = "verblijfplaats__nummeraanduidingIdentificatie";
  public static final String VERBLIJFPLAATS__STRAAT                        = "verblijfplaats__straat";
  public static final String VERBLIJFPLAATS__POSTCODE                      = "verblijfplaats__postcode";

  public static boolean isExistingParameter(final String code) {
    try {
      IngeschrevenPersonenParameters.class.getField(code.toUpperCase());
      return true;
    } catch (NoSuchFieldException e) {
      return false;
    }
  }
}
