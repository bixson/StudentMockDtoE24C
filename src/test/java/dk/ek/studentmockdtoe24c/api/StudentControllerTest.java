// File: `src/test/java/dk/ek/studentmockdtoe24c/api/StudentControllerTest.java`
package dk.ek.studentmockdtoe24c.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ek.studentmockdtoe24c.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for serializing/deserializing JSON

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllStudentsById() throws Exception {
        // create test student
        Student student = new Student(
                "Johnny Doesit Again",
                "Johnny-boy69fshow",
                LocalDate.of(2000, 1, 1),
                LocalTime.of(10, 0)
        );

        // Serialize student to JSON (with objectMapper)
        String jsonRequest = objectMapper.writeValueAsString(student);

        // Perform POST request to create student
        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        // Deserialize response JSON to Student object
        String responseContent = result.getResponse().getContentAsString();
        Student actualStudent = objectMapper.readValue(responseContent, Student.class);
        Long id = actualStudent.getId();

        // perform GET request to retrieve student by ID and assert the name matches
        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(actualStudent.getName()));
    }

    @Test
    void createStudent() {
        // create test student
        Student student = new Student(
                "Johnny Doesit",
                "Johnny-boy69",
                LocalDate.of(2000, 1, 1),
                LocalTime.of(10, 0)
        );

        try {
            // Serialize student to JSON (objectMapper)
            String jsonRequest = objectMapper.writeValueAsString(student);

            // Perform POST request to create student
            mockMvc.perform(post("/api/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Johnny Doesit"))
                    .andExpect(jsonPath("$.password").value("Johnny-boy69"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
