/********************************************************************************************************
 * File:  CustomIdentityStoreJPAHelper.java Course Materials CST 8277
 * 
 * @author Teddy Yap
 * @author Mike Norman
 * 
 */
package com.algonquincollege.cst8277.security;

import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.entity.SecurityRole;
import com.algonquincollege.cst8277.entity.SecurityUser;

import static com.algonquincollege.cst8277.utility.MyConstants.PU_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM1;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@SuppressWarnings("unused")

@Singleton
public class CustomIdentityStoreJPAHelper {

    private static final Logger LOG = LogManager.getLogger();

    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    public SecurityUser findUserByName(String username) {
        LOG.debug("find a SecurityUser by name = {}", username);
        SecurityUser user = null;
        try {
            TypedQuery<SecurityUser> tq = em.createNamedQuery("SecurityUser.userByName", SecurityUser.class);
            tq.setParameter(PARAM1, username);
            user = tq.getSingleResult();
        } catch (NoResultException nre) {
            LOG.debug("No SecurityUser found for name={}", username);
        }
        return user;
    }

    public Set<String> findRoleNamesForUser(String username) {
        LOG.debug("find Roles For Username={}", username);
        Set<String> roleNames = emptySet();
        SecurityUser securityUser = findUserByName(username);
        if (securityUser != null) {
            roleNames = securityUser.getRoles().stream().map(s -> s.getRoleName()).collect(Collectors.toSet());
        }
        return roleNames;
    }

    @Transactional
    public void saveSecurityUser(SecurityUser user) {
        LOG.debug("adding new user={}", user);
        em.persist(user);
    }

    @Transactional
    public void saveSecurityRole(SecurityRole role) {
        LOG.debug("adding new role={}", role);
        em.persist(role);
    }
	
}