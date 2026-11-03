# Test Driven Development - Aufgaben

- Hab die Aufgaben alleine gelöst, deshalb bei Aufgabe 3 eigene Lösung mit möglichen alternativen Implementierungen verglichen und nicht abgedeckte Randfälle ausgewertet.


## Aufgabe 1 - Setup environment

### Einrichtung PHP & PHPUnit
- PHP (8.4) inkl. `mbstring`-Extension und PHPUnit installiert bzw. in der IDE (PhpStorm / IntelliJ) konfiguriert
- `FancyApp.zip` entpackt im Ordner `FancyApp/` (`Application/MyFancyClass.php` und `Testcases/Test.php`)

### Anpassung in `Testcases/Test.php` für aktuelles PHPUnit
Damit `Test.php` mit einer aktuellen PHP-/PHPUnit-Version ausgeführt werden kann, mussten zwei Zeilen angepasst werden (wie im Kommentar der Vorlage bereits vermerkt):
- `use PHPUnit\Framework\TestCase;` einkommentiert und Basisklasse von `PHPUnit_Framework_TestCase` auf `TestCase` geändert
- Pfad in `require_once` mit `__DIR__` relativ zur Datei aufgelöst, damit der Testlauf sowohl aus der IDE als auch über das Terminal aus jedem Verzeichnis funktioniert:

```php
require_once(__DIR__ . '/../Application/MyFancyClass.php');

// for newer PHPUnit uncomment this line
use PHPUnit\Framework\TestCase;

class Test extends TestCase
```

### Initialer Testlauf ohne Implementierung (Red)
Vor dem Schreiben des Codes alle Tests unverändert ausgeführt -> wie erwartet schlagen alle 3 Testmethoden fehl, weil die Methodenrümpfe in `MyFancyClass.php` noch leer sind (`null` zurückgeben):

```text
PHPUnit 13.0.6 by Sebastian Bergmann and contributors.

Runtime:       PHP 8.4.27

FFF                                                                 3 / 3 (100%)

There were 3 failures:

1) Test::testShortString
Failed asserting that null matches expected 'Ich bin Text mit 35 Char Länge (35)'.

2) Test::testCalcAverage
Failed asserting that null matches expected 5.

3) Test::testGetOpposite
Failed asserting that null matches expected '4.5,5,5.5'.

FAILURES!
Tests: 3, Assertions: 5, Failures: 3.
```

<!-- TODO Screenshot einfügen: Initialer Testlauf in der IDE (alle 3 Tests rot / fehlgeschlagen), z.B.:
![FailedTests](./Screenshots/failedTests.png)
-->


## Aufgabe 2 - Methoden implementieren

### Analyse der Anforderungen anhand von `Test.php`

- **`shortString($text, $length, $ending = '...')`**:
  - Kürzt einen String auf maximal `$length` Zeichen inklusive `$ending` (Standard `'...'`)
  - Ist `$text` kürzer oder genau gleich lang wie `$length`, wird `$text` unverändert ohne `$ending` zurückgegeben
  - Ist `$ending` länger als `$length` (zb. 35 Zeichen bei `$length = 20`), gibt die Methode `false` zurück — auch dann, wenn `$text` leer ist (`$textEmpty`)
  - Muss mit Umlauten korrekt umgehen (`'Ich bin Text mit 35 Char Länge (35)'` hat 35 Zeichen, aber wegen `'ä'` 36 Bytes)

- **`calcAverage($values)`**:
  - Berechnet den Durchschnitt aller Zahlen im Array `$values`
  - Leeres Array `[]` gibt `0` zurück
  - Akzeptiert sowohl Zahlen (`[4.5, 5.0, 5.5]`) als auch numerische Strings (`['4.5', '5.0', '5.5']`)
  - Enthält das Array mindestens einen nicht-numerischen Wert (zb. `'bestanden'`), gibt die Methode `false` zurück

- **`getOpposite($value, $delimiter = ',')`**:
  - Kehrt den Typ zwischen `array` und `string` um:
    - Ist `$value` ein Array $\rightarrow$ verbindet die Elemente mit `$delimiter` zu einem String (`implode`)
    - Ist `$value` ein String $\rightarrow$ teilt den String anhand von `$delimiter` in ein Array auf (`explode`)
  - Zweimaliges Aufrufen mit demselben Delimiter hebt sich gegenseitig auf (`getOpposite(getOpposite($stringList)) == $stringList`)
  - Bei ungültigem Datentyp (zb. Objekt `$this->_myFancyClass`) wird `false` zurückgegeben

### Umsetzung in `Application/MyFancyClass.php`

```php
<?php

class MyFancyClass
{
    public function shortString($text, $length, $ending = '...') {
        if (!is_string($text) || !is_string($ending) || mb_strlen($ending, 'UTF-8') > $length) {
            return false;
        }

        if (mb_strlen($text, 'UTF-8') <= $length) {
            return $text;
        }

        $cutLength = $length - mb_strlen($ending, 'UTF-8');
        return mb_substr($text, 0, $cutLength, 'UTF-8') . $ending;
    }

    public function calcAverage($values) {
        if (!is_array($values)) {
            return false;
        }

        if (empty($values)) {
            return 0;
        }

        $sum = 0;
        foreach ($values as $value) {
            if (!is_numeric($value)) {
                return false;
            }
            $sum += (float) $value;
        }

        return $sum / count($values);
    }

    public function getOpposite($value, $delimiter = ',') {
        if (is_array($value)) {
            return implode($delimiter, $value);
        }

        if (is_string($value)) {
            if ($delimiter === '') {
                return false;
            }
            return explode($delimiter, $value);
        }

        return false;
    }
}
```

