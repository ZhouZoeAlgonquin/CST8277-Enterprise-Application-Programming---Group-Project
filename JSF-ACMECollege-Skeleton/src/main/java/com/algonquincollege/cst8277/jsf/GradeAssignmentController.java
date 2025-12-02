/********************************************************************************************************
 * File:  GradeAssignmentController.java
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
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("gradeAssignmentController")
@SessionScoped
public class GradeAssignmentController implements Serializable, MyConstants {
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
    protected CourseRegistrationController courseRegistrationController;

    protected List<CourseRegistration> listOfCourseRegistrations;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public GradeAssignmentController() {
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
        if (listOfCourseRegistrations == null) {
            courseRegistrationController.loadCourseRegistrations();
            listOfCourseRegistrations = courseRegistrationController.getCourseRegistrations();
        }
        return listOfCourseRegistrations;
    }

    public String assignGrade(CourseRegistration registration) {
        Response response = webTarget
        		.register(auth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .path("student/" + registration.getId().getStudentId() + "/course/" + registration.getId().getCourseId())
                .request()
                .put(Entity.json(registration));
        CourseRegistration updatedReg = response.readEntity(CourseRegistration.class);
        int idx = listOfCourseRegistrations.indexOf(registration);
        listOfCourseRegistrations.remove(idx);
        listOfCourseRegistrations.add(idx, updatedReg);
        return null;
    }

    public String refreshGradeAssignmentForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        courseRegistrationController.loadCourseRegistrations();
        return MAIN_PAGE_REDIRECT;
    }
}
