import { Outlet } from "react-router-dom";
// import Navbar from "../components/Navbar";
// import SessionWarning from "../components/SessionWarning";

export default function AuthenticatedLayout() {
  return (
    <>
      {/* <Navbar /> */}

      <main>
        <Outlet />
      </main>

      {/* <SessionWarning /> */}
    </>
  );
}