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

// Assets
import Logo from '../assets/logo-simplified.png'
import '../template.css';

function FormGrid({params, client, token}) {

    const submit = async (event) => {
        event.preventDefault();

        const buttonType = event.nativeEvent.submitter.value;
        if (buttonType == "delete") {
            console.log(params.code);
            const deletee = await client.budModule(token, params.code);

            console.log(deletee);
        } else {
            const title = compValues(event.target[0].placeholder, event.target[0].value, event.target[0]);
            const school = compValues(event.target[1].placeholder, event.target[1].value, event.target[1]);
            const modEmail = compValues(event.target[2].placeholder, event.target[2].value, event.target[2]);
            const leadEmail = compValues(event.target[3].placeholder, event.target[3].value, event.target[3]);

            const modId = await client.getIdFromEmail(token, modEmail, "Moderator");
            const leadId = await client.getIdFromEmail(token, leadEmail, "Module lead");
                
            console.log(modId);

            //token, code, name, school, degree, leadId, modId
            console.log(params.code);
            if (leadId != "ERROR" && modId != "ERROR") {
                client.editModule(token, params.code, title, school, leadId, modId);
            }


            console.log(title, school, modEmail, leadEmail);
            // client.editModule(stuff);

        }
    }

    return (
        <Form onSubmit={submit}>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeHolder={params.name} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridSchool">
                    <Form.Label>School</Form.Label>
                    <Form.Control type="text" placeholder={params.school} maxLength="30" />
                </Form.Group>
            </Row>

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridModeratorID">
                    <Form.Label>Moderator Email</Form.Label>
                    <Form.Control type="email" placeholder={params.modemail} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridModuleLeadID">
                    <Form.Label>Module Lead Email</Form.Label>
                    <Form.Control type="email" placeholder={params.leadmail} />
                </Form.Group>
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

export default function ModuleEditPage({ client, role, token }) {

    const notifications = client.getNotifications()
    const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");

    console.log(role);

    const [assessment, setAssessment] = useState(false);

    const [modEmail, modSetter] = useState(false);
    const [leadEmail, leadSetter] = useState(false)

    const location = useLocation();
    const navigate = useNavigate();

    console.log(role);

    const receivedData = useParams();
    const checkData = (!hasData(["code"], receivedData))

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

    useEffect(() => {
        client.getModuleInfo(token, moduleCode).then(res => {
            setAssessment(res);
        });

    }, [token, moduleCode]);

    useEffect(() => {
        if (assessment === "ERROR" || (canModify == false)) {
            setAssessment(false);
        }
    }, [assessment, canModify]);

    // Emails

    useEffect(() => {
        client.getModuleLead(token, moduleCode).then(res => {
            leadSetter(res);
        });

    }, [token, moduleCode]);

    useEffect(() => {
        if (modEmail === "ERROR") {
            modSetter(false);
        }
    }, [modEmail]);


    useEffect(() => {
        client.getModuleModerator(token, moduleCode).then(res => {
            modSetter(res);
        });

    }, [token, moduleCode]);

    useEffect(() => {
        if (modEmail === "ERROR") {
            leadSetter(false);
        }
    }, [leadEmail]);

    if ((leadEmail != false && leadEmail != null && leadEmail != "ERROR") && (modEmail != false && modEmail != null && modEmail != "ERROR") && (assessment != false && assessment != null && assessment != "ERROR")) {

        return (
            <>
                <TopNavBar />
                <div className="p-4">
                    <Container>
                        <FormGrid client={client} token={token} params={{ "modId": modEmail["id"], "leadId": leadEmail["id"] ,"code": assessment.moduleCode, "name": assessment.moduleName, "school": assessment.school, "modemail": modEmail["email"], "leadmail": leadEmail["email"] }} />
                    </Container>
                </div>
                <NotificationHolder notifications={notifications} client={client} />

            </>
        );
    } else {
        return <ErrorPage />
    }


 
}