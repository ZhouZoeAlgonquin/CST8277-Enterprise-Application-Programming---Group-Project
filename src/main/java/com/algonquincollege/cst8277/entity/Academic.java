/********************************************************************************************************
 * File:  Academic.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Entity class for the ACADEMIC club type.
 */
@Entity
@DiscriminatorValue("1")    // Academic = 1
public class Academic extends StudentClub implements Serializable {
    private static final long serialVersionUID = 1L;

    public Academic() {
        super(true);
    }
}
