package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BankService {
    private final Map<String, Account> accounts = new HashMap<>();
    private int nextAccountNumber = 1;

    // Konto eröffnen – generiert eine Kontonummer
    public String openAccount(List<Client> owners) {
        String accountNumber = "ACC" + nextAccountNumber++;
        Account account = new Account(accountNumber, owners);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    // Geld überweisen
    public boolean transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account from = accounts.get(fromAccountNumber);
        Account to = accounts.get(toAccountNumber);

        if (from == null || to == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (from.withdraw(amount)) {
            to.deposit(amount);
            return true;
        }
        return false;
    }

    private int accountCounter = 1000;

    private String generateAccountNumber() {
        return "ACC" + (accountCounter++);
    }
    // Aufteilen eines Gemeinschaftskontos
    public List<String> split(String accountNumber) {
        Account jointAccount = accounts.get(accountNumber);

        // Wenn Konto nicht existiert oder nur einen Inhaber hat → nicht splittbar
        if (jointAccount == null || jointAccount.getOwners().size() <= 1) {
            return List.of();
        }

        List<Client> owners = jointAccount.getOwners();
        int numOwners = owners.size();
        BigDecimal totalBalance = jointAccount.getBalance();

        // Betrag gleichmäßig aufteilen mit 2 Nachkommastellen, Rest bleibt übrig
        BigDecimal share = totalBalance
                .divide(BigDecimal.valueOf(numOwners), 2, RoundingMode.DOWN);

        // Den Rest berechnen, z. B. durch Abrunden entsteht z. B. 0.01 Rest
        BigDecimal totalSplit = share.multiply(BigDecimal.valueOf(numOwners));
        BigDecimal remainder = totalBalance.subtract(totalSplit);

        List<String> newAccountNumbers = new java.util.ArrayList<>();

        for (int i = 0; i < owners.size(); i++) {
            Client owner = owners.get(i);
            Account newAccount = new Account(generateAccountNumber(), List.of(owner));

            // Der erste bekommt den Rest (z. B. +0.01), um auf exakte Summe zu kommen
            BigDecimal individualShare = (i == 0) ? share.add(remainder) : share;
            newAccount.deposit(individualShare);

            String accNum = newAccount.getAccountNumber();
            accounts.put(accNum, newAccount);
            newAccountNumbers.add(accNum);
        }

        // Altes Gemeinschaftskonto löschen
        accounts.remove(accountNumber);

        return newAccountNumbers;
    }


    // Konto abrufen
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
