import { useNavigate } from 'react-router';
import './App.css';
import AppRoutes from "./AppRoutes";
import { useEffect, useState } from 'react';

export default function App() {
    const router = useNavigate();
    return (
        <AppRoutes/>
    );
}