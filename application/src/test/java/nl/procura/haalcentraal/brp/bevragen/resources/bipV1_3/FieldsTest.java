package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nl.procura.gbaws.testdata.Testdata;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.BadRequestFoutbericht;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.KindHalBasis;

import lombok.SneakyThrows;

/**
 * {@link features/fields.features}
 */
public class FieldsTest extends IngeschrevenPersonenResourceTest {

  /*
  Scenario: De fields-parameter is niet opgenomen
  Als een ingeschreven persoon wordt geraadpleegd zonder fields-parameter
  Dan worden alle attributen van de resource teruggegeven
  En worden alle relaties van de resource teruggegeven
  En wordt er geen gerelateerde sub-resource teruggegeven in _embedded
  */
  @Test
  public void mustReturnAll() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .expand("");

    var persoon = getIngeschrevenPersoon(params);
    assertEquals(2, persoon.getLinks().getOuders().size());
    assertEquals(1, persoon.getLinks().getKinderen().size());
    assertEquals(1, persoon.getLinks().getPartners().size());
    assertNull(persoon.getEmbedded());
  }

  /*
  Scenario: Slechts één enkel attribuut wordt gevraagd
  Als een ingeschreven persoon wordt geraadpleegd met fields=geslachtsaanduiding
  Dan worden alleen attributen geslachtsaanduiding en _links teruggegeven
  En bevat _links alleen attribuut self
  */
  @Test
  public void mustReturnOnlyAttribute() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .fields("geslachtsaanduiding")
        .expand("");

    var persoon = getIngeschrevenPersoon(params);
    assertEquals(GeslachtEnum.VROUW, persoon.getGeslachtsaanduiding());
    assertNull(persoon.getEmbedded());
    assertNull(persoon.getNaam());
    assertNull(persoon.getLinks().getPartners());
    assertNull(persoon.getLinks().getOuders());
    assertNull(persoon.getLinks().getKinderen());
  }

  /*
  Scenario: Meerdere attributen worden gevraagd
  Als een ingeschreven persoon wordt geraadpleegd met fields=burgerservicenummer,burgerlijkeStaat,geslachtsaanduiding
  Dan worden alleen attributen burgerservicenummer, burgerlijkeStaat, geslachtsaanduiding en _links teruggegeven
  En bevat _links alleen attribuut self
  */

  /*
  Scenario: Hele groep wordt gevraagd
  Gegeven de te raadplegen persoon heeft voornamen, geslachtsnaam en voorvoegsel
  Als een ingeschreven persoon wordt geraadpleegd met fields=burgerservicenummer,naam
  Dan worden alleen attributen burgerservicenummer, naam en _links teruggegeven
  En bevat _links alleen attribuut self
  */
  @Test
  public void mustReturnMultipleAttributes() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .fields("geslachtsaanduiding", "naam")
        .expand("");

    var persoon = getIngeschrevenPersoon(params);
    assertEquals(GeslachtEnum.VROUW, persoon.getGeslachtsaanduiding());
    assertNull(persoon.getEmbedded());
    assertEquals("Moulin", persoon.getNaam().getGeslachtsnaam());
  }

  /*
  Scenario: Een of enkele attributen binnen een groep worden gevraagd
  Als een ingeschreven persoon wordt geraadpleegd met fields=naam.aanschrijfwijze,naam.voornamen
  Dan worden alleen attributen naam en _links teruggegeven
  En bevat naam alleen attributen aanschrijfwijze en voornamen
  En bevat _links alleen attribuut self
  */
  @Test
  public void mustReturnAttributesInsideGroup() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .fields("geslachtsaanduiding", "naam.voornamen")
        .expand("");

    var persoon = getIngeschrevenPersoon(params);
    assertEquals(GeslachtEnum.VROUW, persoon.getGeslachtsaanduiding());
    assertNull(persoon.getEmbedded());
    assertEquals("Brigitte", persoon.getNaam().getVoornamen());
    assertNull(persoon.getNaam().getGeslachtsnaam());
  }

  /*
  Scenario: Relaties (links) vragen (en beperken) in het antwoord
  Gegeven de te raadplegen persoon heeft een actuele partner(partnerschap of huwelijk), ouders en kinderen
  Als een ingeschreven persoon wordt geraadpleegd met fields=burgerservicenummer,naam,_links.partners
  Dan worden alleen attributen burgerservicenummer, naam en _links teruggegeven
  En bevat _links alleen attributen self en partners
  */
  public void mustReturnOnlyPartnerLinks() {
    // Fix fields=_links.partners
  }

  /*
  Scenario: Gebruik van de fields parameter heeft geen invloed op embedded sub-resources
  Als een ingeschreven persoon wordt geraadpleegd met fields=geboorte.land&expand=kinderen
  Dan worden alleen attributen geboorte, _links en _embedded teruggegeven
  En bevat geboorte alleen attribuut land
  En bevat _links alleen attribuut self
  En bevat _embedded alleen attribuut kinderen
  En bevat elk voorkomen van _embedded.kinderen attribuut burgerservicenummer met een waarde
  En bevat elk voorkomen van_embedded.kinderen attribuut naam met een waarde
  En bevat elk voorkomen van_embedded.kinderen attribuut geboorte.datum met een waarde
  En bevat elk voorkomen van_embedded.kinderen attribuut geboorte.plaats met een waarde
  En bevat elk voorkomen van_embedded.kinderen attribuut geboorte.land met een waarde
  */
  @Test
  public void mustReturnEmbeddedResourcesWithfields() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .fields("geboorte.land")
        .expand("kinderen");

    var persoon = getIngeschrevenPersoon(params);
    assertNull(persoon.getGeslachtsaanduiding());
    assertNull(persoon.getNaam());
    assertNull(persoon.getEmbedded().getOuders());
    assertNull(persoon.getEmbedded().getPartners());
    assertEquals("Frankrijk", persoon.getGeboorte().getLand().getOmschrijving());
    assertNull(persoon.getGeboorte().getDatum());
    assertNull(persoon.getGeboorte().getPlaats());

    KindHalBasis kindHalBasis = persoon.getEmbedded().getKinderen().get(0);
    assertEquals("Hélène", kindHalBasis.getNaam().getVoornamen());
    assertEquals(LocalDate.of(1950, 07, 23), kindHalBasis.getGeboorte().getDatum().getDatum());
    assertEquals("Narbonne", kindHalBasis.getGeboorte().getPlaats().getOmschrijving());
    assertEquals("5002", kindHalBasis.getGeboorte().getLand().getCode());
  }

  /*
  Scenario: Gebruik van de expand parameter heeft geen invloed op de inhoud van de resource
  Als een ingeschreven persoon wordt geraadpleegd met fields=_links.partners&expand=kinderen
  Dan worden alleen attributen _links en _embedded teruggegeven
  En bevat _links alleen attributen self en partners
  En bevat _embedded alleen attribuut kinderen
  */
  @Test
  public void mustReturnResourceWithExpandParameters() {
    // Fix fields=_links.partners
  }

  /*
  Scenario: Vragen van specifieke velden met de expand parameter heeft geen invloed op de inhoud van de resource, alleen op de inhoud van de embedded subresource
  Als een ingeschreven persoon wordt geraadpleegd met fields=burgerservicenummer,naam,geboorte&expand=kinderen.naam.voornamen
  Dan bevat elk voorkomen van_embedded.kinderen attribuut naam.voornamen met een waarde
  En bevat elk voorkomen van_embedded.kinderen attribuut _links.self met een waarde
  En bevat elk voorkomen van _embedded.kinderen alleen attributen naam en _links
  En bevat in elk voorkomen van _embedded.kinderen naam alleen attribuut voornamen
  En bevat in elk voorkomen van _embedded.kinderen _links alleen attribuut self
  En wordt attribuut burgerservicenummer teruggegeven
  En wordt attribuut naam.voornamen teruggegeven
  En wordt attribuut naam.geslachtsnaam teruggegeven
  En wordt attribuut naam.voorvoegsel teruggegeven
  En wordt attribuut geboorte teruggegeven
  En wordt attribuut _links.self teruggegeven
  En wordt attribuut _links.kinderen teruggegeven
  */
  @Test
  public void mustReturnResourceWithParameterFields() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999995935L)
        .fields("burgerservicenummer", "naam", "geboorte")
        .expand("kinderen.naam.voornamen");

    var persoon = getIngeschrevenPersoon(params);
    assertNull(persoon.getGeslachtsaanduiding());
    assertEquals("Janssen", persoon.getNaam().getGeslachtsnaam());
    assertNull(persoon.getEmbedded().getKinderen().get(0).getNaam().getGeslachtsnaam());
    assertEquals("Jeroen", persoon.getEmbedded().getKinderen().get(0).getNaam().getVoornamen());

    assertNull(persoon.getEmbedded().getKinderen().get(1).getNaam().getGeslachtsnaam());
    assertEquals("Patrick", persoon.getEmbedded().getKinderen().get(1).getNaam().getVoornamen());
  }

  /*
  Scenario: Lege fields parameter geeft alle attributen
  Als een ingeschreven persoon wordt geraadpleegd met fields=
  Dan levert dit alle attributen die een waarde hebben en waarvoor autorisatie is
  */
  @Test
  @SneakyThrows
  public void mustReturnNoAttributesWithEmptyFields() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, Testdata.DataSet.GBAV)));

    String response = mockMvc.perform(get(CONTEXT_PATH
        + "/api/v1.3/ingeschrevenpersonen/999995935?fields=")
            .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    IngeschrevenPersoonHal persoon = objectMapper.readValue(response, IngeschrevenPersoonHal.class);
    assertEquals("Janssen", persoon.getNaam().getGeslachtsnaam());
  }

  /*
  Scenario: Fields parameter met attribuutnaam die niet bestaat
  Als een ingeschreven persoon wordt geraadpleegd met fields=burgerservicenummer,geslachtsaanduiding,bestaatniet
  Dan levert dit een foutmelding
  */
  @Test
  @SneakyThrows
  public void mustReturnErrorWithNonExistingFieldParameter() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, Testdata.DataSet.GBAV)));
    String response = mockMvc.perform(get(CONTEXT_PATH
        + "/api/v1.3/ingeschrevenpersonen/999995935?fields=burgerservicenummer,geslachtsaanduiding,bestaatniet")
            .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    BadRequestFoutbericht foutbericht = objectMapper.readValue(response, BadRequestFoutbericht.class);
    assertEquals(400, foutbericht.getStatus());
  }

  /*
  Scenario: Fields parameter met attribuutnaam met onjuist case
  Als een ingeschreven persoon wordt geraadpleegd met fields=BurgerServiceNummer
  Dan levert dit een foutmelding
  */
  @Test
  @SneakyThrows
  public void mustReturnErrorWithIncorrectLetterCasing() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, Testdata.DataSet.GBAV)));
    String response = mockMvc.perform(get(CONTEXT_PATH
        + "/api/v1.3/ingeschrevenpersonen/999995935?fields=BurgerServiceNummer")
            .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    BadRequestFoutbericht foutbericht = objectMapper.readValue(response, BadRequestFoutbericht.class);
    assertEquals(400, foutbericht.getStatus());
  }

  /*
  Scenario: Met fields vragen om attributen uit een subresource
  Als een ingeschreven persoon wordt geraadpleegd met expand=kinderen&fields=kinderen.naam
  Dan levert dit een foutmelding
  */
  public void mustReturnErrorWhenRequestingFieldsFromSubresource() {
    // Not testable as this moment
  }

  /*
  Scenario: Fields vraagt om een groep attributen en de gebruiker is niet geautoriseerd voor al deze attributen
  Gegeven de gebruiker is geautoriseerd voor geboortedatum
  En de gebruiker is niet geautoriseerd voor geboorteplaats en ook niet voor geboorteland
  Als een ingeschreven persoon wordt geraadpleegd met fields=geboorte
  Dan wordt attribuut geboorte.datum teruggegeven
  En is in het antwoord attribuut geboorte.plaats niet aanwezig
  En is in het antwoord attribuut geboorte.land niet aanwezig
  En wordt attribuut _links.self teruggegeven
  En bevat _links alleen attribuut self
  */
  public void mustReturnOnlyAuthorizedAttributes() {
    // Not testable as this moment
  }

  /*
  Scenario: Fields vraagt specifiek om een gegeven waarvoor deze niet geautoriseerd is
  Gegeven de gebruiker is geautoriseerd voor geboortedatum
  En de gebruiker is niet geautoriseerd voor geboorteplaats
  Als een ingeschreven persoon wordt geraadpleegd met fields=geboorte.datum,geboorte.plaats
  Dan wordt attribuut geboorte.datum teruggegeven
  En is in het antwoord attribuut geboorte.plaats niet aanwezig
  En is in het antwoord attribuut geboorte.land niet aanwezig
  En wordt attribuut _links.self teruggegeven
  */
  public void mustReturnOnlyAuthorizedAttributesFromFields() {
    // Not testable as this moment
  }

  /*
  Scenario: Fields bevat attributen die bij de geraadpleegde persoon geen waarde hebben
  Gegeven de te raadplegen persoon verblijft in het buitenland
  Als een ingeschreven persoon wordt geraadpleegd met fields=verblijfplaats.postcode,verblijfplaats.huisnummer
  Dan wordt alleen attribuut _links teruggegeven
  En bevat _links alleen attribuut self
  En is in het antwoord attribuut verblijfplaats.adresregel1 niet aanwezig
  En is in het antwoord attribuut verblijfplaats.land niet aanwezig
  En is in het antwoord attribuut verblijfplaats.postcode niet aanwezig
  En is in het antwoord attribuut verblijfplaats.huisnummer niet aanwezig
  */
  public void mustNotReturnNLAddressFields() {
    // Not testable as this moment
  }
}
