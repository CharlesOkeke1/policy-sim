import { Outlet } from "react-router-dom";

export default function AuthenticatedLayout() {
  return (
    <>
      <main>
        <Outlet />
      </main>
    </>
  );
}