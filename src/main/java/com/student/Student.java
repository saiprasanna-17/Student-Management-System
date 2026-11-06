package com.student;

public class Student {
	private int id;
	private String name;
	private String department;
	private double marks;

	public Student(int id, String name, String department, double marks) {
		this.id=id;
		this.name=name;
		this.department=department;
		this.marks=marks;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id= id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department=department;
	}
	public double getMarks() {
		return marks;
	}
	public void setMarks(double marks) {
		this.marks=marks;
	}
	
}