**Wichtige Details bei der Umsetzung:**
- **`mb_strlen` / `mb_substr` statt `strlen` / `substr`:** Wegen dem Umlaut `'ä'` in `$text35Char` liefert `strlen()` 36 statt 35, wodurch der Testfall `shortString($text35Char, 35, $endingLong)` fälschlicherweise kürzen würde
- **Reihenfolge in `shortString`:** Die Prüfung `mb_strlen($ending) > $length` muss vor `mb_strlen($text) <= $length` stehen, weil `shortString('', 20, $endingLong)` laut Test `false` liefern muss (obwohl `''` kürzer als 20 ist)
- **Float vs. String in `getOpposite`:** `implode(',', [4.5, 5.0, 5.5])` macht aus dem Float `5.0` automatisch `'5'` (`'4.5,5,5.5'`), während bei Strings `['4.5', '5.0', '5.5']` die Nachkommastelle erhalten bleibt (`'4.5,5.0,5.5'`) — entspricht genau den beiden Assertions in `Test.php`

### Testergebnisse (Green)
Nach der Implementierung laufen alle 3 Testmethoden mit sämtlichen 27 Assertions erfolgreich durch:

```text
PHPUnit 13.0.6 by Sebastian Bergmann and contributors.

Runtime:       PHP 8.4.27

...                                                                 3 / 3 (100%)

Time: 00:00.084, Memory: 8.00 MB

OK (3 tests, 27 assertions)
```

<!-- TODO Screenshot einfügen: Erfolgreicher Testlauf in der IDE (alle 3 Tests / 27 Assertions grün), z.B.:
![PassedTests](./Screenshots/passedTests.png)
-->


## Aufgabe 3 - Auswerten

### Vergleich möglicher Implementierungen
Auch wenn verschiedene Lösungen dieselben 27 Assertions in `Test.php` grün machen, kann der Code unterschiedlich aufgebaut sein:

1. **`calcAverage` — `foreach`-Schleife vs. `array_sum()`:**
   - Unsere Lösung prüft jedes Element in einer `foreach`-Schleife mit `is_numeric()` und summiert gleichzeitig auf
   - Alternativ könnte man mit `array_filter($values, 'is_numeric')` prüfen, ob `count(...) === count($values)` stimmt, und danach `array_sum($values) / count($values)` rechnen
   - Funktional für alle gültigen Arrays identisch; `array_sum()` alleine ohne Vorprüfung würde aber bei `'bestanden'` in PHP 8 eine Type-Warnung auslösen statt `false` zurückzugeben

2. **`shortString` — `mb_strlen` vs. `strlen`:**
   - Eine Implementierung mit `strlen()` und `substr()` besteht fast alle Tests mit lateinischem Blindtext (`Lorem ipsum...`), scheitert oder verhält sich aber anders bei Strings mit Umlauten/Sonderzeichen wie `'Länge'`

### Mögliche Abweichungen bei nicht getesteten Randfällen
Für alle in `Test.php` definierten Fälle verhalten sich grüne Lösungen gleich. Unterschiede in der Funktionalität können aber dort auftreten, wo **keine Unit-Tests existieren**:

| Szenario / Randfall | In `Test.php` geprüft? | Verhalten unserer Lösung | Mögliche Abweichung anderer Lösungen |
|---|---|---|---|
| `calcAverage('kein Array')` oder `null` | Nein | Gibt sauber `false` zurück (`!is_array`) | `TypeError` bei `count()` oder `foreach` in PHP 8 |
| `getOpposite('a,b,c', '')` (leerer Delimiter) | Nein | Gibt `false` zurück | Unbehandelte `ValueError`-Exception bei `explode('', ...)` in PHP 8 |
| `shortString(12345, 10)` (Zahl statt String) | Nein | Gibt `false` zurück (`!is_string`) | Implizite Typumwandlung zu `'12345'` bei fehlendem Typ-Check |
| `shortString('Hallo', -5)` (negative Länge) | Nein | Gibt `false` zurück, da `mb_strlen($ending) > -5` | Negativer Offset in `substr()` schneidet vom Ende her ab |
| `getOpposite('')` (leerer String) | Nein | `explode(',', '')` liefert `['']` (1 Element) | Manche würden für leeren String evtl. `[]` erwarten |

### Erkenntnisse
- Durch die vorgegebenen Unit-Tests ist das gewünschte Verhalten für den Normalbetrieb und die wichtigsten Fehlerfälle klar spezifiziert, weshalb verschiedene Gruppen im Kern zur gleichen Programmlogik kommen
- Abweichungen im Code (zb. Schleife vs. Array-Funktionen) sind vernachlässigbar, solange Lesbarkeit und Verhalten stimmen
- Funktionale Unterschiede entstehen nur bei Randfällen, die in `Test.php` nicht abgedeckt sind -> zeigt gut, dass bei TDD die Qualität der Spezifikation direkt davon abhängt, wie vollständig die Randfälle (Boundary Values / Negativtests) in den Tests abgedeckt wurden
