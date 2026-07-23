chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    id: "saveToCapsule",
    title: "Save Job to Capsule",
    contexts: ["page", "selection"]
  });
});

chrome.contextMenus.onClicked.addListener(async (info, tab) => {
  if (info.menuItemId === "saveToCapsule") {
    
    // Extract company and role from page title simply
    const titleParts = tab.title.split("-");
    let company = "Unknown Company";
    let role = tab.title;
    
    if (titleParts.length >= 2) {
      role = titleParts[0].trim();
      company = titleParts[1].trim();
    } else {
      const byParts = tab.title.split(/ at | in | - /i);
      if (byParts.length >= 2) {
         role = byParts[0].trim();
         company = byParts[1].trim();
      }
    }

    // Read token from storage
    const storage = await chrome.storage.local.get(["token"]);
    const token = storage.token;

    if (!token) {
      chrome.notifications.create({
        type: "basic",
        iconUrl: "icons/capsule-128.png",
        title: "Capsule",
        message: "Please open a New Tab and log into Capsule first to save jobs."
      });
      return;
    }

    const payload = {
      company: company,
      role: role,
      jobDescription: "Saved from: " + tab.url + (info.selectionText ? "\n\nExcerpt:\n" + info.selectionText : ""),
      appliedDate: new Date().toISOString().split('T')[0],
      jobUrl: tab.url,
      source: "Chrome Extension"
    };

    try {
      const response = await fetch("https://capsuleh1.tech/api/applications", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify(payload)
      });
      
      if (response.ok) {
        chrome.notifications.create({
          type: "basic",
          iconUrl: "icons/capsule-128.png",
          title: "Job Saved!",
          message: "Successfully saved '" + role + "' at '" + company + "' to your Capsule tracker."
        });
      } else {
        chrome.notifications.create({
          type: "basic",
          iconUrl: "icons/capsule-128.png",
          title: "Error",
          message: "Failed to save job to Capsule."
        });
      }
    } catch (e) {
      chrome.notifications.create({
        type: "basic",
        iconUrl: "icons/capsule-128.png",
        title: "Error",
        message: "Network error while saving to Capsule."
      });
    }
  }
});
