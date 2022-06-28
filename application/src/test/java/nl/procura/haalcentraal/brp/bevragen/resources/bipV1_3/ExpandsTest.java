package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

/**
 * {@link features/expands.features}
 */
public class ExpandsTest {

  /*
  Scenario: Wanneer de expand-parameter niet is meegegeven, worden gerelateerden niet meegeladen
  Als een ingeschreven persoon wordt geraadpleegd zonder expand-parameter
  Dan is in het antwoord attribuut _embedded niet aanwezig
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustOnlyReturnRelativesWithExpand() {
  }

  /*
  Scenario: Gebruik van expand=true is niet toegestaan
  Als een ingeschreven persoon wordt geraadpleegd met expand=true or True
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnErrorOnExpandTrue() {
  }

  /*
  Als ingeschreven personen gezocht worden met ?naam__geslachtsnaam=groen&geboorte__datum=1983-05-26&expand=true
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnSpecificErrorIfExpandIsTrue() {
  }

  /*
  Scenario: Expand met incorrecte resource of velden
  Als een ingeschreven persoon wordt geraadpleegd met expand=resourcebestaatniet
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnErrorIfUnknownExpandValue() {

  }

  /*
  Als een ingeschreven persoon wordt geraadpleegd met expand=reisdocumenten # automatisch laden van reisdocumenten wordt niet ondersteund
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnErrorIfExpandIsTravelDocuments() {
  }

  /*
  Als een ingeschreven persoon wordt geraadpleegd met expand=ouders.veldbestaatniet
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnErrorIfExpandFieldIsUnknown() {
  }

  /*
  Scenario: Expand met lege waarde
  Als een ingeschreven persoon wordt geraadpleegd met expand=
  Dan levert dit een foutmelding
  En heeft de foutmelding betrekking op parameter expand
  */
  public void mustReturnErrorIfExpandIsEmpty() {
  }

