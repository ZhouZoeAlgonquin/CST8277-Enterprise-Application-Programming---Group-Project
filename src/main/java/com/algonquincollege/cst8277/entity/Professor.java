/********************************************************************************************************
 * File:  Professor.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")

/**
 * The persistent class for the course database table.
 */
@Entity
@Table(name = "professor")
@NamedQuery(
    name = Professor.ALL_PROFESSORS_QUERY,
    query = "SELECT p FROM Professor p"
)
@Access(AccessType.FIELD)


public class Professor extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_PROFESSORS_QUERY = "Professor.findAll";

	@Basic(optional = false)
	@Column(name = "first_name", nullable = false, length = 64)
	protected String firstName;

	@Basic(optional = false)
	@Column(name = "last_name", nullable = false, length = 64)
	protected String lastName;

	@Basic(optional = true)
	@Column(name = "degree", length = 64)
	protected String degree;

	@OneToMany(
		    mappedBy = "professor",
		    fetch = FetchType.LAZY,
		    cascade = {
		        CascadeType.MERGE,
		        CascadeType.PERSIST
		    }
		)
		@JsonIgnore
	protected Set<CourseRegistration> courseRegistrations = new HashSet<>();
	
	@Transient
	protected boolean editable = false;

	public Professor() {
		super();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	@JsonIgnore
	public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Professor[id = ").append(id).append(", firstName = ").append(firstName).append(", lastName = ")
				.append(lastName).append(", degree = ").append(degree)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}
	
}
