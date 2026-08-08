import api from "./axios"

export async function listCuratorItems() {
  const { data } = await api.get("/api/curator")
  return data
}

export async function createCuratorItem(payload) {
  const { data } = await api.post("/api/curator", payload)
  return data
}

export async function updateCuratorItem(id, payload) {
  const { data } = await api.put(`/api/curator/${id}`, payload)
  return data
}

export async function deleteCuratorItem(id) {
  await api.delete(`/api/curator/${id}`)
}

export async function toggleCuratorFavorite(id) {
  const { data } = await api.patch(`/api/curator/${id}/favorite`)
  return data
}
