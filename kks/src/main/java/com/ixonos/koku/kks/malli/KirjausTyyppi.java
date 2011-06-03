package com.ixonos.koku.kks.malli;

import java.util.List;

import com.ixonos.koku.kks.utils.enums.Tietotyyppi;

/**
 * Kuvaa kokoelman yhden tiedon sisällön, edellytykset ja mahdollisuudet
 * 
 * @author tuomape
 */
public class KirjausTyyppi {

  private int koodi;
  private String nimi;
  private String kuvaus;
  private boolean moniArvoinen;
  private Tietotyyppi tietoTyyppi;
  private List<String> arvoJoukko;
  private String vastuutaho;
  private String rekisteri;
  private List<Luokitus> luokitukset;
  private List<Luokitus> kehitysasiaTyypit;

  public int getKoodi() {
    return koodi;
  }

  public void setKoodi(int koodi) {
    this.koodi = koodi;
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

  public boolean isMoniArvoinen() {
    return moniArvoinen;
  }

  public void setMoniArvoinen(boolean moniArvoinen) {
    this.moniArvoinen = moniArvoinen;
  }

  public Tietotyyppi getTietoTyyppi() {
    return tietoTyyppi;
  }

  public void setTietoTyyppi(Tietotyyppi tietoTyyppi) {
    this.tietoTyyppi = tietoTyyppi;
  }

  public List<String> getArvoJoukko() {
    return arvoJoukko;
  }

  public void setArvoJoukko(List<String> arvoJoukko) {
    this.arvoJoukko = arvoJoukko;
  }

  public String getVastuutaho() {
    return vastuutaho;
  }

  public void setVastuutaho(String vastuutaho) {
    this.vastuutaho = vastuutaho;
  }

  public String getRekisteri() {
    return rekisteri;
  }

  public void setRekisteri(String rekisteri) {
    this.rekisteri = rekisteri;
  }

  public List<Luokitus> getLuokitukset() {
    return luokitukset;
  }

  public void setLuokitukset(List<Luokitus> luokitukset) {
    this.luokitukset = luokitukset;
  }

  public List<Luokitus> getKehitysasiaTyypit() {
    return kehitysasiaTyypit;
  }

  public void setKehitysasiaTyypit(List<Luokitus> kehitysasiaTyypit) {
    this.kehitysasiaTyypit = kehitysasiaTyypit;
  }

}
