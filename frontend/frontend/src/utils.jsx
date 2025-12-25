export function RoleToString(role) {
    switch (role) {
        case ("ROLE_USER"):
            return "User";
        case ("ROLE_ACADEMIC_STAFF"):
            return "Academic Staff";
        case ("ROLE_TEACHING_SUPPORT"):
            return "Teaching Support Staff";
        case ("ROLE_EXAMS_OFFICER"):
            return "Exams Officer"
        case ("ROLE_EXTERNAL_EXAMINER"):
            return "External Examiner";
        default:
            return role;
    }
}

export function StringToRole(text) {
    switch (text) {
        case "User":
            return "ROLE_USER";
        case "Academic Staff":
            return "ROLE_ACADEMIC_STAFF";
        case "Teaching Support Staff":
            return "ROLE_TEACHING_SUPPORT";
        case "Exams Officer":
            return "ROLE_EXAMS_OFFICER";
        case "External Examiner":
            return "ROLE_EXTERNAL_EXAMINER";
        default:
            return text;
    }
}

export function ModuleRoleToString(role) {
    switch (role) {
        case ("MODERATOR"):
            return "Moderator";
        case ("LEAD"):
            return "Module Lead";
        case ("STAFF"):
            return "Module Staff";
        default:
            return role;
    }
}

export function StatusToString(status) {
    switch (status) {
        case ("NOT_SET"):
            return "Not set";
        case ("NEEDS_CHECKING"):
            return "Needs checking";
        case ("NEEDS_SETTER_FEEDBACK"):
            return "Needs setter feedback";
        case ("NEEDS_EO_CHECKING"):
            return "Needs exam officer checking";
        case ("NEEDS_EE_FEEDBACK"):
            return "Needs external examiner feedback";
        case ("MARKING_STANDARDISATION"):
            return "Undergoing marking standardisation";
        case ("NEEDS_MARKING"):
            return "Needs marking";
        case ("NEEDS_ADMIN_CHECK"):
            return "Needs admin checking";
        case ("NEEDS_MODERATION"):
            return "Needs moderation";
        case ("NEEDS_APPROVAL"):
            return "Needs approval";
        case ("COMPLETED"):
            return "Completed";
        case ("DRAFT"):
            return "Draft";
        default:
            return status;
    }
}


export function IsCurrentStage(roles, status) {
    const _roles = roles.map((_role) => _role.trim().toUpperCase());
    switch (status) {
        case ("NOT_SET"):
            return (_roles.includes("SETTER"));
        case ("NEEDS_CHECKING"):
            return (_roles.includes("CHECKER"));
        case ("NEEDS_SETTER_FEEDBACK"):
            return (_roles.includes("SETTER"));
        case ("NEEDS_EO_CHECKING"):
            return (_roles.includes("ROLE_EXAMS_OFFICER"));
        case ("NEEDS_EE_FEEDBACK"):
            return (_roles.includes("ROLE_EXTERNAL_EXAMINER"));
        case ("MARKING_STANDARDISATION"):
            return (_roles.includes("STAFF"));
        case ("NEEDS_MARKING"):
            return (_roles.includes("STAFF"));
        case ("NEEDS_ADMIN_CHECK"):
            return (_roles.includes("ROLE_TEACHING_SUPPORT"));
        case ("NEEDS_MODERATION"):
            return (_roles.includes("MODERATOR"));
        case ("NEEDS_APPROVAL"):
            return (_roles.includes("MODERATOR")); 
        case ("DRAFT"):
            return (_roles.includes("SETTER"));
        case ("RETURNS_FEEDBACK"):
            return (_roles.includes("STAFF"));
        case ("SPECIFICATION_RELEASE"):
            return (_roles.includes("STAFF") || _roles.includes("SETTER") || _roles.includes(""))
        case ("COMPLETED"):
            return (false);
        default:
            return false;
    }
}

export function StringToStatus(text) {
    const s = (text || "").toString().trim().toLowerCase();

    switch (s) {
        case "not set":
            return "NOT_SET";
        case "needs checking":
            return "NEEDS_CHECKING";
        case "needs setter feedback":
            return "NEEDS_SETTER_FEEDBACK";
        case "needs exam officer checking":
            return "NEEDS_EO_CHECKING";
        case "needs external examiner feedback":
            return "NEEDS_EE_FEEDBACK";
        case "undergoing marking standardisation":
            return "MARKING_STANDARDISATION";
        case "needs marking":
            return "NEEDS_MARKING";
        case "needs admin checking":
            return "NEEDS_ADMIN_CHECK";
        case "needs moderation":
            return "NEEDS_MODERATION";
        case "needs approval":
            return "NEEDS_APPROVAL";
        case "completed":
            return "COMPLETED";
        case "draft":
            return "DRAFT";
        default:
            return s;
    }
}

export function SetterCheckerToString(role) {
    // No native totitlecase function for some reason idk 
    switch (role) {
        case ("setter"):
            return "Setter";
        case ("checker"):
            return "Checker";
        default:
            return role;
    }
}

export function ExamTypeToString(type) {
    switch (type) {
        case ("FORMAL_EXAM"):
            return "Formal Exam";
        case ("IN_SEMESTER"):
            return "In-Semester Test";
        case ("COURSE_WORK"):
            return "Coursework";
        default:
            return type;
    }
}
