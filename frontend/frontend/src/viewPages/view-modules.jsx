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
import TopNavBar from "../components/LayoutC"
import NotificationHolder from '../components/Notifications'
import ErrorPage from "../components/errorPage";

// React Components
import { Navbar, Container, Row, Col, Form, Button, Nav, Card, Collapse } from 'react-bootstrap';

// Assets
import Logo from '../assets/logo-simplified.png'
import Arrow from '../assets/arrow.png'
import '../template.css';

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
                <h1>No modules found</h1>
            </div>
        );
    }

    return (
        <>
            {params.modules.map((element, index) => (
                <div key={index} id={element[1]} name={element[3]} onClick={divClicked} className="buttonHover position-relative w-100 p-4 d-flex justify-content-center align-items-left flex-column shadow-sm bg-white rounded">
                    <div className="tintBox position-absolute top-0 start-0 w-100 h-100" />
                    <img alt="Logo" src={Arrow} width="10" height="12" className="position-absolute opacity-50 moveArrow" />
                    <Card className="bg-transparent border-0 no-click" style={{ width: '100' }}>
                        <Card.Body>
                            <Card.Title>{element[0]}</Card.Title>
                            <Card.Text>
                                {element[1] + ", " + element[3]} <br /> {element[2]}
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </div>
            ))}
        </>
    )
}
export default function ViewModulePage({ client, token, user, role }) {
    const notifications = client.getNotifications()
    const [modules, setModules] = useState([]);

    if (role == "ROLE_EXTERNAL_EXAMINER") {
        return <ErrorPage />;
    }

    // Get modules from 
    useEffect(() => {
        if ((role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER")) {
            client.getCountModulesAGAIN(token, user.id, 0).then(res => {
                setModules(res);
            });
        } else {
            client.getUserModules(token, user.id, 0).then(res => {
                setModules(res);
            });
        }
    }, [token, user, role]); 

    const dataList = modules.filter(modu => modu.length >= 2);
    const canModify = (role == "ROLE_TEACHING_SUPPORT") || (role == "ROLE_EXAMS_OFFICER");

    return (
        <>
            <TopNavBar client={client} />
            <div className="p-4 d-flex justify-content-center align-items-center flex-column gap-1">
                <div className="position-relative w-100 p-1 d-flex justify-content-center align-items-left flex-column shadow-sm bg-primary-subtle rounded mb-3">
                    <ViewHeaderCard Title="Modules" Specifier="ModuleList" canModify={canModify} Text="This page provides a comprehensive look at the modules offered across the university, giving students insight into the academic content delivered throughout their courses." />
                </div>
                <ListItem modules={dataList} />
            </div>

            <NotificationHolder notifications={notifications} client={client} />

        </>
    );
}