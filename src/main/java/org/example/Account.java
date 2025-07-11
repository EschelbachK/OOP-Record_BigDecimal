package org.example;

import java.math.BigDecimal;
import java.util.List;

// Die Account-Klasse repräsentiert ein Bankkonto
public class Account {
    // Die eindeutige Kontonummer
    private final String accountNumber;

    // Der aktuelle Kontostand
    private BigDecimal balance;

    // Liste der Kontoinhaber (Clients)
    private final List<Client> owners;

    // Konstruktor zum Erstellen eines neuen Kontos mit Kontonummer und Inhabern
    public Account(String accountNumber, List<Client> owners) {
        this.accountNumber = accountNumber;
        this.owners = owners;
        this.balance = BigDecimal.ZERO; // Anfangsstand ist 0
    }

    // Gibt die Kontonummer zurück
    public String getAccountNumber() {
        return accountNumber;
    }

    // Gibt den aktuellen Kontostand zurück
    public BigDecimal getBalance() {
        return balance;
    }

    // Gibt die Liste der Kontoinhaber zurück
    public List<Client> getOwners() {
        return owners;
    }

    // Methode zum Einzahlen eines Betrags auf das Konto
    public void deposit(BigDecimal amount) {
        // Nur positive Beträge werden akzeptiert
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // Betrag zum Kontostand hinzufügen
            balance = balance.add(amount);
        }
    }

    // Methode zum Abheben eines Betrags vom Konto
    public boolean withdraw(BigDecimal amount) {
        // Nur positive Beträge und wenn genug Guthaben vorhanden ist
        if (amount.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(amount) >= 0) {
            // Betrag vom Kontostand abziehen
            balance = balance.subtract(amount);
            // Abhebung erfolgreich
            return true;
        }
        // Abhebung fehlgeschlagen
        return false;
    }
}
