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

package nl.procura.haalcentraal.brp.bevragen.components.v1_3;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import nl.procura.brp.bevragen.client.GbaWsException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.NotFoundException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ParamException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ReturnException;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.BadRequestFoutbericht;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Foutbericht;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.InvalidParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ParamException.class)
  public ResponseEntity<BadRequestFoutbericht> handleParamException(ParamException e) {
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(new BadRequestFoutbericht()
            .type(URI.create("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1"))
            .status(HttpStatus.BAD_REQUEST.value())
            .title(e.getTitle())
            .invalidParams(toInvalidParams(e.getParams())));
  }

  @ExceptionHandler({ NoHandlerFoundException.class, NotFoundException.class })
  public ResponseEntity<Foutbericht> handleNotFoundException(Exception e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(new Foutbericht()
            .type(URI.create("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5"))
            .status(HttpStatus.NOT_FOUND.value())
            .title("Opgevraagde resource bestaat niet."));
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<Foutbericht> handleUnsupportedMediaTypeException(Exception e) {
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(new Foutbericht()
            .type(URI.create("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16"))
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            .title("Gevraagde contenttype wordt niet ondersteund."));
  }

  @ExceptionHandler(ReturnException.class)
  public ResponseEntity<Foutbericht> handleReturnException(ReturnException e) {
    return ResponseEntity.status(e.getStatusCode())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(new Foutbericht()
            .status(e.getStatusCode())
            .title(e.getTitle()));
  }

  @ExceptionHandler(GbaWsException.class)
  public ResponseEntity<Foutbericht> handleGbaWsException(GbaWsException e) {
    HttpStatus httpStatus = HttpStatus.valueOf(e.getStatusCode());
    if (UNAUTHORIZED == httpStatus) {
      return ResponseEntity.status(e.getStatusCode())
          .header("WWW-Authenticate", "Basic realm=\"Haal Centraal BRP bevragen\"")
          .build();
    }
    return handleException(new RuntimeException(
        String.format("De Procura BRP webservice geeft een foutmelding (%s)",
            httpStatus)));
  }

  private static List<InvalidParams> toInvalidParams(List<ReturnException.Param> params) {
    return params.stream()
        .map(p -> new InvalidParams()
            .name(p.name())
            .code(p.code())
            .reason(p.reason()))
        .collect(toList());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Foutbericht> handleException(Exception e) {
    log.error("Error", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new Foutbericht()
            .type(URI.create("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1"))
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .title(e.getMessage()));
  }
}
