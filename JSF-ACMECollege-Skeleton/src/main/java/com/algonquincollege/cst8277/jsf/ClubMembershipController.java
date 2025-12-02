/********************************************************************************************************
 * File:  ClubMembershipController.java
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
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("clubMembershipController")
@SessionScoped
public class ClubMembershipController implements Serializable, MyConstants {
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
    protected StudentClubController studentClubController;

    protected List<Student> listOfStudents;
    protected List<StudentClub> listOfClubs;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public ClubMembershipController() {
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

    public List<Student> getStudents() {
        if (listOfStudents == null) {
            studentController.loadStudents();
            listOfStudents = studentController.getStudents();
        }
        return listOfStudents;
    }
    
    public List<StudentClub> getClubs() {
        if (listOfClubs == null) {
            studentClubController.loadClubs();
            listOfClubs = studentClubController.getClubs();
        }
        return listOfClubs;
    }

    public String addStudentToClub(Student student, StudentClub club) {
        // This operation is typically done through the Student or Club endpoint
        // The endpoint would handle the many-to-many relationship update
        Response response = webTarget
                .register(auth)
                .path(STUDENT_RESOURCE_NAME)
                .path(String.valueOf(student.getId()) + "/club/" + club.getId())
                .request()
                .put(Entity.json(student));
        Student updatedStudent = response.readEntity(Student.class);
        int idx = listOfStudents.indexOf(student);
        listOfStudents.remove(idx);
        listOfStudents.add(idx, updatedStudent);
        return null;
    }

    public String refreshClubMembershipForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        studentController.loadStudents();
        studentClubController.loadClubs();
        return MAIN_PAGE_REDIRECT;
    }
}
