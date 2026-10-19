import { Routes, Route } from "react-router-dom";
import PublicLayout from "./layouts/PublicLayout";
import AuthenticatedLayout from './layouts/AuthenticatedLayout';
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";
import { NewGame } from "./components/NewGame";
import { MyGames } from "./components/MyGames";
import { Dashboard } from "./components/Dashboard";

export default function AppRoutes() {
  return (
    <Routes>
      {/* Public routes */}
      <Route element={<PublicLayout/>}>
        <Route path="/" element={<SignUp/>}/>
        <Route path="/sign-up" element={<SignUp/>}/>
        <Route path="/login" element={<Login/>}/>
      </Route>

      {/* Authenticated routes */}
      <Route element={<AuthenticatedLayout/>}>
        <Route path="/home" element={<Home/>}>
          {/* <Route path="new-game" element={<NewGame/>}/>
          <Route path="my-games" element={<MyGames/>}/>
          <Route path="dashboard" element={<Dashboard/>}/> */}
        </Route>
      </Route>
    </Routes>
  );
}