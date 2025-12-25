import React, { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from 'react-router-dom';

import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import TopNavBar from "../components/LayoutC"
import ErrorPage from "../components/errorPage";

import '../template.css'
import CardTable, { PREVIEW_LIMIT } from "./tableAttrs"
import ViewUserPage, { handleUsersClick } from "../viewPages/viewUser"; 


function modulesViewMore(navigate) {
    navigate(`modules`);
}

function usersViewMore(navigate) {
    navigate(`users`);
}

function handleModulesClick(row, navigate) {
    console.log(row);
    navigate(`modules/${row[0]}/`);
}

export default function AdminDashboard(props) {
    const [ModulesPreview, setModPrev] = useState(Array(PREVIEW_LIMIT).fill(["Loading modules..."]));
    const [UsersPreview, setUsersPrev] = useState(Array(PREVIEW_LIMIT).fill(["Loading users..."]));

    const [show, setShow] = useState(false);
    const [userData, setUserData] = useState(null);

    const navigate = useNavigate();


    useEffect(() => {
        props.client.getCountModules(props.token, PREVIEW_LIMIT).then(res =>
            setModPrev(res)
        )
    }, []);

    useEffect(() => {
        props.client.getCountUsers(props.token, PREVIEW_LIMIT).then(res =>
            setUsersPrev(res)
        )
    }, []);

    if (props.role != "ROLE_TEACHING_SUPPORT" && props.role != "ROLE_EXAMS_OFFICER") {
        return <ErrorPage message="Insufficient Access" code="401" />
    } else {
        return (
            <>
                <TopNavBar client={props.client} />
                <br></br>
                <h2 style={{ textAlign: "center" }}>{(props.role === "ROLE_TEACHING_SUPPORT") ? "Teaching Support Dashboard" : "Exams Officer Dashboard"}</h2>
                <Row className="p-2" style={{ width: "100%" }}>
                    <Col>
                        <CardTable
                            title="Users"
                            headers={["ID", "Name", "Role", "Email"]}
                            body={UsersPreview}
                            tid={1}
                            handleClick={() => usersViewMore(navigate)}
                            handleRowClick={(row) => handleUsersClick(row, props.client, props.token, setUserData, setShow)}
                            viewFull={false}
                        />
                    </Col>
                    <Col>
                        <CardTable
                            title="Modules"
                            headers={["Code", "Name", "School", "Degree Level"]}
                            body={ModulesPreview}
                            tid={1}
                            handleClick={() => modulesViewMore(navigate)}
                            handleRowClick={(row) => handleModulesClick(row, navigate)}
                            viewFull={false}
                        />
                    </Col>
                </Row>
                <ViewUserPage client={props.client} token={props.token} userId={props.user.id} role={props.role} show={show} setShow={setShow} userData={userData} />
            </>
        );
    }
}