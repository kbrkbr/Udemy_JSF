package jsf.jdbc;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class StudentController {

    private List<Student> students;
    private DbUtil dbUtil;
    private Logger logger = Logger.getLogger(getClass().getName());

    public StudentController() throws Exception {
        this.students = new ArrayList<>();
        this.dbUtil = DbUtil.getInstance();
    }

    public List<Student> getStudents() {
        return this.students;
    }

    public String loadStudent(int studentId) {

        logger.info("loading student: " + studentId);

        try {

            Student s = dbUtil.getStudent(studentId);

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> requestMap = externalContext.getRequestMap();
            requestMap.put("student", s);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading student id: " + studentId, e);
            addErrorMessage(e);

            return null;
        }

        return "update-student-form.xhtml";
    }

    public void loadStudents() {
        logger.info("Loading students");

        this.students.clear();

        try {
            students = dbUtil.getStudents();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading students", e);

            addErrorMessage(e);
        }
    }

    private void addErrorMessage(Exception e) {
        FacesMessage msg = new FacesMessage("Error : " + e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String addStudent(Student s) {

        try {
            this.dbUtil.addStudent(s);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding student", e);
            addErrorMessage(e);
            return null;
        }

        logger.log(Level.INFO, "Returning to main page");
        return "list-students";
    }

    public String updateStudent(Student student) {

        logger.info("Updating student " + student);

        try {

            dbUtil.updateStudent(student);

        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error updating  student", e);
            addErrorMessage(e);

            return null;
        }

        return "list-students";

    }
    public String deleteStudent(int studentId) {

        logger.info("Deleting student " + studentId);

        try {

            dbUtil.deleteStudent(studentId);

        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error deleting  student", e);
            addErrorMessage(e);

            return null;
        }

        return "list-students";

    }
}
