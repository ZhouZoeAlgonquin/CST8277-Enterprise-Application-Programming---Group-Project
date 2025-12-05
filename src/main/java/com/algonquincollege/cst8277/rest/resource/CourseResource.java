/**
 * File: CourseResource.java
 * Course: CST 8277
 * Assignment: REST API for ACMECollege
 * Student(s): (Jason)
 * ===================================================================
 *
 * @author (Jason)
 */
package com.algonquincollege.cst8277.rest.resource;

import static com.algonquincollege.cst8277.utility.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.ejb.ACMECollegeService;
import com.algonquincollege.cst8277.entity.Course;

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
 * REST resource for Course CRUD operations.
 * Provides endpoints for managing courses in the ACME College system.
 */
@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Get all courses.
     * Only accessible by ADMIN role.
     *
     * @return Response containing list of all courses
     */
    @GET
    @RolesAllowed({ ADMIN_ROLE })
    public Response getAllCourses() {
        LOG.debug("retrieving all courses ...");
        return Response.ok(service.getAllCourses()).build();
    }

    /**
     * Get a course by ID.
     * Only accessible by ADMIN role.
     *
     * @param courseId the ID of the course to retrieve
     * @return Response containing the course, or 404 if not found
     */
    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int courseId) {
        LOG.debug("retrieving course with ID: " + courseId);
        Course course = service.getCourseById(courseId);
        return course == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(course).build();
    }

    /**
     * Create a new course.
     * Only accessible by ADMIN role.
     *
     * @param newCourse the course to create
     * @return Response containing the created course with 201 status
     */
    @POST
    @RolesAllowed({ ADMIN_ROLE })
    public Response createCourse(Course newCourse) {
        LOG.debug("creating new course ...");
        Course createdCourse = service.persistCourse(newCourse);
        return Response.ok(createdCourse).status(Status.CREATED).build();
    }

    /**
     * Update a course by ID.
     * Only accessible by ADMIN role.
     *
     * @param courseId      the ID of the course to update
     * @param updatedCourse the updated course data
     * @return Response containing the updated course, or 404 if not found
     */
    @PUT
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response updateCourse(
            @PathParam(RESOURCE_PATH_ID_ELEMENT) int courseId,
            Course updatedCourse) {
        LOG.debug("updating course with ID: " + courseId);
        Course course = service.updateCourseById(courseId, updatedCourse);
        return course == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(course).build();
    }

    /**
     * Delete a course by ID.
     * Only accessible by ADMIN role.
     *
     * @param courseId the ID of the course to delete
     * @return 204 No Content on success, 404 if not found
     */
    @DELETE
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ ADMIN_ROLE })
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int courseId) {
        LOG.debug("deleting course with ID: " + courseId);
        service.deleteCourseById(courseId);
        return Response.noContent().build();
    }
}
