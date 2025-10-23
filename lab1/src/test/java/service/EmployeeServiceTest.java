package service;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.POSITION;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class EmployeeServiceTest {
    private EmployeeService employeeService;

    private final Employee e1 = new Employee("Ania","Kołodziejczak","ak@mail.com","komworld", POSITION.PREZES);
    private final Employee e2 = new Employee("Kamil","Maciejowski","km@mail.com","komworld", POSITION.PROGRAMISTA);
    private final Employee e3 = new Employee("Maciej","Kamilczak","mk@mail.com","komputerswiat", POSITION.WICEPREZES);
    private final Employee e4 = new Employee("Hubert","Lampa","hl@mail.com","NoVideo", POSITION.PREZES);
    private final Employee e5 = new Employee("Dragomir","Panicz","dp@mail.com","GloriousComputing", POSITION.STAZYSTA);
    private final Employee e6p = new Employee("Paweł","Stonka","ak@mail.com","PolskaPartiaUczciwości",POSITION.PREZES);
//    private final Employee e7 = new Employee("Paweł","nie@mail.com");

    private void addEmployees(Employee... employees) {
        for (Employee e : employees) {
            employeeService.addEmployee(e);
        }
    }

    @BeforeEach
    void setUp(){
        employeeService = new EmployeeService();
    }
    //testy dodawania
    @Test
    void TestAddEmployee_returnTrue(){
        boolean res = employeeService.addEmployee(e1);
        assertTrue(res,"Something went wrong while adding employee");
    }

    @Test
    void TestAddEmployee_duplicated(){
        employeeService.addEmployee(e1);
        boolean res = employeeService.addEmployee(e6p);
        assertFalse(res,"User with duplicated e-mail has been added");
    }

    @Test
    void TestAddNullEmplyee_False(){
        boolean res = employeeService.addEmployee(null);
        assertFalse(res, "Added null employee");
    }

    @Test
    void TestAddEmployee_AppendsToList(){
        employeeService.addEmployee(e1);
        boolean res = employeeService.displayEmployees().contains(e1);
        assertTrue(res,"Employee wasnt added to list");
    }

    @Test
    void TestAddEmployee_doesNotAppendToList(){
        employeeService.addEmployee(e1);
        employeeService.addEmployee(e6p);
        int listSize = employeeService.displayEmployees().size();
        assertEquals(1, listSize, "List size should not increase when adding a duplicate");
    }

    @Test
    void TestAddMultipleEmployees(){
        addEmployees(e1, e2, e3);
        int res = employeeService.displayEmployees().size();
        assertEquals(3, res, "List size is incorrect after adding multiple employees");
    }
    // testy metod
    @Test
    void TestSearchByCompany_findsMultiple(){
        addEmployees(e1, e2, e3);
        List<Employee> result = employeeService.searchByCompany("komworld");
        assertEquals(2, result.size(), "Should find 2 employees for 'komworld'");
        assertTrue(result.containsAll(List.of(e1, e2)), "Result list doesn't contain expected employees");
    }
    @Test
    void TestSearchByCompany_findsOne(){
        addEmployees(e1, e2, e3);
        List<Employee> result = employeeService.searchByCompany("komputerswiat");
        assertEquals(1, result.size(), "Should find 1 employee for 'komputerswiat'");
        assertTrue(result.contains(e3), "Result list doesn't contain expected employee");
    }

    @Test
    void TestSearchByCompany_notFound(){
        addEmployees(e1, e2, e3);
        List<Employee> result = employeeService.searchByCompany("Microsoft");
        assertTrue(result.isEmpty(), "Should return empty list for non-existent company");
    }

    @Test
    void TestCalcAvgSalary_emptyList(){
        double avg = employeeService.calcAvgSalary();
        assertEquals(0.0, avg, "Average salary for empty list should be 0.0");
    }
    @Test
    void TestCalcAvgSalary_calculatesCorrectly(){
        // e1 (25000) + e2 (8000) + e3 (18000) = 51000. Avg = 17000
        addEmployees(e1, e2, e3);
        double avg = employeeService.calcAvgSalary();
        assertEquals(17000.0, avg, 0.01, "Average salary calculation is incorrect");
    }

    @Test
    void TestCalcAvgSalary_byCompany(){
        addEmployees(e1, e2, e3);
        double avg = employeeService.calcAvgSalary("komworld");
        assertEquals(16500.0, avg, 0.01, "Average salary for 'komworld' is incorrect");
    }

    @Test
    void TestCalcAvgSalary_byCompanyNotFound(){
        addEmployees(e1, e2, e3);
        double avg = employeeService.calcAvgSalary("Microsoft");
        assertEquals(0.0, avg, "Average salary for non-existent company should be 0.0");
    }

    @Test
    void TestFindEmployeeWithHighestSalary_emptyList(){
        Optional<Employee> result = employeeService.findEmployeeWithHighestSalary();
        assertTrue(result.isEmpty(), "Optional should be empty for empty list");
    }

    @Nested
    class WhenFindingHighestSalaryOverall {
        private Optional<Employee> result;

        @BeforeEach
        void setUp() {
            addEmployees(e1, e2, e3);
            result = employeeService.findEmployeeWithHighestSalary();
        }

        @Test
        void shouldFindAnEmployee() {
            assertTrue(result.isPresent(), "Optional should contain an employee");
        }

        @Test
        void shouldFindTheCorrectEmployee() {
            assertEquals(e1, result.get(), "Incorrect employee found as highest paid");
        }
    }

    @Nested
    class WhenFindingHighestSalaryByCompany {
        private Optional<Employee> result;

        @BeforeEach
        void setUp() {
            addEmployees(e1, e2, e3);
            result = employeeService.findEmployeeWithHighestSalary("komworld");
        }

        @Test
        void shouldFindAnEmployee() {
            assertTrue(result.isPresent(), "Optional should contain an employee for 'komworld'");
        }

        @Test
        void shouldFindTheCorrectEmployee() {
            assertEquals(e1, result.get(), "Incorrect employee found as highest paid for 'komworld'");
        }
    }

    @Nested
    class WhenValidatingSalaryWithInvalidData {
        private List<Employee> result;

        @BeforeEach
        void setUp() {
            e5.setSalary(2500.0);
            addEmployees(e1, e2, e5);
            result = employeeService.validateSalaryConsistency();
        }

        @Test
        void shouldFindCorrectNumberOfInvalid() {
            assertEquals(1, result.size(), "Should find one employee with inconsistent salary");
        }

        @Test
        void shouldFindSpecificInvalidEmployee() {
            assertTrue(result.contains(e5), "The list should contain the specific employee with low salary");
        }
    }

    @Nested
    class WhenValidatingSalaryWithValidData {

        @Test
        void shouldNotFindAnyInvalidEmployees() {
            addEmployees(e1, e2, e3, e4, e5);
            List<Employee> result = employeeService.validateSalaryConsistency();
            assertTrue(result.isEmpty(), "Should not find any employees with inconsistent salary");
        }
    }

    @Nested
    class WhenGettingStatisticsForMultipleCompanies {
        private Map<String, CompanyStatistics> stats;

        @BeforeEach
        void setUp() {
            addEmployees(e1, e2, e3, e4, e5);
            stats = employeeService.getCompanyStatistics();
        }

        @Test
        void shouldGroupCorrectly() {
            assertEquals(4, stats.size(), "Should group employees into 4 companies");
        }

        @Test
        void shouldContainGloriousComputing() {
            assertTrue(stats.containsKey("GloriousComputing"), "Stats should contain key 'GloriousComputing'");
        }
    }

    @Nested
    class WhenGettingStatisticsForKomworld {
        private CompanyStatistics komworldStats;

        @BeforeEach
        void setUp() {
            addEmployees(e1, e2, e3);
            Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
            komworldStats = stats.get("komworld");
        }

        @Test
        void shouldGenerateNonNullStatistics() {
            assertNotNull(komworldStats, "'komworld' stats should not be null");
        }

        @Test
        void shouldCountEmployeesCorrectly() {
            assertEquals(2, komworldStats.getEmployeeCount(), "Employee count for 'komworld' is incorrect");
        }

        @Test
        void shouldCalculateAverageSalaryCorrectly() {
            assertEquals(16500.0, komworldStats.getAvgSalary(), 0.01, "Average salary for 'komworld' is incorrect");
        }

        @Test
        void shouldFindHighestPaidEmployeeCorrectly() {
            assertEquals("Ania Kołodziejczak", komworldStats.getHighestPaid(), "Highest paid employee for 'komworld' is incorrect");
        }
    }


}
