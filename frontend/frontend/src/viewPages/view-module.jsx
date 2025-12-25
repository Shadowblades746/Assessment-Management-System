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
import { Navbar, Container, Row, Col, Form, Button, Nav, Card, Collapse } from 'react-bootstrap';

// Assets
import Logo from '../assets/logo-simplified.png'
import Arrow from '../assets/arrow.png'
import '../template.css';


function CreateStaffBox({ canUse, client, token, code }) {


    const submit = async (event) => {
        event.preventDefault();
        const buttonType = event.nativeEvent.submitter.value;

        const leadEmail = event.target[0].value;
        const leadId = await client.getIdFromEmail(token, leadEmail, "Staff");

        console.log(leadId);    

        if ((buttonType == "delete") && (leadId != "ERROR")) {
            client.bunStaffMember(token, code, leadId);
        } else if (leadId != "ERROR") {
            client.addStaffMember(token, code, leadId);
        }
    }

    if (canUse) {
        return (
            <Form onSubmit={submit}>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridTitle">
                    <Form.Label>Add Staff Member</Form.Label>
                    <Form.Control type="text" placeholder="Staff Email" required />
                </Form.Group>

            </Row>
            <Container className="d-flex gap-2">
                <Button variant="success" type="submit" value="submit">
                    Create
                </Button>
                <Button variant="danger" type="submit" value="delete">
                    Delete
                </Button>

            </Container>
           </Form>);
    }
}
function ListItem(params) {
    const navigate = useNavigate();
    function divClicked(event) {
        if (!event || !event.target) {
            return;
        }

        let clickedId = event.target.id
        let moduleName = event.target.name
        navigate(`${clickedId}`);
    }

    if (!params.modules || params.modules.length === 0) {
        return (
            <div className="p-4">
                <h1>No assessments found</h1>
            </div>
        );
    }

    return (
        <>
            {params.modules.map((module, index) => (
                <div key={index} id={module[1]} onClick={divClicked} className="buttonHover position-relative w-100 p-4 d-flex justify-content-center align-items-left flex-column shadow-sm bg-white rounded">
                    <div className="tintBox position-absolute top-0 start-0 w-100 h-100" />
                    <img alt="Logo" src={Arrow} width="10" height="12" className="position-absolute opacity-50 moveArrow" />
                    <Card className="bg-transparent border-0 no-click" style={{ width: '100' }}>
                        <Card.Body>
                            <Card.Title>{module[1]}</Card.Title>
                            <Card.Text>
                                {"Status: " + module[2]} <br /> {module[3]} <br /> {module[4] }
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </div>
            ))}
        </>
    )
}
export default function ViewPage({ client, token, user, role }) {
    const notifications = client.getNotifications()
    const [modules, setModule] = useState(false);
    const [assessments, setAssessments] = useState(false);
    const [moduleStaff, setModuleStaff] = useState(false);
    const [moderatorId, setModeratorId] = useState(false);
    const [moderator2Id, setModerator2Id] = useState(false);


    // Check we have extension & data valid
    const location = useLocation();
    const navigate = useNavigate();

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

    // Check the code is valid and retrieve data
    const code = receivedData["code"];

    useEffect(() => {
        client.getModuleInfo(token, code).then(res => {
            setModule(res);
            if (res == "ERROR") { // ERROR then we just navigate to 404
                setModule(null);
            }
        });
    }, [token, code]);


    useEffect(() => {
        if (!modules) {
            return;
        }

        if ((role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER")) {
            client.getAssessmentsAdmin(token, code).then(res => {
                setAssessments(res);
            });
        } else {

            client.getUserAssessments(token, user.id, 9999).then(res => {
                setAssessments(res);
            });
        }
    }, [token, modules, role, code]);

    // Get module staff & moderator
    useEffect(() => {
        client.getModuleStaff(token, code).then(joe => {
            setModuleStaff(joe);
        });
    }, [token, code]);

    useEffect(() => {
        client.getModuleLead(token, code).then(joe => {
            setModeratorId(joe);
        });
    }, [token, code]);

    useEffect(() => {
        client.getModuleModerator(token, code).then(joe => {
            setModerator2Id(joe);
        });
    }, [token, code]);

    useEffect(() => {
        if (moderator2Id === "ERROR") {
            setAssessments(null);
        }
    }, [moderator2Id]);

    // Check for assessment error
    useEffect(() => {
        if (assessments === "ERROR") {
            setAssessments(null);
        }
    }, [assessments]);

    // Check for module staff errors
    useEffect(() => {
        if (moduleStaff === "ERROR") {
            setModuleStaff(null);
        } else {
            if (moderator2Id['id'] == user.id) {
                client.getAssessmentsAdmin(token, code).then(res => {
                    setAssessments(res);
                });
            }

            for (let i = 0; i < moduleStaff.length; i++) {
                if (moduleStaff[i]['id'] == user.id) {
                    client.getAssessmentsAdmin(token, code).then(res => {
                        setAssessments(res);
                    });
                }
            }
        }
    }, [assessments, user, token, code]);

    useEffect(() => {
        if (moderatorId === "ERROR") {
            setModeratorId(null);
        }
    }, [assessments]);
    // Handle data
    if ((moderator2Id != "ERROR" && moderator2Id != null) && (moderatorId != "ERROR" && moderatorId != null) && (moduleStaff != "ERROR" && moduleStaff != null) && (modules != null && modules != "ERROR" && modules != false) && (assessments !== null && assessments != "ERROR" && assessments != false)) {

        let staffEmails = "";
        if (moduleStaff.length != 0) {
            staffEmails = "Staff Members: "
            for (let i = 0; i < moduleStaff.length; i++) {
                staffEmails += (moduleStaff[i]["email"]) + ", ";
            }
            staffEmails = staffEmails.slice(0, -2);
        }

        const moduleInfo = modules;
        const moduleCode = moduleInfo.moduleCode;

        const filterAssessments = assessments.filter(modu => modu[0] == moduleCode);
        const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");


        return (
            <>
                <TopNavBar client={client} />
                <div className="p-4 d-flex justify-content-center align-items-center flex-column gap-1">
                    <div className="position-relative w-100 p-1 d-flex justify-content-center align-items-left flex-column shadow-sm bg-primary-subtle rounded mb-3">
                        <ViewHeaderCard Title={moduleInfo.moduleName + " - " + moduleInfo.moduleCode} Specifier="ModuleView" canModify={canModify} Text={<> {moduleInfo.school} <br /> {moduleInfo.degreeLevel} <br /> {staffEmails} </>}  />
                        <CreateStaffBox canUse={(moderatorId['id'] == user.id)} client={client} token={token} code={moduleCode} />
                    </div>
                    <ListItem modules={filterAssessments} />

                </div>
                <NotificationHolder notifications={notifications} client={client} />


            </>
        );
    } else if (assessments == null || modules == null) {
        return <ErrorPage />
    }

}