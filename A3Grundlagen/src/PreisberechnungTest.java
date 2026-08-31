public class PreisberechnungTest {

    static Preisberechnung pb = new Preisberechnung();

    public static void main(String[] args) {
        boolean result = test_calculate_price();
        System.out.println("Alle Tests bestanden: " + result);
    }

    static boolean test_calculate_price() {
        double price;
        boolean test_ok = true;

        // Testfall 1: nur Grundpreis, keine Rabatte
        price = pb.calculatePrice(1000, 0, 0, 0, 0);
        if (price != 1000) {
            test_ok = false;
            System.out.println("Testfall 1 NICHT bestanden: " + price);
        }

        // Testfall 2: Händlerrabatt wirkt nur auf den Grundpreis
        price = pb.calculatePrice(1000, 0, 0, 2, 10);
        if (price != 900) {
            test_ok = false;
            System.out.println("Testfall 2 NICHT bestanden: " + price);
        }

        // Testfall 3: Grenzwert 2 Extras -> noch kein Zubehörrabatt
        price = pb.calculatePrice(0, 0, 100, 2, 0);
        if (price != 100) {
            test_ok = false;
            System.out.println("Testfall 3 NICHT bestanden: " + price);
        }

        // Testfall 4: Grenzwert 3 Extras -> 10% Zubehörrabatt
        price = pb.calculatePrice(0, 0, 100, 3, 10);
        if (price != 90) {
            test_ok = false;
            System.out.println("Testfall 4 NICHT bestanden: " + price);
        }

        // Testfall 5: 4 Extras -> weiterhin 10% Zubehörrabatt
        price = pb.calculatePrice(0, 0, 100, 4, 0);
        if (price != 90) {
            test_ok = false;
            System.out.println("Testfall 5 NICHT bestanden: " + price);
        }

        // Testfall 6: Grenzwert 5 Extras -> sollte 15% Zubehörrabatt sein
        price = pb.calculatePrice(0, 0, 100, 5, 0);
        if (price != 85) {
            test_ok = false;
            System.out.println("Testfall 6 NICHT bestanden: " + price);
        }

        // Testfall 7: Sonderpreis wird immer voll addiert, ohne Rabatt
        price = pb.calculatePrice(0, 500, 0, 0, 50);
        if (price != 500) {
            test_ok = false;
            System.out.println("Testfall 7 NICHT bestanden: " + price);
        }

        // Testfall 8: Kombination - alle Parameter gleichzeitig aktiv
        // baseprice (1000 - 20% = 800) + specialprice (200) + extraprice (100 - 20% [discount > addon_discount] = 80)
        price = pb.calculatePrice(1000, 200, 100, 3, 20);
        if (price != 1080) {
            test_ok = false;
            System.out.println("Testfall 8 NICHT bestanden: " + price);
        }

        return test_ok;
    }
}