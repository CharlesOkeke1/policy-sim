import './NewGame.css';
import { StateCard } from '../shared/StateCard';
import rightArrow from '../assets/arrow-right.svg';
import { useNavigate } from 'react-router';
import { DifficultyCard } from '../shared/DifficultyCard';
import { useEffect, useState } from 'react';
import { LengthElement } from '../shared/LengthElement';
import diceIcon from '../assets/dice.svg';

type Length = {
  color: string;
  fontWeight: string;
  bgColor: string;
}

type Difficulty = {
  difficulty: string;
  explanation: string
}

export const NewGame = () => {
  const router = useNavigate();
  const [activeDifficulty, setActiveDifficulty] = useState<number>(0);
  const [activeState, setActiveState] = useState<number>(0);
  const [statesList, setStatesList] = useState<any[]>([]);
  const [difficultyList, setDifficultyList] = useState<Difficulty[]>([]);
  const [chosenState, setChosenState] = useState<any>(null);
  const [chosenDifficulty, setChosenDifficulty] = useState<Difficulty>();
  const [activeLength, setActiveLength] = useState<number>(0);
  const [chosenLength, setChosenLength] = useState(0);
  const [seed, setSeed] = useState(0);


  useEffect(() => {
    getStatesPreview();
    getDifficulty();
  }, []);

  useEffect(() => {
    setChosenState(statesList[activeState]);
    setChosenDifficulty(difficultyList[activeDifficulty]);
  }, [statesList, activeState, difficultyList, activeDifficulty]);

  async function getStatesPreview() {
    const api = "http://localhost:8080/api/games/states";

    fetch(api, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${sessionStorage.getItem("authToken")}`
      }
    })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      setStatesList(data.result);
    })
  }


  async function getDifficulty() {
    const api = "http://localhost:8080/api/games/difficulty";

    fetch(api, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${sessionStorage.getItem("authToken")}`
      }
    })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      setDifficultyList(data.result);
    })
  }

  function generateRandomSeed() {
    const s = Math.floor(Math.random() * 1000) + 1;
    setSeed(s);
  }

  return (
    <div className='newgame'>
      <div className='dashboard_row'>
        <div className='dashboard_text'>
          <p className='dashboard_main'>New game</p>
          <p style={{textWrap: 'wrap'}}>Pick the city you will govern. The computer runs the other seven.</p>
        </div>
      </div>

      <div className='new_game_block'>
        <div className='new_game_left'>

          <div className='choose_city'>
            <div>
              <div style={{display: 'flex', gap: 'var(--gap-sm)', alignItems: 'center'}}>
                <p className='new_game_step_number'>01</p>
                <p className='chart_header'>Choose your city</p>
              </div>
              <p className='chart_sub_header'>Each city starts with its own economy. 
                <a style={{fontWeight: 'var(--weight-semibold)'}}>These baseline values represent the state's initial conditions before seeding.</a></p>
            </div>

            <div className='states'>
              {
                statesList.map((state, index) => (
                  <StateCard luck={state.luck} stateName={state.name} population={state.population} productivity={state.productivity}
                  stability={state.stability} step={index} activeStep={activeState} onClick={() => setActiveState(index)}/>
                ))
              }
            </div>
          </div>  

          <div className='choose_city'>
            <div>
              <div style={{display: 'flex', gap: 'var(--gap-sm)', alignItems: 'center'}}>
                <p className='new_game_step_number'>02</p>
                <p className='chart_header'>Difficulty</p>
              </div>
              <p className='chart_sub_header'>How well the computer governs the other seven cities.</p>
            </div>
            <div className='difficulty_row'>
              {
                difficultyList.map((diff, index) => (
                  <DifficultyCard difficulty={diff.difficulty} explanation={diff.explanation} 
                  step={index} activeStep={activeDifficulty} onClick={() => setActiveDifficulty(index)}/>
                ))
              }
            </div>
          </div>

          <div className='choose_city'>
            <div style={{display: 'flex', gap: 'var(--gap-sm)', alignItems: 'center'}}>
              <p className='new_game_step_number'>03</p>
              <p className='chart_header'>Length and seed</p>
            </div>
            <p className='chart_sub_header'>One round is one month. Pick a preset or set your own length. The same seed always produces the same starting world.</p>
            <div className='length_and_seed_row'>
              <div className='length_container'>
                <div className='length_obj'>
                  <LengthElement step={0} activeStep={activeLength} years={10} onClick={() => {setActiveLength(0); setChosenLength(10)}}/>
                  <LengthElement step={1} activeStep={activeLength} years={20} onClick={() => {setActiveLength(1); setChosenLength(20)}}/>
                  <LengthElement step={2} activeStep={activeLength} years={30} onClick={() => {setActiveLength(2); setChosenLength(30)}}/>
                  <input 
                    className='custom_length' 
                    placeholder='Custom...'
                    value={chosenLength}
                    type='number'
                    onChange={(event) => {
                      const val = event.target.value;
                      setChosenLength(Number(val));
                    }}
                  />
                </div>
              </div>
              <div className='seed_container'>
                <p style={{fontSize: '14px', color: 'var(--text-secondary)'}}>Seed</p>
                <div className='seed_row'>
                  <div className='seed_input_container'>
                    <input 
                    placeholder='Random'
                    className='seed_input'
                    value={seed}
                    onChange={(event) => {
                      const val = event.target.value;
                      setSeed(Number(val));
                    }}
                    />
                  </div>
                  <div className='random_seed_container' onClick={() => generateRandomSeed()}>
                    <img src={diceIcon}/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className='new_game_right'>
          <div className='your_game'>
            <p className='recent_games_text'>Your game</p>
            <div className='chosen_state_container'>
              <div className='state_initials'>
                <p style={{fontWeight: 'var(--weight-semibold)'}}>
                  { chosenState?.initials }
                </p>
              </div>
              <div>
                <p className='chosen_state_name'>
                  { chosenState?.name }
              </p>
                <p className='chosen_state_diff'>
                  { chosenState?.luck }
                </p>
              </div>
            </div>
            <div className='your_game_summary_item'>
              <p>Difficulty</p>
              <p>{ chosenDifficulty?.difficulty }</p>
              
            </div>
            <div className='your_game_summary_item'>
              <p>Length</p>
              <p>240 months · 20 years</p>
            </div>
            <div className='your_game_summary_item'>
              <p>Rivals</p>
              <p>7 computer governors</p>
            </div>
            <div className='your_game_summary_item'>
              <p>Seed</p>
              <p>{seed}</p>
            </div>
            <div className='startGameBtnContainer'>
              <button className='startGameBtn'>
                <p>Start Game</p>
              </button>
              <img src={rightArrow} className='icon'/>
            </div>
            <p className='button_supporting'>Your game saves every month. You can leave and resume it from 
              <a style={{textDecoration: 'underline', fontWeight: 'var(--weight-semibold)'}}
              > My Games.</a>
            </p>
          </div>
        </div>
      </div>            
    </div>
  )
}
