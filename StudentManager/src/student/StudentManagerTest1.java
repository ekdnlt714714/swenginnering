package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest1 {
   private StudentManager studentManager;
   
   @BeforeEach
   void setUp() throws Exception {
      studentManager = new StudentManager();
   }

   @Test
   void testAddStudent() {
      studentManager.addStudent("박의찬");
      assertTrue(studentManager.hasStudent("박의찬"), "학생 추가");
      System.out.println("박의찬: 2025/05/21/22:48");
   }
}
