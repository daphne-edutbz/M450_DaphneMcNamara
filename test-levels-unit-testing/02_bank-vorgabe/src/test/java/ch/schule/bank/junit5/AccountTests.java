package ch.schule.bank.junit5;

import ch.schule.Account;
import ch.schule.SalaryAccount;
import ch.schule.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse Account.
 *
 * @author daphnemcnamara
 * @version 1.0
 */
public class AccountTests {

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("A-100");
    }

    /**
     * Testet Initialisierung eines Kontos
     */
    @Test
    public void testInit() {
        assertEquals("A-100", account.getId());
        assertEquals(0, account.getBalance());
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        assertTrue(account.deposit(5, 100)); // man deposited an tag 5, 100chf
        assertEquals(100, account.getBalance()); // checkt ob echt 100 im konto
        assertFalse(account.deposit(8, -50)); // false, weil negativ
    }

    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    public void testWithdraw() {
        account.deposit(5, 100);
        assertTrue(account.withdraw(5, 100));
        assertEquals(0, account.getBalance());
        assertFalse(account.withdraw(8, -50));
    }

    /**
     * Tests the reference from SavingsAccount
     */
    @Test
    public void testReferences() {
        assertNotNull(account.getId());
    }

    /**
     * testet the canTransact Flag
     */
    @Test
    public void testCanTransact() {
        assertTrue(account.canTransact(5));
        account.deposit(8, 50);

        assertTrue(account.canTransact(8)); // Gleicher Tag -> OK
        assertTrue(account.canTransact(10)); // Späterer Tag -> OK
        assertFalse(account.canTransact(5));  // Früherer Tag -> Verboten!
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        account.deposit(5, 100);
        assertDoesNotThrow(() -> account.print());
    }

    /**
     * Experimente mit print(year,month).
     */
    @Test
    public void testMonthlyPrint() {
        account.deposit(5, 100);
        assertDoesNotThrow(() -> account.print(2026, 9));
    }

}
