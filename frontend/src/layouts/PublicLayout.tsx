import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthProvider";
import { useEffect } from "react";

export default function PublicLayout() {
  const { isAuthenticated, loading } = useAuth();
  const router = useNavigate();

  useEffect(() => {
    if (!loading && isAuthenticated) router("/home", { replace: true });
  }, [loading, isAuthenticated, router]);

  if (loading || isAuthenticated) return null;

  return (
    <>
      <Outlet />
    </>
  );
}