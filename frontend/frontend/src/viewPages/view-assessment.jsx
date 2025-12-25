// API Endpoints
import { MODULES_API, USERS_API } from "../config";

// Dependencies
import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import axios from "axios";

// Components
import ViewHeaderCard from '../components/ViewC';
import hasData from "../components/NetworkingC";
import TopNavBar from "../components/LayoutC";
import ErrorPage from "../components/errorPage";
import NotificationHolder from '../components/Notifications'

// React Components
import { Navbar, Container, Row, Col, Form, Button, Nav, Card, Collapse, ProgressBar } from 'react-bootstrap';

// Assets
import Logo from '../assets/logo-simplified.png'
import Arrow from '../assets/arrow.png'
import '../template.css';

function StatusBar({ status, statusList }) {
    if (statusList == null) {
        return;
    }
    const statusLength = statusList.length;
    const statusPosition = statusList.indexOf(status)+1;  

    return <ProgressBar now={100 * (statusPosition / statusLength)} label={status} />
}

export default function ViewPage({ client, token, user, role }) {
    const notifications = client.getNotifications()

    const [assessment, setAssessment] = useState(null);
    const [roleTo, setRole] = useState("");
    const [userRoles, setUserRoles] = useState([]);

    const location = useLocation();
    const navigate = useNavigate();

    const receivedData = useParams();
    const checkData = (!hasData(["code", "assessmentId"], receivedData))

    useEffect(() => {
        if (checkData) {
            navigate("/modules");
        }
    }, [checkData]);

    if (checkData) {
        return (null);
    }

    // Check if data is correct and can load assessment
    const moduleCode = receivedData["code"];
    const assessmentCode = receivedData["assessmentId"];

    useEffect(() => {
        client.getAssessmentInfo(token, moduleCode, assessmentCode).then(res => {
            setAssessment(res);
        });

    }, [token, moduleCode, assessmentCode, user]);

    useEffect(() => {
        if (assessment === "ERROR") {
            setAssessment(false);
        }
    }, [assessment]);



    // Load user progress thing

    useEffect(() => {
        client.getAssessmentProgressRole(token, moduleCode, assessmentCode).then(res => {
            setRole(res);
        });

    }, [token, moduleCode, assessmentCode]);

    useEffect(() => {
        if (roleTo === "ERROR") {
            setRole(false);
        }
    }, [roleTo]);

    // Load assessment rules
    useEffect(() => {
        client.getAssessmentRoles(token, moduleCode, assessmentCode).then(res => {
            setUserRoles(res);
        });

    }, [token, moduleCode, assessmentCode]);

    useEffect(() => {
        if (userRoles === "ERROR") {
            setUserRoles(false);
        }
    }, [userRoles]);

    function increaseStatus() {
        client.increaseStep(token, moduleCode, assessmentCode);
    }

    function decreaseStatus() {
        client.decreaseStep(token, moduleCode, assessmentCode);
    }


    if ((assessment != null && assessment != "ERROR" && assessment != false) && (roleTo != null && roleTo != "ERROR" && roleTo != false) && (userRoles != [] && userRoles != "ERROR" && userRoles != false)) {

        const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");
        const roleInfo = roleTo['requiredRoles'];
        const roles = roleInfo.join(" & ");

        const splitDateT = (assessment.setDate).split("T");
        let canIncrease = false;
        const setDate = splitDateT[0]
        const setTime = splitDateT[1].substring(0, 5);

        // Create string to show

        let displayString = "";
        displayString += `Set Date: ${setDate} at ${setTime}\n`;
        if (assessment?.dueDate) {
            const splitDueT = (assessment.dueDate).split("T");
            displayString += `Due Date: ${splitDueT[0]} at ${splitDueT[1].substring(0, 5)}\n`
        };
        displayString += `The status of the assessment can be increased by:\n${roles}.`

        canIncrease = (userRoles.staffID.includes(user.id));
        for (const key in userRoles) {

            if (userRoles[key] == user.id) {
                canIncrease = true;
                break;
            }

        }

        console.log(userRoles);



        if (!(canIncrease || canModify)) {
            return <ErrorPage />
        }

        return (
            <>
                <TopNavBar client={client} />
                <div className="p-4 d-flex justify-content-center align-items-center flex-column gap-1">
                    <div className="position-relative w-100 p-1 d-flex justify-content-center align-items-left flex-column shadow-sm bg-primary-subtle rounded mb-0">
                        <ViewHeaderCard Title={assessment.title} Specifier="AssessmentView" canModify={true} canModify2={canModify} canIncrease={true} canDecrease={false} increase={increaseStatus} decrease={decreaseStatus} Text={displayString} />
                    </div>
                    <div className="position-relative w-100 p-1 d-flex justify-content-center align-items-left flex-column shadow-sm bg-transparent rounded ">
                        <StatusBar status={assessment.status} statusList={assessment.process} />
                    </div>
                </div>
                <NotificationHolder notifications={notifications} client={client} />
            </>
        );
    } else if (assessment === false) {
        return <ErrorPage />
    }
}