import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "../config";
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate, useLocation, useParams } from 'react-router-dom';


import axios from "axios";
import hasData from "../components/NetworkingC";

import { Navbar, Container, Row, Col, Form, Button, Nav } from 'react-bootstrap';
import TopNavBar from "../components/LayoutC"
import NotificationHolder from '../components/Notifications'
import ErrorPage from "../components/errorPage";
import compValues from "../components/CompValues";


import Logo from '../assets/logo-simplified.png'
import '../template.css';

function FormGrid({params, client, token, moduleCode}) {

    // Format coursework type

    const submit = async (event) => {
        event.preventDefault();

        const buttonType = event.nativeEvent.submitter.value;
        if (buttonType == "delete") {
            console.log("delete");
            client.budAssessment(token, moduleCode, params.title);
        } else {
            const title = compValues(event.target[0].placeholder, event.target[0].value, event.target[0]);
            const type = event.target[1].value;
            const setterEmail = compValues(event.target[2].placeholder, event.target[2].value, event.target[2]);
            const checkerEmail = compValues(event.target[3].placeholder, event.target[3].value, event.target[3]);

            const setId = await client.getIdFromEmail(token, setterEmail, "Setter");
            const checkId = await client.getIdFromEmail(token, checkerEmail, "Checker");

            if (setId == "ERROR" || checkId == "ERROR") {
                return;
            };


            if (params.type == "COURSE_WORK") {
                const setDate = compValues(params.set, event.target[4].value, event.target[4]);
                const dueDate = compValues(params.dueDate, event.target[5].value, event.target[5]);
                console.log(event.target[5].value);
                console.log(params.dueDate);
                console.log(dueDate);
                client.editAssessment(token, moduleCode, title, { "title": title, "setterID": setId, "checkerID": checkId, "setDate": setDate, "dueDate": dueDate });

            } else if (params.type == "FORMAL_EXAM") {
                const externalMail = compValues(event.target[4].placeholder, event.target[4].value, event.target[4]);
                const setDate = compValues(params.set, event.target[5].value, event.target[5]);

                const extId = await client.getIdFromEmail(token, externalMail, "External Examiner");

                if (extId != "ERROR") {
                    client.editAssessment(token, moduleCode, title, { "title": title, "setterID": setId, "checkerID": checkId, "setDate": setDate, "externalExaminerID": extId });

                };

            } else {
                const setDate = compValues(params.set, event.target[4].value, event.target[4]);
                const autograded = event.target[5].checked;

                client.editAssessment(token, moduleCode, title, { "title": title, "setterID": setId, "checkerID": checkId, "setDate": setDate, "autograded": autograded });


            }
            // client.editAssessment(stuff);

            
        }
    }


    return (
        <Form onSubmit={submit}>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridTitle">
                    <Form.Label>Title</Form.Label>
                    <Form.Control type="text" placeholder={params.title} disabled/>
                </Form.Group>

                <Form.Group as={Col} controlId="formGridType">
                    <Form.Label>Type</Form.Label>
                    <Form.Select aria-label="Assessment Type" disabled defaultValue={params.type}>
                        <option value="COURSE_WORK">{params.type}</option>
                    </Form.Select>
                </Form.Group>
            </Row>

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridSetterID">
                    <Form.Label>Setter Email</Form.Label>
                    <Form.Control type="email" placeHolder={params.setter} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridCheckerID">
                    <Form.Label>Checker Email</Form.Label>
                    <Form.Control type="email" placeHolder={params.checker} />
                </Form.Group>
            </Row>


            <Row className="mb-3">
                {params.type == "FORMAL_EXAM" && (
                    <Form.Group as={Col} controlId="formGridCheckerID">
                        <Form.Label>External Examiner Email</Form.Label>
                        <Form.Control type="email" placeHolder={params.externalId} />
                    </Form.Group>
                )}

                <Form.Group as={Col}  className="mb-3" controlId="formGridSetDate">
                    <Form.Label>Set Date</Form.Label>
                    <Form.Control placeholder={params.set} type="datetime-local" defaultValue={params.set} required/>
                </Form.Group>

                {params.type == "COURSE_WORK" && (
                    <Form.Group as={Col} className="mb-3" controlId="formGridSetDate">
                        <Form.Label>Due Date</Form.Label>
                        <Form.Control placeholder={params.set} type="datetime-local" defaultValue={params.dueDate} required />
                    </Form.Group>
                )}

                {params.type == "IN_SEMESTER" && (
                    <Form.Group as={Col} className="mb-3" controlId="formGridMaxMark">
                        <br></br>
                        <Form.Label>Autograded</Form.Label>
                            <Form.Group className="mb-3" id="formGridCheckbox">
                            <Form.Check type="checkbox" label="Toggle" defaultChecked={params.autograded} />
                            </Form.Group>
                    </Form.Group>
                )}

            </Row>

            <Container className="d-flex gap-2">
                <Button variant="primary" type="submit" value="submit">
                    Submit
                </Button>
            
                <Button variant="danger" type="submit" value="delete">
                    Delete
                </Button>
            </Container>
        </Form>
    );
}

export default function AssessmentEditPage({ client, token, user, role }) {
    const notifications = client.getNotifications()
    const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");

    const [assessment, setAssessment] = useState(null);

    const [setterEmail, setSetter] = useState(null);
    const [checkerEmail, setChecker] = useState(null)
    const [externalEmail, setExternal] = useState(null)

    const location = useLocation();
    const navigate = useNavigate();

    const receivedData = useParams();
    const checkData = (!hasData(["code", "assessmentId"], receivedData))

    useEffect(() => {
        if (checkData) {
            navigate("/error");
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

    }, [token, moduleCode, assessmentCode]);

    useEffect(() => {
        if (assessment === "ERROR" || canModify == false) {
            setAssessment(false);
        } else if (assessment != "ERROR" & assessment != null & assessment != false) {
            client.getUserFromId(token, assessment.setterID).then(email => {
                setSetter(email);
            });
            client.getUserFromId(token, assessment.checkerID).then(email => {
                setChecker(email);
            });
            setExternal(false);
            if (assessment?.externalExaminerId) {
                client.getUserFromId(token, assessment.externalExaminerId).then(email => {
                    setExternal(email);
                });
            }
        }
    }, [assessment, canModify, client, token]);

    if ((assessment != null && assessment != "ERROR" && assessment != false) && (setterEmail != null & setterEmail != "ERROR") && (checkerEmail != null & checkerEmail != "ERROR") && (externalEmail != null && externalEmail != "ERROR")) {

        // Get time
        const formatTime = assessment.setDate.substring(0, 16);

        // Course_work
        const dueDate = assessment?.dueDate?.substring(0, 16);

        // In_Semester
        const autograded = assessment?.autograded;

        return (
            <>
                <TopNavBar />
                <div className="p-4">
                    <Container>
                        <FormGrid params={{ "externalId": externalEmail["email"], "dueDate": dueDate, "autograded": autograded, "setter": setterEmail["email"], "checker": checkerEmail["email"], "title": assessment.title, "type": assessment.type, "set": formatTime }} client={client} token={token} moduleCode={assessment.moduleCode} />
                    </Container>
                </div>
                <NotificationHolder notifications={notifications} client={client} />
                </>
            );
    } else if (assessment === false) {
        return <ErrorPage />
    }
}