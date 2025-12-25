import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useEffect, useState, useRef } from "react";
import { MODULES_API, USERS_API } from "./config";
import axios from "axios";

import { Navbar, Container, Row, Col, Form, Button, Nav, Card, Collapse } from 'react-bootstrap';

import { TopNavBar } from './edit-page-template';
import Logo from './assets/logo-simplified.png'
import Arrow from './assets/arrow.png'

import './template.css';

function ViewHeaderCard(params) { // Params contains card title & description, specifier is to specify if it's assignment or module or none
    return (
        <Card className="mb-1 bg-transparent border-0 text-black">
            <Card.Body>
                <Card.Title>{params.Title}</Card.Title>
                <Card.Text>{params.Text}</Card.Text>
                {params.Specifier === "ModuleList" && (
                    <a href="/">Add Module</a>
                )}

                {params.Specifier === "ModuleView" && (
                    <div className="d-flex gap-2">
                        <a href="/">Edit Module</a>
                        <a href="/">Add Assignment</a>
                    </div>

                )}
            </Card.Body>
        </Card>
    );
}

function ListItem(params) {

    function divClicked(event) {
        if (!event || !event.target) {
            return;
        }

        let clickedId = event.target.id
        console.log("Div clicked: " + clickedId);
    }

    if (!params.modules || params.modules.length === 0) {
        return (
            <div className="p-4">
                <h1>No assignments found</h1>
            </div>
        );
    }

    return (
        <>
            {params.modules.map((module, index) => (
                <div key={index} id={module.code} onClick={divClicked} className="buttonHover position-relative w-100 p-4 d-flex justify-content-center align-items-left flex-column shadow-sm bg-white rounded">
                    <div className="tintBox position-absolute top-0 start-0 w-100 h-100" />
                    <img alt="Logo" src={Arrow} width="10" height="12" className="position-absolute opacity-50 moveArrow" />
                    <Card className="bg-transparent border-0 no-click" style={{ width: '100' }}>
                        <Card.Body>
                            <Card.Title>{module.name}</Card.Title>
                            <Card.Text>
                                {"Due: " + module.code}
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </div>
            ))}
        </>
    )
}
export default function ViewPage() {
    return (
        <>
            <TopNavBar href="modules" />
            <div className="p-4 d-flex justify-content-center align-items-center flex-column gap-1">
                <div className="position-relative w-100 p-1 d-flex justify-content-center align-items-left flex-column shadow-sm bg-primary-subtle rounded mb-3">
                    <ViewHeaderCard Title="{module.code} - View" Specifier="ModuleView" Text="This page provides a comprehensive look at their selected module, giving you insight into the assessments and assignments assigned." />
                </div>
                <ListItem modules={[{ "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" }, { "name": "Farty Assignment", "code": "17/17/2017" },]} />
            </div>
        </>
    );
}