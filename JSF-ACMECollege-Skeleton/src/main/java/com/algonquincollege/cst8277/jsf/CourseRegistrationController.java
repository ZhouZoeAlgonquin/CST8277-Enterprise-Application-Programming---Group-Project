/********************************************************************************************************
 * File:  CourseRegistrationController.java
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
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("courseRegistrationController")
@SessionScoped
public class CourseRegistrationController implements Serializable, MyConstants {
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
    @Inject
    protected StudentController studentController;
    @Inject
    protected CourseController courseController;
    @Inject
    protected ProfessorController professorController;

    protected List<CourseRegistration> listOfCourseRegistrations;
    protected List<Student> listOfStudents;
    protected List<Course> listOfCourses;
    protected List<Professor> listOfProfessors;
    protected CourseRegistration newReg = new CourseRegistration();
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public CourseRegistrationController() {
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

    public List<CourseRegistration> getCourseRegistrations() {
        return listOfCourseRegistrations;
    }
    
    public void setCourseRegistrations(List<CourseRegistration> listOfCourseRegistrations) {
        this.listOfCourseRegistrations = listOfCourseRegistrations;
    }
    
    public CourseRegistration getNewReg() {
        return newReg;
    }
    
    public void setNewReg(CourseRegistration newReg) {
        this.newReg = newReg;
    }
    
    public void loadCourseRegistrations() {
    	Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .get();
        listOfCourseRegistrations = response.readEntity(new GenericType<List<CourseRegistration>>(){});
    }

    public List<Student> getStudents() {
        if (listOfStudents == null) {
            studentController.loadStudents();
            listOfStudents = studentController.getStudents();
        }
        return listOfStudents;
    }
    
    public List<Course> getCourses() {
        if (listOfCourses == null) {
            courseController.loadCourses();
            listOfCourses = courseController.getCourses();
        }
        return listOfCourses;
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

    public String addNewCourseRegistration(CourseRegistration theNewReg) {
        Response response = webTarget
                .register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .post(Entity.json(theNewReg));
        CourseRegistration newReg = response.readEntity(CourseRegistration.class);
        listOfCourseRegistrations.add(newReg);
        return null;
    }

    public String refreshCourseRegistrationForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadCourseRegistrations();
        return MAIN_PAGE_REDIRECT;
    }
}
