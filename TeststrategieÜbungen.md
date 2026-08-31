# Welche Elemente braucht es für eine Teststrategie? - Übungen

## Übung 1

**Wir haben folgende Beschreibung einer Verkaufssoftware:**


*Über die Verkaufssoftware kann das Autohaus seinen Verkäufern Rabattregeln vorgeben: Bei einem Kaufpreis von weniger
als 15’000 CHF soll kein Rabatt gewährt werden. Bei einem Preis bis zu 20’000 CHF sind 5% Rabatt angemessen. Liegt der
Kaufpreis unter 25’000 CHF sind 7% Rabatt möglich, darüber sind 8,5 % Rabatt zu gewähren.
Aufgabe
Leiten Sie aus dieser Beschreibung Testfälle ab. Wir wollen beide Varianten von Testfällen untersuchen.*


### Eine Tabelle mit abstrakten Testfällen. Hier verwenden Sie logische Operatoren wie > , < , etc.

| ID | Bedingung | Rabatt |
|---|---|---| 
| 1 |  >15'000CHF                        | kein Rabatt, 0% |
| 2 |  15'000CHF <= Preis <= 20'000CHF   | 5% |
| 3 |  20'000CHF < Preis < 25'000        | 7% |
| 4 |  >= 25'000        | 8.5% |

 


### Eine Tabelle mit konkreten Testfällen. Hier verwenden Sie ganz konkrete Eingabe-Werte, um die Testfälle zu erstellen.

| ID | Kaufpreis | Rabatt |
|---|---|---| 
| 1 |  12'250CHF               | kein Rabatt, 0% |
| 2 |  18'000CHF | 5% |
| 3 |   24'000CHF        | 7% |
| 4 |  50'000       | 8.5% |


## Übung 2

**Suchen Sie sich eine Webseite zum Thema Autovermietung.
Definieren Sie funktionale Black-Box Tests, die Sie brauchen, um diese Plattform zu betreiben. 
Listen Sie die 5 wichtigsten Testfälle auf
Erstellen Sie eine Tabelle mit diesen Testfälle als Markdown und stellen Sie diese in Ihr Repository.**


### Tabelle der Testfälle

**[Herzt](https://www.hertz.ch/rentacar/reservation/)**


| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache
|---|---|---|---|---|---|  
| 1 | 
| 2 |
| 3 | 
| 4 |
| 5 | 




