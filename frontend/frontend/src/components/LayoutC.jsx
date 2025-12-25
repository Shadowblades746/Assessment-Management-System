import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useLocation, useParams } from 'react-router-dom';

import { Navbar, Container, Form, Nav } from 'react-bootstrap';

import Logo from '../assets/logo-simplified.png'

export default function TopNavBar({ client }) { 
    const navigate = useNavigate();
    const location = useLocation();

    const logoutClick = () => {
        console.log("hey");
        client.logout();
    }
    return (
        <Navbar bg="primary" data-bs-theme="dark">
            <Container fluid className="px-4">
                <Navbar.Brand onClick={() => navigate("/")} style={{ cursor: "pointer" }}>
                    <img alt="Logo" src={Logo} width="24" height="24" className="d-inline-block align-middle mb-1" />{' '}
                    Team 27
                </Navbar.Brand>
                {client && (
                    <Form className="d-flex">
                        <Nav>
                            <Nav.Link onClick={logoutClick}>Logout</Nav.Link>
                        </Nav>
                    </Form>
                )}

            </Container>
        </Navbar>
    );
}