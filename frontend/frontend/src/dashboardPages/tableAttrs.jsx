import React from "react";

import Card from 'react-bootstrap/Card'
import Table from "react-bootstrap/Table";

import { StatusToString, ExamTypeToString } from "../utils"


const tableDivClasses = "border border-dark border-2 rounded-1";
const tableDivStyle = { padding: 0, margin: 0 };
const tableClasses = "rounded-2 p-0 m-0";

export const PREVIEW_LIMIT = 5;

export function FormatAssessmentRows(assessments) {
    if (assessments == null || assessments.length === 0) {
        return [["No assessments found"]];
    } else if (assessments[0].length === 1) {
        return assessments;
    } else {
        return  assessments.map(assessment => {
            return [assessment.assessment.moduleCode, assessment.assessment.title,
            StatusToString(assessment.assessment.status), ExamTypeToString(assessment.assessment.type),
            assessment.roles[0]
            ]
        });
    }
}
export default function CardTable(props) {
    return (
        <Card className={tableDivClasses} style={tableDivStyle}>
            <Card.Header>
                <h3 style={{ textAlign: "center" }}>{props.title}</h3>
            </Card.Header>
            <Card.Body>
                <Table striped bordered hover className={tableClasses}>
                    <thead>
                        <tr>
                            {
                                props.headers.map((_header, i) => 
                                    <th key={i}>{_header}</th>
                                )
                            }
                        </tr>
                    </thead>
                    <tbody>
                        {
                            props.body.map((_row, i) => (
                                <tr key={i} role="button" onClick={() => props.handleRowClick(_row)} style={{ cursor: "pointer"} }>
                                    {
                                        _row.map((_col, j) => 
                                            <td colSpan={(_row.length == 1) ? props.headers.length : 1} key={j}>{_col}</td>
                                        )
                                    }
                                </tr>
                            ))
                        }
                        { props.viewFull ? (<></>) : (
                            <tr tabIndex={props.tid} role="button" onClick={props.handleClick} style={{ cursor: "pointer" }}>
                                <td colSpan={props.headers.length}>
                                    <h6 style={{ textAlign: "center" }}>View More</h6>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            </Card.Body>
        </Card>
    );
}

