package ch.schule.bank.junit5;

import ch.schule.PromoYouthSavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests für das Promo-Jugend-Sparkonto.
 *
 * @author XXXX
 * @version 1.0
 */
public class PromoYouthSavingsAccountTests
{
	/**
	 * Der Test.
	 */
	@Test
	public void test() {
		PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-100");
		account.deposit(10, 10000); // 1% Bonus = 100
		assertEquals(10100, account.getBalance());
	}
}
