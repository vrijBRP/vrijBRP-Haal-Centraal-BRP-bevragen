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

package nl.procura.haalcentraal.brp.bevragen.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.haalcentraal.brp.api.v1.client.api.LandelijkeTabellenApi;
import nl.procura.haalcentraal.brp.api.v1.client.model.Tabel;
import nl.procura.haalcentraal.brp.api.v1.client.model.TabelCollectie;
import nl.procura.haalcentraal.brp.api.v1.client.model.Waarde;
import nl.procura.haalcentraal.brp.bevragen.service.TableWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.model.lt.Foutbericht;

import lombok.SneakyThrows;
import lombok.var;
import okhttp3.mockwebserver.MockResponse;
import retrofit2.Response;
import spring.PersonRecordSource;

@ContextConfiguration(initializers = HcBrpBevragenApiClientV1_3Test.Initializer.class)
class HcBrpBevragenApiClientV1_3Test extends TestBase {

  @Autowired
  private ObjectMapper objectMapper;
  private final String NOT_FOUND_TITLE = "Opgevraagde resource bestaat niet.";

  @Test
  @SneakyThrows
  public void mustReturnAllTables() {
    enqueuePersonWsResponse("all_tables.json");
    Response<TabelCollectie> response = getApi().getTabellen(null, null).execute();
    assertEquals(15, getTables(response).size());
  }

