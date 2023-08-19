package com.blitzar.cards.service.dto;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class AccountHolderDTO {

    private String accountHolderName;

    public AccountHolderDTO() {}

    public AccountHolderDTO(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
}
