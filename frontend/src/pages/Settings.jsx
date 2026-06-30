

import PushToggle from "../components/PushToggle"
import Toast from "../components/Toast"
import ConfirmDialog from "../components/ConfirmDialog"
import { toUserMessage } from "../utils/errorMessage"
import Button from "../mobile/ui/Button"
import { useTopBarActions } from "../mobile/chrome"
import { useMediaQuery } from "../hooks/useMediaQuery"

const UserIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0z" />
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 2a10 10 0 100 20 10 10 0 000-20z" />
  </svg>
)

const BellIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
  </svg>
)



export default function Settings() {
  const online = useOnlineStatus()
  const isMobile = useMediaQuery("(max-width: 768px)")
  // Note: token isn't needed here; sign-in/out handled globally.

  const [searchParams, setSearchParams] = useSearchParams()

  const normalizedTabFromUrl = useMemo(() => {
    const raw = String(searchParams.get("tab") || "").trim().toLowerCase()
    if (raw === "account" || raw === "notifications" || raw === "integrations") return raw
    return ""
  }, [searchParams])

  const [tab, setTab] = useState(() => normalizedTabFromUrl || "account")
  const [meLoading, setMeLoading] = useState(false)
  const [meError, setMeError] = useState("")
  const [me, setMe] = useState(null)
  const [meFetchedAt, setMeFetchedAt] = useState("")
  const [toast, setToast] = useState({ open: false, message: "", tone: "error" })
  // Sign-out is handled from the main sidebar; keep Settings focused on preferences.



  const refreshMe = async () => {
    if (!online) return
    try {
      setMeLoading(true)
      setMeError("")
      const res = await getMe()
      setMe(res.data)
      setMeFetchedAt(new Date().toISOString())
    } catch (e) {
      setMeError(toUserMessage(e, "Couldn't load your account details. Please try again."))
    } finally {
      setMeLoading(false)
    }
  }
  useTopBarActions(
    isMobile ? null : (
      <Button
        variant="secondary"
        size="sm"
        className="px-4 rounded-2xl"
        onClick={refreshMe}
        disabled={!online}
        aria-label="Refresh account"
      >
        Refresh
      </Button>
    ),
    [online, isMobile]
  )

  useEffect(() => {
    refreshMe()
  }, [online])

  useEffect(() => {
    if (!normalizedTabFromUrl) return
    if (normalizedTabFromUrl === tab) return
    setTab(normalizedTabFromUrl)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [normalizedTabFromUrl])

  const setTabAndUrl = (nextTab) => {
    setTab(nextTab)
    const next = new URLSearchParams(searchParams)
    if (nextTab === "account") next.delete("tab")
    else next.set("tab", nextTab)
    setSearchParams(next)
  }


  // (removed) sign out action

  return (
    <div className="space-y-6">
      <Toast open={toast.open} message={toast.message} tone={toast.tone} onClose={() => setToast((t) => ({ ...t, open: false }))} />


      <div className="card overflow-hidden">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <div>
            <h1 className="text-2xl lg:text-3xl font-bold text-white">Settings</h1>
          </div>
          <div className="hidden md:flex items-center gap-2">
            <button type="button" onClick={refreshMe} className="btn-secondary">
              Refresh
            </button>
          </div>
        </div>

        <div className="mt-6 border-t border-dark-700/60" />

        <div className="mt-4">
          <nav className="flex gap-1 overflow-x-auto pb-px scrollbar-hide">
            <TabButton active={tab === "account"} onClick={() => setTabAndUrl("account")} icon={<UserIcon />}>
              Account
            </TabButton>
            <TabButton active={tab === "notifications"} onClick={() => setTabAndUrl("notifications")} icon={<BellIcon />}>
              Notifications
            </TabButton>
            
          </nav>
        </div>
      </div>

      {tab === "account" && (
        <div className="space-y-6">
          <div className="card">
            <div className="flex items-center justify-between gap-4">
              <div>
                <h2 className="text-xl font-semibold text-white">Profile</h2>
                <p className="text-dark-400 text-sm mt-1">Basic information</p>
              </div>
              <div className={`text-xs px-2.5 py-1 rounded-full border ${online ? "border-success-500/30 bg-success-500/10 text-success-300" : "border-warning-500/30 bg-warning-500/10 text-warning-300"}`}>
                {online ? "Online" : "Offline"}
              </div>
            </div>

            <div className="mt-6 grid sm:grid-cols-2 gap-4">
              <InfoField label="Email" value={me?.email || (meLoading ? "Loading…" : meError ? "—" : "—")} />
              <InfoField
                label="Last updated"
                value={meFetchedAt ? new Date(meFetchedAt).toLocaleString() : meLoading ? "Loading…" : "—"}
              />
            </div>

            {meError && <div className="mt-4 text-danger-300 text-sm">{meError}</div>}

            {!online && (
              <div className="mt-6 px-4 py-3 rounded-2xl border border-warning-500/30 bg-warning-500/10 text-warning-300">
                You are offline. Account details may be stale until you reconnect.
              </div>
            )}
          </div>
        </div>
      )}

      {tab === "notifications" && (
        <div className="space-y-6">
          <PushToggle />
        </div>
      )}

     
    </div>
  )
}

function TabButton({ active, onClick, icon, children }) {
  return (
    <button
      onClick={onClick}
      className={`flex items-center gap-2 px-3 sm:px-4 py-3 font-medium text-sm whitespace-nowrap border-b-2 transition-all ${
        active
          ? "text-primary-300 border-primary-400"
          : "text-dark-400 border-transparent hover:text-white hover:border-dark-600"
      }`}
    >
      {icon}
      {children}
    </button>
  )
}

function InfoField({ label, value }) {
  return (
    <div className="rounded-2xl border border-dark-700 bg-dark-800/40 px-4 py-3">
      <div className="text-dark-400 text-xs">{label}</div>
      <div className="mt-1 text-white font-semibold break-words">{value}</div>
    </div>
  )
}

function MiniStat({ label, value }) {
  return (
    <div className="rounded-xl border border-dark-700 bg-dark-800/40 px-3 py-2">
      <div className="text-[11px] text-dark-500">{label}</div>
      <div className="mt-0.5 text-white font-semibold text-sm">{Number(value) || 0}</div>
    </div>
  )
}

function DiagRow({ label, value, mono }) {
  return (
    <div className="flex items-start justify-between gap-4">
      <div className="text-dark-400">{label}</div>
      <div className={`text-white text-right break-all ${mono ? "font-mono text-sm" : "font-semibold"}`}>{value}</div>
    </div>
  )
}
