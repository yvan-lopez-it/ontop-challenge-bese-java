package com.ontop.challenge.backend.apirest.services;

public interface IWalletService {

    void checkBalance(Long userId, Double amount);

    void updateWallet(Long userId, Double amount, boolean isWithdraw);

}
