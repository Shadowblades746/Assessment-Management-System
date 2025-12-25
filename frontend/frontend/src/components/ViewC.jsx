import React, { useEffect, useState, useRef } from "react";
import { Container, Row, Col, Form, Button, Card } from 'react-bootstrap';
import { useNavigate, useLocation, useParams, Link } from 'react-router-dom';

export default function ViewHeaderCard(params) { 

    return (
        <Card className="mb-1 bg-transparent border-0 text-black">
            <Card.Body>
                <Card.Title>{params.Title}</Card.Title>
                <Card.Text>{params.Text}</Card.Text>
                {params.canModify === true && (
                    <>
                    {params.Specifier === "ModuleList" && (
                        <Link to="./add">Add Module</Link>
                    )}

                    {params.Specifier === "ModuleView" && (
                        <div className="d-flex gap-2">
                            <Link to="./edit">Edit Module</Link>
                            <Link to="./add">Add Assessment</Link>
                        </div>

                    )}

                {params.Specifier === "AssessmentView" && (
                            <div className="d-flex gap-2">
                                {params.canModify2 === true && (
                                    <Link to="./edit">Edit Assessment</Link>

                                )}
                                {params.canIncrease === true && (
                                    <a id="increase" href="#" onClick={params.increase}>Increase Status</a>
                                )}
                                {params.canDecrease === true && (
                                    <a id="decrease" href="#" onClick={params.decrease}>Decrease Status</a>
                                )}
                    </div>
                    )}
                </>

                )}
            </Card.Body>
        </Card>
    );
}