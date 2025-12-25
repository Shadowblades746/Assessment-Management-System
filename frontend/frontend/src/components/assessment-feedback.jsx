import React, { useState} from "react";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Alert from 'react-bootstrap/Alert';

export function HandleRowClickAssessments(row, setShow, client, token, setFeedbackModalArgs) {
    client.getForwardAndBackward(token, row[0], row[1]).then(res => {
        setFeedbackModalArgs({
            row: row,
            previousFeedback: (res.forward.previousFeedback == null ? "" : JSON.parse(res.forward.previousFeedback).feedback),
            forwardFeedbackRequired: res.forward.feedbackRequired,
            backwardFeedbackRequired: res.backward.feedbackRequired,
            allowedForward: res.forward.isPossible,
            allowedBackward: res.backward.isPossible
        });
        setShow(true);
    });
}

export default function FeedbackModal({
    show, setShow, client, token, userId, row, previousFeedback=null, forwardFeedbackRequired, backwardFeedbackRequired, allowedForward, allowedBackwards}) {

    const [feedback, setFeedback] = useState("");
    const [isForward, setIsForward] = useState(null);
    const [error, setError] = useState(null);

    const handleSubmit = (event) => {
        event.preventDefault();
        if ((!forwardFeedbackRequired && isForward) ||
            (!backwardFeedbackRequired && !isForward) ||
            (forwardFeedbackRequired && isForward && feedback != "") ||
            (backwardFeedbackRequired && !isForward && feedback != "")
        ) {
            client.postFeedback(token, row[0], row[1], userId, isForward, feedback).then(res => {
                if (res.status === 200 && res != "ERROR ON SUBMISSION") {
                    setFeedback("");
                    setError(null);
                    setIsForward(null);
                    setShow(false);
                } else {
                    setError("Error submitting feedback");
                }
            });
        } else {
            setError("You need to submit feedback before submitting");
        }
    }

    const setClose = () => {
        setFeedback("");
        setError(null);
        setShow(close);
    }

    if (allowedBackwards || allowedForward) {

        return (
            <Modal
                show={show}
                onHide={() => setShow(false)}
                backdrop="static"
                keyboard={true}
            >
                <Form onSubmit={handleSubmit}>
                    <Modal.Header closeButton>
                        <Modal.Title>Submit feedback</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {(previousFeedback != null && previousFeedback != "") ? (
                            <>
                                <h5>Previous Message</h5>
                                <p>{previousFeedback}</p>
                                <hr />
                            </>
                        ) : (<></>)}
                        <Form.Label>Response ({(forwardFeedbackRequired) ? "Required" : "Optional"}):</Form.Label>
                        <Form.Control as="textarea" aria-label="With textarea" value={feedback} onChange={e => setFeedback(e.target.value)} style={{ resize: "None" }} rows={5} />
                        {(error == null) ? (<></>) : (
                            <>
                                <br/>
                                <Alert variant="danger">
                                    {error}
                                </Alert>
                            </>
                        )}
                    </Modal.Body>
                    <Modal.Footer>
                        {allowedForward ? (
                            <Button variant="success" type="submit" onClick={() => setIsForward(true)}>Submit</Button>
                        ) : (<></>)}
                        {allowedBackwards ? (
                            <Button variant="danger" type="submit" onClick={() => setIsForward(false)}>Request Resubmission</Button>
                        ) : (<></>)}
                        <Button variant="secondary" onClick={setClose}>Cancel</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        );
    } else {
        return (
            <Modal
                show={show}
                onHide={() => setShow(false)}
                backdrop="static"
                keyboard={true}

            >
                <Modal.Header closeButton>
                    <Modal.Title>No action needed</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="success">
                        No further action is needed at this minute. Please consult an administrator if you believe this is in error.
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={setClose}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}