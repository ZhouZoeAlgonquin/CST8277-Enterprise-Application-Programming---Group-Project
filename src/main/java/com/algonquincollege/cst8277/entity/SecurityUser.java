/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * JPA Entity for security_user table
 */
@Entity(name = "SecurityUser")
@Table(name = "security_user")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_NAME, 
            query = "SELECT u FROM SecurityUser u WHERE u.username = :param1")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_STUDENT_ID, 
            query = "SELECT u FROM SecurityUser u WHERE u.student.id = :param1")
public class SecurityUser extends PojoBase implements Serializable, Principal {
    private static final long serialVersionUID = 1L;

    public static final String SECURITY_USER_BY_NAME = "SecurityUser.userByName";
    public static final String SECURITY_USER_BY_STUDENT_ID = "SecurityUser.userByStudentId";

    @Basic(optional = false)
    @Column(name = "username", nullable = false, length = 50)
    protected String username;

    @Basic(optional = false)
    @Column(name = "pwhash", nullable = false, length = 255)
    protected String pwHash;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = true)
    protected Student student;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_has_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    protected Set<SecurityRole> roles = new HashSet<>();

    public SecurityUser() {
        super();
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPwHash() { return pwHash; }

    public void setPwHash(String pwHash) { this.pwHash = pwHash; }

    public Set<SecurityRole> getRoles() { return roles; }

    public void setRoles(Set<SecurityRole> roles) { this.roles = roles; }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    @Override
    public String getName() { return getUsername(); }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof SecurityUser otherSecurityUser) {
            return Objects.equals(this.getId(), otherSecurityUser.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "SecurityUser [id = " + id + ", username = " + username + "]";
    }
}

