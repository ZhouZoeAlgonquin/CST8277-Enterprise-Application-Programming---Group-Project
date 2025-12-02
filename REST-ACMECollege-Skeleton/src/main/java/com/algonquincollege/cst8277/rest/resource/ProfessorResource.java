/**
 * File: ProfessorResource.java
 * Course: CST 8277
 * Assignment: REST API for ACMECollege
 * Student(s): (Jason)
 * ===================================================================
 *
 * @author (Jason)
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.Professor;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * REST resource for Professor CRUD operations.
 * Provides endpoints for managing professors in the ACME College system.
 */
@Path(PROFESSOR_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Get all professors.
     * Only accessible by ADMIN role.
     *
     * @return Response containing list of all professors
     */
    @GET
    @RolesAllowed({ ADMIN_ROLE })
    public Response getAllProfessors() {
        LOG.debug("retrieving all professors ...");
        return Response.ok(service.getAllProfessors()).build();
    }

    /**
     * Get a professor by ID.
     * Only accessible by ADMIN role.
     *
     * @param professorId the ID of the professor to retrieve
     * @return Response containing the professor, or 404 if not found
     */
    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
        LOG.debug("retrieving professor with ID: " + professorId);
        Professor professor = service.getProfessorById(professorId);
        return professor == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(professor).build();
    }

    /**
     * Create a new professor.
     * Only accessible by ADMIN role.
     *
     * @param newProfessor the professor to create
     * @return Response containing the created professor with 201 status
     */
    @POST
    @RolesAllowed({ ADMIN_ROLE })
    public Response createProfessor(Professor newProfessor) {
        LOG.debug("creating new professor ...");
        Professor createdProfessor = service.persistProfessor(newProfessor);
        return Response.ok(createdProfessor).status(Status.CREATED).build();
    }

    /**
     * Update a professor by ID.
     * Only accessible by ADMIN role.
     *
     * @param professorId      the ID of the professor to update
     * @param updatedProfessor the updated professor data
     * @return Response containing the updated professor, or 404 if not found
     */
    @PUT
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response updateProfessor(
            @PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId,
            Professor updatedProfessor) {
        LOG.debug("updating professor with ID: " + professorId);
        Professor professor = service.updateProfessorById(professorId, updatedProfessor);
        return professor == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(professor).build();
    }

    /**
     * Delete a professor by ID.
     * Only accessible by ADMIN role.
     *
     * @param professorId the ID of the professor to delete
     * @return 204 No Content on success, 404 if not found
     */
    @DELETE
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int professorId) {
        LOG.debug("deleting professor with ID: " + professorId);
        service.deleteProfessorById(professorId);
        return Response.noContent().build();
    }
}
