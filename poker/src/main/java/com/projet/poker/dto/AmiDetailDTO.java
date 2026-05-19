package com.projet.poker.dto;

public class AmiDetailDTO {

  private Long amitieId;
  private Long amiId;
  private String amiLogin;
  private String status;
  private boolean
      isReceiver; // true si c'est le joueur connecté qui a reçu la demande

  public Long getAmitieId() { return amitieId; }

  public void setAmitieId(Long amitieId) { this.amitieId = amitieId; }

  public Long getAmiId() { return amiId; }

  public void setAmiId(Long amiId) { this.amiId = amiId; }

  public String getAmiLogin() { return amiLogin; }

  public void setAmiLogin(String amiLogin) { this.amiLogin = amiLogin; }

  public String getStatus() { return status; }

  public void setStatus(String status) { this.status = status; }

  public boolean isReceiver() { return isReceiver; }

  public void setIsReceiver(boolean receiver) { isReceiver = receiver; }
}
