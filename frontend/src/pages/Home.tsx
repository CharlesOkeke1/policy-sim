import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router';

const Home = () => {
  const router = useNavigate();
  const socketRef = useRef<WebSocket | null>(null);
  const [currentMonth, setCurrentMonth] = useState(0);
  const [totalRounds, setTotalRounds] = useState(0);
  const [gameData, setGameData] = React.useState<any>({});

  useEffect(() => {
    if (sessionStorage.getItem("authToken") == null) {
      router("/login");
    }

    const socket = new WebSocket("ws://localhost:8081");
    socketRef.current = socket;

    socket.addEventListener("open", () => {
      console.log("Connected to engine");
    });

    socket.addEventListener("message", (event) => {
      console.log("Engine server output =>", JSON.parse(event.data));
    });

    //return () => { socket.close(); };
  }, []);
  const socket = socketRef.current;

  async function getTokens() {
    await fetch("http://localhost:8080/api/tokens", {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem("authToken")}`
      }
    }).then((data) => {
      console.log(data.json());
    })
  }

  async function createGame() {
    const payload = {
        stateName: "Abuja",
        rounds: 50
    };

    try {
      const res = await fetch("http://localhost:8080/api/games", {
          method: "POST",
          headers: {
              //"credentials": "include",
              "Authorization": `Bearer ${sessionStorage.getItem("authToken")}`,
              "Content-Type": "application/json"
          },
          body: JSON.stringify(payload)
      });

      if (!res.ok) {
          throw new Error(`Game creation failed: ${res.status}`);
      }

      const data = await res.json();
      setGameData(data.result);
      setTotalRounds(data.result.game.noOfRounds);

      const initData = {
        ...data.result, 
        authToken: sessionStorage.getItem("authToken")
      }

      oneTest("INITIALIZE_SESSION", initData);
    } catch (error) {
      console.error(error);
    }
  }

  async function playRound() {
    const socket = socketRef.current;

    if (!socket || socket.readyState !== WebSocket.OPEN) {
      console.error("WebSocket is not open");
      return;
    }

    if (currentMonth < totalRounds) {
      const res: any = await new Promise((resolve, reject) => {
        socket.onmessage = (event) => {
          resolve(JSON.parse(event.data));
        };

        socket.send(JSON.stringify({
          type: "GET_ROUND",
          payload: gameData
        }));
      });
      setCurrentMonth(res.data.currentMonth);
      return res;
    }    
  }


  function closeConnection() {
    const socket = socketRef.current;

    if (!socket || socket.readyState !== WebSocket.OPEN) {
      console.error("WebSocket is not open");
      return;
    }

    socket.close();
    console.log("Connection successfully closed");
  }
  
  function oneTest(type: string, data: any) {
    const socket = socketRef.current;

    if (!socket) {
      console.error("WebSocket has not been created");
      return;
    }

    if (socket.readyState === WebSocket.CONNECTING) {
      socket.addEventListener("open", () => {
        socket.send(JSON.stringify({ type, payload: data }));
      }, { once: true });
      return;
    }

    if (socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({ type, payload: data }));
      return;
    }

    console.error("WebSocket is closed or closing:", socket.readyState);
  }

  return (
    <div>
      <div>
        Btn
        <button onClick={() => createGame()}> Start new game</button>
        <button onClick={() => getTokens()}> Get tokens</button>
        <button onClick={() => closeConnection()}> Close Connection</button>
      </div>
      <div>
        <p>
          Current round {
            socketRef.current?.readyState === WebSocket.OPEN
              ? currentMonth
              : "Connection not open"
          }
        </p>
        <button onClick={() => playRound()}> Advance Round</button>
      </div>

    </div>
    
  )
}

export default Home;