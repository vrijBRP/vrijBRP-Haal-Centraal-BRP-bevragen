package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersoonConverterImplV1_3.leaf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.procura.haalcentraal.brp.bevragen.model.exception.ExpandException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.FieldsException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseConverterImplV1_3<T, S> implements BaseConverterV1_3<T, S> {

  @FunctionalInterface
  public interface Handler<T, S> {

    void handle(T target, S source, Set<String> fields, Set<String> expand);
  }

  @FunctionalInterface
  public interface ValueHandler<T, S> {

    void handle(T target, S source);
  }

  @Getter
  public final Map<String, BaseConverterImplV1_3.Handler<T, S>> map = new HashMap<>();

  public void put(String field, BaseConverterImplV1_3.Handler<T, S> handler) {
    getMap().put(field, handler);
  }

  public void put(String field, BaseConverterImplV1_3.ValueHandler<T, S> handler) {
    getMap().put(field, (target, source, fields, expand) -> handler.handle(target, source));
  }

  @Override
  public T convert(final S source, T result, final Set<String> fields, final Set<String> expand) {
    log.debug("fields=" + fields + ", expand=" + expand);
    validate(fields);
    (fields == null || fields.isEmpty() ? getMap().keySet() : fields).stream()
        .map(field -> {
          if (field.contains(".")) {
            return field.substring(0, field.indexOf("."));
          } else {
            return field;
          }
        }).distinct()
        .forEach(e -> getMap().get(e)
            .handle(result, source, leaf(fields, e), expand));

    return postConvert(source, result, fields);
  }

  @Override
  public void validate(final Set<String> fields) {
    if (fields != null) {
      fields.stream()
          .filter(field -> !getMap().containsKey(field) &&
              getMap().keySet().stream().noneMatch(i -> field.startsWith(i + ".")))
          .findAny().ifPresent(entry -> {
            if (this instanceof PersoonConverterImplV1_3) {
              throw new FieldsException(entry);
            } else {
              throw new ExpandException(entry);
            }
          });
    }
  }

  public T postConvert(final S source, T result, final Set<String> fields) {
    return result;
  }
}