  /*
  Scenario: Er kunnen meerdere sub-resources worden meegeladen door deze als een komma's gescheiden lijst te specificeren
  Als een ingeschreven persoon wordt geraadpleegd met expand=partners,kinderen
  Dan wordt attribuut _embedded.partners teruggegeven
  En wordt attribuut _embedded.kinderen teruggegeven
  En is in het antwoord attribuut _embedded.ouders niet aanwezig
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustSupportMultipleExpandValues() {
  }

  /*
  Scenario: De dot-notatie wordt gebruikt om specifieke attributen van resources te selecteren
  Als een ingeschreven persoon wordt geraadpleegd met expand=ouders.geslachtsaanduiding,ouders.ouder_aanduiding
  Dan wordt voor alle ouders in _embedded attribuut geslachtsaanduiding teruggegeven
  En wordt voor alle ouders in _embedded attribuut ouder_aanduiding teruggegeven
  En wordt voor alle ouders in _embedded attribuut _links.self teruggegeven
  En is voor alle ouders in _embedded attribuut burgerservicenummer niet aanwezig
  En is voor alle ouders in _embedded attribuut naam niet aanwezig
  En is voor alle ouders in _embedded attribuut geboorte niet aanwezig
  En is voor alle ouders in _embedded attribuut geldigVan niet aanwezig
  En is voor alle ouders in _embedded attribuut geldigTotEnMet niet aanwezig
  En is voor alle ouders in _embedded attribuut _links.ingeschrevenpersonen niet aanwezig
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustSupportMultipleDotValues() {
  }

  /*
  Scenario: Vragen om een hele gegevensgroep
  Als een ingeschreven persoon wordt geraadpleegd met expand=kinderen.naam,kinderen.geboorte
  Dan worden voor alle kinderen in _embedded alle attributen van naam teruggegeven voor zover ze een waarde hebben (voornamen, geslachtsnaam)
  En worden voor alle kinderen in _embedded alle attributen van geboorte teruggegeven voor zover ze een waarde hebben (plaats, datum, land)
  En wordt voor alle kinderen in _embedded attribuut _links.self teruggegeven
  En is voor alle kinderen in _embedded attribuut burgerservicenummer niet aanwezig
  En is voor alle kinderen in _embedded attribuut geldigVan niet aanwezig
  En is voor alle kinderen in _embedded attribuut geldigTotEnMet niet aanwezig
  En is voor alle kinderen in _embedded attribuut _links.ingeschrevenpersonen niet aanwezig
  En is in het antwoord attribuut _embedded.ouders niet aanwezig
  # geen andere resource dan die gevraagd is
  En is in het antwoord attribuut _embedded.partners niet aanwezig
  # geen andere resource dan die gevraagd is
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustReturnEntireGroup() {
  }

  /*
  Scenario: Vragen om attributen binnen een groep
  Als een ingeschreven persoon wordt geraadpleegd met expand=kinderen.naam.voornamen,kinderen.naam.geslachtsnaam
  Dan wordt voor alle kinderen in _embedded attribuut naam.voornamen teruggegeven
  En wordt voor alle kinderen in _embedded attribuut naam.geslachtsnaam teruggegeven
  En wordt voor alle kinderen in _embedded geen enkel ander attribuut dan naam en _links teruggegeven
  En wordt voor alle kinderen in _embedded geen enkel ander attribuut van naam teruggegeven dan voornamen en geslachtsnaam
  En wordt voor alle kinderen in _embedded geen enkel ander attribuut van _links teruggegeven dan self
  En is in het antwoord attribuut _embedded.ouders niet aanwezig
  En is in het antwoord attribuut _embedded.partners niet aanwezig
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustReturnAttributesInGroup() {
  }

  /*
  Scenario: Vragen om een link
  Als een ingeschreven persoon wordt geraadpleegd met expand=kinderen.naam.voornamen,kinderen.naam.geslachtsnaam
  Dan is voor alle kinderen in _embedded attribuut _links.ingeschrevenpersonen niet aanwezig
  # geen links geven waar niet naar gevraagd is
  En wordt voor alle kinderen in _embedded attribuut naam.voornamen teruggegeven
  En wordt voor alle kinderen in _embedded attribuut naam.geslachtsnaam teruggegeven
  En wordt voor alle kinderen in _embedded attribuut _links.self teruggegeven
  # de self link moet altijd worden opgenomen
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustNotReturnLinksIfNotRequested() {
  }

  /*
  Als een ingeschreven persoon wordt geraadpleegd met expand=kinderen.naam.voornamen,kinderen.naam.geslachtsnaam,kinderen.ingeschrevenpersonen
  Dan wordt voor alle kinderen in _embedded attribuut _links.ingeschrevenpersonen teruggegeven
  En wordt voor alle kinderen in _embedded attribuut naam.voornamen teruggegeven
  En wordt voor alle kinderen in _embedded attribuut naam.geslachtsnaam teruggegeven
  En wordt voor alle kinderen in _embedded attribuut _links.self teruggegeven
  # de self link moet altijd worden opgenomen
  En worden alle attributen van de persoon teruggegeven, voor zover ze een waarde hebben
  En wordt attribuut _links.partners teruggegeven
  En wordt attribuut _links.ouders teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  public void mustReturnLinksIfRequested() {
  }

  /*
  Scenario: property en link met dezelfde naam en property wordt gevraagd met expand
  Gegeven de embedded resource openbareruimte bevat een property ligtInWoonplaats
  En de embedded resource openbareruimte bevat in _links een property ligtInWoonplaats
  En de opgevraagde kadastraalonroerendezaak heeft een waarde voor ligtInWoonplaats.
  Als het adres wordt opgevraagd met expand=openbareruimte.ligtInWoonplaats
  Dan bevat het antwoord property _embedded.openbareruimte.ligtInWoonplaats
  En bevat het antwoord geen property _embedded.openbareruimte._links.ligtInWoonplaats
  En bevat het antwoord property _embedded.openbareruimte._links.self
  */
  public void mustReturnPropertiesIfRequestWithTheSameNameAndProperty() {
  }

  /*
  Scenario: property en link met dezelfde naam en link wordt gevraagd met expand
  Gegeven de embedded resource openbareruimte bevat een property ligtInWoonplaats
  En de embedded resource openbareruimte bevat in _links een property ligtInWoonplaats
  En de opgevraagde kadastraalonroerendezaak heeft een waarde voor ligtInWoonplaats.
  Als het adres wordt opgevraagd met expand=openbareruimte._links.ligtInWoonplaats
  Dan bevat het antwoord geen property _embedded.openbareruimte.ligtInWoonplaats
  En bevat het antwoord een property _embedded.openbareruimte._links.ligtInWoonplaats
  En bevat het antwoord property _embedded.openbareruimte._links.self
  */
  public void mustReturnPropertiesIfRequestWithTheSameNameAndLink() {
  }
}
