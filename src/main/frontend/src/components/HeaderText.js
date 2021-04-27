export const HeaderText = ({player}) => {
    return (
        <div className="flex justify-around p-8 text-3xl text-gray-900 font-medium title-font mb-2">
            <a className="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
               href="https://www.linkedin.com/in/ahmedaziz83">
                <h2>Mancala Game Ahmed Aziz for bol.com project</h2>
            </a>
            <div>
                {player === "PLAYER_1" ? (
                    <div className="bg-blue-100 inline-flex p-5 rounded-lg items-center">
                        <span className="title-font text-blue-600 font-medium">
                                Player 1 turn
                        </span>
                    </div>
                ) : (
                    <div className="bg-blue-100 inline-flex p-5 rounded-lg items-center">
                        <span className="title-font text-yellow-400 font-medium">
                                Player 2 turn
                        </span>
                    </div>
                )}
            </div>
        </div>
    );
}