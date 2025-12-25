import React from "react"
import { Route, Routes } from "react-router-dom";

// View Pages
import ViewModulesPage from '../viewPages/view-modules.jsx';
import ViewModulePage from '../viewPages/view-module.jsx'
import ViewAssessmentPage from '../viewPages/view-assessment.jsx';
import ViewUsersPage from "../viewPages/view-users.jsx"

// Edit Pages
import AssessmentEditPage from '../editPages/edit-assessment.jsx';
import ModuleEditPage from '../editPages/edit-module.jsx';

// Add Pages
import AssessmentAddPage from '../addPages/add-assessment.jsx'
import ModuleAddPage from '../addPages/add-module.jsx'

// Dashboard pages
import AdminDashboard from '../dashboardPages/teaching-support-dashboard.jsx';
import ModuleStaffDashboard from '../dashboardPages/module-staff-dashboard.jsx';
import ExamOfficerDashboard from "../dashboardPages/exam-officer-dashboard.jsx";
import ExternalExaminerDashboard from "../dashboardPages/external-examiner-dashboard.jsx";

// Other Pages
import ErrorPage from '../components/errorPage.jsx';

const AuthorisedRoutes = ({ client, token, user, role }) => {
    var homePage;
    if (role == "ROLE_TEACHING_SUPPORT") {
        homePage = <AdminDashboard token={token} user={user} client={client} role={role} />
    } else if (role == "ROLE_ACADEMIC_STAFF") {
        homePage = <ModuleStaffDashboard token={token} user={user} client={client} role={role} />
    } else if (role == "ROLE_EXAMS_OFFICER") {
        homePage = <ExamOfficerDashboard token={token} user={user} client={client} role={role} />
    } else if (role == "ROLE_EXTERNAL_EXAMINER") {
        homePage = <ExternalExaminerDashboard token={token} user={user} client={client} role={role} />
    } else {
        homePage = <ErrorPage message="You have not been assigned a role. Please contact an adminstrator" code="401" client={client}/>
    }
    return (
        <Routes>
            <Route path="/" element={homePage} />
            <Route path="/modules" element={<ViewModulesPage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/:code" element={<ViewModulePage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/:code/:assessmentId" element={<ViewAssessmentPage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/:code/:assessmentId/edit" element={<AssessmentEditPage token={token} user={user} client={client} role={role} />} />
            <Route path="/users" element={<ViewUsersPage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/:code/add" element={<AssessmentAddPage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/:code/edit" element={<ModuleEditPage token={token} user={user} client={client} role={role} />} />
            <Route path="/modules/add" element={<ModuleAddPage token={token} user={user} client={client} role={role} />} />
            <Route path="/error" element={< ErrorPage message="Page not found." code="404" />} />
            <Route path="*" element={< ErrorPage message="Page not found." code="404" />} />
        </Routes>
    );
};

export default AuthorisedRoutes;
