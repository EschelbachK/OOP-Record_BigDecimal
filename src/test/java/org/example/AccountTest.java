package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;
    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp() {
        // Beispielhafte Clients erstellen (leere Dummy-Clients)
        Client alice = new Client("Alice", "Müller", "C1");
        Client bob = new Client("Bob", "Schmidt", "C2");

        List<Client> owners = Arrays.asList(client1, client2);

        // Account mit zwei Inhabern und leerem Kontostand erstellen
        account = new Account("1234567890", owners);
    }

    @Test
    void testGetAccountNumberReturnsCorrectValue() {
        // Überprüft, ob die Kontonummer korrekt zurückgegeben wird
        assertEquals("1234567890", account.getAccountNumber());
    }

    @Test
    void testInitialBalanceIsZero() {
        // Überprüft, ob das Konto anfangs den Kontostand 0 hat
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void testGetOwnersReturnsCorrectClients() {
        // Überprüft, ob die richtigen Kontoinhaber zurückgegeben werden
        List<Client> owners = account.getOwners();
        assertEquals(2, owners.size());
        assertTrue(owners.contains(client1));
        assertTrue(owners.contains(client2));
    }

    @Test
    void testDepositIncreasesBalance() {
        // Einzahlen von 100 auf das Konto
        account.deposit(new BigDecimal("100.00"));

        // Der neue Kontostand sollte 100 sein
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    void testDepositNegativeAmountDoesNotChangeBalance() {
        // Ein negativer Betrag sollte ignoriert werden
        account.deposit(new BigDecimal("-50.00"));

        // Kontostand bleibt 0
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void testWithdrawWithSufficientBalanceSucceeds() {
        // 100 einzahlen und 50 abheben
        account.deposit(new BigDecimal("100.00"));
        boolean result = account.withdraw(new BigDecimal("50.00"));

        // Abhebung sollte erfolgreich sein
        assertTrue(result);
        assertEquals(new BigDecimal("50.00"), account.getBalance());
    }

    @Test
    void testWithdrawWithInsufficientBalanceFails() {
        // Versuch, 50 abzuheben obwohl Kontostand 0 ist
        boolean result = account.withdraw(new BigDecimal("50.00"));

        // Abhebung sollte fehlschlagen
        assertFalse(result);
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void testWithdrawNegativeAmountFails() {
        // Negativer Betrag darf nicht abgehoben werden
        boolean result = account.withdraw(new BigDecimal("-10.00"));

        // Abhebung schlägt fehl und Kontostand bleibt gleich
        assertFalse(result);
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }
}
