package com.bol.mancala.controllers;

import com.bol.mancala.entities.MancalaEntity;
import com.bol.mancala.model.MancalaGame;
import com.bol.mancala.model.PitPlace;
import com.bol.mancala.model.PlayTurnRequest;
import com.bol.mancala.repositories.MancalaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_A;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_B;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_BIG;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_C;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_D;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_E;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_F;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_BIG;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_U;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_V;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_W;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_X;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_Y;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_Z;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class MancalaApiTest {

    /**
     * Path to Mancala API create endpoints.
     */
    private static final String CREATE_GAME_PATH = "/mancala/api/v1/create-game";
   
    /**
     * Path to Mancala API reset endpoints.
     */
    private static final String DELETE_GAME_PATH = "/mancala/api/v1/delete-game/";

    /**
     * Path to Mancala API update endpoints.
     */
    private static final String UPDATE_GAME_PATH = "/mancala/api/v1/update-game";

    /**
     * Mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * MancalaEntity Repository
     */
    @MockBean
    MancalaRepository mancalaRepository;

    /**
     * mockUUID.
     */
    private UUID mockUUID;

    /**
     * mancalaEntity/
     */
    private MancalaEntity mancalaEntity;

    @BeforeEach
    void setUp() {
        mockUUID = UUID.fromString("09f5cc11-ec7c-4344-8022-5b6ab11546ea");
        Integer startStoneAmount = 6;
        mancalaEntity = new MancalaEntity(mockUUID, startStoneAmount);
    }

    @Test
    @DisplayName("It should create a new Mancala gave Successfully via the endpoint")
    void itShouldCreateMancalaGame() throws Exception {
        // Given
        given(mancalaRepository.save(any(MancalaEntity.class)))
                .willReturn(mancalaEntity.toBuilder().playerId(1).build());
        MancalaGame mancalaGame = getMancalaGame("createNewMancala.json");
        // When
        this.mockMvc.perform(get(CREATE_GAME_PATH))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should delete a Mancala gave Successfully via the endpoint")
    void itShouldDeleteMancalaGame() throws Exception {
        // Given
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity));
        // When
        this.mockMvc.perform(delete(DELETE_GAME_PATH + mockUUID))
                .andDo(print())
        // Then
                .andExpect(status().isAccepted());
        verify(mancalaRepository).delete(mancalaEntity);
    }

    @Test
    @DisplayName("It should update Successfully when player 1 select PLAYER_ONE_PIT_B." +
            " All the next pits will be updated with one stone including PLAYER_TWO_PIT_U." +
            " Next turn will be for Player 2")
    void itShouldMakePlayerOneTurnSuccessfulFromBtoU() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_B);
        givenPlayer_1();
        MancalaGame mancalaGame = getMancalaGame("Player1TurnFromBtoU.json");
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when player 1 select PLAYER_ONE_PIT_A." +
            " All the next pits will be updated with one stone including PLAYER_ONE_PIT_BIG." +
            " Player 1 will have one more turn")
    void itShouldMakePlayerOneHaveOneMoreTurn() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_A);
        givenPlayer_1();
        MancalaGame mancalaGame = getMancalaGame("Player1TurnFromAtoBig.json");
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
        // Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when player 1 skip opponent big pit" +
            " and select PLAYER_ONE_PIT_F with 8 stones in it." +
            " All the next pits will be updated with one stone including PLAYER_ONE_PIT_A." +
            " PLAYER_TWO_PIT_BIG will not be updated and remain 0. Next turn will be for Player 2")
    void itShouldMakePlayerOneSkipOpponentBigPit() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_F);
        MancalaGame mancalaGame = getMancalaGame("Player1TurnFromFtoASkipOpponentBig.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? 
                                                                                                         p.toBuilder().stones(8).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(1)
                                                                                   .build()));

        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
        // Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }
    
    @Test
    @DisplayName("It should update Successfully when player 1 captures opponent stones" +
            " and select PLAYER_ONE_PIT_B with 2 stones in it." +
            " AND PLAYER_ONE_PIT_D has 0 stones AND PLAYER_TWO_PIT_W has 5 stones." +
            " Player 1 will capture the stones from PLAYER_TWO_PIT_W and add them to its PLAYER_ONE_PIT_BIG" +
            " with the last stone that he has from PLAYER_ONE_PIT_D. PLAYER_ONE_PIT_BIG will be int total 6" +
            ". Next turn will be for Player 2")
    void itShouldMakePlayerOneCaptureOpponentStones() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_B);
        MancalaGame mancalaGame = getMancalaGame("Player1TurnFromBtoDCaptureW.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(2).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(5).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(1)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when all pits of player 1 is empty from stones and he/she has more stones" +
            " in his big pit than the opponent. Game will end" )
    void itShouldDeterminePlayerOneIsWinner() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_F);
        MancalaGame mancalaGame = getMancalaGame("Player1Winner.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_A ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_C ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(20).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(14).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(1)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }
    
    @Test
    @DisplayName("It should update Successfully when all pits of player 1 is empty from stones and player2 has more stones" +
            " in his big pit than the opponent. Game will end" )
    void itShouldDeterminePlayerTwoIsWinnerWhenPlayer1EndsGame() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_F);
        MancalaGame mancalaGamePlayer2Wins = getMancalaGame("player2WinnerWhenPlayer1EndsGame.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_A ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_C ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(5).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(20).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(14).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(1)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGamePlayer2Wins)));
    }

    @Test
    @DisplayName("It should fail when player one uses opponent pit")
    void itShouldFailWhenPlayerOneUsesOpponentPit() throws Exception {
        // Given
        givenPlayer_1();
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_X);
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.errorMessage", Matchers.is("Please choose your own pit!")))
                .andExpect(jsonPath("$.httpStatus", Matchers.is(403)));
    }

    @Test
    @DisplayName("It should update Successfully when player 2 select PLAYER_TWO_PIT_W." +
            " All the next pits will be updated with one stone including PLAYER_ONE_PIT_B." +
            " Next turn will be for Player 1")
    void itShouldMakePlayerTwoTurnSuccessfulFromWtoB() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_W);
        givenPlayer_2();
        MancalaGame mancalaGame = getMancalaGame("Player2TurnFromWtoB.json");
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when player 2 select PLAYER_TWO_PIT_U." +
            " All the next pits will be updated with one stone including PLAYER_TWO_PIT_BIG." +
            " Player 2 will have one more turn")
    void itShouldMakePlayerTwoHaveOneMoreTurn() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_U);
        givenPlayer_2();
        MancalaGame mancalaGame = getMancalaGame("Player2TurnFromUtoBig.json");
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when player 2 skip opponent big pit" +
            " and select PLAYER_TWO_PIT_Z with 8 stones in it." +
            " All the next pits will be updated with one stone including PLAYER_TWO_PIT_U." +
            " PLAYER_ONE_PIT_BIG will not be updated and remain 0. Next turn will be for Player 1")
    void itShouldMakePlayerTwoSkipOpponentBigPit() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_Z);
        MancalaGame mancalaGame = getMancalaGame("Player2TurnFromZtoUSkipOpponentBig.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ?
                                                                                                         p.toBuilder().stones(8).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(2)
                                                                                   .build()));

        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                // Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when player 2 captures opponent stones" +
            " and select PLAYER_TWO_PIT_W with 2 stones in it." +
            " AND PLAYER_TWO_PIT_Y has 0 stones AND PLAYER_ONE_PIT_B has 5 stones." +
            " Player 2 will capture the stones from PLAYER_ONE_PIT_B and add them to its PLAYER_TWO_PIT_BIG" +
            " with the last stone that he has from PLAYER_TWO_PIT_Y. PLAYER_TWO_PIT_BIG will be int total 6" +
            ". Next turn will be for Player 1")
    void itShouldMakePlayerTwoCaptureOpponentStones() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_W);
        MancalaGame mancalaGame = getMancalaGame("Player2TurnFromWtoYCaptureB.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ?
                                                                                                         p.toBuilder().stones(2).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ?
                                                                                                         p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ?
                                                                                                         p.toBuilder().stones(5).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(2)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGame)));
    }

    @Test
    @DisplayName("It should update Successfully when all pits of player 2 is empty from stones and he/she has more stones" +
            " in his big pit than the opponent. Game will end" )
    void itShouldDeterminePlayerTwoIsWinner() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_Z);
        MancalaGame mancalaGamePlayer2Wins = getMancalaGame("Player2Winner.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_A ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_C ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(20).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(14).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(2)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGamePlayer2Wins)));
    }

    @Test
    @DisplayName("It should update Successfully when all pits of player 2 is empty from stones " +
            "and player1 has more stones in his big pit than the opponent. Game will end" )
    void itShouldDeterminePlayerOneIsWinnerWhenPlayerTwoEndsGame() throws Exception {
        // Given
        PlayTurnRequest requestBody = selectPit(PLAYER_TWO_PIT_Z);
        MancalaGame mancalaGamePlayer1Wins = getMancalaGame("player1WinnerWhenPlayer2EndsGame.json");
        given(mancalaRepository.findById(mockUUID)).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                   .pits(mancalaEntity.getPits()
                                                                                                 .stream()
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_A ? p.toBuilder().stones(5).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_C ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(0).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ? p.toBuilder().stones(1).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(20).build() : p)
                                                                                                 .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(14).build() : p)
                                                                                                 .collect(Collectors.toList()))
                                                                                   .playerId(2)
                                                                                   .build()));
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*.*", hasSize(14)))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mancalaGamePlayer1Wins)));
    }

    @Test
    @DisplayName("It should fail when player two uses opponent pit")
    void itShouldFailWhenPlayerTwoUsesOpponentPit() throws Exception {
        // Given
        givenPlayer_2();
        PlayTurnRequest requestBody = selectPit(PLAYER_ONE_PIT_A);
        // When
        this.mockMvc.perform(post(UPDATE_GAME_PATH)
                                     .contentType(APPLICATION_JSON)
                                     .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andDo(print())
        // Then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.errorMessage", Matchers.is("Please choose your own pit!")))
                .andExpect(jsonPath("$.httpStatus", Matchers.is(403)));
    }
    
    /**
     * Player selects next pit
     *
     * @param pitPlace selected pit
     * @return player turn
     */
    private PlayTurnRequest selectPit(PitPlace pitPlace) {
        return PlayTurnRequest.builder()
                .gameId(mockUUID)
                .selectedPit(pitPlace)
                .build();
    }
    
    /**
     * Given player 1 turn.
     */
    private void givenPlayer_1() {
        given(mancalaRepository.findById(mockUUID))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(1).build()));
    }

    /**
     * Given player 2 turn.
     */
    private void givenPlayer_2() {
        given(mancalaRepository.findById(mockUUID))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(2).build()));
    }
    
    /**
     * Get JSON response from file.
     *
     * @param jsonFileName name of the file
     * @return json file
     * @throws Exception exp
     */
    private MancalaGame getMancalaGame(final String jsonFileName) throws Exception {
        return new ObjectMapper().readValue(
                new ClassPathResource(jsonFileName).getFile(),
                MancalaGame.class);
    }
}