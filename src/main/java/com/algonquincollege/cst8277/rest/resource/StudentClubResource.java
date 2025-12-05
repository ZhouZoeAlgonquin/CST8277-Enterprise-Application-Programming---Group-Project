/**
 * File: StudentClubResource.java
 * Course: CST 8277
 * Assignment: REST API for ACMECollege
 * Student(s): (Jason)
 * ===================================================================
 *
 * @author (Jason)
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

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
 * REST resource for StudentClub CRUD operations and membership management.
 * Provides endpoints for managing student clubs and club memberships in the
 * ACME College system.
 */
@Path(STUDENT_CLUB_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Get all student clubs.
     * Accessible by all authenticated users.
     *
     * @return Response containing list of all student clubs
     */
    @GET
    @RolesAllowed({ ADMIN_ROLE, USER_ROLE })
    public Response getAllStudentClubs() {
        LOG.debug("retrieving all student clubs ...");
        return Response.ok(service.getAllStudentClubs()).build();
    }

    /**
     * Get a student club by ID.
     * Only accessible by ADMIN role.
     *
     * @param clubId the ID of the student club to retrieve
     * @return Response containing the student club, or 404 if not found
     */
    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int clubId) {
        LOG.debug("retrieving student club with ID: " + clubId);
        StudentClub club = service.getStudentClubById(clubId);
        return club == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(club).build();
    }

    /**
     * Create a new student club.
     * Only accessible by ADMIN role.
     *
     * @param newClub the student club to create
     * @return Response containing the created student club with 201 status
     */
    @POST
    @RolesAllowed({ ADMIN_ROLE })
    public Response createStudentClub(StudentClub newClub) {
        LOG.debug("creating new student club ...");
        StudentClub createdClub = service.persistStudentClub(newClub);
        return Response.ok(createdClub).status(Status.CREATED).build();
    }

    /**
     * Update a student club by ID.
     * Only accessible by ADMIN role.
     *
     * @param clubId      the ID of the student club to update
     * @param updatedClub the updated student club data
     * @return Response containing the updated student club, or 404 if not found
     */
    @PUT
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response updateStudentClub(
            @PathParam(RESOURCE_PATH_ID_ELEMENT) int clubId,
            StudentClub updatedClub) {
        LOG.debug("updating student club with ID: " + clubId);
        StudentClub club = service.updateStudentClubById(clubId, updatedClub);
        return club == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(club).build();
    }

    /**
     * Delete a student club by ID.
     * Only accessible by ADMIN role.
     *
     * @param clubId the ID of the student club to delete
     * @return 204 No Content on success, 404 if not found
     */
    @DELETE
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response deleteStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int clubId) {
        LOG.debug("deleting student club with ID: " + clubId);
        service.deleteStudentClubById(clubId);
        return Response.noContent().build();
    }

    /**
     * Add a student to a club (establish membership).
     * Only accessible by ADMIN role.
     *
     * @param studentId the ID of the student
     * @param clubId    the ID of the student club
     * @return 204 No Content on success
     */
    @POST
    @Path("student/{sid}/club/{cid}")
    @RolesAllowed({ ADMIN_ROLE })
    public Response addStudentToClub(
            @PathParam("sid") int studentId,
            @PathParam("cid") int clubId) {
        LOG.debug("adding student " + studentId + " to club " + clubId);
        Student student = service.getStudentById(studentId);
        StudentClub club = service.getStudentClubById(clubId);

        if (student == null || club == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        service.addStudentToClub(studentId, clubId);
        return Response.noContent().build();
    }

    /**
     * Remove a student from a club (end membership).
     * Only accessible by ADMIN role.
     *
     * @param studentId the ID of the student
     * @param clubId    the ID of the student club
     * @return 204 No Content on success
     */
    @DELETE
    @Path("student/{sid}/club/{cid}")
    @RolesAllowed({ ADMIN_ROLE })
    public Response removeStudentFromClub(
            @PathParam("sid") int studentId,
            @PathParam("cid") int clubId) {
        LOG.debug("removing student " + studentId + " from club " + clubId);
        Student student = service.getStudentById(studentId);
        StudentClub club = service.getStudentClubById(clubId);

        if (student == null || club == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        service.removeStudentFromClub(studentId, clubId);
        return Response.noContent().build();
    }
}
