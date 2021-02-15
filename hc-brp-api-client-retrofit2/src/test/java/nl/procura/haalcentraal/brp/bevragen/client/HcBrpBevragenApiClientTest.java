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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.procura.haalcentraal.brp.api.v1.client.api.LandelijkeTabellenApi;
import nl.procura.haalcentraal.brp.api.v1.client.model.TabelCollectie;

import lombok.SneakyThrows;
import retrofit2.Response;

class HcBrpBevragenApiClientTest extends TestBase {

  @Test
  @SneakyThrows
  public void canGetTablesByIdentificationAndDescription() {
    LandelijkeTabellenApi api = getApiClient().getApiClient().createService(LandelijkeTabellenApi.class);
    Response<TabelCollectie> response = api.getTabellen("", "").execute();
    assertTrue(response.isSuccessful());
    TabelCollectie collectie = response.body();
    assert collectie != null;
    assert collectie.getTabellen() != null;
    assertEquals("voorvoegsels", collectie.getTabellen().get(0).getTabelidentificatie());
    assertEquals("Voorvoegsels", collectie.getTabellen().get(0).getOmschrijving());
  }
}
