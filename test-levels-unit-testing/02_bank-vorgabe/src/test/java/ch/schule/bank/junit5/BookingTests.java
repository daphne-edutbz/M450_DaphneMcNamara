package ch.schule.bank.junit5;

import ch.schule.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests für die Klasse Booking.
 *
 * @author Luigi Cavuoti
 * @version 1.1
 */
public class BookingTests
{
	/**
	 * Tests f�r die Erzeugung von Buchungen.
	 */
	@Test
	public void testInitialization() {
		Booking booking = new Booking(10, 5000);
		assertEquals(10, booking.getDate());
		assertEquals(5000, booking.getAmount());
	}

	/**
	 * Experimente mit print().
	 */
	@Test
	public void testPrint() {
		Booking booking = new Booking(10, 5000);
		assertDoesNotThrow(() -> booking.print(10000));
	}
}
