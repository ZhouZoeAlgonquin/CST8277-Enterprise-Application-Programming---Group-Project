/********************************************************************************************************
 * File:  ACMECollegeService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utility.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utility.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_KEY_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PROPERTY_SALT_SIZE;
import static com.algonquincollege.cst8277.utility.MyConstants.PU_NAME;
import static com.algonquincollege.cst8277.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algonquincollege.cst8277.entity.Course;
import com.algonquincollege.cst8277.entity.CourseRegistration;
import com.algonquincollege.cst8277.entity.CourseRegistrationPK;
import com.algonquincollege.cst8277.entity.Professor;
import com.algonquincollege.cst8277.entity.SecurityRole;
import com.algonquincollege.cst8277.entity.SecurityUser;
import com.algonquincollege.cst8277.entity.Student;
import com.algonquincollege.cst8277.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger();

    private static final String READ_ALL_PROGRAMS = "SELECT name FROM program";
    // TODO ACMECS01 - Add your query constants here.

    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
                DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        SecurityRole userRole = null;
        try {
            TypedQuery<SecurityRole> findRole = em
                    .createNamedQuery(SecurityRole.SECURITY_ROLE_BY_NAME, SecurityRole.class)
                    .setParameter(PARAM1, USER_ROLE);
            userRole = findRole.getSingleResult();
        } catch (NoResultException nr) {
            // create role if not found
            userRole = new SecurityRole();
            userRole.setRoleName(USER_ROLE);
            em.persist(userRole);
        }
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
    }

    /**
     * To update a student
     * 
     * @param id                 - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentWithUpdates;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public Student deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            TypedQuery<SecurityUser> findUser = em
                    .<SecurityUser>createNamedQuery("SecurityUser.userByStudentId", SecurityUser.class)
                    .setParameter(PARAM1, id);
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(student);
        }
        return student;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllPrograms() {
        List<String> programs = new ArrayList<>();
        try {
            programs = (List<String>) em.createNativeQuery(READ_ALL_PROGRAMS).getResultList();
        } catch (Exception e) {
        }
        return programs;
    }

    // ===== Course CRUD =====
    public List<Course> getAllCourses() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        cq.select(cq.from(Course.class));
        return em.createQuery(cq).getResultList();
    }

    public Course getCourseById(int id) {
        return em.find(Course.class, id);
    }

    @Transactional
    public Course persistCourse(Course newCourse) {
        em.persist(newCourse);
        return newCourse;
    }

    @Transactional
    public Course updateCourseById(int id, Course courseWithUpdates) {
        Course courseToBeUpdated = getCourseById(id);
        if (courseToBeUpdated != null) {
            em.refresh(courseToBeUpdated);
            em.merge(courseWithUpdates);
            em.flush();
        }
        return courseWithUpdates;
    }

    @Transactional
    public Course deleteCourseById(int id) {
        Course course = getCourseById(id);
        if (course != null) {
            em.refresh(course);
            em.remove(course);
        }
        return course;
    }

    // ===== Professor CRUD =====
    public List<Professor> getAllProfessors() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Professor> cq = cb.createQuery(Professor.class);
        cq.select(cq.from(Professor.class));
        return em.createQuery(cq).getResultList();
    }

    public Professor getProfessorById(int id) {
        return em.find(Professor.class, id);
    }

    @Transactional
    public Professor persistProfessor(Professor newProfessor) {
        em.persist(newProfessor);
        return newProfessor;
    }

    @Transactional
    public Professor updateProfessorById(int id, Professor professorWithUpdates) {
        Professor professorToBeUpdated = getProfessorById(id);
        if (professorToBeUpdated != null) {
            em.refresh(professorToBeUpdated);
            em.merge(professorWithUpdates);
            em.flush();
        }
        return professorWithUpdates;
    }

    @Transactional
    public Professor deleteProfessorById(int id) {
        Professor professor = getProfessorById(id);
        if (professor != null) {
            em.refresh(professor);
            em.remove(professor);
        }
        return professor;
    }

    // ===== CourseRegistration CRUD =====
    // ===== CourseRegistration CRUD =====
    public List<CourseRegistration> getAllCourseRegistrations() {
        LOG.debug("retrieving all course registrations");
        TypedQuery<CourseRegistration> query = em.createNamedQuery(
            CourseRegistration.ALL_COURSE_REGISTRATIONS_QUERY_NAME, CourseRegistration.class);
        return query.getResultList();
    }

    public CourseRegistration getCourseRegistrationByStudentAndCourse(int studentId, int courseId) {
        LOG.debug("retrieving course registration for student {} and course {}", studentId, courseId);
        try {
            TypedQuery<CourseRegistration> query = em.createNamedQuery(
                CourseRegistration.QUERY_SPECIFIC_COURSE_REGISTRATION, CourseRegistration.class);
            query.setParameter("param1", studentId);
            query.setParameter("param2", courseId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.debug("no course registration found for student {} and course {}", studentId, courseId);
            return null;
        }
    }

    @Transactional
    public CourseRegistration persistCourseRegistration(CourseRegistration newRegistration) {
        // Get managed entities
        Student student = getStudentById(newRegistration.getStudent().getId());
        Course course = getCourseById(newRegistration.getCourse().getId());
        
        if (student == null || course == null) {
            throw new IllegalArgumentException("Student or Course not found");
        }
        
        // Create a fresh CourseRegistration to avoid any ID conflicts
        CourseRegistration registration = new CourseRegistration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setYear(newRegistration.getYear());
        registration.setSemester(newRegistration.getSemester());
        
        if (newRegistration.getProfessor() != null) {
            Professor professor = getProfessorById(newRegistration.getProfessor().getId());
            registration.setProfessor(professor);
        }
        
        if (newRegistration.getLetterGrade() != null) {
            registration.setLetterGrade(newRegistration.getLetterGrade());
        }
        
        em.persist(registration);
        em.flush();
        return registration;
    }

    @Transactional
    public CourseRegistration updateCourseRegistrationByStudentAndCourse(int studentId, int courseId,
            CourseRegistration registrationWithUpdates) {
        CourseRegistration registration = getCourseRegistrationByStudentAndCourse(studentId, courseId);
        if (registration != null) {
            em.refresh(registration);
            if (registrationWithUpdates.getYear() != null && !registrationWithUpdates.getYear().isEmpty()) {
                registration.setYear(registrationWithUpdates.getYear());
            }
            if (registrationWithUpdates.getSemester() != null && !registrationWithUpdates.getSemester().isEmpty()) {
                registration.setSemester(registrationWithUpdates.getSemester());
            }
            em.merge(registration);
            em.flush();
        }
        return registration;
    }

    @Transactional
    public CourseRegistration assignProfessorToCourseRegistration(int studentId, int courseId, Professor professor) {
        if (professor == null) {
            return null;
        }
        
        // Find the professor by ID to ensure it exists and is managed
        Professor managedProfessor = getProfessorById(professor.getId());
        if (managedProfessor == null) {
            return null;
        }
        
        // Use em.find instead of the named query to avoid issues with refresh
        CourseRegistrationPK pk = new CourseRegistrationPK(studentId, courseId);
        CourseRegistration registration = em.find(CourseRegistration.class, pk);
        
        if (registration != null) {
            registration.setProfessor(managedProfessor);
            em.merge(registration);
            em.flush();
            
            // Fetch with all relationships loaded to avoid lazy loading issues
            TypedQuery<CourseRegistration> query = em.createNamedQuery(
                CourseRegistration.QUERY_SPECIFIC_COURSE_REGISTRATION, CourseRegistration.class);
            query.setParameter("param1", studentId);
            query.setParameter("param2", courseId);
            registration = query.getSingleResult();
        }
        return registration;
    }

    @Transactional
    public CourseRegistration assignGradeToCourseRegistration(int studentId, int courseId, String letterGrade) {
        if (letterGrade == null || letterGrade.trim().isEmpty()) {
            return null;
        }
        
        // Use em.find instead of the named query
        CourseRegistrationPK pk = new CourseRegistrationPK(studentId, courseId);
        CourseRegistration registration = em.find(CourseRegistration.class, pk);
        
        if (registration != null) {
            registration.setLetterGrade(letterGrade.trim());
            em.merge(registration);
            em.flush();
            
            // Fetch with all relationships loaded to avoid lazy loading issues
            TypedQuery<CourseRegistration> query = em.createNamedQuery(
                CourseRegistration.QUERY_SPECIFIC_COURSE_REGISTRATION, CourseRegistration.class);
            query.setParameter("param1", studentId);
            query.setParameter("param2", courseId);
            registration = query.getSingleResult();
        }
        return registration;
    }

    @Transactional
    public CourseRegistration deleteCourseRegistrationByStudentAndCourse(int studentId, int courseId) {
        CourseRegistrationPK pk = new CourseRegistrationPK(studentId, courseId);
        CourseRegistration registration = em.find(CourseRegistration.class, pk);
        if (registration != null) {
            em.remove(registration);
            em.flush();
        }
        return registration;
    }

    // ===== StudentClub CRUD =====
    public List<StudentClub> getAllStudentClubs() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }

    public StudentClub getStudentClubById(int id) {
        return em.find(StudentClub.class, id);
    }

    @Transactional
    public StudentClub persistStudentClub(StudentClub newClub) {
        em.persist(newClub);
        return newClub;
    }

    @Transactional
    public StudentClub updateStudentClubById(int id, StudentClub clubWithUpdates) {
        StudentClub clubToBeUpdated = getStudentClubById(id);
        if (clubToBeUpdated != null) {
            em.refresh(clubToBeUpdated);
            em.merge(clubWithUpdates);
            em.flush();
        }
        return clubWithUpdates;
    }

    @Transactional
    public StudentClub deleteStudentClubById(int id) {
        StudentClub club = getStudentClubById(id);
        if (club != null) {
            em.refresh(club);
            em.remove(club);
        }
        return club;
    }

    // ===== Club Membership Operations =====
    @Transactional
    public void addStudentToClub(int studentId, int clubId) {
        Student student = getStudentById(studentId);
        StudentClub club = getStudentClubById(clubId);
        if (student != null && club != null) {
            student.getStudentClubs().add(club);
            club.getStudentMembers().add(student);
            em.merge(student);
            em.merge(club);
        }
    }

    @Transactional
    public void removeStudentFromClub(int studentId, int clubId) {
        Student student = getStudentById(studentId);
        StudentClub club = getStudentClubById(clubId);
        if (student != null && club != null) {
            student.getStudentClubs().remove(club);
            club.getStudentMembers().remove(student);
            em.merge(student);
            em.merge(club);
        }
    }

    // ===== Utility Methods =====
    @SuppressWarnings("unchecked")
    public List<String> getAllLetterGrades() {
        List<String> letterGrades = new ArrayList<>();
        try {
            letterGrades = (List<String>) em.createNativeQuery("SELECT grade FROM letter_grade").getResultList();
        } catch (Exception e) {
        }
        return letterGrades;
    }

}