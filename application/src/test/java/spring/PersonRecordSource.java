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

package spring;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.event.AfterTestMethodEvent;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Class to start one person record web server for all Spring application
 * contexts. It shuts down the server when the a context is closed but as
 * Spring caches the contexts it will be at the end of all tests.
 * Also after each test, it empties the request queue so tests don't have to
 * do it themselves.
 */
public class PersonRecordSource implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static MockWebServer mockServer;
  private static int           queued = 0;

  public static void enqueue(MockResponse response) {
    if (mockServer == null) {
      throw new IllegalStateException("Forgot @ContextConfiguration(initializers = PersonRecordSource.class)?");
    }
    mockServer.enqueue(response);
    queued++;
  }

  public static RecordedRequest takeRequest() {
    try {
      RecordedRequest request = mockServer.takeRequest(2, TimeUnit.SECONDS);
      queued--;
      return request;
    } catch (InterruptedException e) {
      // interrupt thread owner too, if any
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  public static void emptyQueue() {
    while (queued > 0) {
      takeRequest();
    }
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    if (mockServer == null) {
      mockServer = new MockWebServer();
      try {
        mockServer.start();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    TestPropertyValues.of("procura.personrecordsource.url=" + mockServer.url("/").toString())
        .applyTo(applicationContext);
    applicationContext.addApplicationListener(new ShutdownServer());
    applicationContext.addApplicationListener(new EmptyQueue());
  }

  public static void shutdown() {
    if (mockServer == null) {
      return;
    }
    try {
      mockServer.shutdown();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    mockServer = null;
    queued = 0;
  }

  public static class ShutdownServer implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
      PersonRecordSource.shutdown();
    }
  }

  public static class EmptyQueue implements ApplicationListener<AfterTestMethodEvent> {

    @Override
    public void onApplicationEvent(AfterTestMethodEvent event) {
      PersonRecordSource.emptyQueue();
    }
  }
}
