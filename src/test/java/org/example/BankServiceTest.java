package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    private BankService bankService;
    private Client clientA;
    private Client clientB;
    private Client clientC;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
        clientA = new Client("Anna", "Meyer", "C100");
        clientB = new Client("Ben", "Schulz", "C101");
        clientC = new Client("Clara", "Klein", "C102");
    }

    @Test
    void openAccount_shouldCreateNewAccount() {
        // Konto für einen Kunden erstellen
        String accNumber = bankService.openAccount(List.of(clientA));
        Account account = bankService.getAccount(accNumber);

        // Konto prüfen
        assertNotNull(account);
        assertEquals(accNumber, account.getAccountNumber());
        assertEquals(List.of(clientA), account.getOwners());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void depositAndWithdraw_shouldUpdateBalanceCorrectly() {
        // Konto erstellen und Geld einzahlen
        String accNumber = bankService.openAccount(List.of(clientA));
        Account acc = bankService.getAccount(accNumber);
        acc.deposit(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), acc.getBalance());

        // Geld abheben
        boolean success = acc.withdraw(new BigDecimal("40.00"));
        assertTrue(success);
        assertEquals(new BigDecimal("60.00"), acc.getBalance());

        // Abheben mit zu wenig Guthaben
        boolean failed = acc.withdraw(new BigDecimal("100.00"));
        assertFalse(failed);
        assertEquals(new BigDecimal("60.00"), acc.getBalance());
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccounts() {
        // Zwei Konten erstellen
        String fromAcc = bankService.openAccount(List.of(clientA));
        String toAcc = bankService.openAccount(List.of(clientB));

        // Geld einzahlen und überweisen
        Account source = bankService.getAccount(fromAcc);
        source.deposit(new BigDecimal("200.00"));

        boolean result = bankService.transfer(fromAcc, toAcc, new BigDecimal("50.00"));

        assertTrue(result);
        assertEquals(new BigDecimal("150.00"), source.getBalance());
        assertEquals(new BigDecimal("50.00"), bankService.getAccount(toAcc).getBalance());
    }

    @Test
    void transfer_shouldFailIfInsufficientFunds() {
        // Zwei Konten erstellen
        String fromAcc = bankService.openAccount(List.of(clientA));
        String toAcc = bankService.openAccount(List.of(clientB));

        // Wenig Guthaben
        Account source = bankService.getAccount(fromAcc);
        source.deposit(new BigDecimal("10.00"));

        boolean result = bankService.transfer(fromAcc, toAcc, new BigDecimal("20.00"));

        assertFalse(result);
        assertEquals(new BigDecimal("10.00"), source.getBalance());
        assertEquals(BigDecimal.ZERO, bankService.getAccount(toAcc).getBalance());
    }

    @Test
    void transfer_shouldFailIfAccountNotFound() {
        // Versuch mit ungültigen Kontonummern
        boolean result = bankService.transfer("invalidFrom", "invalidTo", new BigDecimal("10.00"));
        assertFalse(result);
    }

    @Test
    void split_shouldSplitJointAccountEvenly() {
        // Gemeinsames Konto mit 3 Personen
        String jointAcc = bankService.openAccount(List.of(clientA, clientB, clientC));
        Account joint = bankService.getAccount(jointAcc);
        joint.deposit(new BigDecimal("0.05"));

        // Split aufrufen
        List<String> splitAccounts = bankService.split(jointAcc);

        assertEquals(3, splitAccounts.size());

        // Gesamtbetrag prüfen
        BigDecimal total = BigDecimal.ZERO;
        for (String accNum : splitAccounts) {
            Account acc = bankService.getAccount(accNum);
            total = total.add(acc.getBalance());
        }

        assertEquals(new BigDecimal("0.05"), total);

        // Altes Konto sollte gelöscht sein
        assertNull(bankService.getAccount(jointAcc));
    }

    @Test
    void split_shouldNotSplitSingleOwnerAccount() {
        // Einzelkonto erstellen
        String soloAcc = bankService.openAccount(List.of(clientA));
        Account acc = bankService.getAccount(soloAcc);
        acc.deposit(new BigDecimal("10.00"));

        // Split sollte nicht funktionieren
        List<String> splitResult = bankService.split(soloAcc);

        assertTrue(splitResult.isEmpty());
        assertNotNull(bankService.getAccount(soloAcc));
    }
}
