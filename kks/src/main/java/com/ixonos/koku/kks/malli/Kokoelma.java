package com.ixonos.koku.kks.malli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ixonos.koku.kks.utils.Vakiot;

/**
 * Sisältää yhden kasvatuksen ja kehityksen kokoelman tiedot
 * 
 * @author tuomape
 */
public class Kokoelma {

  private String uudempiVersio;
  private String edellinenVersio;
  private boolean versioitu;
  private boolean pohjautuuToiseen;

  private String id;

  private String nimi;
  private String kuvaus;
  private String muokkaaja;
  private KokoelmaTila tila;
  private Date luontiAika;
  private int versio;
  private KokoelmaTyyppi tyyppi;
  private List<Luokitus> luokitukset;
  private Map<String, Kirjaus> kirjaukset;
  private Map<String, List<Kirjaus>> moniArvoisetKirjaukset;

  public Kokoelma(String id, Kokoelma edellinen, Date luontiAika, KokoelmaTila tila, int versio) {
    this(id, edellinen.getNimi(), edellinen.getKuvaus(), tila, luontiAika, versio, edellinen.getTyyppi());
    edellinenVersio = edellinen.getId();
    pohjautuuToiseen = true;

    for (Kirjaus k : edellinen.getKirjaukset().values()) {
      lisaaKirjaus(new Kirjaus(k.getArvo(), new Date(), k.getVersio(), k.getRekisteri(), k.getKirjaaja(), k.getTyyppi()));
    }

    for (List<Kirjaus> tmp : edellinen.getMoniArvoisetKirjaukset().values()) {
      for (Kirjaus k : tmp) {

        if (!containsClassification(k.getLuokitukset(), Vakiot.LUOKITUS_KOMMENTTI)) {
          lisaaMoniarvoinenKirjaus(new Kirjaus(k.getArvo(), new Date(), k.getVersio(), k.getRekisteri(),
              k.getKirjaaja(), k.getTyyppi()));
        }
      }
    }
  }

  private boolean containsClassification(List<Luokitus> luokitukset, String... checkedStrings) {
    List<String> tmp = new ArrayList<String>(Arrays.asList(checkedStrings));
    for (Luokitus l : luokitukset) {
      if (tmp.contains(l.getNimi())) {
        return true;
      }
    }
    return false;
  }

  public Kokoelma(String id, String nimi, String kuvaus, KokoelmaTila tila, Date luontiAika, int versio,
      KokoelmaTyyppi tyyppi) {
    super();
    this.edellinenVersio = null;
    this.pohjautuuToiseen = false;
    this.uudempiVersio = null;
    this.versioitu = false;
    this.id = id;
    this.nimi = nimi;
    this.kuvaus = kuvaus;
    this.tila = tila;
    this.luontiAika = luontiAika;
    this.versio = versio;
    this.tyyppi = tyyppi;
    kirjaukset = new HashMap<String, Kirjaus>();
    moniArvoisetKirjaukset = new HashMap<String, List<Kirjaus>>();
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public KokoelmaTila getTila() {
    return tila;
  }

  public void setTila(KokoelmaTila tila) {
    this.tila = tila;
  }

  public Date getLuontiAika() {
    return luontiAika;
  }

  public void setLuontiAika(Date luontiAika) {
    this.luontiAika = luontiAika;
  }

  public int getVersio() {
    return versio;
  }

  public void setVersio(int versio) {
    this.versio = versio;
  }

  public List<Luokitus> getLuokitukset() {
    return luokitukset;
  }

  public void setLuokitukset(List<Luokitus> luokitukset) {
    this.luokitukset = luokitukset;
  }

  public KokoelmaTyyppi getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(KokoelmaTyyppi tyyppi) {
    this.tyyppi = tyyppi;
  }

  public String getMuokkaaja() {
    return muokkaaja;
  }

  public void setMuokkaaja(String muokkaaja) {
    this.muokkaaja = muokkaaja;
  }

  public void lisaaKirjaus(Kirjaus kirjaus) {
    kirjaukset.put("" + kirjaus.getTyyppi().getKoodi(), kirjaus);
  }

  public Map<String, Kirjaus> getKirjaukset() {
    return kirjaukset;
  }

  public void setKirjaukset(Map<String, Kirjaus> kirjaukset) {
    this.kirjaukset = kirjaukset;
  }

  public String getEdellinenVersio() {
    return edellinenVersio;
  }

  public void setEdellinenVersio(String edellinenVersio) {
    this.edellinenVersio = edellinenVersio;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, List<Kirjaus>> getMoniArvoisetKirjaukset() {
    return moniArvoisetKirjaukset;
  }

  public void setMoniArvoisetKirjaukset(Map<String, List<Kirjaus>> moniArvoisetKirjaukset) {
    this.moniArvoisetKirjaukset = moniArvoisetKirjaukset;
  }

  public void lisaaMoniarvoinenKirjaus(Kirjaus kirjaus) {

    String key = "" + kirjaus.getTyyppi().getKoodi();
    if (moniArvoisetKirjaukset.containsKey(key)) {
      moniArvoisetKirjaukset.get(key).add(kirjaus);
    } else {
      List<Kirjaus> tmp = new ArrayList<Kirjaus>();
      tmp.add(kirjaus);
      moniArvoisetKirjaukset.put(key, tmp);
    }
  }

  public void poistaMoniarvoinenKirjaus(String id) {
    for (List<Kirjaus> kirjaukset : moniArvoisetKirjaukset.values()) {

      List<Kirjaus> tmp = new ArrayList<Kirjaus>(kirjaukset);
      for (Kirjaus k : tmp) {
        if (k.getId().equals(id)) {
          kirjaukset.remove(k);
        }
      }
    }
  }

  public Kirjaus getKirjaus(String id) {

    for (List<Kirjaus> tmp : moniArvoisetKirjaukset.values()) {
      for (Kirjaus k : tmp) {
        if (k.getId().equals(id)) {
          return k;
        }
      }
    }

    for (Kirjaus k : kirjaukset.values()) {
      if (k.getId().equals(id)) {
        return k;
      }
    }

    return null;
  }

  public boolean isVersioitu() {
    return versioitu;
  }

  public void setVersioitu(boolean versioitu) {
    this.versioitu = versioitu;
  }

  public String getUudempiVersio() {
    return uudempiVersio;
  }

  public void setUudempiVersio(String uudempiVersio) {
    this.uudempiVersio = uudempiVersio;
  }

  public boolean isPohjautuuToiseen() {
    return pohjautuuToiseen;
  }

  public void setPohjautuuToiseen(boolean pohjautuuToiseen) {
    this.pohjautuuToiseen = pohjautuuToiseen;
  }

}
