/********************************************************************************************************
 * File:  TestACMECollegeSystem.java
 * Course Materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 */
package acmecollege;

import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_API_VERSION;
import static com.algonquincollege.cst8277.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.PROFESSOR_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.algonquincollege.cst8277.entity.Student;
import acmecollege.MyObjectMapperProvider;
import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.entity.Academic;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
                .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
                .scheme(HTTP_SCHEMA)
                .host(HOST)
                .port(PORT)
                .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;

    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(new ClientConfig()).register(MyObjectMapperProvider.class)
                .register(new LoggingFeature());
        webTarget = client.target(uri);
    }

    // ==================== STUDENT TESTS ====================

    @Test
    public void test01_get_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 01: GET all students with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>() {
        });
        assertThat(students, is(not(empty())));
    }

    @Test
    public void test02_get_student_by_id_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 02: GET specific student by ID with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(Student.class);
        assertThat(student.getId(), is(1));
    }

    @Test
    public void test03_create_new_student_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 03: POST create new student with ADMIN role");
        Student newStudent = new Student();
        newStudent.setFirstName("TestFirst");
        newStudent.setLastName("TestLast");
        newStudent.setEmail("test@example.com");
        newStudent.setPhone("555-1234");
        newStudent.setProgram("CST");

        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Student createdStudent = response.readEntity(Student.class);
        assertThat(createdStudent.getFirstName(), is("TestFirst"));
    }

    @Test
    public void test04_update_student_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 04: PUT update student with ADMIN role");
        Student updateStudent = new Student();
        updateStudent.setFirstName("UpdatedFirst");
        updateStudent.setLastName("UpdatedLast");
        updateStudent.setEmail("updated@example.com");
        updateStudent.setPhone("555-9999");
        updateStudent.setProgram("CST");

        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("1")
                .request()
                .put(Entity.entity(updateStudent, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        Student updated = response.readEntity(Student.class);
        assertThat(updated.getFirstName(), is("UpdatedFirst"));
    }

    @Test
    public void test05_get_student_with_user_role_own_record() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 05: GET own student record with USER role");
        Response response = webTarget
                .register(userAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test06_get_programs_with_user_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 06: GET programs endpoint with USER role");
        Response response = webTarget
                .register(userAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("program")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<String> programs = response.readEntity(new GenericType<List<String>>() {
        });
        assertThat(programs, is(not(empty())));
    }

    // ==================== COURSE TESTS ====================

    @Test
    public void test07_get_all_courses_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 07: GET all courses with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<Course> courses = response.readEntity(new GenericType<List<Course>>() {
        });
        assertThat(courses, is(not(empty())));
    }

    @Test
    public void test08_create_new_course_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 08: POST create new course with ADMIN role");
        Course newCourse = new Course();
        newCourse.setCourseCode("CST9999");
        newCourse.setCourseTitle("Advanced Java Testing");
        newCourse.setCreditUnits(3);
        newCourse.setOnline((short) 0);

        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
        Course createdCourse = response.readEntity(Course.class);
        assertThat(createdCourse.getCourseCode(), is("CST9999"));
    }

    @Test
    public void test09_get_course_by_id_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 09: GET specific course by ID with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .path("1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        Course course = response.readEntity(Course.class);
        assertThat(course.getId(), is(1));
    }

    @Test
    public void test10_update_course_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 10: PUT update course with ADMIN role");
        Course updateCourse = new Course();
        updateCourse.setCourseCode("CST8277UPD");
        updateCourse.setCourseTitle("Updated Course Title");
        updateCourse.setCreditUnits(4);
        updateCourse.setOnline((short) 1);

        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .path("1")
                .request()
                .put(Entity.entity(updateCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
    }

    // ==================== PROFESSOR TESTS ====================

    @Test
    public void test11_get_all_professors_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 11: GET all professors with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(PROFESSOR_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>() {
        });
        assertThat(professors, is(not(empty())));
    }

    @Test
    public void test12_create_new_professor_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 12: POST create new professor with ADMIN role");
        Professor newProfessor = new Professor();
        newProfessor.setFirstName("Dr. Test");
        newProfessor.setLastName("Professor");
        newProfessor.setDegree("PhD");

        Response response = webTarget
                .register(adminAuth)
                .path(PROFESSOR_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
        Professor createdProfessor = response.readEntity(Professor.class);
        assertThat(createdProfessor.getFirstName(), is("Dr. Test"));
    }

    @Test
    public void test13_get_professor_by_id_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 13: GET specific professor by ID with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(PROFESSOR_RESOURCE_NAME)
                .path("1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(Professor.class);
        assertThat(professor.getId(), is(1));
    }

    // ==================== COURSE REGISTRATION (ENROLLMENT) TESTS
    // ====================

    @Test
    public void test14_get_all_course_registrations_with_admin_role()
            throws JsonMappingException, JsonProcessingException {
        logger.info("Test 14: GET all course registrations with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<CourseRegistration> registrations = response.readEntity(new GenericType<List<CourseRegistration>>() {
        });
        assertThat(registrations, is(not(empty())));
    }

    @Test
    public void test15_get_letter_grades_with_user_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 15: GET letter grades with USER role");
        Response response = webTarget
                .register(userAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .path("lettergrade")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<String> grades = response.readEntity(new GenericType<List<String>>() {
        });
        assertThat(grades, is(not(empty())));
    }

    @Test
    public void test16_create_course_registration_with_admin_role()
            throws JsonMappingException, JsonProcessingException {
        logger.info("Test 16: POST create course registration with ADMIN role");
        CourseRegistration newReg = new CourseRegistration();
        CourseRegistrationPK pk = new CourseRegistrationPK();
        pk.setStudentId(1);
        pk.setCourseId(1);
        newReg.setId(pk);
        newReg.setYear(2024);
        newReg.setSemester("WINTER");

        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newReg, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
    }

    @Test
    public void test17_get_course_registration_by_student_and_course_admin()
            throws JsonMappingException, JsonProcessingException {
        logger.info("Test 17: GET course registration by student and course with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .path("student/1/course/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test18_get_course_registration_with_user_role_own_course()
            throws JsonMappingException, JsonProcessingException {
        logger.info("Test 18: GET own course registration with USER role");
        Response response = webTarget
                .register(userAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .path("student/1/course/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test19_update_course_registration_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 19: PUT update course registration with ADMIN role");
        CourseRegistration updateReg = new CourseRegistration();
        CourseRegistrationPK pk = new CourseRegistrationPK();
        pk.setStudentId(1);
        pk.setCourseId(1);
        updateReg.setId(pk);
        updateReg.setYear(2024);
        updateReg.setSemester("WINTER");
        updateReg.setLetterGrade("A");

        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_REGISTRATION_RESOURCE_NAME)
                .path("student/1/course/1")
                .request()
                .put(Entity.entity(updateReg, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
    }

    // ==================== STUDENT CLUB TESTS ====================

    @Test
    public void test20_get_all_student_clubs_with_user_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 20: GET all student clubs with USER role");
        Response response = webTarget
                .register(userAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<StudentClub> clubs = response.readEntity(new GenericType<List<StudentClub>>() {
        });
        assertThat(clubs, is(not(empty())));
    }

    @Test
    public void test21_create_student_club_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 21: POST create student club with ADMIN role");
        Academic newClub = new Academic();
        newClub.setName("Test Academic Club");
        newClub.setDesc("A test club for testing");

        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newClub, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
        StudentClub createdClub = response.readEntity(StudentClub.class);
        assertThat(createdClub.getName(), is("Test Academic Club"));
    }

    @Test
    public void test22_get_student_club_by_id_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 22: GET specific student club by ID with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .path("1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        StudentClub club = response.readEntity(StudentClub.class);
        assertThat(club.getId(), is(1));
    }

    @Test
    public void test23_add_student_to_club_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 23: POST add student to club with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .path("student/1/club/1")
                .request()
                .post(Entity.json(""));
        assertThat(response.getStatus(), is(204));
    }

    @Test
    public void test24_update_student_club_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 24: PUT update student club with ADMIN role");
        Academic updateClub = new Academic();
        updateClub.setName("Updated Club Name");
        updateClub.setDesc("Updated description");

        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .path("1")
                .request()
                .put(Entity.entity(updateClub, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test25_remove_student_from_club_with_admin_role() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 25: DELETE remove student from club with ADMIN role");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .path("student/1/club/1")
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
    }

    // ==================== NEGATIVE TESTING - AUTHORIZATION & VALIDATION
    // ====================

    @Test
    public void test26_create_course_with_user_role_forbidden() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 26: Negative test - POST course with USER role should be FORBIDDEN");
        Course newCourse = new Course();
        newCourse.setCourseCode("TEST0001");
        newCourse.setCourseTitle("Test");
        newCourse.setCreditUnits(3);
        newCourse.setOnline((short) 0);

        Response response = webTarget
                .register(userAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(403));
        assertThat(response.getMediaType(), is(not(MediaType.APPLICATION_XML_TYPE)));
    }

    @Test
    public void test27_delete_student_with_user_role_forbidden() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 27: Negative test - DELETE student with USER role should be FORBIDDEN");
        Response response = webTarget
                .register(userAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("1")
                .request()
                .delete();
        assertThat(response.getStatus(), is(403));
    }

    @Test
    public void test28_get_nonexistent_student() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 28: Negative test - GET nonexistent student should return NOT_FOUND");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .path("99999")
                .request()
                .get();
        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void test29_get_nonexistent_course() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 29: Negative test - GET nonexistent course should return NOT_FOUND");
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .path("99999")
                .request()
                .get();
        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void test30_verify_response_media_type_json() throws JsonMappingException, JsonProcessingException {
        logger.info("Test 30: Verify response Content-Type is JSON");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
    }
}