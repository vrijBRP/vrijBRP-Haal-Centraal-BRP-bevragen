package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.net.URLDecoder.decode;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/*
Functionaliteit: JSON HAL self links worden opgenomen in de response

Bij self-link op het hoogste niveau van de response, dus _links.self.href is identiek aan de volledige url van het
request, inclusief alle queryparameters. Een gebruiker moet hiermee de response aan de request kunnen koppelen. 
Deze kan dus niet templated zijn. Dit kan wel een relatieve link zijn.

Een self link in een embedded resource of in een collectie, dus een link in _embedded..[*]._links.self.href is een url 
die verwijst naar de betreffende resource. Deze link kan templated zijn.
*/
public class SelfLinkTest extends IngeschrevenPersonenResourceTest {

  /*
  Scenario: self link van een collectie
  Als ingeschreven personen gezocht worden met "/ingeschrevenpersonen?naam__geslachtsnaam=groen&geboorte__datum=1983-05-26
  &inclusiefoverledenpersonen=true&fields=naam,geboorte,overlijden"
  
  Dan eindigt attribuut "_links.self.href" op "/ingeschrevenpersonen?naam__geslachtsnaam=groen&geboorte__datum=1983-05-26
  &inclusiefoverledenpersonen=true&fields=naam,geboorte,overlijden"
  
  En komt attribuut "templated" niet voor in "_links.self"
  */
  @Test
  public void mustReturnSelfLinkOfCollection() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999995935L, 999995145L, 999990627L)
        .param(IngeschrevenPersonenParameters.NAAM__GESLACHTSNAAM, "Janssen");

    var collection = getIngeschrevenPersonen(params);

    String link = collection.getLinks().getSelf().getHref();
    assertTrue(decode(link, StandardCharsets.UTF_8)
        .contains("ingeschrevenpersonen?expand=ouders,partners,kinderen&naam__geslachtsnaam=Janssen"));
  }

  /*
  Scenario: self link van een resource in een collectie
  Als ingeschreven personen gezocht worden met "/ingeschrevenpersonen?naam__geslachtsnaam=groen&geboorte__datum=1983-05-26
  &inclusiefoverledenpersonen=true&fields=naam,geboorte,overlijden"
  
  Dan geldt voor elk van de gevonden _embedded.ingeschrevenpersonen dat attribuut _links.self.href de tekst "/ingeschrevenpersonen/"
  plus de waarde van attribuut burgerservicenummer in deze resource bevat
  
  En geldt voor elk van de gevonden _embedded.ingeschrevenpersonen dat attribuut _links.self.href niet "naam__geslachtsnaam=groen" bevat
  En geldt voor elk van de gevonden _embedded.ingeschrevenpersonen dat attribuut _links.self.href niet "geboorte__datum=1983-05-26" bevat
  En geldt voor elk van de gevonden _embedded.ingeschrevenpersonen dat attribuut _links.self.href niet "inclusiefoverledenpersonen" bevat
  En geldt voor elk van de gevonden _embedded.ingeschrevenpersonen dat attribuut _links.self.href niet "fields" bevat
  */
  @Test
  public void mustReturnSelfLinkInsideCollection() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999995935L, 999995145L, 999990627L)
        .param(IngeschrevenPersonenParameters.NAAM__GESLACHTSNAAM, "Janssen");
    var people = getIngeschrevenPersonen(params);

    String link1 = people.getEmbedded().getIngeschrevenpersonen().get(0).getLinks().getSelf().getHref();
    assertTrue(decode(link1, StandardCharsets.UTF_8)
        .contains("ingeschrevenpersonen/999995935?expand=ouders,partners,kinderen"));

    String link2 = people.getEmbedded().getIngeschrevenpersonen().get(1).getLinks().getSelf().getHref();
    assertTrue(decode(link2, StandardCharsets.UTF_8)
        .contains("ingeschrevenpersonen/999995145?expand=ouders,partners,kinderen"));

    String link3 = people.getEmbedded().getIngeschrevenpersonen().get(2).getLinks().getSelf().getHref();
    assertTrue(decode(link3, StandardCharsets.UTF_8)
        .contains("ingeschrevenpersonen/999990627?expand=ouders,partners,kinderen"));
  }

  /*
  Scenario: self link van een resource
  Als ingeschreven persoon wordt geraadpleegd met "/ingeschrevenpersonen/999999023?fields=naam,geboorte,overlijden&expand=kinderen
  Dan eindigt attribuut "_links.self.href" met "/ingeschrevenpersonen/999999023?fields=naam,geboorte,overlijden&expand=kinderen"
  */
  @Test
  public void mustReturnSelfLinkOfResource() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999992545L)
        .expand("ouders", "partners", "kinderen")
        .fields("naam", "ouders");

    var people = getIngeschrevenPersoon(params);
    String link = people.getLinks().getSelf().getHref();
    assertTrue(decode(link, StandardCharsets.UTF_8)
        .contains("ingeschrevenpersonen/999992545?expand=ouders,partners,kinderen&fields=naam,ouders"));
  }

  /*
  Scenario: self link van een embedded resource
  Gegeven kadastraal onroerende zaak met identificatie "76870487970000" heeft een zakelijk gerechtigde met identificatie "30493367"
  Als de kadastraal onroerende zaak wordt geraadpleegd met "/kadastraalonroerendezaken/76870487970000?expand=zakelijkGerechtigden.persoon,zakelijkGerechtigden.type&fields=koopsom,aardCultuurBebouwd"
  Dan heeft attribuut "_links.self.href" de waarde "/kadastraalonroerendezaken/76870487970000?expand=zakelijkGerechtigden.persoon,zakelijkGerechtigden.type&fields=koopsom,aardCultuurBebouwd"
  En heeft attribuut "_embedded.zakelijkGerechtigden[0]._links.self.href" de waarde "/kadastraalonroerendezaken/76870487970000/zakelijkgerechtigden/30493367
  */
  public void mustReturnSelfLinkOfEmbeddedResource() {
    // Niet van toepassing
  }
}
