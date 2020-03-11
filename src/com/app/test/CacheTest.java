package com.app.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.app.cache.CustomCache;
import com.app.dto.Student;

public class CacheTest {
	String studentkey = UUID.randomUUID().toString();
	CustomCache cc = new CustomCache();
	
	@Test
	public void testLeastUsedEviction() {
		Student s = new Student();
		s.setAge(12);
		s.setName("Bil");
		cc.addItem(studentkey, s);
		s = new Student();
		s.setAge(12);
		s.setName("Bil");
		
		cc.addItem("1", s);
		s = new Student();
		s.setAge(12);
		s.setName("Bil");
		cc.addItem("2", s);


		Student cacheStudent = (Student) cc.get(studentkey);
		assertEquals(cacheStudent.getAge(), 12);
		cacheStudent = (Student) cc.get("1");
		assertEquals(cacheStudent.getAge(), 12);
		cacheStudent = (Student) cc.get("2");
		assertEquals(cacheStudent.getAge(), 12);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cacheStudent = (Student) cc.get(studentkey);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cacheStudent = (Student) cc.get(studentkey);
		assertTrue(cacheStudent != null);
		cacheStudent = (Student) cc.get("1");
		assertTrue(cacheStudent == null);

	}


	@Test
	public void testTimeEviction() {
		Student s = new Student();
		s.setAge(12);
		s.setName("Bil");
		cc.addItem(studentkey, s);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Student cacheStudent = (Student) cc.get(studentkey);
		assertEquals(cacheStudent.getAge(), 12);

		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cacheStudent = (Student) cc.get(studentkey);
		assertTrue(cacheStudent == null);

	}

	@Test
	public void testSizeEviction() {
		Set<String> keys = new HashSet<>();
		for (int x = 0; x <= 100; x++) {
			String key = UUID.randomUUID().toString();
			keys.add(key);
			System.out.println(x + "x " + key);
			Student s = new Student();
			s.setAge(12);
			s.setName("Bil");
			cc.addItem(key, s);
		}

		Student s = new Student();
		s.setAge(12);
		s.setName("Bil");
		cc.addItem(studentkey, s);
		Enumeration<String> keySet = cc.getKeys();
		String newKey = "";
		while(keySet.hasMoreElements()) {
			String testKey = keySet.nextElement();
			if(!keys.contains(testKey)) {
				newKey=testKey;
			}
		}
		System.out.println(newKey+" "+studentkey);
		assertEquals(newKey, studentkey);
		Student latest = (Student) cc.get(newKey);
		assertEquals(latest.getName(), "Bil");

	}
	
	

}
