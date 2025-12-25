import React, { useState } from "react";

import Button from 'react-bootstrap/Button';

import AdminDashboard from "./teaching-support-dashboard";
import ModuleStaffDashboard from "./module-staff-dashboard";


import ErrorPage from "../components/errorPage";


export default function ExamOfficerDashboard(props) {
    const [isAdminView, setAdminView] = useState(false);

    if (props.role != "ROLE_EXAMS_OFFICER") {
        return (
            <ErrorPage message="Insufficient Access" code="401" client={props.client}/>
        );
    } else if (isAdminView) {
        // Could probably do this more cleanly but this is easier
        return (
            <>
                <AdminDashboard user={props.user} client={props.client} role={props.role} token={props.token} />
                <div className="d-flex justify-content-center">
                    <Button onClick={() => {setAdminView(false)}} variant="primary">Switch to Module Staff View</Button>
                </div>
            </>
        )
    } else {
        return (
            <>
                <ModuleStaffDashboard user={props.user} client={props.client} role={props.role} token={props.token} />
                <br></br>
                <div className="d-flex justify-content-center">
                    <Button onClick={() => {setAdminView(true)}} variant="primary">Switch to Administrator View</Button>
                </div>
            </>
        )
    }
}