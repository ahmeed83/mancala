import {updateGame} from "../services/client";
import {ErrorAlert} from "./common/ErrorAlert";
import {useState} from "react";
import {WinnerAlert} from "./common/WinnerAlert";

export const MancalaPit = ({setGame, color, gameId, pit}) => {
    const [gameWinner, setGameWinner] = useState('');
    const updateMancala = (selectedPit) => {
        const playerTurn = {
            "selectedPit": selectedPit,
            "gameId": gameId
        };
        updateGame(playerTurn)
            .then((res) => {
                setGame(res.data)
                setGameWinner(res.data.playerWinner)
            })
            .catch((err) => {
                ErrorAlert(err);
            });
    };

    // check if the game winner is not empty, otherwise show the winner and end the game.
    if (gameWinner) {
        WinnerAlert(gameWinner);
    }

    return (
        <div onClick={() => updateMancala(pit.pitPlace)}
             className={`${
                 color === "blue" ? "border-blue-400 hover:bg-blue-100" : "border-yellow-400 hover:bg-yellow-100"
             } border-4 cursor-pointer py-12 rounded-lg text-center relative`}>
            <h2 className="text-4xl title-font font-bold text-blue-700">{pit.stones}</h2>
        </div>
    );
}