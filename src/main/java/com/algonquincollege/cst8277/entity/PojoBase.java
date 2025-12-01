/********************************************************************************************************
 * File:  PojoBase.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

/**
 * Abstract class that is base of (class) hierarchy for all @Entity classes
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners({PojoListener.class})   // ← 正确监听器（你项目自带）
public abstract class PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected int id;

    @Version
    @Column(name = "version", nullable = false)
    protected int version = 1;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    protected LocalDateTime created;

    @Basic(optional = false)
    @Column(name = "updated", nullable = false)
    protected LocalDateTime updated;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getUpdated() { return updated; }
    public void setUpdated(LocalDateTime updated) { this.updated = updated; }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof PojoBase otherPojoBase) {
            return Objects.equals(this.getId(), otherPojoBase.getId());
        }
        return false;
    }
}

