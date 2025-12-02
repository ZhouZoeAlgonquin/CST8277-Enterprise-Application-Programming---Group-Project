/********************************************************************************************************
 * File:  CourseController.java
 * Course Materials CST 8277
 * 
 * @author Teddy Yap
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("courseController")
@SessionScoped
public class CourseController implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected List<Course> listOfCourses;
    protected Course newCourse = new Course();
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public CourseController() {
    	super();
    }
    
    @PostConstruct
    public void initialize() {
        uri = UriBuilder
                .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
                .scheme(HTTP_SCHEMA)
                .host(HOST)
                .port(PORT)
                .build();
        
        auth = HttpAuthenticationFeature.basic(loginBean.getUsername(), loginBean.getPassword());
        
        client = ClientBuilder.newClient(
                new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        
        webTarget = client.target(uri);
    }

    public List<Course> getCourses() {
        return listOfCourses;
    }
    
    public void setCourses(List<Course> listOfCourses) {
        this.listOfCourses = listOfCourses;
    }
    
    public Course getNewCourse() {
        return newCourse;
    }
    
    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }
    
    public void loadCourses() {
    	Response response = webTarget
                .register(auth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        listOfCourses = response.readEntity(new GenericType<List<Course>>(){});
    }

    public boolean isAdding() {
        return adding;
    }
    
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
    
    public void toggleAdding() {
        setAdding(!isAdding());
    }

    public String editCourse(Course course) {
        course.setEditable(true);
        return null;
    }

    public String updateCourse(Course course) {
        Response response = webTarget
        		.register(auth)
                .path(COURSE_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(course.getId())))
                .request()
                .put(Entity.json(course));
        Course updatedCourse = response.readEntity(Course.class);
        updatedCourse.setEditable(false);
        int idx = listOfCourses.indexOf(course);
        listOfCourses.remove(idx);
        listOfCourses.add(idx, updatedCourse);
        return null;
    }

    public String cancelUpdate(Course course) {
        course.setEditable(false);
        return null;
    }

    public String deleteCourse(int courseId) {
        Response response = webTarget
        		.register(auth)
                .path(COURSE_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(courseId)))
                .request()
                .get();
        Course courseToBeDeleted = response.readEntity(Course.class);
        if (courseToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(COURSE_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(courseToBeDeleted.getId())))
                    .request()
                    .delete();
        	Course deletedCourse = response.readEntity(Course.class);
            listOfCourses.remove(deletedCourse);
        }
        return null;
    }

    public String addNewCourse(Course theNewCourse) {
        Response response = webTarget
                .register(auth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewCourse));
        Course newCourse = response.readEntity(Course.class);
        listOfCourses.add(newCourse);
        return null;
    }

    public String refreshCourseForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadCourses();
        return MAIN_PAGE_REDIRECT;
    }
}
