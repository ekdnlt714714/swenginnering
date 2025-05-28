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
       studentManager.addStudent("의찬박");
       assertThrows(IllegalArgumentException.class, () -> {
           studentManager.addStudent("의찬박");
       }, "예외 발생");
       System.out.println("송재원: 2025/05/21/23:44");
       //마스터 브랜치에서 작성
   }
}
