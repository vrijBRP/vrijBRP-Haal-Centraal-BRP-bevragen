package nl.procura.haalcentraal.brp.bevragen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.brp.bevragen.client.GbaWsClient;
import nl.procura.gbaws.testdata.Testdata;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListResponse;
import nl.procura.haalcentraal.brp.bevragen.model.BsnParam;

@Primary
@Service
public class TestPersonPersonWsService extends ProcuraPersonWsService {

  @Autowired
  private ObjectMapper objectMapper;

  public TestPersonPersonWsService(GbaWsClient client) {
    super(client);
  }

  @Override
  public GbaWsPersonList getInternal(BsnParam bsn) {
    try {
      byte[] data = Testdata.getPersonDataAsBytes(bsn.toLong(), Testdata.DataSet.GBAV);
      GbaWsPersonListResponse response = objectMapper.readValue(data, GbaWsPersonListResponse.class);
      return response.getPersonlists().get(0);
    } catch (RuntimeException | IOException e) {
      return null;
    }
  }
}
