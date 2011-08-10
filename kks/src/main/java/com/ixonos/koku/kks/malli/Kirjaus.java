package com.ixonos.koku.kks.malli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sisältää yhden kirjauksen tiedot
 * 
 * @author tuomape
 * 
 */
public class Kirjaus {

  private static int idGenerator = 0;
  private String id;
  private String arvo;
  private Date luontiAika;
  private String kirjaaja;
  private int versio;
  private String rekisteri;
  private KirjausTyyppi tyyppi;
  private List<Luokitus> luokitukset;
  private List<String> arvot;

  public Kirjaus(String arvo, Date luontiAika, int versio, String rekisteri, String kirjaaja, KirjausTyyppi tyyppi) {
    super();
    this.id = "" + idGenerator++;
    this.arvo = arvo;
    this.luontiAika = luontiAika;
    this.versio = versio;
    this.rekisteri = rekisteri;
    this.tyyppi = tyyppi;
    this.arvot = new ArrayList<String>();
    this.kirjaaja = kirjaaja;
    this.arvot.add(arvo);
    this.luokitukset = new ArrayList<Luokitus>();
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvot.remove(this.arvo);
    this.arvo = arvo;
    this.arvot.add(arvo);
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

  public String getRekisteri() {
    return rekisteri;
  }

  public void setRekisteri(String rekisteri) {
    this.rekisteri = rekisteri;
  }

  public KirjausTyyppi getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(KirjausTyyppi tyyppi) {
    this.tyyppi = tyyppi;
  }

  public List<Luokitus> getLuokitukset() {

    List<Luokitus> tmp = new ArrayList<Luokitus>(luokitukset);

    tmp.addAll(getTyyppi().getLuokitukset());
    return tmp;
  }

  public void setLuokitukset(List<Luokitus> luokitukset) {
    this.luokitukset = luokitukset;
  }

  public List<String> getArvot() {
    return arvot;
  }

  public void setArvot(List<String> arvot) {
    this.arvot = arvot;
  }

  public String getKirjaaja() {
    return kirjaaja;
  }

  public void setKirjaaja(String kirjaaja) {
    this.kirjaaja = kirjaaja;
  }

  public boolean hasLuokitus(String luokitus) {

    for (Luokitus l : tyyppi.getLuokitukset()) {
      if (l.getNimi().startsWith(luokitus)) {
        return true;
      }
    }

    for (Luokitus l : tyyppi.getKehitysasiaTyypit()) {
      if (l.getNimi().startsWith(luokitus)) {
        return true;
      }
    }
    return false;

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
