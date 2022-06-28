package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static spring.PersonRecordSource.enqueue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.gbaws.testdata.Testdata;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListResponse;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import spring.PersonRecordSource;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = PersonRecordSource.class)
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@AutoConfigureMockMvc
public abstract class IngeschrevenPersonenResourceTest {

  public static final String CONTEXT_PATH = "/haal-centraal-brp-bevragen";
  public static MockMvc      mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @BeforeEach
  public void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(documentationConfiguration(restDocumentation)
            .snippets().withEncoding("UTF-8").and()
            .uris()
            .withScheme("https")
            .withHost("api.procura.nl")
            .withPort(443))
        .build();
  }

  @SneakyThrows
  public IngeschrevenPersoonHal getIngeschrevenPersoon(long bsn) {
    return getIngeschrevenPersoon(bsn, Testdata.DataSet.GBAV);
  }

  @SneakyThrows
  public IngeschrevenPersoonHal getIngeschrevenPersoon(long bsn, Testdata.DataSet dataSet) {
    return getIngeschrevenPersoon(new IngeschrevenPersonenTestParams().bsn(bsn, dataSet));
  }

  @SneakyThrows
  public IngeschrevenPersoonHal getIngeschrevenPersoon(IngeschrevenPersonenTestParams params) {
    long bsn = params.getBsns().keySet().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No BSN"));
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(bsn, Testdata.DataSet.GBAV)));

    String response = mockMvc
        .perform(get(CONTEXT_PATH
            + "/api/v1.3/ingeschrevenpersonen/" + bsn
            + params.toQueryString())
                .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    log.info(response);
    return objectMapper.readValue(response, IngeschrevenPersoonHal.class);
  }

  @SneakyThrows
  public IngeschrevenPersoonHalCollectie getIngeschrevenPersonen(long bsn) {
    return getIngeschrevenPersonen(bsn, Testdata.DataSet.GBAV);
  }

  @SneakyThrows
  public IngeschrevenPersoonHalCollectie getIngeschrevenPersonen(long bsn, Testdata.DataSet dataSet) {
    return getIngeschrevenPersonen(new IngeschrevenPersonenTestParams().bsn(bsn, dataSet));
  }

  @SneakyThrows
  public IngeschrevenPersoonHalCollectie getIngeschrevenPersonen(IngeschrevenPersonenTestParams params) {
    if (params.getBsns().keySet().stream().anyMatch(bsn -> bsn.longValue() > 0)) {
      enqueueResponse(getResponseFromMultipleBsn(params.getBsns()));
    }

    String response = mockMvc
        .perform(get(CONTEXT_PATH
            + "/api/v1.3/ingeschrevenpersonen"
            + params.toQueryString())
                .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    log.info(response);
    return objectMapper.readValue(response, IngeschrevenPersoonHalCollectie.class);
  }

  @SneakyThrows
  public KindHalBasis getKind(long bsn, String id) {
    return getRelative(bsn, id, "kinderen", KindHalBasis.class);
  }

  @SneakyThrows
  public PartnerHalBasis getPartner(long bsn, String id) {
    return getRelative(bsn, id, "partners", PartnerHalBasis.class);
  }

  @SneakyThrows
  public OuderHalBasis getOuder(long bsn, String id) {
    return getRelative(bsn, id, "ouders", OuderHalBasis.class);
  }

  protected void enqueueResponse(String personWsResponse) {
    PersonRecordSource.enqueueResponse(personWsResponse);
  }

  /**
   * Pretty print request and response
   */
  public RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase) {
    return document(useCase,
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()));
  }

  @SneakyThrows
  protected String getResponseFromMultipleBsn(Map<Long, Testdata.DataSet> bsns) {
    GbaWsPersonListResponse response = new GbaWsPersonListResponse();
    for (Map.Entry<Long, Testdata.DataSet> entry : bsns.entrySet()) {
      response.getPersonlists()
          .addAll(objectMapper.readValue(Testdata
              .getPersonDataAsBytes(entry.getKey(), entry.getValue()),
              GbaWsPersonListResponse.class)
              .getPersonlists());
    }
    return objectMapper.writeValueAsString(response);
  }

  @SneakyThrows
  private <T> T getRelative(long bsnL, String id, String relativeType, Class<T> clazz) {
    String personWsResponse = new String(Testdata.getPersonDataAsBytes(bsnL, Testdata.DataSet.GBAV));

    enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .setBody(personWsResponse));

    String response = mockMvc
        .perform(get(CONTEXT_PATH + "/api/v1.3/ingeschrevenpersonen/"
            + bsnL + "/" + relativeType + "/" + id)
                .contextPath(CONTEXT_PATH))
        .andReturn()
        .getResponse()
        .getContentAsString(UTF_8);

    log.info(response);
    return objectMapper.readValue(response, clazz);
  }
}
