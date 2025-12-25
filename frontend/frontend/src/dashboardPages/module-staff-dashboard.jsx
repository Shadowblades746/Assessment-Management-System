import React, { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from 'react-router-dom';

import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import TopNavBar from "../components/LayoutC";
import FeedbackModal, { HandleRowClickAssessments } from "../components/assessment-feedback";
import ErrorPage from "../components/errorPage";

import CardTable, { PREVIEW_LIMIT, FormatAssessmentRows } from './tableAttrs';
import '../template.css';

function modulesViewMore(nav) {
    nav("modules");
}

function handleRowClickModules(row, nav) {
    nav(`modules/${row[1]}`);
}
export default function ModuleStaffDashboard(props) {
    const nav = useNavigate();

    const [moduleRoles, setModuleRoles] = useState(Array(PREVIEW_LIMIT).fill(["Loading..."]));
    const [assessmentRoles, setAssessmentRoles] = useState([["Loading..."]]);

    // Modal states
    const [show, setShow] = useState(false);
    const [feedbackModalArgs, setFeedbackModalArgs] = useState({
        previousFeedback: null,
        forwardFeedbackRequired: false,
        backwardFeedbackRequired: false,
        allowedForward: false,
        allowedBackward: false
    })

    useEffect(() => {
        props.client.getUserModules(props.token, props.user.id, PREVIEW_LIMIT).then(res => {
            setModuleRoles(res);
        });
    }, []);

    useEffect(() => {
        props.client.getFilteredAssessments(props.token, props.user.id, 0).then(res => {
            setAssessmentRoles(res);
        })
    }, []);

    if (props.role !== "ROLE_ACADEMIC_STAFF" && props.role !== "ROLE_EXAMS_OFFICER") {
        return <ErrorPage code="401" message="Insufficent access" />
    }

    return (
        <>
            <TopNavBar client={props.client}/>
            <br></br>
            <h2 style={{textAlign: "center"} }>{(props.role === "ROLE_ACADEMIC_STAFF") ? "Academic Staff Dashboard" : "Exams Officer Dashboard"}</h2>
            <Row className="p-2" style={{ width: "100%" }}>
                <Col>
                    <CardTable
                        title="Module Roles"
                        headers={["Name", "Module Code", "Role", "School"]}
                        body={moduleRoles}
                        tid={1}
                        handleClick={() => modulesViewMore(nav)}
                        handleRowClick={(row) => handleRowClickModules(row, nav)}
                        viewFull={false}
                    />
                </Col>
                <Col>
                    <CardTable
                        title="Assessment Roles"
                        headers={["Module Code", "Title", "Status", "Type", "Role"]}
                        body={FormatAssessmentRows(assessmentRoles)}
                        tid={1}
                        handleClick={() => { }}
                        handleRowClick={(row) => HandleRowClickAssessments(row, setShow, props.client, props.token, setFeedbackModalArgs)}
                        viewFull={true}
                    />
                </Col>
            </Row>
            <FeedbackModal
                show={show}
                setShow={setShow}
                client={props.client}
                role={props.role}
                token={props.token}
                userId={props.user.id}
                {...feedbackModalArgs }
            />
        </>
            
    );
}