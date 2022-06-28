package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.gbaws.testdata.Testdata.DataSet.GBAV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;
import org.junit.jupiter.api.Test;

import nl.procura.gbaws.testdata.Testdata;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHalCollectie;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentationTest extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoon1() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
    IngeschrevenPersonenTestParams params = new IngeschrevenPersonenTestParams();
    params.param(IngeschrevenPersonenParameters.BURGERSERVICENUMMER, "999995935");
    params.param(IngeschrevenPersonenParameters.GEBOORTE__DATUM, "1952-03-04");
    params.param(IngeschrevenPersonenParameters.GESLACHTSAANDUIDING, GeslachtEnum.VROUW.name());
    params.param(IngeschrevenPersonenParameters.INCLUSIEFOVERLEDENPERSONEN, "True");
    params.param(IngeschrevenPersonenParameters.NAAM__GESLACHTSNAAM, "Janssen");
    params.param(IngeschrevenPersonenParameters.NAAM__VOORNAMEN, "Petronella Alida");
    params.param(IngeschrevenPersonenParameters.VERBLIJFPLAATS__HUISNUMMER, "30");
    params.param(IngeschrevenPersonenParameters.VERBLIJFPLAATS__HUISLETTER, "A");
    params.param(IngeschrevenPersonenParameters.VERBLIJFPLAATS__POSTCODE, "3511GB");
    params.param(IngeschrevenPersonenParameters.VERBLIJFPLAATS__STRAAT, "Catharijnesingel");
    params.getExpand().clear();

    String response = mockMvc.perform(get(CONTEXT_PATH + "/api/v1.3/ingeschrevenpersonen" + params.toQueryString())
        .contextPath(CONTEXT_PATH))
        .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon1"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHalCollectie persons = objectMapper.readValue(response, IngeschrevenPersoonHalCollectie.class);
    assertEquals("999995935", persons.getEmbedded().getIngeschrevenpersonen().get(0).getBurgerservicenummer());
    assertEquals("P.A.", persons.getEmbedded().getIngeschrevenpersonen().get(0).getNaam().getVoorletters());
  }

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoonByBsn1() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
    String response = mockMvc.perform(get(CONTEXT_PATH + "/api/v1.3/ingeschrevenpersonen/999995935")
        .contextPath(CONTEXT_PATH))
        .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon_by_bsn1"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHal persoon = objectMapper.readValue(response, IngeschrevenPersoonHal.class);
    assertEquals("999995935", persoon.getBurgerservicenummer());
    assertEquals("P.A.", persoon.getNaam().getVoorletters());
    assertNull(persoon.getEmbedded());
  }

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoonByBsnWithExpand() {
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));

    String response = mockMvc
        .perform(get(CONTEXT_PATH + "/api/v1.3/ingeschrevenpersonen/999995935?expand=kinderen,ouders,partners")
            .contextPath(CONTEXT_PATH))
        .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon_by_bsn_with_expand"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHal persoon = objectMapper.readValue(response, IngeschrevenPersoonHal.class);
    assertEquals("999995935", persoon.getBurgerservicenummer());
    assertEquals("P.A.", persoon.getNaam().getVoorletters());
    assertEquals(4, persoon.getEmbedded().getKinderen().size());
    assertEquals(2, persoon.getEmbedded().getOuders().size());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
  }
}
