package com.blitzar.cards.service.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class BankAccountDTO {

     private String iban;
     private List<AccountHolderDTO> accountHolders;

     public BankAccountDTO() {}

     public BankAccountDTO(String iban, List<AccountHolderDTO> accountHolders) {
          this.iban = iban;
          this.accountHolders = accountHolders;
     }

     public String getIban() {
          return iban;
     }

     public void setIban(String iban) {
          this.iban = iban;
     }

     public List<AccountHolderDTO> getAccountHolders() {
          return accountHolders;
     }

     public void setAccountHolders(List<AccountHolderDTO> accountHolders) {
          this.accountHolders = accountHolders;
     }
}