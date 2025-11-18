package dk.ek.studentmockdtoe24c.service;

import dk.ek.studentmockdtoe24c.model.Student;
import dk.ek.studentmockdtoe24c.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // missing this to run mock on test class
class StudentServiceTest {


    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    private Student s1;
    private Student s2;

    @BeforeEach
    void setUp() {
        s1 = new Student("Johnny Doesit", "Johnnyboy69", LocalDate.of(2000, 1, 1), LocalTime.of(10, 0));
        s2 = new Student("Jane Doesit", "Janeboy69", LocalDate.of(1999, 2, 2), LocalTime.of(11, 0));
        studentService = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents() {
        List<Student> students = new ArrayList<>();
        students.add(s1);
        students.add(s2);

        when(studentRepository.findAll()).thenReturn(students);
        //arrange
        //act
        //assert
        List<Student> result = studentService.getAllStudents();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Johnny Doesit", result.get(0).getName());
    }

    @Test
    void getStudentById() {
        //found case
        s1.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s1));

        Student studentResponse = studentService.getStudentById(1L);
        assertNotNull(studentResponse);
        assertEquals(1L, studentResponse.getId());
        assertEquals("Johnny Doesit", studentResponse.getName());

        //not found case
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.getStudentById(2L);
        });
        assertEquals("Student not found with id 2", exception.getMessage());
    }

    @Test
    void createStudent() {
        Student toSave = s1;
        Student saved = new Student(s1.getName(), s1.getPassword(), s1.getBornDate(), s1.getBornTime());
        saved.setId(1L);

        when(studentRepository.save(toSave)).thenReturn(saved);

        //act
        Student result = studentService.createStudent(toSave);
        //assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Johnny Doesit", result.getName());
        //verify
        verify(studentRepository).save(toSave);
    }

    @Test
    void updateStudent() {
    }

    @Test
    void deleteStudent() {
    }
}