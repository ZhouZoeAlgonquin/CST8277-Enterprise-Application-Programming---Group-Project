package com.algonquincollege.cst8277.rest;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_API_VERSION;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import com.algonquincollege.cst8277.rest.resource.StudentResource;

@ApplicationPath(APPLICATION_API_VERSION)  // => /api/v1
@DeclareRoles({USER_ROLE, ADMIN_ROLE})
public class RestConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(StudentResource.class);
        return resources;
    }
}
