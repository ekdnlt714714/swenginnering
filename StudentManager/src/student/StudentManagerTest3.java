package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest3 {
   private StudentManager studentManager;
   
   @BeforeEach
   void setUp() throws Exception {
      studentManager = new StudentManager();
   }
   @Test
   void testAddStudentDuplicate() {
       studentManager.addStudent("송재원");
       assertThrows(IllegalArgumentException.class, () -> {studentManager.addStudent("송재원");}, "예외 발생");
       System.out.println("송재원");
   }
}