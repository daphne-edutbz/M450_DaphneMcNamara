package ch.schule.bank.junit5;

import ch.schule.SavingsAccount;



/**
 * Tests f�r die Klasse SavingsAccount.
 *
 * @author Roger H. J&ouml;rg
 * @version 1.0
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse SavingsAccount.
 *
 * @author XXX
 * @version 1.0
 */
public class SavingsAccountTests {
	@Test
	public void test() {
		SavingsAccount account = new SavingsAccount("S-100");
		account.deposit(10, 2000);
		assertTrue(account.withdraw(11, 1500));
		assertFalse(account.withdraw(12, 1000)); // Darf nicht ins Minus
	}
}

