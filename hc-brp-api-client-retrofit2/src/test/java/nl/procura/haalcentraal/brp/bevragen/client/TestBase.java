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

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import nl.procura.haalcentraal.brp.bevragen.HcBrpBevragenApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HcBrpBevragenApplication.class)
public class TestBase {

  @LocalServerPort
  private int serverPort;

  public HcBrpBevragenApiClient getApiClient() {
    //TODO Add authentication
    //    Client adminClient = createClient(
    //        new Client("admin", "secret",
    //            Scope.of("api"), singleton(Grants.CLIENT_CREDENTIALS)));

    String baseURL = String.format("http://localhost:%d/", serverPort);
    String tokenUrl = baseURL + "oauth/token";

    ApiClientConfiguration config = ApiClientConfiguration.builder()
        //        .clientId(adminClient.clientId())
        //        .clientSecret(adminClient.rawSecret())
        .tokenUrl(tokenUrl)
        .baseUrl(baseURL)
        .scopes(ApiClientConfiguration.SCOPE_API)
        .build();

    return HcBrpBevragenApiClient
        .builder()
        .config(config)
        .build();
  }

  //  private Client createClient(Client client) {
  //    clientDetailsService.create(client);
  //    return client;
  //  }
}