  @Test
  @SneakyThrows
  public void mustReturnTableByCode() {
    enqueuePersonWsResponse("table_36.json");
    Response<Tabel> response = getApi().getTabel("36").execute();
    assertTrue(response.isSuccessful());
    assertEquals("Voorvoegsels", response.body().getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustReturnTablesByCode() {
    enqueuePersonWsResponse("table_36.json");
    Response<TabelCollectie> response = getApi().getTabellen("36", null).execute();
    assertTrue(response.isSuccessful());
    assertEquals("Voorvoegsels", getTables(response).get(0).getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustReturnErrorByNonNumericCode() {
    Response<TabelCollectie> response = getApi().getTabellen("aa", null).execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustThrowExceptionByUnknownCode() {
    enqueuePersonWsResponse("no_tables.json");
    Response<TabelCollectie> response = getApi().getTabellen("99", null).execute();
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustThrowExceptionByUnknownDescription() {
    enqueuePersonWsResponse("no_tables.json");
    Response<TabelCollectie> response = getApi().getTabellen(null, "Hobbies").execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustReturnTableByBothCodeAndDescription() {
    enqueuePersonWsResponse("table_36.json");
    Response<TabelCollectie> response = getApi().getTabellen("36", "Voorvoegsels").execute();
    assertTrue(response.isSuccessful());
    assertEquals(1, getTables(response).size());
    assertEquals("Voorvoegsels", getTables(response).get(0).getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustReturnTableByDescription() {
    enqueuePersonWsResponse("table_36.json");
    Response<TabelCollectie> response = getApi().getTabellen(null, "Voorvoegsels").execute();
    assertTrue(response.isSuccessful());
    assertEquals(1, getTables(response).size());
    assertEquals("Voorvoegsels", getTables(response).get(0).getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustNotReturnTablesWithNonMatchingCodeDescription() {
    enqueuePersonWsResponse("no_tables.json");
    Response<TabelCollectie> response = getApi().getTabellen("32", "Voorvoegsels").execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustReturnExpiredNationalities() {
    enqueuePersonWsResponse("table_32_all_values.json");
    var response = getApi().getWaarden("32", null, null, true).execute();
    assertTrue(response.isSuccessful());
    var waardeCollectie = response.body();
    assert waardeCollectie != null;
    List<Waarde> waarden = waardeCollectie.getWaarden();
    assert waarden != null;
    assertEquals(217, waarden.size());
    assertEquals("0000", waarden.get(0).getCode());
    assertEquals("Onbekend", waarden.get(0).getOmschrijving());

    assertEquals("0001", waarden.get(1).getCode());
    assertEquals("Nederlandse", waarden.get(1).getOmschrijving());
    // Expired, but is returned
    assertTrue(waarden.stream().anyMatch(w -> "0065".equals(w.getCode()))); // Joegoslavische
  }

  @Test
  @SneakyThrows
  public void mustReturnOnlyNonExpiredNationalities() {
    enqueuePersonWsResponse("table_32_non_expired_values.json");
    var response = getApi().getWaarden("32", null, null, false).execute();
    var waardeCollectie = response.body();
    assertTrue(response.isSuccessful());
    assert waardeCollectie != null;
    List<Waarde> waarden = waardeCollectie.getWaarden();
    assert waarden != null;
    assertEquals(203, waarden.size());
    assertEquals("0000", waarden.get(0).getCode());
    assertEquals("Onbekend", waarden.get(0).getOmschrijving());

    assertEquals("0001", waarden.get(1).getCode());
    assertEquals("Nederlandse", waarden.get(1).getOmschrijving());
    // Expired and is not returned
    assertTrue(waarden.stream().noneMatch(w -> "0065".equals(w.getCode()))); // Joegoslavische
  }

  @Test
  @SneakyThrows
  public void mustReturnNationalityByNumericCode2() {
    enqueuePersonWsResponse("table_32_record_0065.json");
    var response = getApi().getWaarden("32", "0065", null, true).execute();
    var waardeCollectie = response.body();
    assertTrue(response.isSuccessful());
    assert waardeCollectie != null;
    assert waardeCollectie.getWaarden() != null;
    assertEquals(1, waardeCollectie.getWaarden().size());
    assertEquals("0065", waardeCollectie.getWaarden().get(0).getCode());
    assertEquals("Joegoslavische", waardeCollectie.getWaarden().get(0).getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustReturnExpiredNationalityByDescription() {
    enqueuePersonWsResponse("table_32_record_0065.json");
    var response = getApi().getWaarden("32", null, "Joegoslavische", true).execute();
    var waardeCollectie = response.body();
    assertTrue(response.isSuccessful());
    assert waardeCollectie != null;
    assert waardeCollectie.getWaarden() != null;
    assertEquals(1, waardeCollectie.getWaarden().size());
    assertEquals("0065", waardeCollectie.getWaarden().get(0).getCode());
    assertEquals("Joegoslavische", waardeCollectie.getWaarden().get(0).getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustNotReturnNationalityByCode() {
    enqueuePersonWsResponse("no_tables.json");
    var response = getApi().getWaarden("32", "0065", null, false).execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustNotReturnNationalityByDescription() {
    enqueuePersonWsResponse("no_tables.json");
    var response = getApi().getWaarden("32", null, "Joegoslavische", false).execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustReturnNationalityByCode() {
    enqueuePersonWsResponse("table_32_record_0065.json");
    var response = getApi().getWaarde("32", "0065").execute();
    var waarde = response.body();
    assertTrue(response.isSuccessful());
    assert waarde != null;
    assertEquals("0065", waarde.getCode());
    assertEquals("Joegoslavische", waarde.getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustReturnNationalityByNumericCode() {
    enqueuePersonWsResponse("table_32_record_0065.json");
    var response = getApi().getWaarde("32", "65").execute();
    var waarde = response.body();
    assertTrue(response.isSuccessful());
    assert waarde != null;
    assertEquals("0065", waarde.getCode());
    assertEquals("Joegoslavische", waarde.getOmschrijving());
  }

  @Test
  @SneakyThrows
  public void mustThrowExceptionByUnknownRecord() {
    enqueuePersonWsResponse("no_tables.json");
    var response = getApi().getWaarde("32", "NietBestaandeWaarde").execute();
    assertFalse(response.isSuccessful());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    assertEquals(NOT_FOUND_TITLE, getFoutBericht(response).getTitle());
  }

  @Test
  @SneakyThrows
  public void mustReturnExpiredNationality() {
    enqueuePersonWsResponse("table_32_record_0065.json");
    LandelijkeTabellenApi api = getApi();
    var responseWaarde = api.getWaarde("32", "0065").execute();
    var waarde = responseWaarde.body();
    assert waarde != null;
    assertEquals("0065", waarde.getCode());
    assertEquals("Joegoslavische", waarde.getOmschrijving());
  }

  private LandelijkeTabellenApi getApi() {
    return getApiClient().getApiClient().createService(LandelijkeTabellenApi.class);
  }

  private Foutbericht getFoutBericht(Response<?> response) throws java.io.IOException {
    assert response.errorBody() != null;
    return objectMapper.readValue(response.errorBody().bytes(), Foutbericht.class);
  }

  private List<Tabel> getTables(Response<TabelCollectie> response) {
    assert response.body() != null;
    return response.body().getTabellen();
  }

  private void enqueuePersonWsResponse(String fileName) {
    Initializer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .setBody(fromJson(fileName)));
  }

  @SneakyThrows
  private String fromJson(String fileName) {
    InputStream stream = TableWsService.class.getClassLoader().getResourceAsStream("tables/" + fileName);
    assert stream != null;
    return IOUtils.toString(stream, UTF_8);
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PersonRecordSource personRecordSource;

    public Initializer() {
      personRecordSource = new PersonRecordSource();
    }

    public static void enqueue(MockResponse response) {
      PersonRecordSource.enqueue(response);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of("procura.personrecordsource.username=test").applyTo(applicationContext);
      TestPropertyValues.of("procura.personrecordsource.password=pw").applyTo(applicationContext);
      personRecordSource.initialize(applicationContext);
    }
  }
}
