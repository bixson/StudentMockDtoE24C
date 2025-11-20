package dk.ek.studentmockdtoe24c.service;

import dk.ek.studentmockdtoe24c.dto.StudentRequestDTO;
import dk.ek.studentmockdtoe24c.dto.StudentResponseDTO;
import dk.ek.studentmockdtoe24c.model.Student;
import dk.ek.studentmockdtoe24c.repository.StudentRepository;
import dk.ek.studentmockdtoe24c.utils.StudentMapper;
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

    @Mock
    private StudentMapper studentMapper;

    private Student s1;
    private Student s2;

    @BeforeEach
    void setUp() {
        s1 = new Student("Johnny Doesit", "Johnnyboy69", LocalDate.of(2000, 1, 1), LocalTime.of(10, 0));
        s2 = new Student("Jane Doesit", "Janeboy69", LocalDate.of(1999, 2, 2), LocalTime.of(11, 0));
        studentService = new StudentService(studentRepository, studentMapper);
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
        List<?> result = studentService.getAllStudents();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getStudentById() {
        //found case (ID 1)
        s1.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s1));

        // mock mapper to return DTO
        StudentResponseDTO expectedDTO = new StudentResponseDTO(1L, "Johnny Doesit", s1.getBornDate(), s1.getBornTime());
        when(studentMapper.toStudentResponseDTO(s1)).thenReturn(expectedDTO);

        StudentResponseDTO studentResponse = studentService.getStudentById(1L);
        assertNotNull(studentResponse);
        assertEquals(1L, studentResponse.id());
        assertEquals("Johnny Doesit", studentResponse.name());


        //not found case (ID 2)
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {studentService.getStudentById(2L);});
        assertEquals("Student not found with id 2", exception.getMessage());
    }

    @Test
    void createStudent() {
        // Arrange - create a StudentRequestDTO
        StudentRequestDTO requestDTO = new StudentRequestDTO(
                s1.getName(),
                s1.getPassword(),
                s1.getBornDate(),
                s1.getBornTime()
        );

        Student toSave = s1;
        Student saved = new Student(s1.getName(), s1.getPassword(), s1.getBornDate(), s1.getBornTime());
        saved.setId(1L);

        // Mock mapper: RequestDTO -> Student
        when(studentMapper.toStudent(requestDTO)).thenReturn(toSave);
        // Mock repository save
        when(studentRepository.save(toSave)).thenReturn(saved);
        // Mock mapper: Student -> RequestDTO (current implementation returns RequestDTO)
        StudentRequestDTO responseDTO = new StudentRequestDTO(
                saved.getName(),
                saved.getPassword(),
                saved.getBornDate(),
                saved.getBornTime()
        );
        when(studentMapper.toStudentRequestDTO(saved)).thenReturn(responseDTO);

        //act
        StudentRequestDTO result = studentService.createStudent(requestDTO);

        //assert
        assertNotNull(result);
        assertEquals("Johnny Doesit", result.name());
        assertEquals("Johnnyboy69", result.password());

        //verify
        verify(studentMapper).toStudent(requestDTO);
        verify(studentRepository).save(toSave);
        verify(studentMapper).toStudentRequestDTO(saved);
    }

    @Test
    void updateStudent() {
        s1.setId(1L);

        StudentRequestDTO requestDTO = new StudentRequestDTO(
                "Johnny Updated",
                "NewPass123",
                LocalDate.of(2001, 1, 1),
                LocalTime.of(12, 0)
        );

        // mock mapper to convert requestDTO to Student
        Student updatedInfo = new Student(
                requestDTO.name(),
                requestDTO.password(),
                s1.getBornDate(),
                s1.getBornTime()
        );
        when(studentMapper.toStudent(requestDTO)).thenReturn(updatedInfo);

        //mock repository ops
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s1));
        when(studentRepository.save(s1)).thenReturn(s1);

        //mock mapper to convert saved Student to requestDTO
        StudentRequestDTO responseDTO = new StudentRequestDTO(
                s1.getName(),
                s1.getPassword(),
                s1.getBornDate(),
                s1.getBornTime()
        );
        when(studentMapper.toStudentRequestDTO(s1)).thenReturn(responseDTO);

        //act
        StudentRequestDTO result = studentService.updateStudent(1L, requestDTO);

        //assert
        assertNotNull(result);
        assertEquals("Johnny Updated", result.name());
        assertEquals("NewPass123", result.password());

        //verify
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(s1);
        verify(studentMapper).toStudent(requestDTO);
        verify(studentMapper).toStudentRequestDTO(s1);
    }

    @Test
    void deleteStudent() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        studentService.deleteStudent(1L);
        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);

        //not found case
        when(studentRepository.existsById(2L)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.deleteStudent(2L);
    });
        assertEquals("Student not found with id 2", exception.getMessage());
    }
}