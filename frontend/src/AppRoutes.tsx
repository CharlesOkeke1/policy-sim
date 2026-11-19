import { Routes, Route } from "react-router-dom";
import PublicLayout from "./layouts/PublicLayout";
import AuthenticatedLayout from "./layouts/AuthenticatedLayout";

import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";
import { GameInterface } from "./pages/GameInterface";
import Support from "./pages/Support";
import NotFoundPage from "./pages/NotFoundPage";
import ServerError from "./pages/ServerError";

import { useAuth } from "./services/AuthProvider";
import PageLoader from "./shared/PageLoader";
import "../src/styles/Home.css";
import Maintenance from "./pages/Maintenace";

export default function AppRoutes() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="loader_container">
        <PageLoader />
      </div>
    );
  }

  return (
    <Routes>
      {/* Public routes */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<SignUp />} />
        <Route path="/sign-up" element={<SignUp />} />
        <Route path="/login" element={<Login />} />
      </Route>

      {/* Authenticated routes */}
      <Route element={<AuthenticatedLayout />}>
        <Route path="/support" element={<Support />} />
        <Route path="/home" element={<Home />} />
        <Route path="/home/:sessionId" element={<Home />} />
        <Route path="/game" element={<GameInterface />} />
        <Route path="/game/:gameId" element={<GameInterface />} />
      </Route>

      {/* Error routes */}
      <Route path="/not-found" element={<NotFoundPage />} />
      <Route path="/server-error" element={<ServerError />} />

      {/* Catch-all */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}