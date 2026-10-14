import React from 'react'

const Home = () => {
  async function getTokens() {
    await fetch("http://localhost:8080/api/tokens", {
        headers: {
            Authorization: `Bearer ${sessionStorage.getItem("authToken")}`
        }
    }).then((data) => {
      console.log(data.json());
    })
  }

  return (
    <div>
      Btn
      <button onClick={() => getTokens()}> Button</button>
    </div>
  )
}

export default Home;