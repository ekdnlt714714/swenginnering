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
<<<<<<< HEAD
       //
=======

>>>>>>> branch 'ekdnlt714' of https://github.com/ekdnlt714714/swenginnering.git
       System.out.println("이강유");
       System.out.println("emailTest");
       System.out.println("branchTest");
   }