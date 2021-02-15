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

import org.apache.oltu.oauth2.common.OAuth;

import com.google.gson.GsonBuilder;

import nl.procura.haalcentraal.brp.api.v1.client.handler.ApiClient;
import nl.procura.haalcentraal.brp.bevragen.client.customizations.GsonTypeAdapters;

import lombok.Builder;
import lombok.Value;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Value
public class HcBrpBevragenApiClient {

  private ApiClient apiClient;
  //private OAuth oAuth; //TODO add authentication if available
  private ApiClientConfiguration config;

  @Builder
  public HcBrpBevragenApiClient(ApiClient apiClient, OAuth oAuth, ApiClientConfiguration config) {
    this.config = config;
    //this.oAuth = oAuth == null ? createOAuth() : oAuth;
    this.apiClient = apiClient == null ? createApiClient() : apiClient;
  }

  private ApiClient createApiClient() {
    Retrofit.Builder adapterBuilder = new Retrofit.Builder()
        .baseUrl(this.config.getBaseUrl())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(getGsonFactory());

    return new ApiClient()
        .setAdapterBuilder(adapterBuilder);
    //.addAuthorization("", this.oAuth);
  }

  private GsonConverterFactory getGsonFactory() {
    return GsonConverterFactory.create(GsonTypeAdapters
        .registerAdapters(new GsonBuilder())
        .serializeNulls()
        .create());
  }

  //    private OAuth createOAuth() {
  //        return new OAuth(OAuthClientRequest
  //                .tokenLocation(config.getTokenUrl())
  //                .setGrantType(GrantType.CLIENT_CREDENTIALS)
  //                .setScope(String.join(" ", config.getScopes()))
  //                .setClientId(config.getClientId())
  //                .setClientSecret(config.getClientSecret()));
  //    }
}
