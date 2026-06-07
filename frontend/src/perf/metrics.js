import { cacheAppendToSet, cacheGet, cacheSet } from "../offline/cache"

const INDEX_KEY = "perf:index"

function entryKey(id) {
  return `perf:entry:${id}`
}

function uuid() {
  if (typeof crypto !== "undefined" && crypto.randomUUID) return crypto.randomUUID()
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

export async function recordAiMetric({ kind, durationMs, ok }) {
  const id = uuid()
  const entry = {
    id,
    kind: String(kind || "unknown"),
    durationMs: Math.max(0, Number(durationMs) || 0),
    ok: Boolean(ok),
    at: new Date().toISOString(),
  }

  await cacheSet(entryKey(id), entry)
  await cacheAppendToSet(INDEX_KEY, id)
  return entry
}

export async function listAiMetrics({ limit = 50 } = {}) {
  const raw = (await cacheGet(INDEX_KEY)) || []
  const ids = Array.isArray(raw) ? raw : []
  const out = []
  for (let i = ids.length - 1; i >= 0 && out.length < limit; i -= 1) {
    // eslint-disable-next-line no-await-in-loop
    const e = await cacheGet(entryKey(ids[i]))
    if (e) out.push(e)
  }
  return out
}

export async function summarizeAiMetrics({ kind } = {}) {
  const all = await listAiMetrics({ limit: 200 })
  const rows = kind ? all.filter((r) => r.kind === kind) : all
  const durs = rows.map((r) => Number(r.durationMs) || 0).filter((n) => n >= 0)
  if (durs.length === 0) return { count: 0, avgMs: 0, maxMs: 0 }
  const total = durs.reduce((a, b) => a + b, 0)
  const max = durs.reduce((m, n) => (n > m ? n : m), 0)
  return { count: durs.length, avgMs: Math.round(total / durs.length), maxMs: max }
}
