import React, { useState, useEffect } from "react";
import CardTable from "../dashboardPages/tableAttrs"; 
import TopNavBar from "../components/LayoutC";
import ErrorPage from "../components/errorPage";
import ViewUserPage, { handleUsersClick } from "./viewUser";
import '../template.css';

export default function ViewUsersPage(props) {
    const [users, setUsers] = useState([["Loading..."]]);
    const [userData, setUserData] = useState(null);
    const [show, setShow] = useState(false);

    useEffect(() => {
        props.client.getAllUsers(props.token).then(res => {
            setUsers(res);
        })
    }, []);

    if (props.role !== "ROLE_TEACHING_SUPPORT" && props.role !== "ROLE_EXAMS_OFFICER") {
        return <ErrorPage code="401" message="Insufficient access" client={props.client} />
    } else {
        return (
            <>
                <TopNavBar client={props.client} />
                <CardTable
                    title="Users"
                    headers={["ID", "Name", "Role", "Email"]}
                    body={users}
                    tid={1}
                    handleClick={() => { }}
                    handleRowClick={(row) => handleUsersClick(row, props.client, props.token, setUserData, setShow)}
                    viewFull={true}
                />

                <ViewUserPage client={props.client} token={props.token} userId={props.user.id} role={props.role} show={show} setShow={setShow} userData={userData} />

            </>
        );
    }
}
