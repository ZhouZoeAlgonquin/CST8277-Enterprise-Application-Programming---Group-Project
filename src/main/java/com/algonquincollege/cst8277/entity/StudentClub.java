/********************************************************************************************************
 * File:  StudentClub.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import com.algonquincollege.cst8277.entity.Academic;
import com.algonquincollege.cst8277.entity.NonAcademic;


@SuppressWarnings("unused")

/**
 * The persistent class for the student_club database table.
 */
@Entity
@Table(name = "student_club")

@NamedQuery(
	    name = StudentClub.ALL_STUDENT_CLUBS_QUERY,
	    query = "SELECT sc FROM StudentClub sc"
	)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) 
@DiscriminatorColumn(name = "club_type")




public class StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_STUDENT_CLUBS_QUERY = "StudentClub.findAll";
	protected int id;
	protected LocalDateTime created;
	protected LocalDateTime updated;
	protected int version;

	@Basic(optional = false)
    @Column(name = "name", nullable = false, length = 128)
	protected String name;

	@Basic(optional = true)
    @Column(name = "description", length = 512)
	protected String desc;

	@Basic(optional = false)
    @Column(name = "is_academic", nullable = false)
	protected boolean isAcademic;

	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.MERGE,
					CascadeType.PERSIST
			}
			)
	@JoinTable(
			name = "student_club_student",
			joinColumns = @JoinColumn(name = "club_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id")
			)
	@JsonIgnore
	protected Set<Student> studentMembers = new HashSet<Student>();
	
	@Transient
	protected boolean editable = false;

	public StudentClub() {
		super();
	}

    public StudentClub(boolean isAcademic) {
        this();
        this.isAcademic = isAcademic;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public boolean getAcademic() {
		return this.isAcademic;
	}

	public void setAcademic(boolean isAcademic) {
		this.isAcademic = isAcademic;
	}

	@JsonIgnore
	public Set<Student> getStudentMembers() {
		return studentMembers;
	}

	public void setStudentMembers(Set<Student> studentMembers) {
		this.studentMembers = studentMembers;
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
		builder.append("StudentClub[id = ").append(id).append(", name = ").append(name).append(", desc = ")
				.append(desc).append(", isAcademic = ").append(isAcademic)
				.append(", created = ").append(created).append(", updated = ").append(updated).append(", version = ").append(version).append("]");
		return builder.toString();
	}
	
}
