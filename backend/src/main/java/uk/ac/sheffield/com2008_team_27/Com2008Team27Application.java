package uk.ac.sheffield.com2008_team_27;

import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.config.RsaKeyProperties;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Coursework;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.FormalExam;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.InSemester;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class Com2008Team27Application {

    public static void main(String[] args) {
        SpringApplication.run(Com2008Team27Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                               ModuleRepository moduleRepository, AssessmentRepository assessmentRepository, ModuleRoleRepository moduleRoleRepository) {
        return args -> {
            // ---------------------- USERS --------------------------------
            // Academic staff (Names are taken from the top 100 forename and surname list)
            User academicStaff1 = new  User("academic1@sheffield.ac.uk", "James", "Jones", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff2 = new  User("academic2@sheffield.ac.uk", "Michael", "Williams", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff3 = new  User("academic3@sheffield.ac.uk", "John", "Taylor", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff4 = new  User("academic4@sheffield.ac.uk", "David", "Brown", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff5 = new  User("academic5@sheffield.ac.uk", "Donald", "Green", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff6 = new  User("academic6@sheffield.ac.uk", "Andrew", "Hall", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff7 = new  User("academic7@sheffield.ac.uk", "Joshua", "Lewis", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            User academicStaff8 = new  User("academic8@sheffield.ac.uk", "Paul", "Harris", passwordEncoder.encode("password"), Authorities.ROLE_ACADEMIC_STAFF);
            // Teaching support staff
            User teachingSupport1 = new  User("teachingsupport1@sheffield.ac.uk", "Richard", "Davies", passwordEncoder.encode("password"), Authorities.ROLE_TEACHING_SUPPORT);
            User teachingSupport2 = new  User("teachingsupport2@sheffield.ac.uk", "Joseph", "Evans", passwordEncoder.encode("password"), Authorities.ROLE_TEACHING_SUPPORT);
            User teachingSupport3 = new  User("teachingsupport3@sheffield.ac.uk", "Thomas", "Wilson", passwordEncoder.encode("password"), Authorities.ROLE_TEACHING_SUPPORT);
            User teachingSupport4 = new  User("teachingsupport4@sheffield.ac.uk", "Christopher", "Thomas", passwordEncoder.encode("password"), Authorities.ROLE_TEACHING_SUPPORT);
            // Exams officers (They are also have privileges/responsibility of an academics taff)
            User examsOfficer1 = new  User("examsofficer1@sheffield.ac.uk", "Charles", "Johnson", passwordEncoder.encode("password"), Authorities.ROLE_EXAMS_OFFICER);
            // External Examiners
            User externalExaminer1 = new  User("externalexaminer1@sheffield.ac.uk", "Mark", "Wright", passwordEncoder.encode("password"), Authorities.ROLE_EXTERNAL_EXAMINER);
            User externalExaminer2 = new  User("externalexaminer2@sheffield.ac.uk", "Stephen", "Walker", passwordEncoder.encode("password"), Authorities.ROLE_EXTERNAL_EXAMINER);

            // Saving users the database
            userRepository.save(academicStaff1);
            userRepository.save(academicStaff2);
            userRepository.save(academicStaff3);
            userRepository.save(academicStaff4);
            userRepository.save(academicStaff5);
            userRepository.save(academicStaff6);
            userRepository.save(academicStaff7);
            userRepository.save(academicStaff8);
            userRepository.save(teachingSupport1);
            userRepository.save(teachingSupport2);
            userRepository.save(teachingSupport3);
            userRepository.save(teachingSupport4);
            userRepository.save(examsOfficer1);
            userRepository.save(externalExaminer1);
            userRepository.save(externalExaminer2);
            //-------------------------------------------- MODULES -----------
            // Undergraduate modules (Leads and moderator have to be different academic staff or exams officers)
            Module com1001 = new Module("COM1001", "Introduction to Software Engineering", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com1001Lead = new ModuleRole(academicStaff1, com1001, ModuleRole.Role.LEAD);
            ModuleRole com1001Moderator = new ModuleRole(academicStaff2, com1001, ModuleRole.Role.MODERATOR);
            ModuleRole com1001Staff1 = new ModuleRole(academicStaff3, com1001, ModuleRole.Role.STAFF);
            ModuleRole com1001Staff2 = new ModuleRole(academicStaff4, com1001, ModuleRole.Role.STAFF);
            Assessment com1001Coursework = new Coursework("Group Project",
                    Assessment.Type.COURSE_WORK,
                    com1001Lead.getUser().getId(),
                    academicStaff8.getId(),
                    com1001,
                    LocalDateTime.now(),
                    null,
                    LocalDateTime.of(2026, 2, 8, 16, 40)); // year, month, day, hour, minutes
            moduleRepository.save(com1001);
            moduleRoleRepository.save(com1001Lead);
            moduleRoleRepository.save(com1001Moderator);
            moduleRoleRepository.save(com1001Staff1);
            moduleRoleRepository.save(com1001Staff2);
            assessmentRepository.save(com1001Coursework);

            // Module lead is also an exams officer (allowed)
            Module com1002 = new Module("COM1002", "Foundations of Computer Science","School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com1002Lead = new ModuleRole(examsOfficer1, com1002, ModuleRole.Role.LEAD);
            ModuleRole com1002Moderator = new ModuleRole(academicStaff1, com1002, ModuleRole.Role.MODERATOR);
            moduleRepository.save(com1002);
            moduleRoleRepository.save(com1002Lead);
            moduleRoleRepository.save(com1002Moderator);

            Module com1003 = new Module("COM1003", "Java Programming", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com1003Lead = new ModuleRole(academicStaff3, com1003, ModuleRole.Role.LEAD);
            moduleRepository.save(com1003);
            moduleRoleRepository.save(com1003Lead);

            Module com1004 = new Module("COM1005", "Machines and Intelligence","School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com1004Lead = new ModuleRole(academicStaff4, com1004, ModuleRole.Role.LEAD);
            ModuleRole com1004Moderator = new ModuleRole(academicStaff5, com1004, ModuleRole.Role.MODERATOR);
            Assessment com1004FormalExam = new FormalExam("Linear Algebra", Assessment.Type.FORMAL_EXAM, com1004Lead.getUser().getId(), academicStaff7.getId(), com1004, LocalDateTime.now(), externalExaminer1.getId(), null);
            moduleRepository.save(com1004);
            moduleRoleRepository.save(com1004Lead);
            moduleRoleRepository.save(com1004Moderator);
            assessmentRepository.save(com1004FormalExam);

            Module com2001 = new Module("COM2001", "Data Driven Computing", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com2001Lead = new ModuleRole(academicStaff5, com2001, ModuleRole.Role.LEAD);
            moduleRepository.save(com2001);
            moduleRoleRepository.save(com2001Lead);

            Module com2002 = new Module("COM2002", "Systems Design and Security", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com2002Lead = new ModuleRole(academicStaff6, com2002, ModuleRole.Role.LEAD);
            ModuleRole com2002Moderator = new ModuleRole(academicStaff8, com2002, ModuleRole.Role.MODERATOR);
            ModuleRole com2002Staff1 = new ModuleRole(academicStaff4, com1001, ModuleRole.Role.STAFF);
            ModuleRole com2002Staff2 = new ModuleRole(academicStaff5, com1001, ModuleRole.Role.STAFF);
            Assessment com2002InSemester = new InSemester("UML Quiz", Assessment.Type.IN_SEMESTER, com2002Lead.getUser().getId(), academicStaff2.getId(), com2002, LocalDateTime.now(), null, true);
            moduleRepository.save(com2002);
            moduleRoleRepository.save(com2002Lead);
            moduleRoleRepository.save(com2002Moderator);
            moduleRoleRepository.save(com2002Staff1);
            moduleRoleRepository.save(com2002Staff2);
            assessmentRepository.save(com2002InSemester);

            Module com2003 = new Module("COM2003", "Functional Programing", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com2003Lead = new ModuleRole(academicStaff7, com2003, ModuleRole.Role.LEAD);
            moduleRepository.save(com2003);
            moduleRoleRepository.save(com2003Lead);

            Module com2004 = new Module("COM2004", "Robotics", "School of CS", Module.DegreeLevel.UNDERGRADUATE);
            ModuleRole com2004Lead = new ModuleRole(academicStaff8, com2004, ModuleRole.Role.LEAD);
            moduleRepository.save(com2004);
            moduleRoleRepository.save(com2004Lead);

            // Postgraduate modules
            Module com4001 = new Module("COM4001", "Natural Language Processing", "School of CS", Module.DegreeLevel.POSTGRADUATE);
            ModuleRole com4001Lead = new ModuleRole(academicStaff4, com4001, ModuleRole.Role.LEAD);
            ModuleRole com4001Moderator = new ModuleRole(academicStaff6, com4001, ModuleRole.Role.MODERATOR);
            moduleRepository.save(com4001);
            moduleRoleRepository.save(com4001Lead);
            moduleRoleRepository.save(com4001Moderator);

            Module com4002 = new Module("COM4002", "Computer Vision", "School of CS", Module.DegreeLevel.POSTGRADUATE);
            ModuleRole com4002Lead = new ModuleRole(academicStaff8, com4002, ModuleRole.Role.LEAD);
            ModuleRole com4002Staff1 = new ModuleRole(academicStaff2, com4002, ModuleRole.Role.STAFF);
            Assessment com4002FormalExam = new FormalExam("Neural Networks", Assessment.Type.FORMAL_EXAM, com4002Lead.getUser().getId(), academicStaff5.getId(), com4002, LocalDateTime.now(), externalExaminer2.getId(), null);
            moduleRepository.save(com4002);
            moduleRoleRepository.save(com4002Lead);
            moduleRoleRepository.save(com4002Staff1);
            assessmentRepository.save(com4002FormalExam);
        };
    }

}