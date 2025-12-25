import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "./config";
import axios from "axios";

import { Navbar, Container, Row, Col, Form, Button, Nav } from 'react-bootstrap';

import Logo from './assets/logo-simplified.png'
import './template.css';

export const TopNavBar = (params) => {
    return (
        <Navbar bg="primary" data-bs-theme="dark">
            <Container fluid className="px-4">
                <Navbar.Brand>
                    <img alt="Logo" src={Logo} width="24" height="24" className="d-inline-block align-middle mb-1"/>{'    '}
                    Team 27
                </Navbar.Brand>
                {params.href && (
                    <Form className="d-flex">
                        <Nav>
                            <Nav.Link href={params.href}>←</Nav.Link>
                        </Nav>
                    </Form>
                )}

            </Container>
        </Navbar>
    );
}
function FormGrid() {
    return (
        <Form>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridEmail">
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" placeholder="Enter email" />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" placeholder="Password" />
                </Form.Group>
            </Row>

            <Form.Group className="mb-3" controlId="formGridAddress1">
                <Form.Label>Address</Form.Label>
                <Form.Control placeholder="1234 Main St" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formGridAddress2">
                <Form.Label>Address 2</Form.Label>
                <Form.Control placeholder="Apartment, studio, or floor" />
            </Form.Group>

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridCity">
                    <Form.Label>City</Form.Label>
                    <Form.Control />
                </Form.Group>

                <Form.Group as={Col} controlId="formGridState">
                    <Form.Label>State</Form.Label>
                    <Form.Select defaultValue="Choose...">
                        <option>Choose...</option>
                        <option>...</option>
                    </Form.Select>
                </Form.Group>

                <Form.Group as={Col} controlId="formGridZip">
                    <Form.Label>Zip</Form.Label>
                    <Form.Control />
                </Form.Group>
            </Row>

            <Form.Group className="mb-3" id="formGridCheckbox">
                <Form.Check type="checkbox" label="Check me out" />
            </Form.Group>
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

export default function EditPage() {
    return (
        <>
            <TopNavBar backHref="/" />
            <div className="p-4">
                <Container>
                    <FormGrid />
                </Container>
            </div>
        </>
    );
}