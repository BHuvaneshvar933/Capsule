const fs = require('fs');
const file = 'C:/Users/bhuva/Desktop/tracker1/frontend/src/pages/Settings.jsx';
let text = fs.readFileSync(file, 'utf8');

// Remove imports related to integrations
text = text.replace(/import \{[\s\S]*?\} from "\.\.\/utils\/integrations"/g, '');
text = text.replace(/import \{ Heatmap90Grid \} from "\.\.\/components\/ActivityHeatmap90Widget"/g, '');

// Remove DatabaseIcon
text = text.replace(/const DatabaseIcon = \(\) => \([\s\S]*?<\/svg>\n\)/g, '');

// Remove integrations state (lines 74 to 87 approx)
text = text.replace(/  const \[ghUsername, setGhUsername\] = useState\(""\)\n[\s\S]*?const \[confirmClearIntegrations, setConfirmClearIntegrations\] = useState\(false\)\n/g, '');

// Remove useEffect for integrations tab
text = text.replace(/  useEffect\(\(\) => \{\n    if \(tab !== "integrations"\) return[\s\S]*?\}, \[tab\]\)\n/g, '');

// Remove syncGitHub
text = text.replace(/  const syncGitHub = async \(\) => \{[\s\S]*?  \}\n/g, '');

// Remove syncLeetCode
text = text.replace(/  const syncLeetCode = async \(\) => \{[\s\S]*?  \}\n/g, '');

// Remove clearIntegrations
text = text.replace(/  const clearIntegrations = async \(\) => \{[\s\S]*?  \}\n/g, '');

// Remove TabButton for integrations
text = text.replace(/<TabButton active=\{tab === "integrations"\} onClick=\{\(\) => setTabAndUrl\("integrations"\)\} icon=\{<DatabaseIcon \/>\}>\n              Integrations\n            <\/TabButton>/g, '');

// Remove {tab === "integrations" && ...} block (up to {tab === "notifications" &&)
text = text.replace(/      \{tab === "integrations" && \([\s\S]*?      \{tab === "notifications" && \(/g, '      {tab === "notifications" && (');

// Remove ConfirmDialog
text = text.replace(/      <ConfirmDialog[\s\S]*?      \/>/g, '');

fs.writeFileSync(file, text);
