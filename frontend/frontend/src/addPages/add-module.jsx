import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "../config";

import axios from "axios";

import { Navbar, Container, Row, Col, Form, Button, Nav } from 'react-bootstrap';
import TopNavBar from "../components/LayoutC"
import ErrorPage from "../components/errorPage";
import NotificationHolder from '../components/Notifications'
import compValues from "../components/CompValues";

import Logo from '../assets/logo-simplified.png'
import '../template.css';

function FormGrid({client, token}) {
    const [hasFile, setFile] = useState(true);

    const changeFileState = (event) => {
        const fileLength = (event.target.files).length
        if (fileLength == 1) {
            setFile(false);
        } else {
            setFile(true);
        };
        console.log(hasFile);
    }

    const submitForm = async (event) => { 
        event.preventDefault();
        if (!hasFile) { // If file uploaded
            const files = event.target[6].files;
            if (files.length > 0) {
                (files[0].text()).then(output => {
                    client.createModuleCSV(token, output);
                })
            }
            

        } else { // No file
            const code = event.target[0].value;
            const degree = event.target[1].value;
            const name = event.target[2].value;
            const school = event.target[3].value;
            const modEmail = event.target[4].value;
            const leadEmail = event.target[5].value;

            let modId = ""

            if (modEmail != "") {
                modId = await client.getIdFromEmail(token, modEmail, "Moderator");
            } else {
                modId = "";
            }

            console.log(modId);
            const leadId = await client.getIdFromEmail(token, leadEmail, "Module lead");

            if (leadId != "ERROR" && modId != "ERROR") {
                client.createModule(token, code, name, school, degree, leadId, modId);
            }

        };
    }

    return (
        <Form onSubmit={submitForm}>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridName">
                    <Form.Label>Code</Form.Label>
                    <Form.Control type="text" placeHolder="Module Code" required={hasFile} disabled={!hasFile} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridType">
                    <Form.Label>Degree Level</Form.Label>
                    <Form.Select aria-label="Assessment Type">
                        <option value="UNDERGRADUATE">Undergraduate</option>
                        <option value="POSTGRADUATE">Postgraduate</option>
                    </Form.Select>
                </Form.Group>
            </Row>

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeHolder="Module Name" required={hasFile} disabled={!hasFile} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridSchool">
                    <Form.Label>School</Form.Label>
                    <Form.Control type="text" placeholder="School Name" maxLength="30" required={hasFile} disabled={!hasFile} />
                </Form.Group>
            </Row>

            <Row className="mb-4">
                <Form.Group as={Col} controlId="formGridModeratorID">
                    <Form.Label>Moderator Email</Form.Label>
                    <Form.Control type="email" placeholder="example@team27.com" required={false} disabled={!hasFile} />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridModuleLeadID">
                    <Form.Label>Module Lead Email</Form.Label>
                    <Form.Control type="email" placeholder="example@team27.com" required={hasFile} disabled={!hasFile} />
                </Form.Group>
            </Row>

            <Row>
                <div className="align-items-center d-flex flex-column justify-content-center">
                    <h6>━━━ OR ━━━</h6>
                    <h0>Upload a .csv file instead!</h0>
                </div>
            </Row>

            <Row className="mb-3">
                <Form.Group controlId="formFile" className="mb-3">
                    <Form.Label>CSV File:</Form.Label>
                    <Form.Control type="file" accept=".csv" onChange={changeFileState} />
                </Form.Group>

            </Row>


            <Container className="d-flex gap-2">
                <Button variant="success" type="submit" value="submit">
                    Create
                </Button>
            </Container>
        </Form>
    );
}

export default function ModuleAddPage({ client, role, token }) {
    const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");
    const notifications = client.getNotifications()

    if (canModify == true) {
        return (
            <>
                <TopNavBar client={client} />
                <div className="p-4">
                    <Container>
                        <FormGrid client={client} token={token} />
                    </Container>
                </div>
                <NotificationHolder notifications={notifications} client={client} />
            </>
        );
    } else {
        return <ErrorPage />
    }
}