/********************************************************************************************************
 * File:  CourseController.java
 * Course Materials CST 8277
 * 
 * @author Generated
 *
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.algonquincollege.cst8277.utility.MyConstants;
import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ExternalContext;

@Named("courseController")
@SessionScoped
public class CourseController implements Serializable, MyConstants {
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
        client = ClientBuilder.newClient(new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
        loadCourses();
    }

    public List<Course> getCourses() {
        return listOfCourses;
    }

    public void setCourses(List<Course> listOfCourses) {
        this.listOfCourses = listOfCourses;
    }

    public void loadCourses() {
        Response response = webTarget
                .register(auth)
                .path("course")
                .request()
                .get();
        listOfCourses = response.readEntity(new GenericType<List<Course>>(){});
    }
}
