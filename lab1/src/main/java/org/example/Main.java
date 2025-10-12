package org.example;

import org.example.model.Employee;
import org.example.model.POSITION;
import org.example.service.EmployeeService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();


        Employee emp1 = new Employee("Ania","Kołodziejczak","ak@mail.com","komworld", POSITION.PREZES);
        Employee emp2 = new Employee("Kamil","Maciejowski","km@mail.com","komworld", POSITION.PROGRAMISTA);
        Employee emp3 = new Employee("Maciej","Kamilczak","mk@mail.com","komputerswiat", POSITION.WICEPREZES);
        Employee emp4 = new Employee("Hubert","Lampa","hl@mail.com","NoVideo", POSITION.PREZES);
        Employee emp5 = new Employee("Dragomir","Panicz","dp@mail.com","GloriousComputing", POSITION.STAZYSTA);
        service.addEmployee(emp1);
        service.addEmployee(emp2);
        service.addEmployee(emp3);
        service.addEmployee(emp4);
        service.addEmployee(emp5);

        System.out.println("dodano pracowników: ");
        List<Employee> listEmployees = service.displayEmployees();
        for (Employee e : listEmployees) {
            System.out.println(e.toString());
        }
        System.out.println("---------------");
        System.out.println("wyszukiwanie pracowników po firmie");
        String searchCompany = "komworld";
        List<Employee> komworldEmployees = service.searchByCompany(searchCompany);
        System.out.println("Pracownicy firmy " + searchCompany + " (" + komworldEmployees.size() + " osób):");
        komworldEmployees.forEach(e -> System.out.println(e.toString()));

        System.out.println("---------------");
        System.out.println("sortowanie pracowników po nazwisku");
        List<Employee> sortedBySurname = service.groupBySurname();
        System.out.println("Pracownicy posortowani alfabetycznie:");
        sortedBySurname.forEach(e -> System.out.println(e.toString()));

        System.out.println("-----------------");
        System.out.println("pracownicy pogrupowani według pozycji w firmie");
        Map<POSITION, List<Employee>> byPosition = service.groupByPosition();
        byPosition.forEach((position, employees) -> {
            System.out.println("\n" + position.getName() + " (" + employees.size() + " osób):");
            employees.forEach(e -> System.out.println("  - " + e.getName() + " " + e.getSurname()));
        });
        System.out.println("---------------");
        System.out.println("pracownicy wyliczeni na poszczególnych stanowiskach");
        Map<POSITION, Long> countByPos = service.countByPosition();
        countByPos.forEach((position, count) ->
                System.out.println(position.getName() + ": " + count + " pracownik(ów)"));

        System.out.println("---------------");
        System.out.println("średnia pensja");
        double avgSalary = service.calcAvgSalary();
        System.out.println(String.format("Średnia pensja wszystkich pracowników: %.2f PLN", avgSalary));

        System.out.println("---------------");
        Optional<Employee> highest = service.findEmployeeWithHighestSalary();
        if (highest.isPresent()) {
            Employee highestPaid = highest.get();
            System.out.println("Najwyżej opłacany pracownik:");
            System.out.println("  Imię i nazwisko: " + highestPaid.getName() + " " + highestPaid.getSurname());
            System.out.println("  Stanowisko: " + highestPaid.getPosition().getName());
            System.out.println(String.format("  Pensja: %.2f PLN", highestPaid.getSalary()));
        } else {
            System.out.println("Brak pracowników w systemie.");
        }
        System.out.println("dodawanie pracownika o istniejącym mailu:");
        Employee powtorka = new Employee("hehe","siuu","ak@mail.com","firmaKrzak",POSITION.PREZES);
        service.addEmployee(powtorka);

//        List<Employee> listEmployees2 = service.displayEmployees();
//        for (Employee e : listEmployees2) {
//            System.out.println(e.toString());
//        }
    }
}