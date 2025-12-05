/**
 * File: CourseRegistrationResource.java
 * Course: CST 8277
 * Assignment: REST API for ACMECollege
 * Student(s): (Jason)
 * ===================================================================
 *
 * @author (Jason)
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.SecurityUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
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
 * REST resource for CourseRegistration (enrollment) operations.
 * Provides endpoints for managing student course enrollments in the ACME
 * College system.
 */
@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Get all course registrations/enrollments.
     * Only accessible by ADMIN role.
     *
     * @return Response containing list of all course registrations
     */
    @GET
    @RolesAllowed({ ADMIN_ROLE })
    public Response getAllCourseRegistrations() {
        LOG.debug("retrieving all course registrations ...");
        return Response.ok(service.getAllCourseRegistrations()).build();
    }

    /**
     * Get all available letter grades (A+, A, A-, B+, etc.).
     * Accessible by all authenticated users.
     *
     * @return Response containing list of available letter grades
     */
    @GET
    @Path("lettergrade")
    @RolesAllowed({ ADMIN_ROLE, USER_ROLE })
    public Response getAllLetterGrades() {
        LOG.debug("retrieving all letter grades ...");
        return Response.ok(service.getAllLetterGrades()).build();
    }

    /**
     * Get a course registration by student ID and course ID.
     * ADMIN can view any registration. USER_ROLE can only view their own.
     *
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @return Response containing the course registration, or 404 if not found
     */
    @GET
    @Path("student/{sid}/course/{cid}")
    @RolesAllowed({ ADMIN_ROLE, USER_ROLE })
    public Response getCourseRegistrationByStudentAndCourse(
            @PathParam("sid") int studentId,
            @PathParam("cid") int courseId) {
        LOG.debug("retrieving course registration for student " + studentId + " and course " + courseId);

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            CourseRegistration registration = service.getCourseRegistrationByStudentAndCourse(studentId, courseId);
            return registration == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(registration).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            // USER_ROLE can only view their own registrations
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            if (sUser.getStudent() != null && sUser.getStudent().getId() == studentId) {
                CourseRegistration registration = service.getCourseRegistrationByStudentAndCourse(studentId, courseId);
                return registration == null ? Response.status(Status.NOT_FOUND).build()
                        : Response.ok(registration).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own");
            }
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    /**
     * Create a new course registration (enroll a student in a course).
     * Only accessible by ADMIN role.
     *
     * @param newCourseRegistration the course registration to create
     * @return Response containing the created registration with 201 status
     */
    @POST
    @RolesAllowed({ ADMIN_ROLE })
    public Response createCourseRegistration(CourseRegistration newCourseRegistration) {
        LOG.debug("creating new course registration ...");
        CourseRegistration createdRegistration = service.persistCourseRegistration(newCourseRegistration);
        return Response.ok(createdRegistration).status(Status.CREATED).build();
    }

    /**
     * Assign a professor to a course registration.
     * Accepts a Professor JSON object.
     * Only accessible by ADMIN role.
     *
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @param professor the professor to assign
     * @return Response containing the updated registration, or 404 if not found
     */
    @PUT
    @Path("student/{sid}/course/{cid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ ADMIN_ROLE })
    public Response assignProfessorToCourseRegistration(
            @PathParam("sid") int studentId,
            @PathParam("cid") int courseId,
            Professor professor) {
        LOG.debug("assigning professor {} to course registration for student {} and course {}", 
                  professor != null ? professor.getId() : null, studentId, courseId);
        CourseRegistration registration = service.assignProfessorToCourseRegistration(
                studentId, courseId, professor);
        return registration == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(registration).build();
    }

    /**
     * Assign a grade to a course registration.
     * Accepts a plain text letter grade (e.g., "A+", "B", "C-").
     * Only accessible by ADMIN role.
     *
     * @param studentId   the ID of the student
     * @param courseId    the ID of the course
     * @param letterGrade the letter grade to assign (sent as plain text)
     * @return Response containing the updated registration, or 404 if not found
     */
    @PUT
    @Path("student/{sid}/course/{cid}")
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({ ADMIN_ROLE })
    public Response assignGradeToCourseRegistration(
            @PathParam("sid") int studentId,
            @PathParam("cid") int courseId,
            String letterGrade) {
        LOG.debug("assigning grade {} to course registration for student {} and course {}", 
                  letterGrade, studentId, courseId);
        CourseRegistration registration = service.assignGradeToCourseRegistration(
                studentId, courseId, letterGrade);
        return registration == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(registration).build();
    }

    /**
     * Delete a course registration by student ID and course ID.
     * Only accessible by ADMIN role.
     *
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @return 204 No Content on success, 404 if not found
     */
    @DELETE
    @Path("student/{sid}/course/{cid}")
    @RolesAllowed({ ADMIN_ROLE })
    public Response deleteCourseRegistration(
            @PathParam("sid") int studentId,
            @PathParam("cid") int courseId) {
        LOG.debug("deleting course registration for student " + studentId + " and course " + courseId);
        service.deleteCourseRegistrationByStudentAndCourse(studentId, courseId);
        return Response.noContent().build();
    }
}