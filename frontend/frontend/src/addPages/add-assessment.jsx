import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "../config";
import { useNavigate, useLocation, useParams } from 'react-router-dom';


import axios from "axios";

import { Navbar, Container, Row, Col, Form, Button, Nav } from 'react-bootstrap';
import TopNavBar from "../components/LayoutC"
import ErrorPage from "../components/errorPage";
import NotificationHolder from '../components/Notifications'
import hasData from "../components/NetworkingC";
import compValues from "../components/CompValues";

import Logo from '../assets/logo-simplified.png'
import '../template.css';


function FormGrid({ code, client, token }) {

    const [formType, setForm] = useState("IN_SEMESTER");

    function formTypeChange(event) {
        setForm(event.target.value);
    }

    const submitForm = async (event) => {
        event.preventDefault();

        const code = event.target[0].value;
        const title = event.target[1].value;
        const type = event.target[2].value;
        const setEmail = event.target[3].value;
        const checkEmail = event.target[4].value;

        const setId = await client.getIdFromEmail(token, setEmail, "Setter");
        const checkId = await client.getIdFromEmail(token, checkEmail, "Checker");

        if (setId == "ERROR" || checkId == "ERROR") {
            return;
        };

        if (formType == "IN_SEMESTER") {
            const setDate = event.target[5].value + ":00";
            const autograded = event.target[6].checked;

            client.createAssessment(token, code, { "title": title, "type": type, "setterID": setId, "checkerID": checkId, "setDate": setDate, "previousFeedback": "", "autograded": autograded })
        } else if (formType == "FORMAL_EXAM") {
            const externalMail = event.target[5].value;
            const setDate = event.target[6].value + ":00";
            const extId = await client.getIdFromEmail(token, externalMail, "External Examiner");

            if (extId != "ERROR") {
                client.createAssessment(token, code, { "title": title, "type": type, "setterID": setId, "checkerID": checkId, "setDate": setDate, "previousFeedback": "", "externalExaminerID": extId })

            };

        } else {
            const setDate = event.target[5].value + ":00";
            const dueDate = event.target[6].value + ":00";

            client.createAssessment(token, code, { "title": title, "type": type, "setterID": setId, "checkerID": checkId, "setDate": setDate, "previousFeedback": "", "dueDate": dueDate })
        };

    }

    return (
        <Form onSubmit={submitForm} >
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridTitle">
                    <Form.Label>Module Code</Form.Label>
                    <Form.Control type="text" value={code} disabled required />
                </Form.Group>
            </Row>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridTitle">
                    <Form.Label>Title</Form.Label>
                    <Form.Control type="text" placeholder="Assessment Title" required />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridType">
                    <Form.Label>Type</Form.Label>
                    <Form.Select aria-label="Assessment Type" onChange={formTypeChange}>
                        <option value="IN_SEMESTER">In-Semester</option>
                        <option value="FORMAL_EXAM">Formal Exam</option>
                        <option value="COURSE_WORK">Coursework</option>
                    </Form.Select>
                </Form.Group>
            </Row>

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridSetterID">
                    <Form.Label>Setter Email</Form.Label>
                    <Form.Control type="email" placeHolder="example@team27.com" required />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridCheckerID">
                    <Form.Label>Checker Email</Form.Label>
                    <Form.Control type="email" placeHolder="example@team27.com" required />
                </Form.Group>
            </Row>


            <Row className="mb-1">
                {formType == "FORMAL_EXAM" && (
                    <Form.Group as={Col} controlId="formGridCheckerID">
                        <Form.Label>External Examiner Email</Form.Label>
                        <Form.Control type="email" placeHolder="example@team27.com" required />
                    </Form.Group>
                )}

                <Form.Group as={Col}  className="mb-3" controlId="formGridSetDate">
                    <Form.Label>Set Date</Form.Label>
                    <Form.Control placeholder="DD/MM/YYYY 00:00" type="datetime-local" max="9999-12-31T23:59" required />
                </Form.Group>
                {formType =="COURSE_WORK" && (
                <Form.Group as={Col} className="mb-3" controlId="formGridDueDate">
                    <Form.Label>Due Date</Form.Label>
                    <Form.Control placeholder="DD/MM/YYYY 00:00" type="datetime-local" max="9999-12-31T23:59" required />
                </Form.Group>)}
            </Row>

            <Row className="mb-1">
                {formType == "IN_SEMESTER" && (

                    <Form.Group as={Col} className="mb-3" controlId="formGridMaxMark">
                        <br></br>
                        <Form.Label>Autograded</Form.Label>
                        <Form.Group className="mb-3" id="formGridCheckbox">
                            <Form.Check type="checkbox" label="Toggle" />
                        </Form.Group>
                    </Form.Group>
                )}
            </Row>

            <Container className="d-flex gap-2">
                <Button variant="success" type="submit" value="submit">
                    Create
                </Button>
           
            </Container>
        </Form>
    );
}

export default function AssessmentAddPage({ client, role, token }) {
    const [modules, setModule] = useState(false);

    const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");
    const notifications = client.getNotifications()

    const receivedData = useParams();
    const checkData = (!hasData(["code"], receivedData))

    // Check we have needed data
    useEffect(() => {
        if (checkData) {
            navigate("/error");
        }
    }, [checkData]);

    if (checkData) {
        return (null);
    }

    const code = receivedData["code"];

    useEffect(() => {
        client.getModuleInfo(token, code).then(res => {
            setModule(res);
            if (res == "ERROR") { // ERROR then we just navigate to 404
                setModule(null);
            }
        });
    }, [token, code]);

    console.log(modules);



    if (canModify == true && modules != null) {
        return (
            <>
                <TopNavBar client={client} />
                <div className="p-4">
                    <Container>
                        <FormGrid code={code} client={client} token={token} />
                    </Container>
                </div>
                <NotificationHolder notifications={notifications} client={client} />

            </>
        );
    } else {
        return <ErrorPage />
    }
}