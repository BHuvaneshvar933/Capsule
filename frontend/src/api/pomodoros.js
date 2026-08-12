import api from "./axios"

export function listPomodoros(since) {
  return api.get("/api/pomodoros", { params: { since } }).then(res => res.data)
}

export function createPomodoro(payload) {
  return api.post("/api/pomodoros", payload).then(res => res.data)
}

export function clearPomodoros() {
  return api.delete("/api/pomodoros")
}
