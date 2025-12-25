import { Route, Routes } from "react-router-dom";

import AdminDashboard from "../dashboardPages/teaching-support-dashboard"
import LoginPage from '../Login.jsx';

import ErrorPage from '../components/errorPage.jsx';


const UnauthorisedRoutes = ({ client }) => {
    //client.login("1", "2");
    return (
        <Routes>
            <Route path="/error" element={< ErrorPage message="Page not found." code="404" />} />
            <Route path="*" element={<LoginPage client={client} />} /* Temporary for testing */ /> 
        </Routes>
    );
};


export default UnauthorisedRoutes;

