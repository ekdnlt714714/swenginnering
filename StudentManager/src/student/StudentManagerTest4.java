package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest4 {
   private StudentManager studentManager;
   
   @BeforeEach
   void setUp() throws Exception {
      studentManager = new StudentManager();
   }
   @Test
   void testRemoveNonExistentStudent() {
       assertThrows(IllegalArgumentException.class, () -> {
           studentManager.removeStudent("신경철");
       }, "예외 발생");
       System.out.println("신경철: 2025/05/28/16:04");
       //신경철 브랜치에서 작성
   }


}
