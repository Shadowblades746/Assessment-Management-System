import React, { useEffect, useState } from "react";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Table from 'react-bootstrap/Table';
import Alert from 'react-bootstrap/Alert';
import {RoleToString, StringToRole} from "../utils";

export function handleUsersClick(row, client, token, setUserData, setShow) {
    client.getUserAndRoleFromId(token, row[0]).then(res => {
        setUserData(res);
        setShow(true);
    })
}

export default function ViewUserPage({ client, token, userId, role, show, setShow, userData }) {
    

    const [error, setError] = useState(null);
    const [userRole, setUserRole] = useState(null);


    const allUserRoles = (role === "ROLE_EXAMS_OFFICER") ?
        ["ROLE_ACADEMIC_STAFF", "ROLE_EXAMS_OFFICER", "ROLE_TEACHING_SUPPORT", "ROLE_EXTERNAL_EXAMINER"] :
        ["ROLE_ACADEMIC_STAFF", "ROLE_TEACHING_SUPPORT", "ROLE_EXTERNAL_EXAMINER"];
    const otherUserRoles = (userData !== null) ? (allUserRoles.filter(r => r !== userData.role)) : [];

    console.log(userData)

    const handleSubmit = (event) => {
        if (userRole !== userData.role && userRole != null) {
            event.preventDefault();
            client.setUserRole(token, userData.id, userRole).then(res => {
                if (res !== "ERROR") {
                    setShow(close);
                    setUserRole(null);
                    setError(null);
                } else {
                    setError("Error updating user role");
                }
            });
        } else {
            setUserRole(null);
            setShow(close);
            setError(null);
        }
    }

    const handleClose = () => {
        setShow(close);
        setError(null);
        setUserRole(null);
    }

    const handleDelete = () => {
        client.deleteUser(token, userData.id).then(res => {
            if (res !== "ERROR") {
                setShow(close);
                setUserRole(null);
                setError(null);
            } else {
                setError("Error deleting user");
            }
        })
    }
    return (
        <Modal show={show}
            onHide={handleClose}
            backdrop="static"
            keyboard={true}>
            <Form>
                <Modal.Header closeButton>
                    <Modal.Title>{  (userData !== null) ? userData.fullname : ""}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h5>User Details</h5>
                    <Table>
                    <tbody>
                        <tr>
                            <td>
                                <strong>ID:</strong>
                            </td>
                            <td>{(userData !== null) ? userData.id : ""}</td>
                        </tr>
                        <tr>
                            <td>
                                <strong>Email:</strong>
                            </td>
                            <td>{(userData !== null) ? userData.email : ""}</td>
                        </tr>
                        <tr>
                            <td>
                                <strong>Role:</strong>
                            </td>
                            {userData !== null ? (
                                (role === "ROLE_TEACHING_SUPPORT" && userData.role === "ROLE_EXAMS_OFFICER") ?
                                    (<td>{"Exams Officer"}</td>) :
                                    (
                                        <td>
                                            <Form.Select size="sm" onChange={(e) => setUserRole(StringToRole(e.target.value))}>
                                                    <option value={userData.role}>
                                                        {RoleToString(userData.role)}
                                                    </option>
                                                {otherUserRoles.map((r) => (
                                                    <option key={r} value={r}>
                                                        {RoleToString(r)}
                                                    </option>
                                                ))}
                                            </Form.Select>
                                        </td>
                                    )
                            ) : (<></>)
                            }
                            </tr>
                        </tbody>
                    </Table>
                    {
                        (error != null) ? (
                            <Alert variant="danger">
                                {error}
                            </Alert>
                        ) : (<></>)
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>
                    <Button variant="primary" type="submit" onClick={handleSubmit}>
                        Save Changes
                    </Button>
                    <Button variant="danger" onClick={() => { handleDelete() }}>
                        Delete User
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}