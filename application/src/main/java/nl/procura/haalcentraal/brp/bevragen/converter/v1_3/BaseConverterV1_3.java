package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import java.util.Set;

import nl.procura.haalcentraal.brp.bevragen.model.exception.ReturnException;

public interface BaseConverterV1_3<T, S> {

  default T convert(final S source) throws ReturnException {
    return convert(source, null, null);
  }

  default T convert(final S source, final Set<String> fields, final Set<String> expand) throws ReturnException {
    return convert(source, createTarget(source, fields), fields, expand);
  }

  T convert(final S source, final T target, final Set<String> fields, final Set<String> expand) throws ReturnException;

  T createTarget(final S source, final Set<String> fields);

  void validate(final Set<String> fields);
}
