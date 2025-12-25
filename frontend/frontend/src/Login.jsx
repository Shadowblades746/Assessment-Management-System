import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "./config";
import axios from "axios";
import Alert from 'react-bootstrap/Alert';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Navbar from 'react-bootstrap/Navbar'; 
import FormText from 'react-bootstrap/FormText';
import { Toast, ToastContainer } from 'react-bootstrap';

import FloatingLabel from 'react-bootstrap/FloatingLabel';

import { AUTH_API } from "./config";

import Logo from './assets/logo.png'
import './template.css';
import TopNavBar from "./components/LayoutC"
import NotificationHolder from './components/Notifications'

const maxPasswordLenghth = 20;
const maxEmailLength = 50;

function CreateAlert(params) {
    return (
    <Alert variant={params.variant}>
        {params.message}
    </Alert>

)
}; 

function LoginForm( {client, buttons }) {
    const usernameType = useRef(null);
    const passwordType = useRef(null);

    const submitHandler = (e) => {
        e.preventDefault();

        const username = usernameType.current.value;
        const password = passwordType.current.value;

        client.postLogin(username, password);
        //client.addNotification("hey", "hey");
    }

    return (
            <Form onSubmit={submitHandler}>
            <Form.Group as={Row} className="mb-3" controlId="formHorizontalInputs">
                <Col>
                    <Form.Group className="mb-3" controlId="formBasicEmail">

                        <FloatingLabel controlId="floatingInput" label="Email">

                            <Form.Control ref={usernameType} type="email" placeholder="Email" required minLength="3" maxLength={maxEmailLength} />
                        </FloatingLabel>
                        <Form.Text id="emailHelp" muted>Please enter a valid & unique email address</Form.Text>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <FloatingLabel controlId="floatingInput" label="Password">
                            <Form.Control ref={passwordType} type="password" placeholder="Password" required minLength="5" maxLength={maxPasswordLenghth} />
                        </FloatingLabel>
                        <Form.Text id="emailHelp" muted>Please enter a password between 5-30 characters long</Form.Text>
                    </Form.Group>
                </Col>
                <Form.Group controlId="formSubmit" className="gap-1 d-flex">
                    {buttons.map(param => (
                        <Button type="submit" className="flex-fill" name={param}>{param}</Button>
                    ))}
                </Form.Group>

            </Form.Group>
        </Form>
    );
}

export default function LoginPage({ client }) {
    const notifications = client.getNotifications()

    return (
        <>
            <TopNavBar />
            <div className="p-4 d-flex justify-content-center align-items-center flex-column">
                    <img src={Logo} alt="Logo" id="logo" />
                    <h2>Welcome.</h2>
                       <h4>Login or create an account.</h4>
                    <div className="shadow-sm p-4 bg-white rounded mt-4">
                        <LoginForm buttons={["Login"]} client={client}/>
                    </div>
            </div>

            <NotificationHolder notifications={notifications} client={client} />

       </>

    )
}