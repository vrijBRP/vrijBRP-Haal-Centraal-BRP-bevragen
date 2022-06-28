package nl.procura.haalcentraal.brp.bevragen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.testdata.Testdata;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VNGExampleDownloader {

  private final String API_KEY = "<ENTER-API-KEY>";

  public static void main(String[] args) {
    new VNGExampleDownloader();
  }

  public VNGExampleDownloader() {
    int nr = 0;
    List<Long> bsns = getBsns(3);
    for (Long bsn : bsns) {
      log.info("Downloading: " + ++nr + " / " + bsns.size());
      download(bsn);
    }
  }

  @SneakyThrows
  public void download(long bsnLong) {
    String bsn = new Bsn(bsnLong).toString();
    String BASE_URL = "https://www.haalcentraal.nl/haalcentraal";
    String url = new StringBuilder()
        .append(BASE_URL)
        .append("/api/brp/ingeschrevenpersonen/")
        .append(bsn)
        .append("?expand=ouders,kinderen,partners")
        .toString();
    URLConnection conn = new URL(url).openConnection();
    conn.setRequestProperty("x-api-key", API_KEY);
    try {
      byte[] response = IOUtils.toByteArray(new BufferedReader(new InputStreamReader(conn.getInputStream())),
          StandardCharsets.UTF_8);

      File folder = new File("vng");
      if (folder.mkdirs()) {
        log.debug("Created directory " + folder);
      }

      File bsnfile = new File(folder, bsn + ".json");
      FileUtils.writeStringToFile(bsnfile, new String(response), StandardCharsets.UTF_8);

    } catch (IOException e) {
      log.error("Error with BSN " + new Bsn(bsn));
    }
  }

  private List<Long> getBsns(int max) {
    return new ArrayList<>(new TreeSet<>(Testdata.getGbaVBsns())).subList(0, max);
  }
}
