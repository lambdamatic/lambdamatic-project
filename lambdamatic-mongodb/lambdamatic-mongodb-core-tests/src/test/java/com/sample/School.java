/**
 * 
 */
package com.sample;

import java.util.List;

import org.lambdamatic.mongodb.annotations.Document;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 * @see http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/
 */
@Document(collection = "schools")
public class School {

	private String zipcode;

	private List<Student> students;

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode
	 *            the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the students
	 */
	public List<Student> getStudents() {
		return students;
	}

	/**
	 * @param students
	 *            the students to set
	 */
	public void setStudents(List<Student> students) {
		this.students = students;
	}

}
