import React, { useState, useEffect } from "react";
import TopNavBar from "../components/LayoutC";
import FeedbackModal, { HandleRowClickAssessments } from "../components/assessment-feedback";
import ErrorPage from "../components/errorPage";

import CardTable, { PREVIEW_LIMIT, FormatAssessmentRows } from './tableAttrs';
import '../template.css';

function assessmentsViewMore() {
    console.log("Assessments view more clicked");
}
export default function ExternalExaminerDashboard(props) {
    const [assessments, setAssessments] = useState(Array(PREVIEW_LIMIT).fill(["Loading..."]));

    // Modal states
    const [show, setShow] = useState(false);
    const [feedbackModalArgs, setFeedbackModalArgs] = useState({
        previousFeedback: null,
        forwardFeedbackRequired: false,
        backwardFeedbackRequired: false,
        allowedForward: false,
        allowedBackward: false
    })

    useEffect(() => {
        props.client.getFilteredAssessments(props.token, props.user.id, 0).then(res => {
            setAssessments(res);
        })
    }, [])
    if (props.role !== "ROLE_EXTERNAL_EXAMINER") {
        return <ErrorPage code="401" message="Insufficient access" client={props.client} />
    } else {
        return (
            <>
                <TopNavBar client={props.client} />
                <div className="container-fluid px-4 mt-4">
                    <h2 style={{ textAlign: "center" }}>External Examiner Dashboard</h2>
                    <div className="mt-4">
                        <CardTable
                            title="Assessments Overview"
                            headers={["Module Code", "Assessment Title", "Status", "Type", "Your Role"]}
                            body={FormatAssessmentRows(assessments)}
                            handleClick={assessmentsViewMore}
                            tid={1}
                            handleRowClick={(row) => HandleRowClickAssessments(row, setShow, props.client, props.token, setFeedbackModalArgs)}
                            viewFull={true}
                        />
                    </div>
                </div>
                <FeedbackModal
                    show={show}
                    setShow={setShow}
                    client={props.client}
                    role={props.role}
                    token={props.token}
                    userId={props.user.id}
                    {...feedbackModalArgs}
                />
            </>
        )
    }
}