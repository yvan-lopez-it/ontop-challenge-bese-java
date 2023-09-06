package com.ontop.challenge.backend.apirest.services;

public interface IWalletService {

    Double getBalance(Long userId, Double amount);

    void updateWallet(Long userId, Double amount, boolean isWithdraw);

}
