/********************************************************************************************************
 * File:  PojoBaseCompositeKey.java Course Materials CST 8277
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
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@SuppressWarnings("unused")

/**
 * Abstract class that is base of (class) hierarchy for all @Entity classes
 * @param <ID> - type of composite key used
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners({PojoListener.class})   // ← 修复：使用正确的 Listener
public abstract class PojoBaseCompositeKey<ID extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Version
    @Column(name = "version", nullable = false)
    protected int version;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    protected LocalDateTime created;

    @Basic(optional = false)
    @Column(name = "updated", nullable = false)
    protected LocalDateTime updated;

    public abstract ID getId();
    public abstract void setId(ID id);

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
        if (obj instanceof PojoBaseCompositeKey<?> otherPojoBaseComposite) {
            return Objects.equals(this.getId(), otherPojoBaseComposite.getId());
        }
        return false;
    }
}
