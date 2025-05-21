package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest {
   private StudentManager studentManager;
   
   @BeforeEach
   void setUp() throws Exception {
      studentManager = new StudentManager();
   }

   @Test
   void testAddStudent() {
      studentManager.addStudent("박의찬");
      assertTrue(studentManager.hasStudent("박의찬"), "학생 추가");
   }

   @Test
   void testRemoveStudent() {
       studentManager.addStudent("의찬");
       studentManager.removeStudent("의찬");
       assertFalse(studentManager.hasStudent("의찬"), "학생 제거");
   }

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
   
   @Test
   void testGitCommitShkch() {
	   studentManager.addStudent("신경철");
	   assertTrue(studentManager.hasStudent("신경철"));	   
   }
//brach commit test, shkch
}
