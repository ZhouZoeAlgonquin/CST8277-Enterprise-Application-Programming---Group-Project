/********************************************************************************************************
 * File:  StudentClubController.java
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.algonquincollege.cst8277.entity.Academic;
import com.algonquincollege.cst8277.entity.NonAcademic;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.rest.resource.MyObjectMapperProvider;

@Named("studentClubController")
@SessionScoped
public class StudentClubController implements Serializable, MyConstants {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();

    @Inject
    protected FacesContext facesContext;
    @Inject
    protected ExternalContext externalContext;
    @Inject
    protected ServletContext sc;
    @Inject
    protected LoginBean loginBean;

    protected List<StudentClub> listOfClubs;
    protected StudentClub newClub = new StudentClub();
    protected boolean adding;
    protected ResourceBundle bundle;
    
    static URI uri;
    static HttpAuthenticationFeature auth;
    protected Client client;
    protected WebTarget webTarget;

    public StudentClubController() {
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

    public List<StudentClub> getClubs() {
        return listOfClubs;
    }
    
    public void setClubs(List<StudentClub> listOfClubs) {
        this.listOfClubs = listOfClubs;
    }
    
    public StudentClub getNewClub() {
        return newClub;
    }
    
    public void setNewClub(StudentClub newClub) {
        this.newClub = newClub;
    }
    
    public void loadClubs() {
    	Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .get();
        listOfClubs = response.readEntity(new GenericType<List<StudentClub>>(){});
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

    public String editClub(StudentClub club) {
        club.setEditable(true);
        return null;
    }

    public String updateClub(StudentClub club) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(club.getId())))
                .request()
                .put(Entity.json(club));
        
        // Check if the response was successful
        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            StudentClub updatedClub = response.readEntity(StudentClub.class);
            updatedClub.setEditable(false);
            int idx = listOfClubs.indexOf(club);
            listOfClubs.remove(idx);
            listOfClubs.add(idx, updatedClub);
        } else {
            // Log error and show message to user
            String errorMsg = "Failed to update club. Status: " + response.getStatus();
            LOG.error(errorMsg);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, null));
            club.setEditable(false);
        }
        
        return null;
    }

    public String cancelUpdate(StudentClub club) {
        club.setEditable(false);
        return null;
    }

    public String deleteClub(int clubId) {
        Response response = webTarget
        		.register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(clubId)))
                .request()
                .get();
        StudentClub clubToBeDeleted = response.readEntity(StudentClub.class);
        if (clubToBeDeleted != null) {
        	response = webTarget     	
                    .register(auth)
                    .path(STUDENT_CLUB_RESOURCE_NAME + RESOURCE_PATH_ID_PATH.replace("{" + RESOURCE_PATH_ID_ELEMENT + "}", String.valueOf(clubToBeDeleted.getId())))
                    .request()
                    .delete();
        	StudentClub deletedClub = response.readEntity(StudentClub.class);
            listOfClubs.remove(deletedClub);
        }
        return null;
    }

    public String addNewClub(StudentClub theNewClub) {
        // Create the appropriate subclass based on academic flag
        StudentClub clubToCreate;
        if (theNewClub.getAcademic()) {
            clubToCreate = new Academic();
        } else {
            clubToCreate = new NonAcademic();
        }
        clubToCreate.setName(theNewClub.getName());
        clubToCreate.setDesc(theNewClub.getDesc());
        
        Response response = webTarget
                .register(auth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .post(Entity.json(clubToCreate));
        
        // Check if the response was successful
        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            StudentClub newClub = response.readEntity(StudentClub.class);
            listOfClubs.add(newClub);
            
            // Reset the form
            this.newClub = new StudentClub();
            toggleAdding();
        } else {
            // Log error and show message to user
            String errorMsg = "Failed to create club. Status: " + response.getStatus();
            LOG.error(errorMsg);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, null));
        }
        
        return null;
    }

    public String refreshClubForm() {
        Iterator<FacesMessage> facesMessageIterator = facesContext.getMessages();
        while (facesMessageIterator.hasNext()) {
            facesMessageIterator.remove();
        }
        loadClubs();
        return MAIN_PAGE_REDIRECT;
    }
}
