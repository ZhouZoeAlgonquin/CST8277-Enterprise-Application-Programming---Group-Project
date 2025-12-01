/********************************************************************************************************
 * File:  SecurityRole.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@SuppressWarnings("unused")

/**
 * Role class used for (JSR-375) Jakarta EE Security authorization/authentication
 */
@Entity(name = "SecurityRole")
@Table(name = "security_role")
@NamedQuery(name = SecurityRole.SECURITY_ROLE_BY_NAME, query = "SELECT r FROM SecurityRole r WHERE r.roleName = :param1")
public class SecurityRole extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String SECURITY_ROLE_BY_NAME = "SecurityRole.RoleByName";

    @Basic(optional = false)
    @Column(name = "role_name", nullable = false, length = 50)
    protected String roleName;

    @ManyToMany(mappedBy = "roles")
    protected Set<SecurityUser> users = new HashSet<>();

    public SecurityRole() {
        super();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<SecurityUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SecurityUser> users) {
        this.users = users;
    }

    public void addUserToRole(SecurityUser user) {
        getUsers().add(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SecurityRole other)) return false;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return "SecurityRole[id=" + id + ", roleName=" + roleName + "]";
    }
}
