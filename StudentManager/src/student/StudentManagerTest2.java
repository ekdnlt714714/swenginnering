package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest2 {
   private StudentManager studentManager;
   
   @BeforeEach
   void setUp() throws Exception {
      studentManager = new StudentManager();
   }

   @Test
   void testRemoveStudent() {
       studentManager.addStudent("의찬");
       studentManager.removeStudent("의찬");
       assertFalse(studentManager.hasStudent("의찬"), "학생 제거");
       
       System.out.println("이강유");
   }
<<<<<<< Upstream, based on origin/master
=======

   @Test
   void testAddStudentDuplicate() {
       studentManager.addStudent("의찬박");
       assertThrows(IllegalArgumentException.class, () -> {
           studentManager.addStudent("의찬박");
       }, "예외 발생");
   }

   @Test
   void testRemoveNonExistentStudent() {
       assertThrows(IllegalArgumentException.class, () -> {
           studentManager.removeStudent("박박");
       }, "예외 발생");
   }
   //song commit test
>>>>>>> 411c3b1 commit test
}
