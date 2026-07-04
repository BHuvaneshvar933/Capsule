import { useEffect, useState } from "react"
import { createReminder, deleteReminder, listReminders } from "../api/reminders"
import Toast from "./Toast"
import ConfirmDialog from "./ConfirmDialog"
import { formatLocalDateTime, toInstantISOStringFromLocalInput, toLocalDatetimeInputValue } from "../utils/datetime"
import { toUserMessage } from "../utils/errorMessage"
import { getExistingSubscription, getNotificationPermission, pushSupported } from "../push/push"

const BellIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
    />
  </svg>
)

const TrashIcon = () => (
  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
  </svg>
)

const XIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
  </svg>
)

function isSameId(a, b) {
  if (!a && !b) return true
  if (!a || !b) return false
  return String(a) === String(b)
}

export default function RemindersPanel({ applicationId, todoId, open, onClose }) {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [submitting, setSubmitting] = useState(false)
  const [toast, setToast] = useState({ open: false, message: "", tone: "error" })
  const [confirm, setConfirm] = useState({ open: false, id: null })
  const [pushEnabled, setPushEnabled] = useState(true)

  const [form, setForm] = useState(() => ({
    title: todoId ? "To-do reminder" : "Follow up",
    message: "",
    remindAtLocal: toLocalDatetimeInputValue(new Date(Date.now() + 60 * 60 * 1000)),
  }))

  useEffect(() => {
    setForm((f) => ({
      ...f,
      remindAtLocal: f.remindAtLocal || toLocalDatetimeInputValue(new Date(Date.now() + 60 * 60 * 1000)),
    }))

    // Check if push notifications are enabled
    const checkPushStatus = async () => {
      if (!pushSupported() || getNotificationPermission() !== "granted") {
        setPushEnabled(false)
        return
      }
      try {
        const sub = await getExistingSubscription()
        setPushEnabled(!!sub)
      } catch (err) {
        setPushEnabled(false)
      }
    }
    checkPushStatus()
  }, [])

  const load = async () => {
    try {
      setLoading(true)
      setError("")
      const res = await listReminders()
      const all = res.data || []
      const filtered = todoId
        ? all.filter((r) => isSameId(r.todoId, todoId))
        : applicationId
          ? all.filter((r) => isSameId(r.applicationId, applicationId))
          : all
      setItems(filtered)
    } catch (e) {
      setError(toUserMessage(e, "Couldn't load reminders. Please try again."))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [applicationId, todoId])

  const submit = async (e) => {
    e.preventDefault()
    const remindAt = toInstantISOStringFromLocalInput(form.remindAtLocal)
    if (!remindAt) {
      setToast({ open: true, message: "Please choose a valid date/time.", tone: "error" })
      return
    }

    try {
      setSubmitting(true)
      await createReminder({
        applicationId,
        todoId,
        title: form.title,
        message: form.message,
        remindAt,
      })
      setToast({ open: true, message: "Reminder created.", tone: "success" })
      setForm((f) => ({ ...f, message: "" }))
      await load()
    } catch (e2) {
      setToast({ open: true, message: toUserMessage(e2, "Couldn't create the reminder. Please try again."), tone: "error" })
    } finally {
      setSubmitting(false)
    }
  }

  const askDelete = (id) => setConfirm({ open: true, id })
  const doDelete = async () => {
    const id = confirm.id
    setConfirm({ open: false, id: null })
    if (!id) return
    try {
      await deleteReminder(id)
      setToast({ open: true, message: "Reminder deleted.", tone: "success" })
      await load()
    } catch (e) {
      setToast({ open: true, message: toUserMessage(e, "Couldn't delete the reminder. Please try again."), tone: "error" })
    }
  }

  // If it's closed, we can still render it but off-screen, or we could unmount.
  // Rendering it allows the transition to work correctly when opening/closing.

  return (
    <>
      {/* Backdrop overlay */}
      <div 
        className={`fixed inset-0 bg-black/50 z-40 transition-opacity duration-300 ${open ? 'opacity-100' : 'opacity-0 pointer-events-none'}`} 
        onClick={onClose} 
      />

      {/* Sliding Drawer */}
      <div className={`fixed inset-y-0 right-0 w-full sm:w-[450px] bg-dark-900 border-l border-dark-700 p-6 z-50 overflow-y-auto transition-transform duration-300 ease-in-out ${open ? 'translate-x-0' : 'translate-x-full'}`}>
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-primary-500/10 text-primary-200 border border-primary-500/20">
              <BellIcon />
            </div>
            <div>
              <h2 className="text-xl font-bold text-white">Reminders</h2>
              <p className="text-dark-400 text-sm">Create follow-ups and notifications</p>
              {!pushEnabled && (
                <div className="mt-2 text-warning-400 text-xs bg-warning-500/10 border border-warning-500/20 p-2 rounded-lg">
                  ⚠️ Push notifications are disabled. You will not receive alerts on your device. You can enable them in your settings.
                </div>
              )}
            </div>
          </div>
          <button onClick={onClose} className="p-2 text-dark-400 hover:text-white hover:bg-dark-700 rounded-xl transition-all">
            <XIcon />
          </button>
        </div>

        <div className="space-y-8">
          <Toast open={toast.open} message={toast.message} tone={toast.tone} onClose={() => setToast((t) => ({ ...t, open: false }))} />
          <ConfirmDialog
            open={confirm.open}
            title="Delete reminder?"
            message="This will permanently remove it."
            confirmText="Delete"
            cancelText="Cancel"
            tone="danger"
            onCancel={() => setConfirm({ open: false, id: null })}
            onConfirm={doDelete}
          />

          <div>
            <h3 className="text-white font-semibold mb-4">New Reminder</h3>
            <form onSubmit={submit} className="grid lg:grid-cols-2 gap-4">
          <div className="lg:col-span-1">
            <label className="block text-sm text-dark-400 mb-2">Title *</label>
            <input
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
              className="input-field"
              required
            />
          </div>
          <div className="lg:col-span-1">
            <label className="block text-sm text-dark-400 mb-2">Remind at *</label>
            <input
              type="datetime-local"
              value={form.remindAtLocal}
              onChange={(e) => setForm((f) => ({ ...f, remindAtLocal: e.target.value }))}
              className="input-field"
              required
            />
          </div>
              <div className="lg:col-span-2 flex items-end">
                <button type="submit" disabled={submitting} className="btn-primary w-full disabled:opacity-50">
                  {submitting ? "Creating..." : "Create reminder"}
                </button>
              </div>
            </form>
          </div>
          
          <div className="border-t border-dark-700 pt-8">
        <div className="flex items-center justify-between gap-4">
          <div>
            <div className="text-white font-semibold">Upcoming</div>
            <p className="text-dark-400 text-sm mt-1">
              {todoId
                ? "Only reminders for this to-do are shown here."
                : applicationId
                  ? "Only reminders for this application are shown here."
                  : "All reminders are shown here."}
            </p>
          </div>
          <button type="button" onClick={load} className="btn-ghost">
            Refresh
          </button>
        </div>

        {loading ? (
          <div className="mt-6 text-dark-400">Loading reminders...</div>
        ) : error ? (
          <div className="mt-6 text-danger-300">{error}</div>
        ) : items.length === 0 ? (
          <div className="mt-6 text-dark-500">No reminders yet.</div>
        ) : (
          <div className="mt-6 space-y-3">
            {items.map((r) => (
              <div key={r.id} className="rounded-2xl border border-dark-700 bg-dark-800/40 px-4 py-3">
                <div className="flex items-start justify-between gap-3">
                  <div className="min-w-0">
                    <div className="text-white font-semibold break-words">{r.title}</div>
                    <div className="text-dark-400 text-sm mt-1">{formatLocalDateTime(r.remindAt)}</div>
                    {r.message ? <div className="text-dark-300 text-sm mt-2 break-words">{r.message}</div> : null}
                  </div>
                  <button type="button" onClick={() => askDelete(r.id)} className="btn-ghost text-danger-300 flex items-center justify-center">
                    <TrashIcon />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  </div>
</>
  )
}
