import Axios from 'axios';
import {CONTENT_TYPE_JSON_VALUE, CREATE_GAME, DELETE_GAME, UPDATE_GAME} from "./constants";

const checkStatus = (response) => {
    if (response.status >= 200) {
        return response;
    } else {
        let error = new Error(response.statusText);
        error.response = response;
        response.then((e) => {
            error.error = e;
        });
        return Promise.reject(error);
    }
};

export const createGame = () =>
    Axios.get(CREATE_GAME, {
        headers: {
            'Content-Type': CONTENT_TYPE_JSON_VALUE,
        },
    }).then(checkStatus);

export const resetGame = (gameId) =>
    Axios.delete(DELETE_GAME + gameId, {
        headers: {
            'Content-Type': CONTENT_TYPE_JSON_VALUE,
        },
    }).then(checkStatus);

export const updateGame = (playerTurn) =>
    Axios.post(UPDATE_GAME, playerTurn, {
        headers: {
            'Content-Type': CONTENT_TYPE_JSON_VALUE,
        },
    }).then(checkStatus);