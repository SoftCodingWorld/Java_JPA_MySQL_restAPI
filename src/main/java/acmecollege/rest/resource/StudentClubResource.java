/**
 * File:  StudentClubResource.java Course materials (23S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import java.util.List;

import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import javax.ws.rs.core.Response.Status;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.StudentClub;
import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.Student;

@Path(STUDENT_CLUB_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {
    
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getStudentClubs() {
        LOG.debug("Retrieving all student clubs...");
        List<StudentClub> studentClubs = service.getAllStudentClubs();
        LOG.debug("Student clubs found = {}", studentClubs);
        Response response = Response.ok(studentClubs).build();
        return response;
    }
    
    @GET
    // TODO SCR01 - Specify the roles allowed for this method
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int studentClubId) {
        LOG.debug("Retrieving student club with id = {}", studentClubId);
        StudentClub studentClub = service.getStudentClubById(studentClubId);
        Response response = Response.ok(studentClub).build();
        return response;
    }

    @DELETE
    // TODO SCR02 - Specify the roles allowed for this method
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int scId) {
        LOG.debug("Deleting student club with id = {}", scId);
        StudentClub sc = service.deleteStudentClub(scId);
        Response response = Response.ok(sc).build();
        return response;
    }
    
    //Please try to understand and test the below methods:
    @RolesAllowed({ADMIN_ROLE})
    @POST
    public Response createNewStudentClub(StudentClub newStudentClub) {
        LOG.debug("Adding a new student club = {}", newStudentClub);
        StudentClub studentClub;
        if (newStudentClub.isAcademic()) {
        	studentClub =  new AcademicStudentClub();
        }
        else {
        	studentClub =  new NonAcademicStudentClub();
        }
        studentClub.setName(newStudentClub.getName());
        studentClub.setAcademic(newStudentClub.isAcademic());
        
        if (service.isDuplicated(newStudentClub)) {
            HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "Entity already exists");
            return Response.status(Status.CONFLICT).entity(err).build();
        }
        else {
            StudentClub tempStudentClub = service.persistStudentClub(studentClub);
            return Response.ok(tempStudentClub).build();
        }
    }

    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @PUT
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int scId, StudentClub updatingStudentClub) {
        LOG.debug("Updating a specific student club with id = {}", scId);
        Response response = null;
        StudentClub updatedStudentClub = service.updateStudentClub(scId, updatingStudentClub);
        response = Response.ok(updatedStudentClub).build();
        return response;
    }
    
}
