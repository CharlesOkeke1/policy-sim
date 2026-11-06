import { FaPlus } from 'react-icons/fa6';
import type { ChildProps } from '../pages/Home';
import { useAuth } from '../services/AuthProvider';
import { CustomIcon } from '../shared/Icon';
import './MyGames.css';
import './NewGame.css';
import MyGameCard from '../shared/MyGameCard';
import type { ApiResponse, StatePreview } from '../dtos/Models';
import { customFetch } from '../utils/FetchWrapper';
import { act, useEffect, useState } from 'react';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import PageLoader from '../shared/PageLoader';
import { API_DELAY, getStates, resumeGameInProgress } from '../services/ApiService';
import { useNavigate } from 'react-router';


export const MyGames = ({setStep}: ChildProps) => {
  const {user} = useAuth();
  const [gamesList, setGamesList] = useState<any[]>();
  const [gamesCount, setGamesCount] = useState<number>(0);
  const [stateList, setStateList] = useState<StatePreview[]>();
  const [activePage, setActivePage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [maxPage, setMaxPage] = useState(1);
  const router = useNavigate();
  const pageClass = "current_page";
  const PAGE_SIZE = 6;
  const [pageStop, setPageStop] = useState(PAGE_SIZE);

  useEffect(() => {
    getPaginatedGames(0, PAGE_SIZE);
    getStatesPreview();
  }, []);

  useEffect(() => {
    getMaxPage();
  }, [maxPage]); 

  async function getStatesPreview() {
    const res: StatePreview[] = await getStates();
    setStateList(res);
  }

  async function getPaginatedGames(start: number, stop: number) {
    setLoading(true);
    
    const url = `http://localhost:8080/api/games/${user?.id}/paginated?start=${start}&stop=${stop}`;
    const res: ApiResponse<any> = await customFetch({method: "GET", url: url});
    //console.log(res);

    setGamesList(res.result.games);
    console.log(res.result);
    setGamesCount(res.result.gameCount);
    setMaxPage(Math.floor(res.result.gameCount / PAGE_SIZE) + 1);

    //Just for the visual effect of the presence of a load but not too quick.
    // Too quick, it looks like a flash. Too slow, user is frustrated
    setTimeout(()=> {
      setLoading(false);
    }, API_DELAY);
  }

  function getMaxPage() {
    const max = Math.floor(gamesCount / PAGE_SIZE) + 1;
    setMaxPage(max);
  }

  function incrementPage() {
    console.log("Before Increment Active page => ", activePage);
    console.log("Max page before increment =>", maxPage);
    
    if (maxPage > activePage) {
      const nextPage = activePage + 1;
      setActivePage(nextPage);
      console.log("Incrememnt Active page => ", nextPage);

      const start = (nextPage - 1) * PAGE_SIZE;
      const stop = nextPage * PAGE_SIZE;
      setPageStop(stop);

      getPaginatedGames(start, stop);
    }  
  }
  
  function decrementPage() {
    console.log("Before Decrement Active page => ", activePage);
    if (activePage > 1) {
      const nextPage = activePage - 1;
      setActivePage(nextPage);
      console.log(" Decrement Active page => ", nextPage);

      const start = (nextPage - 1) * PAGE_SIZE;
      const stop = nextPage * PAGE_SIZE;
      setPageStop(stop);

      getPaginatedGames(start, stop);
    }
  }

  return (
    <>
      {
        loading ?
        <PageLoader/>
        :
        (
          gamesList ?
          <div className='my_games'>
            <div className='dashboard_row'>
              <div className='dashboard_text'>
                <p className='dashboard_main'>My games</p>
                <p style={{textWrap: 'wrap', maxWidth: '450px'}}>Every state you have governed: how it ended, and where you can pick it back up.</p>
              </div>
              <div className='dashboard_right'>
                <div className='calendar'>
                  <input className='date_input' type='date'/>
                  {/* <img src="" className='date_icon'/> */}
                </div>
                <div className='new_game_btn' onClick={() => setStep(2)}>
                  <div className='icon_bg'>
                    <div style={{marginTop: '3px'}}>
                      <CustomIcon icon={FaPlus} fill='var(--text-primary)' strokeWidth={20} variant="circular" size={"var(--text-stat)"}/>
                    </div>
                  </div>
                  New game
                </div>
              </div>
            </div>
            <div className='game_page_row'>
              <p>Showing {(activePage - 1) * PAGE_SIZE} to {pageStop} games</p>
              <div className='page_row'>
                <div style={{marginTop: '3px', cursor: "pointer"}} onClick={() => decrementPage()}>
                  <CustomIcon icon={FiChevronLeft} stroke='var(--text-muted)' fill='none' strokeWidth={1} variant="circular" size={"var(--text-stat)"}/>
                </div>
                <div className={pageClass}>
                  {activePage}
                </div>
                {
                  activePage === maxPage ?
                  ""
                  :
                  <div style={{marginTop: '3px', cursor: "pointer"}} onClick={() => incrementPage()}>
                    <CustomIcon icon={FiChevronRight} stroke='var(--text-muted)' fill='none' strokeWidth={1} variant="circular" size={"var(--text-stat)"}/>
                  </div>
                }    
              </div>
            </div>
            <div className='games'>
              {
                gamesList ? 
                gamesList?.map((game: any) => (
                  <MyGameCard
                    stateId={game.stateId}
                    gameId={game.id}
                    gameDifficulty={game.difficulty}
                    gameSeed={game.seed}
                    gameStartDate={game.startDate}
                    isGameCompleted={game.completed}
                    currentMonth={game.rounds?.at(-1)?.currentMonth ?? 0}
                    score={game.score}
                    rank={game.rank}
                    totalRounds={game.noOfRounds}
                    stateList={stateList ? stateList : []}
                    roundList={game.rounds}
                    click={() => router(`/game/resume/${game.id}`, {
                      state: {
                        game: game,
                        isInit: false,  
                        userLastRound: game.rounds.filter((round: any) => round.currentMonth === game.rounds.at(-1).currentMonth &&
                        game.stateId === round.stateId)[0],
                        prevLastRound: game.rounds.filter((round: any) => round.currentMonth === game.rounds.at(-1).currentMonth - 1 &&
                        game.stateId === round.stateId)[0],
                        state: stateList?.filter((state) => state.id === game.stateId) [0]                  
                      }
                    })}
                  />
                ))
                :
                <div></div>
              }
            </div>  
          </div>
          :
          <div>Nothing to see here</div>          
        )        
      }
    </> 
  )
}
