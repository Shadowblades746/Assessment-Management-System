// API Endpoints
import { MODULES_API, USERS_API, AUTH_API, USER_ROLES_API, ALL_ASSESSMENTS_API } from "./config";

// Dependencies
import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { Toast, ToastContainer } from "react-bootstrap";

import axios from 'axios'

// Components
import UnauthorisedRoutes from './Routes/UnauthorisedRoutes.jsx';
import AuthorisedRoutes from './Routes/AuthorisedRoutes.jsx';
import NotificationHolder from './components/Notifications.jsx';

import {
    RoleToString, ExamTypeToString, StatusToString, SetterCheckerToString, ModuleRoleToString, IsCurrentStage } from "./utils"

// View Pages
import ViewModulesPage from './viewPages/view-modules.jsx';
import ViewModulePage from './viewPages/view-module.jsx'
import ViewAssessmentPage from './viewPages/view-assessment.jsx';

function NotificationHandler() {
    const [notifications, setNotifications] = useState([]); 

    function addNotification(message) {
        setNotifications(prev => [...prev, message]);
    }

    return (
        <NotificationHolder notifications={notifications} />
    )
}

export default function App() {

    const [token, setToken] = useState("")
    const [user, setUser] = useState({})
    const [role, setRole] = useState("");
    const [notifications, setNotification] = useState([]);

    const nav = useNavigate();

    const client = {

        errorHandler: (errort) => {

            console.log(errort.message);
            if (errort?.status) {
                const errMessage = errort.response.data

                if (!errMessage.message) {
                    client.addNotification("Invalid Data sent to server.", errMessage.status, errMessage.timestamp, "primary");

                } else {
                    client.addNotification(errMessage.message, errMessage.status, errMessage.timestamp, "primary");

                }

                return
            } else if (errort.code == "ERR_NETWORK") {
                client.addNotification("The server isn't ready to receive requests.", "Server Error", "503", "danger");
            } else {
                client.addNotification("An error has occured", "Server Error", "404", "primary")
            }
            return;
        },

        /* Account Data
        NEEDS to : error handling and throwing errors to be caught for 404 page
        NEEDS ASAP : implement actual GET request to backend */

        getCountUsers: (token, count) => {
            return axios.get(USERS_API, {
                headers: { "Authorization": `Bearer ${token}` },
                params: { count: count }
            })
                .then((response) => {
                    return Promise.all(
                        response.data.map((_user) => {
                            return axios.get(USER_ROLES_API + String(_user.id), {
                                headers: { "Authorization": `Bearer ${token}` }
                            }).then((_response) => {
                                return [_user.id, (_user.forename + " " + _user.surname), RoleToString(_response.data), _user.email];
                            }).catch(() => {
                                return [_user.id, (_user.forename + " " + _user.surname), "Error", _user.email];
                            })
                        })
                    );
                }).then(res => {
                    return res;
                })
                .catch(() => {
                    if (count === 0) {
                        return [["Error fetching users"]];
                    } else {
                        return Array(count).fill(["Error fetching users"]);
                    }
                });
        },

        getAllUsers: (token) => {
            return client.getCountUsers(token, 0);
        },

        // Gets all the user modules then returns all assessments for those modules
        getExpandAssessments: (token, userId, count = 0) => {
            return axios.get(`${ALL_ASSESSMENTS_API}/expand`, {
                headers: { "Authorization": `Bearer ${token}` },
                params: {
                    userId: userId,
                    count: count
                }
            }).then((response) => {
                return response.data;
            }).catch(() => {
                return Array(count).fill(["Could not fetch assessments"]);
            })
        },

        // Same as the above but filtered to only the assessments in which the user can currently take action in
        getFilteredAssessments(token, userId, count = 0) {
            return axios.get(`${ALL_ASSESSMENTS_API}/expand/filter`, {
                headers: { "Authorization": `Bearer ${token}` },
                params: {
                    userId: userId,
                    count: count
                }
            }).then((response) => {
                return response.data;
            }).catch(() => {
                if (count <= 0) {
                    return [["Could not fetch assessment roles"]]
                } else {
                    return Array(count).fill(["Could not fetch assessments"]);
                }
            })
        },

        getCountModules: (token, count) => {
            return axios.get(MODULES_API, {
                headers: { "Authorization": `Bearer ${token}` },
                params: { count: count }
            })
                .then((response) => {
                    return response.data.map((_module) => {
                        return [_module.moduleCode, _module.moduleName, _module.school, _module.degreeLevel];
                    });
                })
                .catch(() => {
                    return Array(count).fill(["Error fetching modules"]);
                });
        },

        // Same as the above but with a different return order. It's too late to change now
        getCountModulesAGAIN: (token, count) => {
            return axios.get(MODULES_API, {
                headers: { "Authorization": `Bearer ${token}` },
                params: { count: count }
            })
                .then((response) => {
                    return response.data.map((_module) => {
                        return [_module.moduleName, _module.moduleCode, _module.school, _module.degreeLevel];
                    });
                })
                .catch(() => {
                    return Array(count).fill(["Error fetching modules"]);
                });
        },

        budModule: (token, moduleCode) => {

            return axios.delete(`${MODULES_API}/${moduleCode}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then((response) => {
                    return response;
                })
                .catch((err) => {
                    client.errorHandler(err);
                    return err;
                });
        },

        setUserRole: (token, userId, role) => {
            axios.patch(`${USERS_API}/${userId}`, {}, {
                params: { role: role},
                headers: { Authorization: `Bearer ${token}` }
            }).then((response) => {
                return response.data;
            }).catch(() => {
                return "ERROR";
            })
        },

        deleteUser: (token, userId) => {
            return axios.delete(`${USERS_API}/${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            }).then((response) => {
                return response.data;
            }).catch(() => {
                return "ERROR";
            })
        }, 

        budAssessment: (token, moduleCode, assessment) => {

            return axios.delete(`${MODULES_API}/${moduleCode}/assessments/${assessment}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then((response) => {
                    return response;
                })
                .catch((err) => {
                    client.errorHandler(err);
                    return err;
                });
        },


        getUserAssessments: (token, userId, count) => {
            return axios.get(`${ALL_ASSESSMENTS_API}/role`, {
                headers: { "Authorization": `Bearer ${token}` },
                params: {
                    userId: userId,
                    count: count
                }
            }).then((response) => {
                const __response = response.data;
                if (__response.length === 0) {
                    return [["No assessment roles"]];
                }
                else {
                    return __response.map((_response) => {
                        return [_response.assessment.moduleCode, _response.assessment.title,
                        StatusToString(_response.assessment.status), ExamTypeToString(_response.assessment.type),
                        SetterCheckerToString(_response.role)];
                    })
                }
            }).catch(() => {
                return Array(count).fill(["Could not fetch assessment roles"]);
            })
        },

        canUserViewAssessment: (token, userId, modulecode, assessment) => {
            return client.getUserAssessments(token, userId, 0)
                .then((response) => {
                    console.log(response);
                    return response;
                })
                .catch(() => {
                    return "ERROR";
                });
        },

        getUserModules: (token, userId, count) => {
            return axios.get(MODULES_API, {
                headers: { Authorization: `Bearer ${token}` },
                params: {
                    userId: userId,
                    count: count
                }
            })
                .then((response) => {
                    const _response = response.data;
                    if (_response.length === 0) {
                        return [["No module Roles"]];
                    } else {
                        return _response.map((__response) => {
                            return [
                                __response.module.moduleName,
                                __response.module.moduleCode,
                                ModuleRoleToString(__response.role),
                                __response.module.school
                            ];
                        });
                    }
                })
                .catch((err) => {
                    client.errorHandler(err);
                    console.log(err);
                    return Array(count).fill(["Could not fetch module roles"]);
                });
        },

        getUserAssessmentStages: (token, userId, roles) => {
            return client.getUserModules(token, userId, 0).then(resModule => {
                if (resModule[0] != ["Could not fetch module roles"]) {
                    return Promise.all(
                        resModule.map(_module => {
                            return axios.get(`${MODULES_API}/${_module[1]}/assessments/`, {
                                headers: {"Authorization": `Bearer ${token}`}
                            }).then(resAssessment => {
                                return resAssessment.data
                            }).catch(err => {
                                console.log(err);
                                return ["Could not fetch assessment roles"];
                            })
                        })
                    )
                } else {
                    return ["Could not fetch assessment roles"];
                }
            }).catch(() => {
                return ["Could not fetch assessment roles"];
            })
        },

        getForward: (token, moduleCode, assessmentTitle) => {
            return axios.get(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}/forward`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR";
            })
        },

        getBackward: (token, moduleCode, assessmentTitle) => {
            return axios.get(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}/backward`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR";
            })
        },

        getForwardAndBackward: (token, moduleCode, assessmentTitle) => {
            return Promise.all([
                client.getForward(token, moduleCode, assessmentTitle),
                client.getBackward(token, moduleCode, assessmentTitle)
            ]).then(([forward, backward]) => {
                console.log(forward);
                console.log(backward);
                return {
                    forward: forward,
                    backward: backward
                }
            }).catch(() => {
                return {
                    forward: "ERROR",
                    backward: "ERROR"
                }
            })
        },

        getModuleInfo: (token, moduleCode) => {
            return axios.get(`${MODULES_API}/${moduleCode}`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch((err) => {
                return "ERROR";
            })
        },

        editModuleInfo: (token, moduleCode) => {
            return axios.post(`${MODULES_API}/${moduleCode}`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch((err) => {
                return "ERROR";
            })
        },

        getUserFromId: (token, id) => {
            return axios.get(`${USERS_API}/${id}`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                const data = res.data

                const name = data["forename"] + " " + data["surname"]
                const email = data["email"]

                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },

        getUserAndRoleFromId: (token, id) => {
            return Promise.all([
                axios.get(`${USERS_API}/${id}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                }).then(res => {
                    return res.data;
                }).catch(() => {
                    return "ERROR"
                }),
                axios.get(`${USER_ROLES_API}${id}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                }).then(res => {
                    return res.data;
                }).catch(() => {
                    return "ERROR"
                })
            ]).then(([user, role]) => {
                if (user === "ERROR" || role === "ERROR") {
                    return "ERROR"
                } else {
                    return {
                        id: user.id,
                        name: user.forename + " " + user.surname,
                        email: user.email,
                        role: role
                    }
                }
            })
        },

        getIdFromEmail: (token, id, field) => {
            return axios.get(`${USERS_API}/id/${id}`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                client.addNotification(`${ field } is not a valid email!`, "Server Error", "401", "primary");
                return "ERROR"
            })
        },

        getAssessmentInfo: (token, moduleCode, assessmentTitle) => {
            return axios.get(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },

        getModuleLead: (token, moduleCode) => {
            return axios.get(`${MODULES_API}/${moduleCode}/lead`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },


        getModuleModerator: (token, moduleCode) => {
            return axios.get(`${MODULES_API}/${moduleCode}/moderator`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },

        addStaffMember: (token, code, id) => {
            return axios.put(
                `${MODULES_API}/${code}/staff`,
                {},
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    },
                    params: {
                        newStaffId: id
                    }
                }
            )
                .then(res => {
                    client.addNotification("Staff added!", "Success", "", "success");
                    return res.data;
                })
                .catch(err => {
                    client.errorHandler(err);
                    return "ERROR";
                });
        },

        bunStaffMember: (token, code, id) => {
            return axios.delete(
                `${MODULES_API}/${code}/staff`,
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    },
                    params: {
                        staffId: id
                    }
                }
            )
                .then(res => {
                    client.addNotification("Staff removed!", "Success", "", "success");
                    return res.data;
                })
                .catch(err => {
                    client.errorHandler(err);
                    return "ERROR";
                });
        },




        getModuleStaff: (token, moduleCode) => {
            return axios.get(`${MODULES_API}/${moduleCode}/staff`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },

        createModule: (token, code, name, school, degree, leadId, modId) => {
            return axios.post(`${MODULES_API}/`,
                    {
                        moduleCode: code,
                        moduleName: name,
                        school: school,
                        degreeLevel: degree,
                        moduleLeadId: leadId,
                        moderatorId: modId
                    },
                    {headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }}
                ).then(res => {
                    return res.data;
                }).catch((err) => {
                    client.errorHandler(err);
                    return "ERROR"
                })
        },

        createAssessment: (token, code, paramaters) => {
            return axios.post(`${MODULES_API}/${code}/assessments`,
                paramaters,
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                }).then(res => {
                    client.addNotification("Assessment created!", "Success", "", "success");
                    return res.data;
                }).catch((err) => {
                    client.errorHandler(err);
                    return "ERROR"
                })
        },

        editModule: (token, code, name, school, leadId, modId) => {
            return axios.put(`${MODULES_API}/${code}`,
                {
                        moduleCode: code,
                        moduleName: name,
                        school: school,
                        moduleLeadId: leadId,
                        moderatorId: modId
                    },
                    {headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }}
                ).then(res => {
                    return res.data;
                }).catch((err) => {
                    client.errorHandler(err);
                    return "ERROR"
                })
        },

        editAssessment: (token, code, name, params) => {
            return axios.put(`${MODULES_API}/${code}/assessments/${name}`,
               params,
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                }
            ).then(res => {
                client.addNotification("Assessment edited!", "Success", "", "success");

                return res.data;
            }).catch((err) => {
                client.errorHandler(err);
                return "ERROR"
            })
        },


        createModuleCSV: (token, text) => {
            return axios.post(`${MODULES_API}/import/csv`, text,
                {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "text/plain"
                    }
                }).then(res => {
                    console.log(res.data);
                    if (res.data.length == 0) {
                        client.addNotification("File contains invalid data!", "Server Error", "401", "primary")
                    }
                    return res.data;
                }).catch((err) => {
                    client.errorHandler(err);
                    return "ERROR"
                })
        },

        getAssessmentProgressRole: (token, moduleCode, assessmentTitle) => {
            return axios.get(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}/required-role`, {
                headers: { "Authorization": `Bearer ${token}` }
            }).then(res => {
                return res.data;
            }).catch(() => {
                return "ERROR"
            })
        },

        getAssessmentsAdmin: (token, moduleCode, count) => {
            return axios.get(`${MODULES_API}/${moduleCode}/assessments/`, {
                headers: { "Authorization": `Bearer ${token}` },
                params: {
                    count: count
                }
            }).then((response) => {
                const __response = response.data;
                if (__response.length === 0) {
                    return [["No assessment roles"]];
                }
                else {
                    return __response.map((_response) => {
                        return [moduleCode, _response.title,
                        StatusToString(_response.status), ExamTypeToString(_response.type),
                        ""];
                    })
                }
            }).catch(() => {
                return Array(count).fill(["Could not fetch assessment roles"]);
            })
        },



        increaseStep: (token, moduleCode, assessmentTitle) => {
            return axios.post(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}?direction=FORWARD`, {},
                {
                headers: { "Authorization": `Bearer ${token}`, }
            }).then(res => {
                return res.data;
            }).catch((err) => {
                client.errorHandler(err);
                return "ERROR"
            })
        },

        decreaseStep: (token, moduleCode, assessmentTitle) => {
            return axios.post(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}?direction=BACKWARD`, {},
                {
                    headers: { "Authorization": `Bearer ${token}`, }
                }).then(res => {
                    return res.data;
                }).catch((err) => {
                    client.errorHandler(err);
                    return "ERROR"
                })
        },

        getAssessmentRoles: (token, moduleCode, assessmentTitle) => {
            return Promise.all([
                axios.get(`${MODULES_API}/${moduleCode}/lead`, {
                    headers: {"Authorization": `Bearer ${token}`}
                }).then(res => {
                    return res.data.id;
                }).catch(() => {
                    return "ERROR";
                }),
                axios.get(`${MODULES_API}/${moduleCode}/moderator`, {
                    headers: {"Authorization": `Bearer ${token}`}
                }).then(res => {
                    return res.data.id;
                }).catch(() => {
                    return "ERROR";
                }),
                axios.get(`${MODULES_API}/${moduleCode}/staff`, {
                    headers: {"Authorization": `Bearer ${token}`}
                }).then(res => {
                    return res.data;
                }).catch(() => {
                    return "ERROR";
                }),
                axios.get(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}`, {
                    headers: {"Authorization": `Bearer ${token}`}
                }).then(res => {
                    return res.data;
                }).catch(() => {
                    return {
                        checkerID: "ERROR",
                        setterID: "ERROR",
                        externalExaminerID: "ERROR"
                    };
                })
            ]).then(([resLead, resModerator, resStaff, resAssessment]) => {
                return {
                    checkerID: resAssessment.checkerID,
                    externalExaminerID: resAssessment.externalExaminerId, // FIX PLEASE
                    setterID: resAssessment.setterID,
                    moderatorID: resModerator,
                    moduleLeadID: resLead,
                    staffID: resStaff.map(_res => { return _res.id })
                };
            })
        },

        postEditModule: (token, moduleCode, moduleData) => { 
            // Needs to implement POST request

            // Sends -> User Token & Module Code & Module Data to be pushed
            // Returns -> Success/Failure
            return {};
        },

        postEditAssessment: (token, moduleCode, assessmentCode, assessmentData) => {
            // Needs to implement POST request

            // Sends -> User Token & Module Code & Assessment Code & Assessment Data to be pushed
            // Returns -> Success/Failure
            return {};
        },

        deleteModule: (token, moduleCode) => {
            // Needs to implement DELETE request

            // Sends -> User Token & Module Code
            // Returns -> Success/Failure
            return {};
        },

        deleteAssessment: (token, moduleCode, assessmentId) => {
            // Needs to implement DELETE request

            // Sends -> User Token & Module Code & Assessment Code
            // Returns -> Success/Failure
            return {};
        },

        progressAssessment(token, moduleCode, assessmentId) {
            // Needs to implement POST request

            // Sends -> User Token & Module Code & Assessment Code
            // Returns -> Status
            return {};
        },

        addNotification(message, title, subtitle, background) {
            setNotification(prev => [
                ...prev,
                { text: message, title: title, bg: background, subtitle: subtitle, id: Date.now() }
            ]);

            return notifications;
        },

        removeNotification(id) {
            setNotification(notifications => notifications.filter(n => n.id !== id));

            return notifications;
        },

        getNotifications() {
            return notifications;
        },

        postFeedback(token, moduleCode, assessmentTitle, userId, isForward, body) {
            return axios.post(`${MODULES_API}/${moduleCode}/assessments/${assessmentTitle}/feedback`, {
                feedback: body
            }, {
                headers: { "Authorization": `Bearer ${token}` },
                params: { userId: userId, isForward: isForward }
            }).then(res => {
                return res;
            }).catch(() => {
                return "ERROR ON SUBMISSION"
            });
        },

        postLogin(username, password) {
            axios.post(`${AUTH_API}login`, {}, {
                auth: {
                    username: username,
                    password: password
                }
            }).then((response) => {
                client.login(response.data.token, response.data.user, response.data.roles);
                nav("/"); // temp for now
            })
            .catch((err) => {
                client.errorHandler(err);

            })
            //client.addNotification("hey", "hey");
        },

        logout : () => {
            setToken("");
            setUser({});
            setRole("")
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            localStorage.removeItem("role");
        },

        login : (token, user, role) => {
            setToken(token);
            setUser(user);
            setRole(role);
            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(user))
            localStorage.setItem("role", role);
        },
    }

    useEffect(() => {
        setToken(localStorage.getItem("token") ?? "");
        setUser(JSON.parse(localStorage.getItem("user") ?? "{}"));
        setRole(localStorage.getItem("role") ?? "");
    }, []);

    return (
        <>
            {token === ""
                ? <UnauthorisedRoutes client={client} />
                : <AuthorisedRoutes token={token} user={user} client={client} role={role} />
            }
            <NotificationHandler />
        </>
    )
}
