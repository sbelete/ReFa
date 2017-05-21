// background.js

// Called when the user clicks on the browser action.
chrome.browserAction.onClicked.addListener(function(tab) {
  // Send a message to the active tab
  chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
    var activeTab = tabs[0];
    chrome.runtime.sendMessage(activeTab.id, {"message": "clicked_browser_action"});
  });
});

// This block is new!
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    var whitelistPath = chrome.extension.getURL('whitelist.html'); 
    if( request.message === "viewWhitelist" ) {
      chrome.tabs.create({"url": whitelistPath}, function(tab){});
    }
    if (request.message === 'setIcon') {
      chrome.browserAction.setIcon({path: request.iconpath, tabId: sender.tab.id});
    }
  }
);
