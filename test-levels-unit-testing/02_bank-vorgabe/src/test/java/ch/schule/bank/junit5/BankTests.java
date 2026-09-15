package ch.schule.bank.junit5;

import ch.schule.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests f�r die Klasse 'Bank'.
 *
 * @author xxxx
 * @version 1.0
 */
public class BankTests {
    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
    }

    /**
     * Tests to create new Accounts
     */
    @Test
    public void testCreate() {
        String id1 = bank.createSavingsAccount();
        String id2 = bank.createPromoYouthSavingsAccount();
        String id3 = bank.createSalaryAccount(-5000);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNull(bank.createSalaryAccount(5000));
    }


    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        String id1 = bank.createSavingsAccount();
        assertTrue(bank.deposit(id1, 5, 100));
        assertEquals(100, bank.getBalance(id1));
        assertFalse(bank.deposit("67", 10, 100));
    }
    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    public void testWithdraw() {
        String id1 = bank.createSavingsAccount();
        bank.deposit(id1, 5, 750);
        assertTrue(bank.withdraw(id1, 5, 250));
        assertEquals(500, bank.getBalance(id1));
        assertFalse(bank.withdraw("67", 10, 100));
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        String id = bank.createSavingsAccount();
        bank.deposit(id, 10, 1000);
        assertDoesNotThrow(() -> bank.print(id));
    }

    /**
     * Experimente mit print(year, month).
     */
    @Test
    public void testMonthlyPrint() {
        String id = bank.createSavingsAccount();
        bank.deposit(id, 5, 100);
        assertDoesNotThrow(() -> bank.print(id, 2026, 9));    }

    /**
     * Testet den Gesamtkontostand der Bank.
     */
    @Test
    public void testBalance() {
        String id1 = bank.createSavingsAccount();
        String id2 = bank.createSavingsAccount();
        bank.deposit(id1, 10, 3000);
        bank.deposit(id2, 10, 2000);
        assertEquals(-5000, bank.getBalance());
    }

    /**
     * Tested die Ausgabe der "top 5" konten.
     */
    @Test
    public void testTop5() {
        for (int i = 0; i < 6; i++) {
            String id = bank.createSavingsAccount();
            bank.deposit(id, 10, (i + 1) * 1000);
        }
        assertDoesNotThrow(() -> bank.printTop5());
    }

    /**
     * Tested die Ausgabe der "top 5" konten.
     */
    @Test
    public void testBottom5() {
        for (int i = 0; i < 6; i++) {
            String id = bank.createSavingsAccount();
            bank.deposit(id, 10, (i + 1) * 1000);
        }
        assertDoesNotThrow(() -> bank.printBottom5());
    }

}
