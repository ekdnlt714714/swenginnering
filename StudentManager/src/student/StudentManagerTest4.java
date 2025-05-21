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
           studentManager.removeStudent("박박");
       }, "예외 발생");
       System.out.println("신경철: 2025/05/21/22:04");
       //마스터 브랜치에서 작성
   }


}
