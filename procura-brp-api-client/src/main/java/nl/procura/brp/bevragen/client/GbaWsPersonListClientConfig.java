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

package nl.procura.brp.bevragen.client;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "GbaWsPersonListClientConfigBuilder")
public class GbaWsPersonListClientConfig {

  private WebClient webClient;
  private String    baseUrl;
  private String    username;
  private String    password;
  private String    authorization;

  public static class GbaWsPersonListClientConfigBuilder {
  }
}
