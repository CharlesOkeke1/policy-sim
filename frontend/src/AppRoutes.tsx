import { Routes, Route } from "react-router-dom";
import PublicLayout from "./layouts/PublicLayout";
import AuthenticatedLayout from './layouts/AuthenticatedLayout';
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";
import { GameInterface } from "./pages/GameInterface";
import { useAuth } from "./services/AuthProvider";
import PageLoader from "./shared/PageLoader";
import '../src/styles/Home.css';
import Support from "./pages/Support";
import NotFoundPage from "./pages/NotFoundPage";
import ServerError from "./pages/ServerError";

export default function AppRoutes() {
  const { user, loading } = useAuth();
  if (loading) {
    return <div className="loader_container"><PageLoader/></div>
  }

  return (
    <Routes>
      {user ? (
        <Route element={<AuthenticatedLayout />}>
          <Route path="/" element={<SignUp />} />
          <Route path="/sign-up" element={<SignUp />} />
          <Route path="/login" element={<Login />} />
          <Route path="/support" element={<Support/>}/>
          <Route path="/home/:sessionId" element={<Home />} />
          <Route path="/home" element={<Home />} />
          <Route path="/game" element={<GameInterface />} />
          <Route path="/game/resume/:gameId" element={<GameInterface />} />
          <Route path="/not-found" element={<NotFoundPage />} />
          <Route path="/server-error" element={<ServerError />} />
        </Route>
      ) : (
        <Route element={<PublicLayout />}>
        <Route path="/" element={<SignUp />} />
        <Route path="/sign-up" element={<SignUp />} />
        <Route path="/login" element={<Login />} />
        <Route path="/support" element={<Support/>}/>
        <Route path="/not-found" element={<NotFoundPage />} />
        <Route path="/server-error" element={<ServerError />} />
        </Route>
      )}
    </Routes>
  );
}