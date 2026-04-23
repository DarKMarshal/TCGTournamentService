/** Mirrors Backend com.darkmarshal.tournamentservice.Models.Player */
export interface Player {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Models.Result (Jackson serialization of getters) */
export interface Result {
  player: Player;
  placement: number;
  matchPoints: number;
  opponentWinPercentage: number;
  opponentOpponentWinPercentage: number;
  championshipPointsEarned: number;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.Network.DivisionDTO */
export interface Division {
  ageDivision: string;
  tournamentType: string;
  results: Result[];
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.Network.EventSummaryDTO */
export interface EventSummary {
  id: string;
  name: string;
  winners: Record<string, string>;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.Network.EventDetailsDTO */
export interface EventDetails {
  id: string;
  name: string;
  divisions: Division[];
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.PlayerDTO */
export interface PlayerDTO {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.LeaderboardDTO */
export interface LeaderboardDTO {
  ageDivision: string;
  players: PlayerDTO[];
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.AccountResponse */
export interface AccountResponse {
  id: number;
  username: string;
  playerId: number;
  dateOfBirth: string;
  role: "PLAYER" | "ORGANIZER" | "ADMIN";
  token?: string;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.SignupRequest */
export interface SignupRequest {
  username: string;
  playerId: number;
  dateOfBirth: string;
  password: string;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.LoginRequest */
export interface LoginRequest {
  username: string;
  password: string;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.PersonalPlayerDTO */
export interface PersonalPlayerDTO {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.PersonalResultDTO */
export interface PersonalResultDTO {
  eventId: string;
  eventName: string;
  ageDivision: string;
  placement: number;
  pointsEarned: number;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.PersonalEventDTO */
export interface PersonalEventDTO {
  id: string;
  name: string;
}

/** Mirrors Backend com.darkmarshal.tournamentservice.Services.com.darkmarshal.tournamentservice.DTO.PersonalDataDTO */
export interface PersonalDataDTO {
  player: PersonalPlayerDTO | null;
  results: PersonalResultDTO[];
  uploadedEvents: PersonalEventDTO[];
}
