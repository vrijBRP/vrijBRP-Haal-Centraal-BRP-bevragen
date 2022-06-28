package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Functionaliteit: Als gemeente wil ik de juiste en consistente aanschrijfwijze van mijn burgers
 * Attribuut aanschrijfwijze bij een ingeschreven persoon wordt gevuld door de provider om op deze wijze op eenduidige
 * wijze een persoon te kunnen aanschrijven. Bij het samenstellen van de aanschrijfwijze worden academische titels
 * vooralsnog niet opgenomen. Academische titels zijn geen authentiek gegeven en daarom buiten scope geplaatst.
 * De aanschrijfwijze wordt gebruikt als eerste regel in de adressering op een envelop, of links bovenaan een brief,
 * direct boven het adres.
 * Bij personen met een adellijke titel of predikaat wordt ook regelVoorafgaandAanAanschrijfwijze gevuld.
 * Deze wordt in de adressering in de regel boven aanschrijfwijze geplaatst om een correcte aanschrijving van een
 * adellijke persoon samen te stellen.
 * De aanschrijfwijze kan ook worden gebruikt in lijsten met zoekresultaten, of op een website om te tonen op wie
 * het betrekking heeft.
 */
public class AanschrijfwijzeTest extends IngeschrevenPersonenResourceTest {
    @Test
    @SneakyThrows
    public void testAddressingAPersonBasedOnDesignationAndStatus() {
      // designation "V"  status "H" or "P"
        var persoon = getIngeschrevenPersoon(999990950);
        assertEquals("Maassen", persoon.getNaam().getGeslachtsnaam());
        assertEquals(1, persoon.getEmbedded().getPartners().size());
        assertEquals("Altena", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
        assertEquals("D.G. Altena-Maassen", persoon.getNaam().getAanschrijfwijze());


        persoon = getIngeschrevenPersoon( 999991413);
        assertEquals("d'Angelo-Huis in 't Veld", persoon.getNaam().getGeslachtsnaam());
        assertNull(persoon.getNaam().getVoorvoegsel());
        assertEquals("P. d'Angelo-Huis in 't Veld", persoon.getNaam().getAanschrijfwijze());
        assertEquals("Geachte mevrouw d'Angelo-Huis in 't Veld", persoon.getNaam().getAanhef());//FIXME: how to deal with foreign names?
        assertEquals("mevrouw d'Angelo-Huis in 't Veld", persoon.getNaam().getGebruikInLopendeTekst());

        // designation "V"  status "O" or "S" FIXME: geen testcase in DB
        persoon = getIngeschrevenPersoon(999990482);
        assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
        assertEquals("do Livramento de La Salete Jansz.", persoon.getNaam().getGeslachtsnaam());
        assertEquals("De las", persoon.getNaam().getVoorvoegsel());
        assertEquals("eigen", persoon.getNaam().getAanduidingNaamgebruik().toString());
        assertEquals("Geachte mevrouw De las do Livramento de La Salete Jansz.", persoon.getNaam().getAanhef());
        assertEquals("L.T.D.N.J.V. de las do Livramento de La Salete Jansz.", persoon.getNaam().getAanschrijfwijze());

//        bsnL = 0L;// designation "E"  status "H" or "P" FIXME: gebruik andere bsn
//        persoon = getIngeschrevenPersoon(bsnL);
//        assertEquals("", persoon.getNaam().getGeslachtsnaam());
//        assertEquals(1, persoon.getEmbedded().getPartners().size());
//        assertEquals("", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
//        assertEquals("", persoon.getNaam().getAanschrijfwijze());

        // designation "P"  status "H" or "P"
        persoon = getIngeschrevenPersoon(999993409);
        assertEquals("Kouwenhoven", persoon.getNaam().getGeslachtsnaam());
        assertEquals(1, persoon.getEmbedded().getPartners().size());
        assertEquals("Groeman", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
        assertEquals("T. Groeman", persoon.getNaam().getAanschrijfwijze());

//        bsnL = 0L;// designation "P"  status "O" or "S" FIXME: geen testcase in DB
//        persoon = getIngeschrevenPersoon(bsnL);
//        assertEquals("", persoon.getNaam().getGeslachtsnaam());
//        assertNull(persoon.getEmbedded().getPartners().size());
//        assertEquals("Voorletters + ex-partner Gnaam", persoon.getNaam().getAanschrijfwijze());


//        bsnL = 0L;// designation "N"  status "H" or "P" FIXME: geen testcase in DB
//        persoon = getIngeschrevenPersoon(bsnL);
//        assertEquals( "eigen naam", persoon.getNaam().getGeslachtsnaam());
//        assertEquals(1, persoon.getEmbedded().getPartners().size());
//        assertEquals("naam partner", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
//        assertEquals("Voorletters + eigen Gnaam - partner's Gnaam", persoon.getNaam().getAanschrijfwijze());

//        bsnL = 0L;// designation "N"  status "O" or "S" FIXME: geen testcase in DB
//        persoon = getIngeschrevenPersoon(bsnL);
//        assertEquals( "eigen naam", persoon.getNaam().getGeslachtsnaam());
//        assertNull(persoon.getEmbedded().getPartners().size());
//        assertEquals("Voorletters + eigen Gnaam + partner's Gnaam - ", persoon.getNaam().getAanschrijfwijze());

    }


    /**
     *  Scenario: meerdere actuele relaties
     *  Gegeven de ingeschreven persoon F.C. Groen is getrouwd in 1958 met Geel
     *  En de ingeschreven persoon is getrouwd in 1961 met Roodt
     *  En geen van beide relaties is beëindigd
     *  En de ingeschreven persoon heeft aanduidingAanschrijving='V'
     *  Als de ingeschreven persoon wordt geraadpleegd
     *  Dan is in het antwoord naam.aanschrijfwijze=F.C. Geel-Groen
     */
    @Test
    @SneakyThrows
    public void testAddressingAPersonWithMultipleMarriages() {
//        Long bsnL = 0L;
//        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);
//
//        assertEquals("F.C. Geel-Groen",
//                persons.getEmbedded().getIngeschrevenpersonen().get(0).getNaam().getAanschrijfwijze());
    }

    /**
     *   Scenario: meerdere ontbonden relaties gebruikt de laatst ontbonden relatie
     *   Gegeven de ingeschreven persoon J. Wit is getrouwd in 1958 met Geel
     *   En de ingeschreven persoon is getrouwd in 1961 met Roodt
     *   En het huwelijk met Geel is ontbonden in 1960
     *   En het huwelijk met Roodt is ontbonden in 2006
     *   En de ingeschreven persoon heeft aanduidingAanschrijving='V'
     *   Als de ingeschreven persoon wordt geraadpleegd
     *   Dan is in het antwoord naam.aanschrijfwijze=J. Roodt-Wit
     */
    @Test
    @SneakyThrows
    public void testAddressingAPersonWithPluralDivorcesWhichUsesLastMarriedName() {
//        Long bsnL = 0L;//"V" of "M" met naamsaanduiding "V" and status "O" or "S"
//    IngeschrevenPersoonHal persoon = getIngeschrevenPersoon(bsnL);

//    assertEquals("Eigen naam", persoon.getNaam().getGeslachtsnaam());
//    assertNull(persoon.getEmbedded().getPartners().size());
//    assertEquals("L. Partner-Eigen ", persoon.getNaam().getAanschrijfwijze());
    }

    /**
     *   Scenario: meerdere ontbonden relaties en oudste relatie is het laatst ontbonden //FIXME is dit mogelijk?
     *   Gegeven de ingeschreven persoon de heer J. Wit is getrouwd in 1958 met Zwart
     *   En de ingeschreven persoon is getrouwd in 1961 met Blaauw
     *   En het huwelijk met Blaauw is ontbonden in 1983
     *   En het huwelijk met Zwart is ontbonden in 2006
     *   En de ingeschreven persoon heeft aanduidingAanschrijving='V'
     *   Als de ingeschreven persoon wordt geraadpleegd
     *   Dan is in het antwoord naam.aanhef=J. Zwart-Wit
     */

    @Test
    @SneakyThrows
    public void testAddressingAPersonWhichOldestMarriageIsLastDissolved() {
//        Long bsnL = 0L;//
//        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);
//
//        assertEquals("J. Zwart-Wit",
//                persons.getEmbedded().getIngeschrevenpersonen().get(0).getNaam().getAanschrijfwijze());
    }
}
