import { Routes, Route } from "react-router-dom";
import PublicLayout from "./layouts/PublicLayout";
import AuthenticatedLayout from './layouts/AuthenticatedLayout';
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";

export default function AppRoutes() {
  return (
    <Routes>
      {/* Public routes */}
      <Route element={<PublicLayout/>}>
        <Route path="/" element={<SignUp/>}/>
        <Route path="/login" element={<Login/>}/>
        <Route path="/signup" element={<SignUp/>}/>
      </Route>

      {/* Authenticated routes */}
      <Route element={<AuthenticatedLayout/>}>
        <Route path="/home" element={<Home/>}/>
      </Route>
    </Routes>
  );
}