# TechCorp - system zarządzania pracownikami

## Wymagania
- JDK w wersji 11+
- Maven
- Połączenie z internetem

## Konfiguracja zależności:
- wymagane:
  - opencsv
  - gson
  
```xml
<dependencies>
        <dependency>
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>5.12.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
```

## Instrukcja uruchomienia

- Dla IntelliJ:
  - Wymagana wtyczka maven
  - po wczytaniu projektu należy kliknąć w pop-up maven, który automatycznie pobierze zależności
  - uruchamiać runnerem z IntelliJ

- Standalone:
  - mvn compile exec:java -Dexec.mainClass="com.example.Main"
  - mvn compile exec:java