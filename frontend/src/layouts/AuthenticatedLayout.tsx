import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthProvider";
import { useEffect } from "react";

const AuthenticatedLayout = () => {
    const { isAuthenticated, loading } = useAuth();
    const router = useNavigate();

    useEffect(() => {
      if (!loading && !isAuthenticated) router("/login", { replace: true });  
    }, [isAuthenticated, loading, router]);

    if (loading || !isAuthenticated) {
      return null;
    }

    return (
      <main>
        <Outlet />
      </main>
    );
};

export default AuthenticatedLayout;