package org.example;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BankService bank = new BankService();

        Client alice = new Client("Alice", "Müller", "C1");
        Client bob = new Client("Bob", "Schmidt", "C2");

        // Gemeinschaftskonto eröffnen
        String jointAcc = bank.openAccount(List.of(alice, bob));
        bank.getAccount(jointAcc).deposit(new BigDecimal("0.03"));

        // Aufteilen
        List<String> splitAccounts = bank.split(jointAcc);

        // Ausgabe
        for (String accNum : splitAccounts) {
            Account acc = bank.getAccount(accNum);
            System.out.println("Neues Konto: " + acc.getAccountNumber() + ", Besitzer: " +
                    acc.getOwners().get(0).firstName() + ", Betrag: " + acc.getBalance());
        }
    }
}
