package uk.ac.sheffield.com2008_team_27.config;

public class Authorities {

    // default role with small amount of permissions
    public static final String ROLE_USER = "ROLE_USER";
    public static final String SCOPED_USER = "SCOPE_" + ROLE_USER;

    public static final String ROLE_TEACHING_SUPPORT = "ROLE_TEACHING_SUPPORT";
    public static final String SCOPED_TEACHING_SUPPORT = "SCOPE_" + ROLE_TEACHING_SUPPORT;

    public static final String ROLE_ACADEMIC_STAFF = "ROLE_ACADEMIC_STAFF";
    public static final String SCOPED_ACADEMIC_STAFF = "SCOPE_" + ROLE_ACADEMIC_STAFF;

    public static final String ROLE_EXTERNAL_EXAMINER = "ROLE_EXTERNAL_EXAMINER";
    public static final String SCOPED_EXTERNAL_EXAMINER = "SCOPE_" + ROLE_EXTERNAL_EXAMINER;

    public static final String ROLE_EXAMS_OFFICER =  "ROLE_EXAMS_OFFICER";
    public static final String SCOPED_EXAMS_OFFICER = "SCOPE_" + ROLE_EXAMS_OFFICER;
}