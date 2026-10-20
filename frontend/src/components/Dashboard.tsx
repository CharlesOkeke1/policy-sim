import React, { use, useEffect, useRef, useState } from 'react'
import plusIcon from '../assets/add.svg';
import dropdownIcon from '../assets/dropdown.svg';
import './Dashboard.css';
import { StatCard } from '../shared/StatCard';
import { MiniStatCard } from '../shared/MiniStatCard';
import { RecentGame } from '../shared/RecentGame';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  BarChart, 
  Bar,
  Pie, 
  Cell
} from 'recharts';
import { PieChartLegend } from '../shared/PieChartLegend';
import { RecentActivity } from '../shared/RecentActivity';
import { useNavigate } from 'react-router';

export interface EconomyData {
    name: string;
    population: number;
    productivity: number;
    priceLevel: number;
    governmentDebt: number;
    stability: number;
}

interface userIdInterface {
    id?: number;
}

//Should take user as prop
export const Dashboard = ({id}: userIdInterface) => {
    const [userGames, setUserGames] = useState<any[]>();
    const router = useNavigate();
    const outcomes = [
        { name: 'Completed', value: 12 },
        { name: 'In Progress', value: 2 },
        { name: 'Abandoned', value: 3 },
    ];

    useEffect(() => {
        getUserData();
    }, []);

    async function getUserData() {
        const api = `http://localhost:8080/api/users${sessionStorage.getItem("userId")}/games`;

        await fetch(api, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${sessionStorage.getItem("authToken")}`
            }
        })
        .then((res) => {
            return res.json();
        })
        .then((data) => {
            setUserGames(data.result);
            console.log(data.result);
        })
        .catch((error) => {
            console.error(error);
        });
    }

    const bars = [
        { name: '#1', count: 4},
        { name: '#2', count: 2},
        { name: '#3', count: 1},
        { name: '#4', count: 1},
        { name: '#5', count: 0},
        { name: '#6', count: 1},
        { name: '#7', count: 0},
        { name: '#8', count: 0},
    ];

    function formatDate(date: string): string {
        return new Date(date).toLocaleDateString("en-GB", {
            day: "2-digit",
            month: "short",
            timeZone: "UTC"
        });
    }

    const COLORS = ['#16A34A', '#D97706', '#EF4444'];

    //Dummy data
    const data: EconomyData[] = [
        { name: "1", population: 89.0, productivity: 95.6, priceLevel: 78.3, governmentDebt: 82.0, stability: 65.4 },
        { name: "2", population: 75.0, productivity: 82.4, priceLevel: 62.5, governmentDebt: 45.0, stability: 78.2 },
        { name: "3", population: 57.0, productivity: 91.3, priceLevel: 69.6, governmentDebt: 56.0, stability: 69.5 },
        { name: "4", population: 81.0, productivity: 74.8, priceLevel: 59.2, governmentDebt: 29.0, stability: 81.6 },
        { name: "5", population: 50.0, productivity: 68.2, priceLevel: 55.7, governmentDebt: 31.0, stability: 72.8 },
        { name: "6", population: 48.0, productivity: 86.1, priceLevel: 65.4, governmentDebt: 27.0, stability: 84.3 },
        { name: "7", population: 58.0, productivity: 71.5, priceLevel: 57.8, governmentDebt: 34.0, stability: 63.7 },
        { name: "8", population: 30.0, productivity: 77.9, priceLevel: 60.6, governmentDebt: 22.0, stability: 88.1 },
        { name: "9", population: 32.0, productivity: 89.2, priceLevel: 67.3, governmentDebt: 49.0, stability: 70.6 },
        { name: "10", population: 37.0, productivity: 62.7, priceLevel: 53.9, governmentDebt: 18.0, stability: 75.9 },
    ];

    function toNewGame() {
        sessionStorage.setItem("current_step", JSON.stringify(2));
        router("/home");
    }

    return (
        <div className='dashboard'>
            <div className='dashboard_row'>
                <div className='dashboard_text'>
                    <p className='dashboard_main'>Dashboard</p>
                    <p style={{textWrap: 'wrap'}}>How your games are going, and how the economies you ran ended up.</p>
                </div>

                <div className='dashboard_right'>
                    <div className='calendar'>
                        <input className='date_input' type='date'/>
                        <img src={dropdownIcon} className='date_icon'/>
                    </div>
                    <div className='new_game_btn' onClick={() => toNewGame()}>
                        <div className='icon_bg'>
                            <img src={plusIcon} className='plusIcon'/>
                        </div>
                        Start a new game
                    </div>
                </div>
            </div>

            <div className='stats'>
                <StatCard label='Games played' stat={userGames?.length ?? "N/A"} supporting='Up 2 vs previous 30 days'></StatCard>
                <StatCard label='Average score' stat='TODO' supporting='Up 4.3 vs previous 30 days'></StatCard>
                <StatCard label='Finished ranked #1' stat='TODO' supporting='Up 1 vs previous 30 days'></StatCard>
                <StatCard label='Months governed' stat='TODO' supporting='About stat/12 simulated years'></StatCard>
            </div>

            <div className='previous_games'>
                <div className='chart_container'>
                    <div>
                        <p className='chart_header'>Final score and stability, by game</p>
                        <p className='chart_sub_header'>Your last 12 games, oldest first. Both on a 0–100 scale.</p>
                    </div>
                    <div style={{ width: '100%', height: 300}}>
                        <ResponsiveContainer>
                            <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name"/>
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Line type="monotone" name="Population" dataKey="population" stroke="#8884d8"/>
                            <Line type="monotone" name="Productivity" dataKey="productivity" stroke="#82ca9d"/>
                            <Line type="monotone" name="Price Level" dataKey="priceLevel" stroke="#ffc658"/>
                            <Line type="monotone" name="Government Debt" dataKey="governmentDebt" stroke="#ff8042"/>
                            <Line type="monotone" name="Stability" dataKey="stability" stroke="#0088fe"/>
                            </LineChart>
                        </ResponsiveContainer>
                    </div>    
                </div>
                <div className='recent_games'>
                    <div className='recent_games_header'>
                        <p className='recent_games_text'>Recent games</p>
                        <p className='view_all'>View all</p>
                    </div>
                    {
                        userGames ? 
                        userGames.slice(0,5)?.map((game: any) => (
                            <RecentGame
                            content={game.completed ? "Completed" : "In progress"}
                            name={game.stateName}
                            currentMonth={game.rounds?.at(-1)?.currentMonth}
                            totalRounds={game.noOfRounds}
                            startDate={formatDate(game.startDate)}
                            score={game.score ? game.score.toFixed(2) : "N/A"}
                        />
                        )) : 
                        <div className='recent_games_container'>
                            <p>No recent games to show</p>
                        </div> 
                    }                                       
                </div>
            </div>

            <div className='previous_games'>
                <div className='mini_stats_container'>
                    <div>
                        <p className='chart_header'>How your economies ended</p>
                        <p className='chart_sub_header'>Averages at the final month of your completed games, against the previous 30 days.</p>
                    </div>
                    <div className='stats'>
                        <MiniStatCard label='Inflation (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                        <MiniStatCard label='Unemployment' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                        <MiniStatCard label='Debt / GDP' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                        <MiniStatCard label='Stability' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                    </div>
                    <div className='stats'>
                        <MiniStatCard label='Real growth (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                        <MiniStatCard label='Average final rank' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                    </div>
                </div>
                <div className='recent_games'>
                    <div className='recent_games_header'>
                        <p className='recent_games_text'>Recent activity</p>
                        <p className='view_all'>View all</p>
                    </div>
                    <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                    <RecentActivity main='Saved Manchester at month 142' supporting='Inflation running above target' type='In progress' time='2h ago'/>
                    <RecentActivity main='Abandoned Plymouth at month 61' supporting='Stability fell to 31' type='Abandoned' time='2h ago'/>
                    <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                    <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                </div>
            </div>

            <div className='game_outcomes'>
                <div>
                    <p className='chart_header'>Game outcomes</p>
                    <p className='chart_sub_header'>Where your {'totalNumberOfGames'} games stand, and where the {'totalNumberOfCompletedGames'} completed ones finished.</p>
                </div>
                <div className='outcomes_charts'>
                    <div className='outcomes_pie_chart'>
                        <div style={{ width: '40%', height: '100%', position: 'relative'}}>
                            <ResponsiveContainer>
                                <PieChart>
                                <Pie
                                    data={outcomes}
                                    type='basis'
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={100}
                                    innerRadius={50}
                                >
                                    {outcomes.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} strokeWidth={3}/>
                                    ))}
                                </Pie>
                                <Tooltip />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div className='pie_chart_legend'>
                            <PieChartLegend type='Completed' value={12}  total={17}/>
                            <PieChartLegend type='Abandoned' value={2}  total={17}/>
                            <PieChartLegend type='In progress' value={3}  total={17}/>
                        </div>
                    </div>
                    <div className='outcomes_bar_chart'>
                        <div style={{ width: '100%', height: '100%', position: 'relative'}}>
                            <ResponsiveContainer>
                                <BarChart data={bars} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="name" />
                                    <YAxis />
                                    <Tooltip />
                                    <Legend />
                                    <Bar dataKey="count" name='Positions' fill="#2563EB" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </div>                
            </div>
        </div>
    )
}
