package com.app;

import java.util.Scanner;
import java.util.UUID;

import com.app.cache.CustomCache;
import com.app.dto.Student;

public class Main {

	public static void main(String[] args) {

		CustomCache cc = new CustomCache();

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		System.out.println("This is a console application you will be prompted to make a student. \n"
				+ "The console will guide you through the state of the cache");
		Student s = new Student();
		System.out.println("Enter a student name: ");
		String name = in.nextLine();
		s.setName(name);
		System.out.println("Enter a student age (a number integer): ");
		int age = in.nextInt();
		s.setAge(age);
		String studentkey = UUID.randomUUID().toString();
		cc.addItem(studentkey, s);
		System.out.println("cache added wait 5 seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Student cacheStudent = (Student) cc.get(studentkey);
		System.out.println(cacheStudent.getAge() + "   name = " + cacheStudent.getName());
		System.out.println("cache added wait 10 seconds");
		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cacheStudent = (Student) cc.get(studentkey);
		System.out.println("STUDENT SHOULD BE NULL = " + cacheStudent);

		System.out.println("READDING STUDENT");
		cc.addItem(studentkey, s);
		cacheStudent = (Student) cc.get(studentkey);
		System.out.println(cacheStudent.getAge() + "   name = " + cacheStudent.getName());
		System.out.println("removing STUDENT");
		cc.removeItem(studentkey);

		cacheStudent = (Student) cc.get(studentkey);
		System.out.println("STUDENT SHOULD BE NULL = " + cacheStudent);

		System.out.println("READDING STUDENT TO TEST CLEAR");
		cc.addItem(studentkey, s);
		cacheStudent = (Student) cc.get(studentkey);
		System.out.println(cacheStudent.getAge() + "   name = " + cacheStudent.getName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cc.empty();
		cacheStudent = (Student) cc.get(studentkey);
		System.out.println("STUDENT SHOULD BE NULL = " + cacheStudent);

	}

}
