package student;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest {
	
	StudentManager sm;

	@BeforeEach
	void setUp() throws Exception {
		sm = new StudentManager();
	}

	@Test
	void testStudentDup() {
		sm.addStudent("Undav");
		//sm.addStudent("Undav");
		assertThrows(IllegalArgumentException.class, () -> {sm.addStudent("Undav");}, "already exist");
		//sm.addStudent("Undav");
	}

	@Test
	void testStudentHalu() {
		//sm.removeStudent("Undav");
		assertThrows(IllegalArgumentException.class, () -> {sm.removeStudent("Undav");}, "does not exist");
	}

	@Test
	void testAddStudent() {
		sm.addStudent("Stiller");
		assertTrue(sm.hasStudent("Stiller"));
		
	}

	@Test
	void testRemoveStudent() {
		sm.addStudent("Stiller");
		sm.removeStudent("Stiller");
		assertFalse(sm.hasStudent("Stiller"));
		
	}
}
