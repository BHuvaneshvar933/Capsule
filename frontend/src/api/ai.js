import api from "./axios"

import { recordAiMetric } from "../perf/metrics"

async function timed(kind, fn) {
  const perf = typeof performance !== "undefined" ? performance : null
  const t0 = perf && perf.now ? perf.now() : Date.now()
  try {
    const res = await fn()
    const t1 = perf && perf.now ? perf.now() : Date.now()
    await recordAiMetric({ kind, durationMs: Math.round(t1 - t0), ok: true })
    return res
  } catch (e) {
    const t1 = perf && perf.now ? perf.now() : Date.now()
    await recordAiMetric({ kind, durationMs: Math.round(t1 - t0), ok: false })
    throw e
  }
}

export function uploadResume(resumeFile) {
  const form = new FormData()
  form.append("resumeFile", resumeFile)
  return timed("uploadResume", () => api.post("/api/resumes", form))
}

export function listResumes() {
  return api.get("/api/resumes")
}

export function analyzeResume(resumeFile, jobDescription) {
  const form = new FormData()
  form.append("resumeFile", resumeFile)
  form.append("jobDescription", jobDescription)
  return timed("analyzeResume", () => api.post("/api/analyze-resume", form))
}

export function generateQuestions({ company, role, resumeId, difficulty }) {
  return timed("generateQuestions", () =>
    api.post("/api/generate-questions", {
      company,
      role,
      resumeId,
      difficulty: difficulty || undefined,
    })
  )
}

export function getQuestionSet(questionSetId) {
  return api.get(`/api/questions/${questionSetId}`)
}
