package service;


import org.example.exception.ApiException;
import org.example.model.Employee;
import org.example.service.ApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private HttpResponse<String> mockHttpRes;
    @InjectMocks
    private ApiService apiService;

    private String sampleJson() {
        return """
                [
                  {
                    "id": 1,
                    "name": "Leanne Graham",
                    "username": "Bret",
                    "email": "Sincere@april.biz",
                    "company": {
                      "name": "Romaguera-Crona"
                    }
                  },
                  {
                    "id": 2,
                    "name": "Ervin Howell",
                    "username": "Antonette",
                    "email": "Shanna@melissa.tv",
                    "company": {
                      "name": "Deckow-Crist"
                    }
                  }
                ]
                """;
    }
    @Nested
    class FetchIsSuccessfull{
        private List<Employee> employees;
        private Employee e1;
        private Employee e2;

        @BeforeEach
        void setUp() throws IOException, InterruptedException, ApiException {
            when(mockHttpRes.statusCode()).thenReturn(200);
            when(mockHttpRes.body()).thenReturn(sampleJson());
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpRes);

            employees = apiService.fetchEmployeesFromAPI();
//
            if (employees.size()>0) e1 = employees.get(0);
            if (employees.size()>1) e2 = employees.get(1);
        }

        @Test
        void TestShouldReturn2Employees(){
            assertEquals(2,employees.size(),"Should import 2 employees");
        }
        @Test
        void TestShouldParseEmployeeName(){
            assertAll(
                    ()-> assertNotNull(e1,"Employee cannot be null"),
                    ()->assertEquals("Leanne",e1.getName(),"Error parsing first name of an employee")
            );
        }
        @Test
        void shouldParseFirstEmployeeSurname() {
            assertAll(
                    ()->assertNotNull(e1, "First employee should not be null"),
                    ()->assertEquals("Graham", e1.getSurname())
            );
        }

        @Test
        void shouldParseFirstEmployeeEmail() {
            assertAll(
                    () ->assertNotNull(e1, "First employee should not be null"),
                    ()->assertEquals("Sincere@april.biz", e1.getEmail())
            );

        }

        @Test
        void shouldParseFirstEmployeeCompany() {
            assertAll(
                    () -> assertNotNull(e1, "First employee should not be null"),
                    () -> assertEquals("Romaguera-Crona", e1.getCompany())
            );

        }

        @Test
        void shouldParseSecondEmployeeName() {
            assertAll(
                    () -> assertNotNull(e2, "Second employee should not be null"),
                    () -> assertEquals("Ervin", e2.getName())
            );
        }
    }
    @Nested
    class HttpError{
        private ApiException error;

        @BeforeEach
        void setUp() throws IOException, InterruptedException {
            when(mockHttpRes.statusCode()).thenReturn(500);
            when(mockHttpRes.body()).thenReturn("Server Error");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpRes);

            error  = assertThrows(ApiException.class,()-> {apiService.fetchEmployeesFromAPI();},
                    "should throw apiException");
        }
        @Test
        void TestExceptionStatusCode(){
            assertTrue(error.getMessage().contains("500"),"exception message should include status code");
        }
    }
    @Nested
    class NetworkError{
        private ApiException error;

        @BeforeEach
        void setUp()throws IOException,InterruptedException,ApiException{
            when(mockHttpClient.send(any(HttpRequest.class),any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("Network error"));
            error = assertThrows(ApiException.class,()->{
                apiService.fetchEmployeesFromAPI();
            }, "should throw apiException");
        }
        @Test
        void exceptionMessageShouldIndicateCommunicationError() {
            assertTrue(error.getMessage().contains("Błąd komunikacji z API"),
                    "Exception message should indicate communication error");
        }

        @Test
        void exceptionCauseShouldBeIOException() {
            assertTrue(error.getCause() instanceof IOException,
                    "Exception cause should be IOException");
        }
    }
    @Nested
    class JSONInvalid{
        private ApiException error;

        @BeforeEach
        void setUp() throws IOException, InterruptedException {
            String invalidJson = "{ \"no\": \"array\" }";
            when(mockHttpRes.statusCode()).thenReturn(200);
            when(mockHttpRes.body()).thenReturn(invalidJson);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockHttpRes);

            error = assertThrows(ApiException.class, () -> {
                apiService.fetchEmployeesFromAPI();
            });
        }
        @Test
        void exceptionMessageShouldIndicateInvalidStructure() {
            assertTrue(error.getMessage().contains("Oczekiwana jest tablica użytkowników"));
        }
    }

}
