import api from "./axios"

export function listHabits() {
  return api.get("/api/habits").then(res => res.data)
}

export function createHabit(payload) {
  return api.post("/api/habits", payload).then(res => res.data)
}

export function updateHabit(id, payload) {
  return api.put(`/api/habits/${id}`, payload).then(res => res.data)
}

export function deleteHabit(id) {
  return api.delete(`/api/habits/${id}`)
}

export function resetHabitsToDefaults() {
  return api.post("/api/habits/reset").then(res => res.data)
}

export function listHabitLogsForDays(days) {
  const query = days.map(d => `days=${encodeURIComponent(d)}`).join("&")
  return api.get(`/api/habits/logs?${query}`).then(res => res.data)
}

export function setHabitDoneForDay(dayKey, habitId, done) {
  return api.post(`/api/habits/logs/${dayKey}/${habitId}?done=${done}`).then(res => res.data)
}
