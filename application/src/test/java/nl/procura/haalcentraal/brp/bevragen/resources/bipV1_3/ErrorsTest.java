package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

/**
 * {@link features/foutafhandeling.features}
 */
public class ErrorsTest {

  /*
  Scenario: geen enkele zoekparameter opgegeven in zoekvraag
  Als ingeschrevenpersonen worden gezocht zonder parameters
  Dan is de http status code van het antwoord 400
  En is in het antwoord status=400
  En eindigt attribuut instance met /ingeschrevenpersonen
  En is in het antwoord code=paramsRequired
  En komt attribuut invalidParams niet voor in het antwoord
  */
  public void mustReturnErrorOnNoParameter() {
  }

  /*
  Scenario: personen zoeken zonder minimale combinatie van zoekparameters
  Als ingeschrevenpersonen worden gezocht met naam__geslachtsnaam=jansen
  Dan is de http status code van het antwoord 400
  En is in het antwoord status=400
  En eindigt attribuut instance met ingeschrevenpersonen?naam__geslachtsnaam=jansen
  En is in het antwoord code=paramsCombination
  En komt attribuut invalidParams niet voor in het antwoord
  */
  public void mustReturnErrorWithOutMinimalArguments() {
  }

  /*
  Scenario: meerdere fouten in parameters
  Als ingeschrevenpersonen worden gezocht met verblijfplaats__huisnummer=a&verblijfplaats__postcode=b&inclusiefoverledenpersonen=c&geboorte__datum=d
  Dan is de http status code van het antwoord 400
  En is in het antwoord status=400
  En eindigt attribuut instance met ingeschrevenpersonen?huisnummer=a&postcode=b&inclusiefoverledenpersonen=c&geboorte__datum=d
  En bevat invalidParams exact 4 voorkomen(s)
  En is er een invalidParams met name=verblijfplaats__huisnummer
  En is er een invalidParams met name=verblijfplaats__postcode
  En is er een invalidParams met name=inclusiefoverledenpersonen
  En is er een invalidParams met name=geboorte__datum
  */
  public void mustSupportMultipleErrors() {
  }

  /*
  Scenario: niet gevonden
  Als de ingeschrevenpersonen wordt geraadpleegd met burgerservicenummer=123456789
  Dan is de http status code van het antwoord 404
  En is in het antwoord status=404
  En is in het antwoord code=notFound
  En komt attribuut invalidParams niet voor in het antwoord
  */
  public void mustReturn404OnNotFoundResource() {
  }

  /*
  Scenario: niet ondersteund contenttype
  Als de ingeschrevenpersonen wordt geraadpleegd met acceptheader application/xml
  Dan is de http status code van het antwoord 406
  En is in het antwoord status=406
  En is in het antwoord code=notAcceptable
  En komt attribuut invalidParams niet voor in het antwoord
  */
  public void mustReturnErrorOnUnknownContentType() {
  }

  /*
  Scenario: bronservice is niet beschikbaar
  Als een ingeschreven persoon wordt geraadpleegd
  En de bron GBA-V geen response of een timeout geeft
  Dan is de http status code van het antwoord 503
  En is in het antwoord status=503
  En is in het antwoord code=sourceUnavailable
  En komt attribuut invalidParams niet voor in het antwoord
  Als een ingeschreven persoon wordt geraadpleegd
  En de bron GBA-V geeft de foutmelding “Service is niet geactiveerd voor dit account.”
  Dan is de http status code van het antwoord 503
  En is in het antwoord status=503
  En is in het antwoord code=sourceUnavailable
  En komt attribuut invalidParams niet voor in het antwoord
  */
  public void mustReturn503Unavailable() {
  }
}
