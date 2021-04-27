import {resetGame} from "../../services/client";

const MancalaButton = ({gameId, text}) => {

    const gameReset = () => {
        resetGame(gameId).then(() => {
            window.location.reload();
        });
    }
    return (
        <button
            onClick={gameReset}
            className="bg-red-200 inline-flex p-5 rounded-lg items-center hover:bg-red-500">
            <span className="title-font font-medium">{text}</span>
        </button>
    )
}
export default MancalaButton;