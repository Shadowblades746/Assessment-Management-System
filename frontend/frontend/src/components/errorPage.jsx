import React from "react";
import '../template.css'
import Logo from '../assets/logoWhite.png'

import Button from "react-bootstrap/Button";

export default function ErrorPage(params) {
    let message = params.message;
    let code = params.code;
    if (!params || !params.message) {
        message = "An unknown error occurred.";
    }
    if (!params || !params.code) {
        code = 500;
    }
    return (
        <>
            <div style={{ backgroundColor: "#0D6EFD" }} className="p-4 d-flex justify-content-center align-items-center flex-row gap-2 min-vh-100">
                <img src={Logo} alt="Logo" id="logo" />
                <div className="p-4 bg-transparent">
                    <h0 className="text-white">{code}</h0>
                    <h2 className="text-white">{message}</h2>
                </div>

                {
                    params.client && (
                        <div className="d-flex justify-content-center">
                            <Button onClick={() => { params.client.logout() }} variant="primary">Logout</Button>
                        </div>
                    )
                }
            </div>
        </>
    );
}